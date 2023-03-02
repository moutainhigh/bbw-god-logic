package com.bbw.god.gameuser.task.timelimit;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class UserDispatchTaskServiceTest {

    @Test
    public void test() {

        Assert.assertEquals(false, isToResetDate(TaskGroupEnum.CUN_ZHUANG_TASK, DateUtil.fromDateTimeString("2022-07-28 01:00:00"), getCalendar("2022-07-28 02:01:00")));
        Assert.assertEquals(true, isToResetDate(TaskGroupEnum.CUN_ZHUANG_TASK, DateUtil.fromDateTimeString("2022-07-28 01:00:00"), getCalendar("2022-07-28 08:01:00")));
        Assert.assertEquals(true, isToResetDate(TaskGroupEnum.CUN_ZHUANG_TASK, DateUtil.fromDateTimeString("2022-07-28 01:00:00"), getCalendar("2022-07-29 01:01:00")));
        Assert.assertEquals(true, isToResetDate(TaskGroupEnum.CUN_ZHUANG_TASK, DateUtil.fromDateTimeString("2022-07-28 01:00:00"), getCalendar("2022-07-29 08:01:00")));
        Assert.assertEquals(true, isToResetDate(TaskGroupEnum.CUN_ZHUANG_TASK, DateUtil.fromDateTimeString("2022-07-28 01:00:00"), getCalendar("2022-07-30 02:01:00")));
        Assert.assertEquals(false, isToResetDate(TaskGroupEnum.CUN_ZHUANG_TASK, DateUtil.fromDateTimeString("2022-07-28 08:01:00"), getCalendar("2022-07-28 09:01:00")));
        Assert.assertEquals(false, isToResetDate(TaskGroupEnum.CUN_ZHUANG_TASK, DateUtil.fromDateTimeString("2022-07-28 08:01:00"), getCalendar("2022-07-29 01:01:00")));
        Assert.assertEquals(true, isToResetDate(TaskGroupEnum.CUN_ZHUANG_TASK, DateUtil.fromDateTimeString("2022-07-28 08:01:00"), getCalendar("2022-07-29 08:01:00")));
        Assert.assertEquals(true, isToResetDate(TaskGroupEnum.CUN_ZHUANG_TASK, DateUtil.fromDateTimeString("2022-07-28 08:01:00"), getCalendar("2022-07-30 02:01:00")));

    }

    private Calendar getCalendar(String dateTime) {
        Date date = DateUtil.fromDateTimeString(dateTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private boolean isToResetDate(TaskGroupEnum taskGroup, Date lastUpdateDate, Calendar now) {
        CfgTimeLimitTaskRules rules = TimeLimitTaskTool.getRules(taskGroup);
        Integer resetHour = rules.getCardVigorRestHour();
        Calendar lastUpdateTime = Calendar.getInstance();
        lastUpdateTime.setTime(lastUpdateDate);
        int nowHour = now.get(Calendar.HOUR_OF_DAY);
        int lastUpdateHour = lastUpdateTime.get(Calendar.HOUR_OF_DAY);
        int daysBetween = DateUtil.getDaysBetween(lastUpdateDate, now.getTime());
        if (daysBetween > 1) {
            return true;
        }
        if (daysBetween == 0 && nowHour >= resetHour && lastUpdateHour < resetHour) {
            return true;
        }
        if (daysBetween == 1 && (nowHour >= resetHour || lastUpdateHour < resetHour)) {
            return true;
        }
        return false;
    }

}