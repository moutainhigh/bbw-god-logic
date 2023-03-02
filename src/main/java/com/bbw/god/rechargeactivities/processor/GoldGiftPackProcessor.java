package com.bbw.god.rechargeactivities.processor;

import com.bbw.common.CloneUtil;
import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.holiday.processor.holidaydaydoublegold.HolidayDayDoubleGoldProcessor;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.CfgDailyShake;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rechargeactivities.RDRechargeActivity;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import com.bbw.god.rechargeactivities.processor.dailyshake.DailyShakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 元宝礼包
 *
 * @author lwb
 * @date 2020/7/2 10:36
 */
@Service
public class GoldGiftPackProcessor extends AbstractRechargeActivityProcessor {
    @Autowired
    private DailyShakeService dailyShakeService;
    @Autowired
    private HolidayDayDoubleGoldProcessor holidayDayDoubleGoldProcessor;
    @Autowired
    private MailService mailService;
    /** 已购买 */
    private static final int IS_BOUGHT = 1;

    @Override
    public RechargeActivityEnum getParent() {
        return RechargeActivityEnum.DIAMOND_PACK;
    }

    @Override
    public RechargeActivityItemEnum getCurrentEnum() {
        return RechargeActivityItemEnum.GOLD_GIFT_PACK;
    }

    @Override
    public boolean isShow(long uid) {
        return true;
    }

    @Override
    public RDRechargeActivity listAwards(long uid) {
        RDRechargeActivity rd = new RDRechargeActivity();
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getGoldRechargeMalls();
        List<RDRechargeActivity.GiftPackInfo> goodsInfos = toRdGoodsInfoList(uid, fMalls, MallEnum.GOLD_RECHARGE_BAG, false);
        CfgDailyShake.Welfare welfare = dailyShakeService.getWelfare(uid);
        for (RDRechargeActivity.GiftPackInfo giftPackInfo : goodsInfos) {
            //是否有福利效果
            if (null == welfare) {
                continue;
            }
            //是否该礼包的福利加成
            if (!welfare.getMallIds().contains(giftPackInfo.getMallId())) {
                continue;
            }
            //是否设置福利id
            if (rd.getDailyShakeWelfareId() != 0) {
                continue;
            }
            rd.setDailyShakeWelfareId(welfare.getId());
        }
        boolean firstGoldPayDouble = holidayDayDoubleGoldProcessor.isFirstGoldPayDouble(uid);
        rd.setActivityPerDayFirstBought(firstGoldPayDouble ? 0 : 1);
        rd.setGoodsList(goodsInfos);
        rd.setCountdown((long) DateUtil.getTimeToNextDay());
        return rd;
    }

    @Override
    public RDRechargeActivity gainAwards(long uid, int pid) {
        return null;
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
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getGoldRechargeMalls();
        buyAwards(fMalls, mallId, uid, rd, WayEnum.GOLD_BAG_TIP, MallEnum.GOLD_RECHARGE_BAG);
        return rd;
    }

    /**
     * 获得的奖励
     *
     * @param cfgMallEntity
     * @param userMallRecord
     * @param isWelfare
     * @return
     */
    @Override
    protected List<Award> getAwards(long uid, CfgMallEntity cfgMallEntity, UserMallRecord userMallRecord, boolean isWelfare, CfgDailyShake.Welfare welfare) {
        //获得奖励
        List<Award> awards = new ArrayList<>();
        awards.addAll(productService.getProductAward(getProductGoodsId(cfgMallEntity.getGoodsId())).getAwardList());
        //节日首充双倍
        boolean isActivityFirstGoldPayDouble = holidayDayDoubleGoldProcessor.isFirstGoldPayDouble(uid);
        if (isActivityFirstGoldPayDouble) {
            return awards;
        }
        //首充双倍
        boolean isFirstCharge = userMallRecord.getNum() == 0;
        if (isFirstCharge) {
            List<Award> clones = CloneUtil.cloneList(awards);
            awards.addAll(clones);
            return awards;
        }
        //福利加成
        if (isWelfare) {
            int welfarAddIndex = welfare.getMallIds().indexOf(cfgMallEntity.getId());
            int welfarAdd = welfare.getWelfareAdds().get(welfarAddIndex);
            awards.add(new Award(0, AwardEnum.YB, welfarAdd));
            //删除福利加成
            dailyShakeService.setWelfare(uid, null);
            return awards;
        }
        return awards;
    }

    /**
     * 增加购买次数
     *
     * @param userMallRecord
     * @param awards
     * @param isWelfare
     */
    @Override
    protected void addBuyNum(UserMallRecord userMallRecord, List<Award> awards, boolean isWelfare) {
        long uid = userMallRecord.getGameUserId();
        //节日首充双倍
        boolean isActivityFirstGoldPayDouble = holidayDayDoubleGoldProcessor.isFirstGoldPayDouble(userMallRecord.getGameUserId());
        //发送活动双倍邮件,先增加次数，后发送邮件
        if (isActivityFirstGoldPayDouble) {
            holidayDayDoubleGoldProcessor.addBuyNum(uid);
            // 活动每日首冲翻倍
            String title = LM.I.getMsgByUid(uid, "activity.day.first.doubled.title");
            String content = LM.I.getMsgByUid(uid, "activity.day.first.doubled.content");
            mailService.sendAwardMail(title, content, uid, awards);
            return;
        }
        //福利效果不增加次数
        if (isWelfare && userMallRecord.getNum() != 0) {
            return;
        }
        //增加次数
        userMallRecord.add();
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
            goodsInfo.setAwards(ListUtil.copyList(productService.getProductAward(goodsInfo.getRechargeId()).getAwardList(), Award.class));
            //是否买过
            if (isBought(userMallRecords, mall)) {
                goodsInfo.setIsBought(IS_BOUGHT);
            }
            list.add(goodsInfo);
        }
        list = list.stream().sorted(Comparator.comparing(RDRechargeActivity.GiftPackInfo::getStatus).reversed()).collect(Collectors.toList());
        return list;
    }

    /**
     * 是否买过
     *
     * @param userMallRecords
     * @param mall
     * @return
     */
    private boolean isBought(List<UserMallRecord> userMallRecords, CfgMallEntity mall) {
        if (ListUtil.isEmpty(userMallRecords)) {
            return false;
        }
        if (ListUtil.isNotEmpty(userMallRecords)) {
            UserMallRecord userMallRecord = userMallRecords.stream().filter(tmp -> tmp.getBaseId().equals(mall.getId())).findFirst().orElse(null);
            if (null == userMallRecord) {
                return false;
            }
            if (null != userMallRecord && userMallRecord.getNum() == 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getCanGainAwardNum(long uid) {
        return 0;
    }
}
