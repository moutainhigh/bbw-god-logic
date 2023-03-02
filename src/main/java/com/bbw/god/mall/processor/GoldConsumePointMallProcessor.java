package com.bbw.god.mall.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.processor.GoldConsumeProcessor;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.mall.CfgMallEntity;
import com.bbw.god.game.config.mall.MallEnum;
import com.bbw.god.game.config.mall.MallTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.mall.MallService;
import com.bbw.god.mall.RDGoldConsumePointMallList;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.UserMallRecord;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 特惠礼包
 *
 * @author suhq
 * @date 2018年12月6日 上午10:58:36
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class GoldConsumePointMallProcessor extends AbstractMallProcessor {

    @Autowired
    private AwardService awardService;
    @Autowired
    private MallService mallService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private GoldConsumeProcessor goldConsumeProcessor;
    @Autowired
    private UserTreasureService userTreasureService;

    GoldConsumePointMallProcessor() {
        this.mallType = MallEnum.GOLD_CONSUME;
    }

    @Override
    public RDMallList getGoods(long guId) {
        List<CfgMallEntity> fMalls = MallTool.getMallConfig().getGoldConsumeMalls();
        RDGoldConsumePointMallList rd = new RDGoldConsumePointMallList();
        this.toRdMallList(guId, fMalls, false, rd);
        int sId = this.gameUserService.getActiveSid(guId);
        IActivity a = this.activityService.getActivity(sId, ActivityEnum.GOLD_CONSUME);
        UserTreasure ut = this.userTreasureService.getUserTreasure(guId, TreasureEnum.GOLD_CONSUME_POINT.getValue());
        int point = 0;
        if (ut != null) {
            if (DateUtil.isBetweenIn(ut.getLastGetTime(), a.gainBegin(), a.gainEnd())) {
                // 本期积分
                point = ut.gainTotalNum();
            } else {
                // 清除积分
                userTreasureService.doDelTreasure(ut);
            }
        }
        GameUser gu = this.gameUserService.getGameUser(guId);
        CfgActivityEntity ca = ActivityTool.getActivityByType(ActivityEnum.GOLD_CONSUME);
        UserActivity ua = this.activityService.getUserActivity(guId, a.gainId(), ca.getId());

        rd.setActivityId(ca.getId());
        rd.setPoint(point);
        rd.setRemainTime(a.gainEnd().getTime() - System.currentTimeMillis());
        rd.setAwards(this.goldConsumeProcessor.getAwardsToShow(gu, ua, ca));

        int progress = 0;
        if (ua != null) {
            progress = ua.getProgress();
        }
        rd.setProgress(progress);

        AwardStatus status = this.goldConsumeProcessor.getUAStatus(gu, a, ua, ca);
        rd.setStatus(status.getValue());
        if (status == AwardStatus.AWARDED) {
            rd.setAwardIndex(ua.getAwardIndex());
        }
        return rd;
    }

    @Override
    public void deliver(long guId, CfgMallEntity mall, int buyNum, RDCommon rd) {
        int num = mall.getNum() * buyNum;
        Award award = new Award(mall.getGoodsId(), AwardEnum.fromValue(mall.getItem()), num);
        String broadcastPrefix = "在" + WayEnum.EXCHANGE_GOLD_CONSUME_POINT.getName();
        this.awardService.fetchAward(guId, Arrays.asList(award), WayEnum.EXCHANGE_GOLD_CONSUME_POINT, broadcastPrefix, rd);

    }

    @Override
    protected List<UserMallRecord> getUserMallRecords(long guId) {
        // 读取类型为特惠礼包的UserMallRecord
        // TODO:可能会有性能问题
        List<UserMallRecord> favorableRecords = this.mallService.getUserMallRecord(guId, MallEnum.GOLD_CONSUME);
        List<UserMallRecord> validRecords = favorableRecords.stream().filter(umr -> umr.ifValid())
                .collect(Collectors.toList());
        int sId = this.gameUserService.getActiveSid(guId);
        IActivity a = this.activityService.getActivity(sId, ActivityEnum.GOLD_CONSUME);
        // 删除过期的消费积分兑换记录
        List<UserMallRecord> outDateRecords = validRecords.stream().filter(tmp -> !DateUtil.isBetweenIn(tmp.getDateTime(), a.gainBegin(), a.gainEnd())).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(outDateRecords)) {
            this.gameUserService.deleteItems(guId, outDateRecords);
            LogUtil.logDeletedUserDatas(outDateRecords, "过期的元宝消费积分兑换记录");
            validRecords.removeAll(outDateRecords);
        }
        return validRecords;
    }

}
