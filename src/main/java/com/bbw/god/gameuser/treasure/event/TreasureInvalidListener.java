package com.bbw.god.gameuser.treasure.event;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.login.event.EPFirstLoginPerDay;
import com.bbw.god.login.event.FirstLoginPerDayEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author suhq
 * @date 2018年10月17日 下午3:01:07
 */
@Slf4j
@Component
public class TreasureInvalidListener {
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private MailService mailService;
    @Autowired
    private ActivityService activityService;

    @Async
    @EventListener
    public void firstLoginPerDay(FirstLoginPerDayEvent event) {
        Date now = DateUtil.now();
        EPFirstLoginPerDay ep = event.getEP();
        long guId = ep.getUid();
        List<UserTreasure> uts = userTreasureService.getAllUserTreasures(guId);
        if (ListUtil.isEmpty(uts)) {
            return;
        }
        for (UserTreasure ut : uts) {
            List<UserTreasure.LimitInfo> limitInfos = ut.getLimitInfos();
            if (ListUtil.isEmpty(limitInfos)) {
                continue;
            }
            Optional<UserTreasure.LimitInfo> limitInfoOp = limitInfos.stream().filter(tmp -> DateUtil.getDaysBetween(now, DateUtil.fromDateLong(tmp.getExpireTime())) == 1).findFirst();
            if (!limitInfoOp.isPresent()) {
                continue;
            }
            if (TreasureEnum.SAI_ZHOU_POINT.getValue() == ut.getBaseId()) {
                continue;

            }
            UserTreasure.LimitInfo limitInfo = limitInfoOp.get();
            String title = LM.I.getMsgByUid(ut.getGameUserId(), "mail.treasure.going.to.outdate.title", ut.getName());
            String expireDatInfo = DateUtil.toDateTimeString(DateUtil.fromDateLong(limitInfo.getExpireTime()));
            String content = LM.I.getMsgByUid(ut.getGameUserId(), "mail.treasure.going.to.outdate.content", limitInfo.getTimeLimitNum(), ut.getName(), expireDatInfo);
            mailService.sendSystemMail(title, content, guId);
        }
    }

    @Async
    @EventListener
    public void goldConsumeInvalid(FirstLoginPerDayEvent event) {
        EPFirstLoginPerDay ep = event.getEP();
        long uid = ep.getUid();
        Integer sid = ep.getLoginInfo().getUser().getServerId();
        IActivity activity = activityService.getActivity(sid, ActivityEnum.GOLD_CONSUME);
        if (null == activity) {
            return;
        }
        UserTreasure userTreasure = userTreasureService.getUserTreasure(uid, TreasureEnum.GOLD_CONSUME_POINT.getValue());
        if (null == userTreasure) {
            return;
        }

        long hourBetween = DateUtil.getHourBetween(DateUtil.now(), activity.gainEnd());
        boolean betweenIn = DateUtil.isBetweenIn(userTreasure.getLastGetTime(), activity.gainBegin(), activity.gainEnd());
        if (hourBetween > 24 || !betweenIn) {
            return;
        }
        int treasureNum = userTreasure.gainTotalNum();
        if (treasureNum <= 0) {
            return;
        }
        String title = LM.I.getMsgByUid(uid, "mail.treasure.going.to.outdate.title", TreasureEnum.GOLD_CONSUME_POINT.getName());
        String endTime = DateUtil.toDateTimeString(activity.gainEnd());
        String content = LM.I.getMsgByUid(uid, "mail.treasure.going.to.outdate.content", treasureNum, TreasureEnum.GOLD_CONSUME_POINT.getName(), endTime);
        mailService.sendSystemMail(title, content, uid);
    }
}
