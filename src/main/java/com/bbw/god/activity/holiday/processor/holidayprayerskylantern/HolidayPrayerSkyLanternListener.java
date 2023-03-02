package com.bbw.god.activity.holiday.processor.holidayprayerskylantern;

import com.bbw.common.LM;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.cfg.CfgPrayerSkyLanternConfig;
import com.bbw.god.activity.config.LanternFestivalTool;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.MailService;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.gameuser.treasure.event.EPTreasureDeduct;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureDeductEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 天灯祈福监听
 *
 * @author fzj
 * @date 2022/2/8 18:57
 */
@Component
@Slf4j
@Async
public class HolidayPrayerSkyLanternListener {
    @Autowired
    HolidayPrayerSkyLanternProcessor holidayPrayerSkyLanternProcessor;
    @Autowired
    HolidayPrayerSkyLanternService holidayPrayerSkyLanternService;
    @Autowired
    MailService mailService;
    @Autowired
    GameUserService gameUserService;

    /**
     * 发放放飞天灯奖励
     *
     * @param event
     */
    @Order(1000)
    @EventListener
    public void sendPrayAward(TreasureDeductEvent event) {
        EPTreasureDeduct ep = event.getEP();
        Long guId = ep.getGuId();
        int sid = gameUserService.getActiveSid(guId);
        boolean opened = holidayPrayerSkyLanternProcessor.isOpened(sid);
        if (!opened){
            return;
        }
        EVTreasure deductTreasure = ep.getDeductTreasure();
        if (deductTreasure.getId() != TreasureEnum.SKY_LANTERN.getValue()) {
            return;
        }
        //获取全服放飞天灯次数
        Integer skyLanternTimes = holidayPrayerSkyLanternService.getPutSkyLanternTimes();
        //获得对应配置
        CfgPrayerSkyLanternConfig.SkyLanternTarget targetAndAward = LanternFestivalTool.getTargetAndAward(skyLanternTimes);
        if (null == targetAndAward) {
            return;
        }
        //获得奖励发放状态
        Integer target = targetAndAward.getSkyLanternTimes();
        Integer awardStatus = holidayPrayerSkyLanternService.getAwardStatus(target);
        if (null != awardStatus && awardStatus == AwardStatus.AWARDED.getValue()) {
            return;
        }
        //发放邮件奖励奖励
        sendMailAwards(targetAndAward.getAwards(), target);
        //更新目标奖励状态
        holidayPrayerSkyLanternService.updateAwardStatus(target, AwardStatus.AWARDED.getValue());
    }

    /**
     * 发送目标奖励
     *
     * @param awards
     * @param target
     */
    private void sendMailAwards(List<Award> awards, int target) {
        //获取参与玩家
        List<Long> joiners = holidayPrayerSkyLanternService.getJoiners();
        if (joiners.isEmpty()){
            throw new ExceptionForClientTip("message.param.error");
        }
        //发放邮件奖励
        List<UserMail> userMails = new ArrayList<>();
        Long joiner = joiners.get(0);
        String title = LM.I.getMsgByUid(joiner, "activity.put.skyLantern.title");
        String content = LM.I.getMsgByUid(joiner, "activity.put.skyLantern.massage", target / 10000);
        for (long uid : joiners) {
            UserMail userMail = UserMail.newAwardMail(title, content, uid, awards);
            userMails.add(userMail);
        }
        gameUserService.addItems(userMails);
    }
}
