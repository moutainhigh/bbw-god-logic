package com.bbw.god.game.wanxianzhen.service;

import com.bbw.common.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author lwb
 * @date 2020/5/26 14:30
 */
@Slf4j
@Component("wanxianJob")
public class WanXianJob {
    @Autowired
    private WanXianJobService wanXianJobService;

    /**
     * 结束报名
     */
    public void endSignUp() {
        wanXianJobService.endSignUp();
    }

    /**
     * 战斗执行
     */
    public void fightJob() {
        int weekday = DateUtil.getToDayWeekDay();
        Date now = new Date();
        Date end = DateUtil.toDate(now, 12, 0, 0);
        boolean isBefore12Hour = DateUtil.millisecondsInterval(now, end) < 0;
        wanXianJobService.fightJob(weekday, isBefore12Hour);
    }
    /**
     * 淘汰邮件定时器
     */
    public void sendEliminationMailJob(String val) {
        int weekday=DateUtil.getToDayWeekDay();
        wanXianJobService.sendEliminationMailJob(weekday, val);
    }

    /**
     * 邮件定时器
     */
    public void sendMailJob() {
        int weekday= DateUtil.getToDayWeekDay();
        wanXianJobService.sendMailJob(weekday);
    }
}
