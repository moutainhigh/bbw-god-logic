package com.bbw.god.rechargeactivities.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgDailyShake;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.CfgMallExtraPackEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rechargeactivities.RDRechargeActivity;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import com.bbw.god.rechargeactivities.processor.dailyshake.DailyShakeProcessor;
import com.bbw.god.rechargeactivities.processor.dailyshake.DailyShakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 每日礼包
 *
 * @author lwb
 * @date 2020/7/1 17:18
 */
@Service
public class DailyGiftPackProcessor extends AbstractRechargeActivityProcessor {
    @Autowired
    private DailyShakeService dailyShakeService;
    private static final int ZHAO_MU_GIFT_PACK_ID = 105098;
    /**
     * 1元宝的礼包
     */
    private static final int ONE_GOLD_GIFT_PACK_ID = 105001;

    @Override
    public RechargeActivityEnum getParent() {
        return RechargeActivityEnum.DIAMOND_PACK;
    }

    @Override
    public RechargeActivityItemEnum getCurrentEnum() {
        return RechargeActivityItemEnum.DAILY_GIFT_PACK;
    }

    /**
     * 获取展示的等级所需值
     *
     * @return
     */
    @Override
    protected int getShowNeedLevel() {
        return 15;
    }

    /**
     * 获取展示的充值所需值
     *
     * @return
     */
    @Override
    protected int getShowNeedRecharge() {
        return 10;
    }

    @Override
    public int getCanGainAwardNum(long uid) {
        return 0;
    }
    @Override
    public RDRechargeActivity listAwards(long uid) {
        RDRechargeActivity rd = new RDRechargeActivity();
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getDailyRechargeMalls();
        fMalls = fMalls.stream().filter(tmp -> tmp.getId() != DailyShakeProcessor.DAILY_SHAKE_ID).collect(Collectors.toList());
        List<RDRechargeActivity.GiftPackInfo> goodsInfos = toRdGoodsInfoList(uid, fMalls, MallEnum.DAILY_RECHARGE_BAG, false);
        for (RDRechargeActivity.GiftPackInfo giftPackInfo : goodsInfos) {
            CfgDailyShake.Welfare welfare = dailyShakeService.getWelfare(uid);
            if (null != welfare && welfare.getMallIds().contains(giftPackInfo.getMallId())) {
                giftPackInfo.setWelfareId(welfare.getId());
            }
            if (giftPackInfo.getMallId() != ZHAO_MU_GIFT_PACK_ID) {
                continue;
            }
            //补充可选的奖励
            UserMallRecord zhaoMuMallRecord = mallService.getUserMallRecord(uid, ZHAO_MU_GIFT_PACK_ID);
            giftPackInfo.setExtraAwardStatus(0);
            if (zhaoMuMallRecord == null || zhaoMuMallRecord.getNum() == 0) {
                //可选择奖励
                giftPackInfo.setExtraAwards(getZhaoMuAwards(giftPackInfo.getMallId()));
            }
            if (zhaoMuMallRecord != null && ListUtil.isNotEmpty(zhaoMuMallRecord.getPickedAwards())) {
                //已选的奖励补充进去
                giftPackInfo.getAwards().addAll(zhaoMuMallRecord.getPickedAwards());
                giftPackInfo.setExtraAwardStatus(zhaoMuMallRecord.getNum() > 0 ? null : 1);
            }
        }
        rd.setGoodsList(goodsInfos);
        rd.setCountdown((long) DateUtil.getTimeToNextDay());
        return rd;
    }

    /**
     * 购买礼包
     *
     * @param uid
     * @param mallId
     * @return
     */
    @Override
    public RDRechargeActivity buyAwards(long uid, int mallId) {
        RDRechargeActivity rd = new RDRechargeActivity();
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getDailyRechargeMalls();
        buyAwards(fMalls, mallId, uid, rd, WayEnum.DAILY_DIAMOND_GIFT_PACK, MallEnum.DAILY_RECHARGE_BAG);
        return rd;
    }

    /**
     * 获得价格
     *
     * @param cfgMallEntity
     * @param isWelfare
     * @param welfare
     * @return
     */
    @Override
    protected int getPrice(long uid, CfgMallEntity cfgMallEntity, boolean isWelfare, CfgDailyShake.Welfare welfare) {
        //获得价格
        int price = cfgMallEntity.getPrice();
        //福利加成
        if (!isWelfare) {
            return price;
        }
        int welfarAddIndex = welfare.getMallIds().indexOf(cfgMallEntity.getId());
        price = welfare.getWelfareAdds().get(welfarAddIndex);
        //删除福利加成
        dailyShakeService.setWelfare(uid, null);
        return price;
    }

    /**
     * 获得的奖励
     *
     * @param cfgMallEntity
     * @param isWelfare
     * @return
     */
    @Override
    protected List<Award> getAwards(long uid, CfgMallEntity cfgMallEntity, UserMallRecord userMallRecord, boolean isWelfare, CfgDailyShake.Welfare welfare) {
        List<Award> awards = new ArrayList<>();
        awards.addAll(productService.getProductAward(getProductGoodsId(cfgMallEntity.getGoodsId())).getAwardList());

        //不是可选礼包直接返回
        if (cfgMallEntity.getId() != ZHAO_MU_GIFT_PACK_ID) {
            return awards;
        }

        List<Award> pickedAwards = userMallRecord.getPickedAwards();
        if (ListUtil.isEmpty(pickedAwards)) {
            throw new ExceptionForClientTip("rechargeActivity.not.select.Awards");
        }
        awards.addAll(pickedAwards);
        return awards;
    }


    @Override
    public RDRechargeActivity pickAwards(long uid, Integer mallId, String awardIds) {
        if (mallId != ZHAO_MU_GIFT_PACK_ID) {
            //该项没有可选择的奖励
            throw new ExceptionForClientTip("rechargeActivity.not.extraAwards");
        }
        UserMallRecord zhaoMuMallRecord = mallService.getUserMallRecord(uid, ZHAO_MU_GIFT_PACK_ID);
        if (zhaoMuMallRecord == null) {
            zhaoMuMallRecord = UserMallRecord.instance(uid, ZHAO_MU_GIFT_PACK_ID, MallEnum.DAILY_RECHARGE_BAG.getValue(), 0);
            mallService.addRecord(zhaoMuMallRecord);
        }
        if (zhaoMuMallRecord.getNum() >= 1) {
            //已购买不可再换
            throw new ExceptionForClientTip("rechargeActivity.picked.Awards");
        }
        List<Integer> awardIdList = ListUtil.parseStrToInts(awardIds);
        Optional<Award> optionalAward = getZhaoMuAwards(mallId).stream().filter(p -> awardIdList.contains(p.getAwardId())).findFirst();
        if (!optionalAward.isPresent()) {
            throw new ExceptionForClientTip("rechargeActivity.not.extraAwards");
        }
        zhaoMuMallRecord.setPickedAwards(Arrays.asList(optionalAward.get()));
        gameUserService.updateItem(zhaoMuMallRecord);
        return new RDRechargeActivity();
    }

    /**
     * 获取每日招募额外自选奖励
     *
     * @return
     */
    private List<Award> getZhaoMuAwards(int mallId) {
        //获得额外礼包配置
        CfgMallExtraPackEntity extraPack = MallTool.getMallExtraPack(mallId);
        return extraPack.getExtraAwards();
    }
}
