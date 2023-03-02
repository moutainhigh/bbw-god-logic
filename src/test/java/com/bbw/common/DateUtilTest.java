package com.bbw.common;

import com.bbw.god.game.sxdh.SxdhZoneService.ZoneDate;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-26 23:36
 */
@Slf4j
public class DateUtilTest {

    @Test
    public void test() {
        System.out.println(DateUtil.fromDateTimeString("2022-11-06 23:59:59").getTime());
        System.out.println(DateUtil.toDateTimeString(new Date(1667457338897L)));
        Date now = DateUtil.now();
        System.out.println(DateUtil.toString(now, "M月d日 08:00"));
        log.info(DateUtil.getMinute(DateUtil.fromDateTimeString("2022-02-02 10:00:00")) + "");
        log.info(DateUtil.getMinute(DateUtil.fromDateTimeString("2022-02-02 10:01:01")) + "");
        log.info(DateUtil.getMinute(DateUtil.fromDateTimeString("2022-02-02 10:59:59")) + "");

        // Date d1 = DateUtil.fromDateInt(20190326t);
        // Date d2 = DateUtil.fromDateInt(20190326);
        // System.out.println(DateUtil.until(d1, d2));
        // Date date1 = DateUtil.fromDateTimeString("2019-05-12 22:00:00");
        // Date date2 = DateUtil.fromDateTimeString("2019-05-13 22:00:00");
        // assertEquals(true, DateUtil.isWeekEndDate(date1));
        // assertEquals(true, DateUtil.isWeekBeginDate(date2));
        // assertEquals(1, DateUtil.getDaysBetween(date1, date2));
        System.out.println(DateUtil.toDateTimeString(DateUtil.addDays(now, -11)));
        //
        Date d1 = DateUtil.fromDateTimeString("2019-01-01 22:22:21");
        Date d2 = DateUtil.fromDateTimeString("2019-01-01 22:22:22");
        assertEquals(true, d1.before(d2));
        System.out.println(DateUtil.toDate(d1, 19, 59, 59));
        System.err.println(DateUtil.getHourBetween(d1, d2));
        d1 = DateUtil.fromDateTimeString("2019-01-01 22:22:23");
        d2 = DateUtil.fromDateTimeString("2019-01-02 22:22:22");
        System.err.println(DateUtil.getHourBetween(d1, d2));
        d1 = DateUtil.fromDateTimeString("2019-06-28 21:00:00");
        d2 = DateUtil.fromDateTimeString("2019-06-30 23:59:59");
        System.err.println(DateUtil.getHourBetween(d1, d2) / 24);
        System.out.println("间隔天数：" + DateUtil.getDaysBetween(d1, d2));
        System.out.println("间隔天数：" + DateUtil.getDaysBetween(d2, d1));
        System.out.println(DateUtil.getMonthBegin(now, 0));
        System.out.println(DateUtil.getMonthEnd(now, 0));

        // Calendar calendar = Calendar.getInstance();
        // calendar.set(Calendar.HOUR_OF_DAY, 21);
        // calendar.set(Calendar.MINUTE, 0);
        // calendar.set(Calendar.SECOND, 0);
        // calendar.set(Calendar.MILLISECOND, 0);
        // calendar.add(Calendar.MONTH, -1);

        ZoneDate zoneDate = getZoneDate(1);
        System.out.println(DateUtil.toDateTimeString(zoneDate.getBeginDate()));
        System.out.println(DateUtil.toDateTimeString(zoneDate.getEndDate()));
        getAwardDateTime();
        getYesterdayAwardDateTime();
    }

    public ZoneDate getZoneDate(int seasonIndex) {
        Date tomorrow = DateUtil.addDays(DateUtil.now(), 1);
        // 赛季开始时间
        Date beginDate = DateUtil.getMonthBegin(tomorrow, seasonIndex);
        int awardHour = 21;// SxdhTool.getSxdh().getBeanAwardHour();
        beginDate = DateUtil.addHours(beginDate, -DateUtil.HOUR_ONE_DAY + awardHour);

        // 赛季结束时间
        Date endDate = DateUtil.getMonthEnd(tomorrow, seasonIndex);
        endDate = DateUtil.getDateBegin(endDate);
        endDate = DateUtil.addHours(endDate, awardHour);
        return new ZoneDate(beginDate, endDate);
    }

    /**
     * 获得当前时间对应的今日领奖时间
     */
    public static Date getAwardDateTime() {
        int awardHour = 21;
        Calendar now = Calendar.getInstance();
        Date awardDateTime = null;
        if (now.get(Calendar.HOUR_OF_DAY) >= awardHour) {
            awardDateTime = DateUtil.toDate(now.getTime(), awardHour, 0, 0);
        } else {
            Date yesterday = DateUtil.addDays(now.getTime(), -1);
            awardDateTime = DateUtil.toDate(yesterday, awardHour, 0, 0);
        }
        System.out.println("当前对应的今日领奖时间{}" + DateUtil.toDateTimeString(awardDateTime));
        return awardDateTime;
    }

    /**
     * 获得当前时间对应的昨日领奖时间
     *
     * @return
     */
    public static Date getYesterdayAwardDateTime() {
        int awardHour = 21;
        Calendar now = Calendar.getInstance();
        Date awardDateTime = null;
        if (now.get(Calendar.HOUR_OF_DAY) >= awardHour) {
            Date yesterday = DateUtil.addDays(now.getTime(), -1);
            awardDateTime = DateUtil.toDate(yesterday, awardHour, 0, 0);
        } else {
            Date yesterday = DateUtil.addDays(now.getTime(), -2);
            awardDateTime = DateUtil.toDate(yesterday, awardHour, 0, 0);
        }
        System.out.println("当前对应的昨日领奖时间{}" + DateUtil.toDateTimeString(awardDateTime));
        return awardDateTime;
    }

    @Test
    public void test2() {
        Date now = DateUtil.now();
        for (int i = -3; i < 7; i++) {
            Date end = DateUtil.addMonths(now, i);
            int days = DateUtil.getMonthsBetween(now, end);
            System.err.println("当前时间：" + DateUtil.toDateInt(now) + "---" + DateUtil.toDateInt(end) + "间隔" + days);
        }


    }
}
