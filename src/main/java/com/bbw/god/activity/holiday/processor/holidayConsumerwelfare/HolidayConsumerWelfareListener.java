package com.bbw.god.activity.holiday.processor.holidayConsumerwelfare;

import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.res.diamond.DiamondDeductEvent;
import com.bbw.god.gameuser.res.diamond.EPDiamondDeduct;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.EPTreasureExpired;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.gameuser.treasure.event.TreasureExpiredEvent;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消费福利监听类
 *
 * @author: suhq
 * @date: 2023/1/11 6:31 下午
 */
@Slf4j
@Component
public class HolidayConsumerWelfareListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private MailService mailService;

    @Autowired
    private UserTreasureService userTreasureService;

    /**
     * 钻石扣除监听
     *
     * @param event
     */
    @EventListener
    public void deductDiamond(DiamondDeductEvent event) {
        EPDiamondDeduct ep = event.getEP();
        long uid = ep.getGuId();
        int sid = gameUserService.getActiveSid(uid);
        //获取玩家活动信息
        IActivity a = activityService.getActivity(sid, ActivityEnum.CONSUMPTION_WELFARE);
        //活动未开启
        if (null == a) {
            return;
        }
        List<UserActivity> userActivities = activityService.getUserActivities(ep.getGuId(), a.gainId(), ActivityEnum.CONSUMPTION_WELFARE);
        UserActivity userActivity;
        //活动信息为空,初始化
        if (ListUtil.isEmpty(userActivities)) {
            List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(ActivityEnum.CONSUMPTION_WELFARE);
            userActivity = UserActivity.fromActivity(uid, a.gainId(), 0, cas.get(0));
            userActivity.setProgress(ep.getDeductDiamond());
            activityService.addUserActivity(uid, userActivity);
            return;
        }
        //获取总进度
        userActivity = userActivities.get(0);
        int progress = null == userActivity.getProgress() ? ep.getDeductDiamond() : ep.getDeductDiamond() + userActivity.getProgress();
        //需要钻石福利返回数量(即钻石消费积分)
        int needDiamondConsumePointNum = progress;
        //拥有钻石消费积分
        int ownDiamondConsumePointNum = userTreasureService.getTreasureNum(uid, TreasureEnum.DIAMOND_CONSUMPTION_POINTS.getValue());
        //是否需要增加钻石积分
        if (needDiamondConsumePointNum > ownDiamondConsumePointNum) {
            int addDiamondConsumePoint = needDiamondConsumePointNum - ownDiamondConsumePointNum;
            TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.DIAMOND_CONSUMPTION_POINTS.getValue(), addDiamondConsumePoint, WayEnum.EXCHANGE_GOLD_CONSUME_POINT, ep.getRd());
            List<RDCommon.RDTreasureInfo> rdTreasureInfos = ep.getRd().getTreasures().stream().filter(tmp -> tmp.getId() != TreasureEnum.DIAMOND_CONSUMPTION_POINTS.getValue()).collect(Collectors.toList());
            ep.getRd().setTreasures(rdTreasureInfos);
        }
        //添加进度
        userActivity.setProgress(progress);
        activityService.addUserActivity(uid, userActivity);
    }


    /**
     * 钻石消费积分过期
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void diamondConsumePointExpired(TreasureExpiredEvent event) {
        EPTreasureExpired ep = event.getEP();
        int treasureId = ep.getTreasureId();
        //不是钻石积分过期
        if (TreasureEnum.DIAMOND_CONSUMPTION_POINTS.getValue() != treasureId) {
            return;
        }
        //获得玩家钻石积分数量
        int diamondPointNum = (int) ep.getExpiredNum();
        //钻石返回数量
        int returnQuantity = getReturnDiamondNum(diamondPointNum);
        System.out.println(ep.getGuId() + "钻石积分[" + treasureId + "]过期,过期数量：" + diamondPointNum + ",返钻石：" + returnQuantity);

        Award award = new Award(AwardEnum.ZS, returnQuantity);
        String title = LM.I.getMsgByUid(ep.getGuId(), "activity.game.consumerWelfare.title");
        String content = LM.I.getMsgByUid(ep.getGuId(), "activity.game.consumerWelfare.content", diamondPointNum, returnQuantity);
        mailService.sendAwardMail(title, content, ep.getGuId(), Arrays.asList(award));
    }


    /**
     * 获得返还钻石数量
     *
     * @param consumeDiamondPointNum
     * @return
     */
    private Integer getReturnDiamondNum(Integer consumeDiamondPointNum) {
        System.out.println("getReturnDiamondNum param:" + consumeDiamondPointNum);
        if (consumeDiamondPointNum >= 5000) {
            return consumeDiamondPointNum * 20 / 100;
        }
        if (consumeDiamondPointNum >= 3000) {
            return consumeDiamondPointNum * 15 / 100;
        }
        if (consumeDiamondPointNum >= 1000) {
            return consumeDiamondPointNum * 10 / 100;
        }
        if (consumeDiamondPointNum >= 100) {
            return consumeDiamondPointNum * 5 / 100;
        }
        return 0;
    }
}
