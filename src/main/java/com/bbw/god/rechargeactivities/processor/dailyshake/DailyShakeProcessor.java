package com.bbw.god.rechargeactivities.processor.dailyshake;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.ConsumeType;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgDailyShake;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rechargeactivities.RDRechargeActivity;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import com.bbw.god.rechargeactivities.processor.AbstractRechargeActivityProcessor;
import com.bbw.god.rechargeactivities.processor.RechargeStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 每日摇一摇
 *
 * @author: huanghb
 * @date: 2022/6/14 15:32
 */
@Service
public class DailyShakeProcessor extends AbstractRechargeActivityProcessor {
    @Autowired
    private DailyShakeService dailyShakeService;
    /** 每日摇一摇id */
    public static final int DAILY_SHAKE_ID = 105111;

    @Override
    public RechargeActivityEnum getParent() {
        return RechargeActivityEnum.DIAMOND_PACK;
    }

    @Override
    public RechargeActivityItemEnum getCurrentEnum() {
        return RechargeActivityItemEnum.DAILY_SHAKE;
    }

    @Override
    public boolean isShow(long uid) {
        return true;
    }

    /**
     * 获得活动信息
     *
     * @param uid
     * @return
     */
    @Override
    public RDRechargeActivity listAwards(long uid) {
        RDRechargeActivity rd = new RDRechargeActivity();
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getDailyRechargeMalls();
        fMalls = fMalls.stream().filter(tmp -> tmp.getId() == DAILY_SHAKE_ID).collect(Collectors.toList());
        List<RDRechargeActivity.GiftPackInfo> goodsInfos = toRdGoodsInfoList(uid, fMalls, MallEnum.DAILY_RECHARGE_BAG, false);
        rd.setGoodsList(goodsInfos);
        rd.setCountdown((long) DateUtil.getTimeToNextDay());
        //获取福利信息
        CfgDailyShake.Welfare welfare = dailyShakeService.getWelfare(uid);
        if (null == welfare) {
            return rd;
        }
        rd.setDailyShakeWelfareId(welfare.getId());
        return rd;
    }

    /**
     * 通过元宝购买的走该方法
     *
     * @param uid
     * @param realId
     * @return
     */
    @Override
    public RDRechargeActivity buyAwards(long uid, int realId) {
        RDRechargeActivity rd = new RDRechargeActivity();
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getDailyRechargeMalls();
        buyAwards(fMalls, realId, uid, rd, WayEnum.DAILY_DIAMOND_GIFT_PACK, MallEnum.DAILY_RECHARGE_BAG);
        return rd;
    }

    /**
     * 将配置的商品转换为返回的参数类型
     *
     * @param guId
     * @param fMalls
     * @param mallEnum
     * @param isExtraDiscount
     * @return
     */
    @Override
    protected List<RDRechargeActivity.GiftPackInfo> toRdGoodsInfoList(long guId, List<CfgMallEntity> fMalls, MallEnum mallEnum, boolean isExtraDiscount) {
        List<RDRechargeActivity.GiftPackInfo> list = new ArrayList<>(16);
        List<UserMallRecord> userMallRecords = mallService.getUserMallRecord(guId, mallEnum);
        if (ListUtil.isNotEmpty(userMallRecords)) {
            userMallRecords = userMallRecords.stream().filter(p -> p.ifValid()).collect(Collectors.toList());
        }
        for (CfgMallEntity mall : fMalls) {
            RDRechargeActivity.GiftPackInfo goodsInfo = RDRechargeActivity.GiftPackInfo.instance(mall, mall.getPrice(isExtraDiscount));
            // 数量受限的商品
            if (mall.getLimit() > 0 && ListUtil.isNotEmpty(userMallRecords)) {
                // 礼包购买情况
                Optional<UserMallRecord> optional = userMallRecords.stream().filter(p -> p.getBaseId().equals(mall.getId())).findFirst();
                if (optional.isPresent()) {
                    UserMallRecord um = optional.get();
                    goodsInfo.setRemainTimes(mall.getLimit() - um.getNum());
                    if (um.getStatus() != null && um.getStatus() == RechargeStatusEnum.CAN_GAIN_AWARD.getStatus()) {
                        goodsInfo.setStatus(um.getStatus());
                    }
                }
            }
            if (goodsInfo.getRemainTimes() <= 0) {
                goodsInfo.setStatus(-1);
            }
            list.add(goodsInfo);
        }
        list = list.stream().sorted(Comparator.comparing(RDRechargeActivity.GiftPackInfo::getStatus).reversed()).collect(Collectors.toList());
        return list;
    }

    /**
     * 具体购买
     *
     * @param fMalls
     * @param mallId
     * @param uid
     * @param rd
     * @param wayEnum
     * @param mallEnum
     */
    @Override
    protected void buyAwards(List<CfgMallEntity> fMalls, int mallId, long uid, RDRechargeActivity rd, WayEnum wayEnum, MallEnum mallEnum) {
        Optional<CfgMallEntity> optional = fMalls.stream().filter(p -> p.getId() == mallId).findFirst();
        if (!optional.isPresent()) {
            //不存在
            throw new ExceptionForClientTip("rechargeActivity.cant.use.diamond");
        }
        CfgMallEntity cfgMallEntity = optional.get();
        if (cfgMallEntity.getUnit() != ConsumeType.DIAMOND.getValue()) {
            //不支持的购买类型：非钻石
            throw new ExceptionForClientTip("rechargeActivity.cant.use.diamond");
        }
        UserMallRecord mallRecord = mallService.getUserMallRecord(uid, cfgMallEntity.getId());
        if (mallRecord == null) {
            mallRecord = UserMallRecord.instance(uid, cfgMallEntity.getId(), mallEnum.getValue(), 0);
            mallService.addRecord(mallRecord);
        }
        if (mallRecord.getNum() >= cfgMallEntity.getLimit()) {
            //上限
            throw new ExceptionForClientTip("rechargeActivity.awarded");
        }
        //获得购买次数
        int shakeTimes = mallRecord.getNum();
        //不是第一次购买
        if (shakeTimes > 0) {
            ResChecker.checkDiamond(gameUserService.getGameUser(uid), cfgMallEntity.getPrice());
            ResEventPublisher.pubDiamondDeductEvent(uid, cfgMallEntity.getPrice(), wayEnum, rd);
        }
        shakeTimes++;
        //获得奖励
        List<Award> awards = CfgDailyShakeTool.getDailyShakeAwards(shakeTimes);
        //获得福利
        Award award = awards.stream().filter(tmp -> CfgDailyShakeTool.getWelfareType().contains(tmp.getAwardId())).findFirst().orElse(null);
        CfgDailyShake.Welfare welfare = CfgDailyShakeTool.getWelfare((award.getAwardId()));
        rd.setDailyShakeWelfareId(welfare.getId());
        //缓存福利
        dailyShakeService.setWelfare(uid, welfare);

        awardService.fetchAward(uid, awards, wayEnum, "", rd);
        mallRecord.setNum(shakeTimes);
        gameUserService.updateItem(mallRecord);
    }


    @Override
    public RDRechargeActivity gainAwards(long uid, int pid) {
        return null;
    }

    @Override
    public int getCanGainAwardNum(long uid) {
        UserMallRecord userMallRecord = mallService.getUserMallRecord(uid, DAILY_SHAKE_ID);
        if (null == userMallRecord) {
            return 1;
        }
        if (userMallRecord.getNum() == 0) {
            return 1;
        }
        return 0;
    }


}
