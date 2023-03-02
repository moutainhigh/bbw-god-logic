package com.bbw.common;

import com.bbw.exception.CoderException;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * <pre>
 * 日期处理。
 * <br/>完整日期时间字符串默认："yyyy-MM-dd HH:mm:ss"格式
 * <br/>日期字符串默认："yyyy-MM-dd"格式
 * <br/>日期int数字。默认为"yyyyMMdd"格式的字符串转换为int
 * <br/>时间字符串默认24小时制："HH:mm:ss"。
 * <br/>时间int数字。默认为"HHmmss"格式的字符串转换为int
 *
 * <br/>方法名中的 datetime 表示完整日期和时间，24小时制，如：2018-08-01 17:12:01。
 * <br/>方法名中的 date 表示日期，如：2018-08-01。
 * <br/>方法名中的 time 表示时间，24小时制，如：17:12:01，01:01:01。
 *
 * <br/>java.util.Date转换为其他类型，方法名以"to"开头
 * <br/>其他类型转换为java.util.Date,方法名以"from"开头
 * </pre>
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年8月25日 下午11:26:36
 */
public class DateUtil {
    /**
     * 日期格式(yyyyMMddHHmmss)
     */
    public final static String DATE_TIME_LONG_PATTERN = "yyyyMMddHHmmss";
    /**
     * 日期格式(yyyyMMdd)
     */
    public final static String DATE_INT_PATTERN = "yyyyMMdd";
    /**
     * 日期格式(yyyyMM)
     */
    public final static String MONTH_INT_PATTERN = "yyyyMM";
    /**
     * 日期格式(yyyyMMddHH)
     */
    public final static String HOUR_INT_PATTERN = "yyyyMMddHH";
    /**
     * 日期格式(yyyy-MM-dd)
     */
    public final static String DATE_STRING_PATTERN = "yyyy-MM-dd";
    /**
     * 时间格式(yyyy-MM-dd)
     */
    public final static String TIME_INT_PATTERN = "HHmmss";
    /**
     * 时间格式(yyyy-MM-dd)
     */
    public final static String TIME_STRING_PATTERN = "HH:mm:ss";
    /**
     * 完整时间格式(yyyy-MM-dd HH:mm:ss)
     */
    public final static String DATE_TIME_STRING_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * 时间戳格式(yyyy-MM-dd HH:mm:ss.SSS)
     */
    public final static String TIMESTAMP_STRING_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    /**
     * 一天的开始 00:00:00
     */
    private final static String DATE_BEGIN_HMS = "00:00:00";
    /**
     * 一天的结束 23:59:59
     */
    private final static String DATE_END_HMS = "23:59:59";
    /**
     * 一天的秒数
     */
    public final static Integer SECOND_ONE_DAY = 24 * 3600;
    /**
     * 一天的小时数
     */
    public final static Integer HOUR_ONE_DAY = 24;
    /**
     * 一周的秒数
     */
    public final static Integer SECOND_ONE_WEEK = 7 * 24 * 3600;

    /**
     * 日期格式化：yyyy-MM-dd
     *
     * @param date 日期
     * @return 返回yyyy-MM-dd格式日期
     */
    public static String toDateString(Date date) {
        return toString(date, DATE_STRING_PATTERN);
    }

    /**
     * 完整时间格式化：yyyy-MM-dd HH:mm:ss
     *
     * @param date
     * @return
     */
    public static String toDateTimeString(Date date) {
        return toString(date, DATE_TIME_STRING_PATTERN);
    }

    /**
     * 时间戳格式化：yyyy-MM-dd HH:mm:ss.SSS
     *
     * @param date
     * @return
     */
    public static String toTimeStampString(Date date) {
        return toString(date, TIMESTAMP_STRING_PATTERN);
    }

    /**
     * 格式化为字符串
     *
     * @param date    日期
     * @param pattern 格式，如：DateUtils.DATE_TIME_STRING_PATTERN
     * @return
     */
    public static String toString(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    /**
     * 日期格式化为long 。 yyyyMMddHHmmss
     *
     * @param date
     * @return
     */
    public static long toDateTimeLong(Date date) {
        String s = toString(date, DATE_TIME_LONG_PATTERN);
        return Long.parseLong(s);
    }

    /**
     * 当前日期格式化为long 。 yyyyMMddHHmmss
     */
    public static long toDateTimeLong() {
        String s = toString(now(), DATE_TIME_LONG_PATTERN);
        return Long.parseLong(s);
    }

    /**
     * 日期格式化为int.yyyyMM
     *
     * @param date
     * @return
     */
    public static int toMonthInt(Date date) {
        String s = toString(date, MONTH_INT_PATTERN);
        return Integer.parseInt(s);
    }

    /**
     * 日期格式化为int.yyyyMMddHH
     *
     * @param date
     * @return
     */
    public static int toHourInt(Date date) {
        String s = toString(date, HOUR_INT_PATTERN);
        return Integer.parseInt(s);
    }

    /**
     * 日期格式化为int 。 yyyyMMdd
     *
     * @param date
     * @return
     */
    public static int toDateInt(Date date) {
        String s = toString(date, DATE_INT_PATTERN);
        return Integer.parseInt(s);
    }

    /**
     * 日期格式化为int 。 HHmmss
     *
     * @param date
     * @return
     */
    public static int toHMSInt(Date date) {
        String s = toString(date, "HHmmss");
        return Integer.parseInt(s);
    }

    /**
     * 日期格式化为int 。 HH
     *
     * @param date
     * @return
     */
    public static int toHInt(Date date) {
        String s = toString(date, "HH");
        return Integer.parseInt(s);
    }

    /**
     * 当前日期格式化为double 。 0.0yyMMddHHmmss
     */
    public static double toDateTimeDouble() {
        String s = toString(now(), "yyMMddHHmmss");
        BigDecimal b = new BigDecimal("0.0" + s);
        return b.doubleValue();
    }

    /**
     * 日期格式为String， MMdd 月份和日期 如0101
     *
     * @param date
     * @return
     */
    public static String toDayString(Date date) {
        String s = toString(date, "MMdd");
        return s;
    }

    /**
     * 将 yyyyMMdd 数字转为 Date
     *
     * @param dayInt
     * @return
     */
    public static Date fromDateInt(int dateInt) {
        return fromString(String.valueOf(dateInt) + "000000", "yyyyMMddHHmmss");
    }

    /**
     * 将 yyyyMMddHHmmss 数字转为 Date
     *
     * @param dayLong
     * @return
     */
    public static Date fromDateLong(long dayLong) {
        return fromString(String.valueOf(dayLong), DATE_TIME_LONG_PATTERN);
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     *
     * @return
     */
    public static String nowToString() {
        return toString(new Date(), DATE_TIME_STRING_PATTERN);
    }

    /**
     * 现在
     *
     * @return
     */
    public static Date now() {
        return new Date();
    }

    public static Timestamp nowTimeStamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 将yyyy-MM-dd字符串转换为Date对象
     *
     * @param strDate：yyyy-MM-dd格式字符串
     * @return
     */
    public static Date fromDateString(String strDate) {
        return fromString(strDate + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss字符串转换为Date对象
     *
     * @param strDateTime：yyyyy-MM-dd HH:mm:ss格式字符串
     * @return
     */
    public static Date fromDateTimeString(String strDateTime) {
        return fromString(strDateTime, DATE_TIME_STRING_PATTERN);
    }

    /**
     * 字符串转换成java.util.Date java8
     *
     * @param strDate 字符串
     * @param pattern 格式，如：DateUtil.DATE_TIME_STRING_PATTERN
     */
    public static Date fromString(String strDate, String pattern) {
        if (StringUtils.isBlank(strDate)) {
            return null;
        }
        LocalDateTime localDateTime = LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern(pattern));
        return localDateTimeToDate(localDateTime);
    }

    /**
     * 是否有效时间格式
     *
     * @param strDate
     * @param pattern
     * @return
     */
    public static Boolean isVaildDatePattern(String strDate, String pattern) {
        if (StringUtils.isBlank(strDate)) {
            return false;
        }
        try {
            LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern(pattern));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 将时间指定为特定时间日期
     *
     * @param date
     * @param pattern
     * @return
     */
    public static Date toDate(Date date, int hour, int minute, int second) {
        if (date == null) {
            return null;
        }
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        localDateTime = localDateTime.withHour(hour).withMinute(minute).withSecond(second);
        return localDateTimeToDate(localDateTime);
    }

    /**
     * 将指定日期 与 指定时间合并
     *
     * @param date
     * @param hms  必须是HH:mm:ss
     * @return
     */
    public static Date toDate(Date date, String hms) {
        if (date == null) {
            return null;
        }
        String[] paramStrs = hms.split(":");
        if (paramStrs.length != 3) {
            return null;
        }
        int hour = Integer.parseInt(paramStrs[0]);
        int minute = Integer.parseInt(paramStrs[1]);
        int second = Integer.parseInt(paramStrs[2]);
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        localDateTime = localDateTime.withHour(hour).withMinute(minute).withSecond(second);
        return localDateTimeToDate(localDateTime);
    }

    /**
     * 将时间指定为同样的时分秒的特定日期
     *
     * @param date
     * @param days -1昨天 0今天 1明天
     * @return
     */
    public static Date toDateWithSameHMS(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    /**
     * 对日期的【秒】进行加/减
     *
     * @param date    日期
     * @param seconds 秒数，负数为减
     * @return 加/减几秒后的日期
     */
    public static Date addSeconds(Date date, int seconds) {
        LocalDateTime dateTime = dateToLocalDateTime(date);
        return localDateTimeToDate(dateTime.plusSeconds(seconds));
    }

    /**
     * 对日期的【分钟】进行加/减
     *
     * @param date    日期
     * @param minutes 分钟数，负数为减
     * @return 加/减几分钟后的日期
     */
    public static Date addMinutes(Date date, int minutes) {
        LocalDateTime dateTime = dateToLocalDateTime(date);
        return localDateTimeToDate(dateTime.plusMinutes(minutes));
    }

    /**
     * 对日期的【小时】进行加/减 java8
     *
     * @param date  日期
     * @param hours 小时数，负数为减
     * @return 加/减几小时后的日期
     */
    public static Date addHours(Date date, int hours) {
        LocalDateTime dateTime = dateToLocalDateTime(date);
        return localDateTimeToDate(dateTime.plusHours(hours));
    }

    /**
     * 对日期的【天】进行加/减 java8
     *
     * @param date 日期
     * @param days 天数，负数为减
     * @return 加/减几天后的日期
     */
    public static Date addSimpleDays(Date date, int days) {
        LocalDateTime dateTime = dateToLocalDateTime(date);
        return localDateTimeToDate(dateTime.plusDays(days));
    }

    /**
     * 对日期的【天】进行加/减,并将目标时间设为23:59:59 java8
     *
     * @param date 日期
     * @param days 天数，负数为减
     * @return 加/减几天后的日期
     */
    public static Date addDays(Date date, int days) {
        LocalDateTime dateTime = dateToLocalDateTime(date);
        return localDateTimeToDate(dateTime.plusDays(days).withHour(23).withMinute(59).withSecond(59));
    }

    /**
     * 对日期的【天】进行加/减,时间分钟不变 java8
     *
     * @param date 日期
     * @param days 天数，负数为减
     * @return 加/减几天后的日期
     */
    public static Date addDaysWithoutHHmmss(Date date, int days) {
        LocalDateTime dateTime = dateToLocalDateTime(date);
        return localDateTimeToDate(dateTime.plusDays(days));
    }

    /**
     * 对日期的【周】进行加/减 java8
     *
     * @param date  日期
     * @param weeks 周数，负数为减
     * @return 加/减几周后的日期
     */
    public static Date addWeeks(Date date, int weeks) {
        LocalDateTime dateTime = dateToLocalDateTime(date);
        return localDateTimeToDate(dateTime.plusWeeks(weeks));
    }

    /**
     * 对日期的【月】进行加/减 java8
     *
     * @param date   日期
     * @param months 月数，负数为减
     * @return 加/减几月后的日期
     */
    public static Date addMonths(Date date, int months) {
        LocalDateTime dateTime = dateToLocalDateTime(date);
        return localDateTimeToDate(dateTime.plusMonths(months));
    }

    /**
     * 对日期的【年】进行加/减 java8
     *
     * @param date  日期
     * @param years 年数，负数为减
     * @return 加/减几年后的日期
     */
    public static Date addYears(Date date, int years) {
        LocalDateTime dateTime = dateToLocalDateTime(date);
        return localDateTimeToDate(dateTime.plusYears(years));
    }

    /**
     * 根据周数，获取开始日期、结束日期 java8
     *
     * @param week 周期 0本周，-1上周，-2上上周，1下周，2下下周
     * @return 返回date[0]开始日期、date[1]结束日期
     */
    public static Date[] getWeekStartAndEnd(int week) {
        LocalDateTime dateTime = LocalDateTime.now().plusWeeks(week);
        WeekFields weekFields = getWeekFields();
        Date beginDate = localDateTimeToDate(
                dateTime.with(weekFields.dayOfWeek(), 1).withHour(0).withMinute(0).withSecond(0));
        Date endDate = localDateTimeToDate(
                dateTime.with(weekFields.dayOfWeek(), 7).withHour(23).withMinute(59).withSecond(59));
        return new Date[]{beginDate, endDate};
    }

    /**
     * 得到本周的截止时间，即：星期日 晚上 23:59:59 java8
     *
     * @param date
     * @return
     */
    public static Date getThisWeekEndDateTime() {
        return getWeekEndDateTime(now());
    }

    /**
     * 得到某个日期的周截止时间。即：星期日 晚上 23:59:59
     *
     * @param date
     * @return
     */
    public static Date getWeekEndDateTime(Date date) {
        WeekFields weekFields = getWeekFields();
        LocalDateTime sundayEnd = dateToLocalDateTime(date).with(weekFields.dayOfWeek(), 7).withHour(23).withMinute(59)
                .withSecond(59);
        return localDateTimeToDate(sundayEnd);
    }

    /**
     * 得到本周的周起始时间。即：周一 凌晨 00:00:00 java8
     *
     * @param date
     * @return
     */
    public static Date getThisWeekBeginDateTime() {
        return getWeekBeginDateTime(now());
    }

    /**
     * 得到某个日期的周起始时间。即：周一 凌晨 00:00:00 java8
     *
     * @param date
     * @return
     */
    public static Date getWeekBeginDateTime(Date date) {
        WeekFields weekFields = getWeekFields();
        LocalDateTime mondayBegin = dateToLocalDateTime(date).with(weekFields.dayOfWeek(), 1).withHour(0).withMinute(0)
                .withSecond(0);
        return localDateTimeToDate(mondayBegin);
    }

    /**
     * 获得月的开始时间 **:**:** 00:00:00
     *
     * @param date
     * @param offset 0本月，1下个月，-1上个月，依次类推
     * @return
     */
    public static Date getMonthBegin(Date date, int offset) {
        LocalDate monthBeginDate = dateToLocalDate(date).plusMonths(offset).with(TemporalAdjusters.firstDayOfMonth());
        return localDateToDate(monthBeginDate);
    }

    /**
     * 获得月的结束时间 **:**:** 23:59:59
     *
     * @param date
     * @param offset 0本月，1下个月，-1上个月，依次类推
     * @return
     */
    public static Date getMonthEnd(Date date, int offset) {
        LocalDate monthEndDate = dateToLocalDate(date).plusMonths(offset).with(TemporalAdjusters.lastDayOfMonth());
        Date monthEnd = localDateToDate(monthEndDate);
        return getDateEnd(monthEnd);
    }

    /**
     * 获得从某一天起，过了多少个月
     *
     * @param date
     * @return
     */
    public static int getMonthSince(Date date) {
        LocalDate nowLocalDate = LocalDate.now();
        LocalDate localDate = dateToLocalDate(date);
        int yearSince = nowLocalDate.getYear() - localDate.getYear();
        int monthPart = nowLocalDate.getMonthValue() - localDate.getMonthValue() + 1;
        int monthSince = yearSince * 12 + monthPart;
        return monthSince;
    }

    /**
     * 一天的起始 yyyy-MM-dd 00:00:00
     *
     * @param date
     * @return
     */
    public static Date getDateBegin(Date date) {
        String dateStr = DateUtil.toDateString(date);
        String datePattern = String.format("%s %s", dateStr, DATE_BEGIN_HMS);
        Date beginDate = DateUtil.fromDateTimeString(datePattern);
        return beginDate;
    }

    /**
     * 一天的结束 yyyy-MM-dd 23:59:59
     *
     * @param date
     * @return
     */
    public static Date getDateEnd(Date date) {
        String dateStr = DateUtil.toDateString(date);
        String datePattern = String.format("%s %s", dateStr, DATE_END_HMS);
        Date beginDate = DateUtil.fromDateTimeString(datePattern);
        return beginDate;
    }

    /**
     * 两个java.util.Date对象的 毫秒间隔数。 <br/>
     * 可以用来做 时间比较。 <br/>
     * date1时间比date2 早，返回负数。 <br/>
     * date1时间比date2 晚，返回正数。
     *
     * @param date1
     * @param date2
     * @return long。 date1时间比date2早，返回负数。 date1比date2晚，返回整数
     */
    public static long millisecondsInterval(Date date1, Date date2) {
        return date1.getTime() - date2.getTime();
    }

    /**
     * 当前距离第二天0点的毫秒数 java8
     *
     * @return
     */
    public static int getTimeToNextDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long millis = ChronoUnit.MILLIS.between(now, midnight);
        return (int) millis;
    }

    /**
     * 当前距离下个小时零分零秒的毫秒数 java8
     *
     * @return
     */
    public static long getTimeToNextHour() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        return ChronoUnit.MILLIS.between(now, midnight);
    }

    /**
     * 当前距离本周星期几什么时间点的毫秒数 java8
     *
     * @param dayOfWeek 周一为1 以此类推
     * @param hour
     * @return
     */
    public static long getTimeToDayWeek(Integer dayOfWeek, Integer hour) {
        return getTimeToDayWeek(DateUtil.now(), dayOfWeek, hour);
    }

    /**
     * 某个日期距离对应周星期几什么时间点的毫秒数 java8
     *
     * @param sinceDate
     * @param dayOfWeek 周一为1 以此类推
     * @param hour
     * @return
     */
    public static long getTimeToDayWeek(Date sinceDate, Integer dayOfWeek, Integer hour) {
        LocalDateTime localDateTime = dateToLocalDateTime(sinceDate);
        LocalDateTime midnight;
        if (DateUtil.getWeekDay(sinceDate) == dayOfWeek && DateUtil.getHourOfDay(sinceDate) < hour) {
            midnight = localDateTime.plusWeeks(-1).with(TemporalAdjusters.next(DayOfWeek.of(dayOfWeek))).withHour(hour).withMinute(0).withSecond(0).withNano(0);
        } else {
            midnight = localDateTime.plusWeeks(0).with(TemporalAdjusters.next(DayOfWeek.of(dayOfWeek))).withHour(hour).withMinute(0).withSecond(0).withNano(0);
        }
        return ChronoUnit.MILLIS.between(localDateTime, midnight);
    }

    /**
     * 是否是今天
     *
     * @param date
     * @return
     */
    public static boolean isToday(Date date) {
        return toDateInt(date) == toDateInt(now());
    }

    /**
     * 是否为当周（周一为第一天）java8
     *
     * @param date
     * @return
     */
    public static boolean isThisWeek(Date date) {
        int year = DateUtil.getYear(date);
        int newYear = DateUtil.getYear(new Date());
        LocalDateTime localDate = dateToLocalDateTime(date);
        WeekFields weekFields = getWeekFields();
        // System.out.println(localDate.get(getWeekFields().weekOfYear()));
        return localDate.get(weekFields.weekOfYear()) == LocalDateTime.now().get(weekFields.weekOfYear()) && year == newYear;
    }

    /**
     * 是否为周末（周六、周日）
     *
     * @return
     */
    public static boolean isWeekend() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.get(getWeekFields().dayOfWeek()) >= 6;
    }

    /**
     * 是否为指定 周几
     *
     * @param weekDay 周一为1 以此类推
     * @return
     */
    public static boolean isWeekDay(int weekDay) {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.get(getWeekFields().dayOfWeek()) == weekDay;
    }

    /**
     * 获取当前是周几
     *
     * @return
     */
    public static int getToDayWeekDay() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return localDateTime.get(getWeekFields().dayOfWeek());
    }

    /**
     * 获取周几
     *
     * @param date
     * @return
     */
    public static int getWeekDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        return localDateTime.get(getWeekFields().dayOfWeek());
    }

    /**
     * 是否为一周的结束
     *
     * @param date
     * @return
     */
    public static boolean isWeekEndDate(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        return localDateTime.get(getWeekFields().dayOfWeek()) == 7;
    }

    /**
     * 是否一周的开始
     *
     * @param date
     * @return
     */
    public static boolean isWeekBeginDate(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        return localDateTime.get(getWeekFields().dayOfWeek()) == 1;
    }

    /**
     * 是否为当周（周一为第一天）java8
     *
     * @param date
     * @return
     */
    public static boolean isThisMonth(Date date) {
        LocalDateTime localDate = dateToLocalDateTime(date);
        // System.out.println(localDate.getMonthValue());
        return localDate.getMonthValue() == LocalDateTime.now().getMonthValue();
    }

    /**
     * 某个时间是否在某个时间范围
     *
     * @param date
     * @param begin
     * @param end
     * @return
     */
    public static boolean isBetweenIn(Date date, Date begin, Date end) {
        if (null == date || null == begin || null == end) {
            return false;
        }

        return date.after(begin) && date.before(end);
    }

    /**
     * 得到月份差,同一天返回0 java8
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getMonthsBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw CoderException.normal("getDaysBetween(startDate,endDate)传入的时间参数有空值");
        }
        Date dateBegin = getDateBegin(startDate);
        Date dateEnd = getDateBegin(endDate);
        LocalDateTime startLocalDate = dateToLocalDateTime(dateBegin);
        LocalDateTime endLocalDate = dateToLocalDateTime(dateEnd);
        Long days = ChronoUnit.MONTHS.between(startLocalDate, endLocalDate);
        return days.intValue();
    }

    /**
     * 得到天数差,同一天返回0 java8
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getDaysBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw CoderException.normal("getDaysBetween(startDate,endDate)传入的时间参数有空值");
        }
        Date dateBegin = getDateBegin(startDate);
        Date dateEnd = getDateBegin(endDate);
        LocalDateTime startLocalDate = dateToLocalDateTime(dateBegin);
        LocalDateTime endLocalDate = dateToLocalDateTime(dateEnd);
        Long days = ChronoUnit.DAYS.between(startLocalDate, endLocalDate);
        return days.intValue();
    }

    /**
     * 得到秒数差 java8
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static long getSecondsBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw CoderException.normal("时间参数有空值");
        }
        LocalDateTime startLocalDate = dateToLocalDateTime(startDate);
        LocalDateTime endLocalDate = dateToLocalDateTime(endDate);
        return ChronoUnit.SECONDS.between(startLocalDate, endLocalDate);
        // return (int) startLocalDate.until(endLocalDate, ChronoUnit.DAYS);
    }

    /**
     * 得到分钟差 java8
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static long getMinutesBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw CoderException.normal("时间参数有空值");
        }
        LocalDateTime startLocalDate = dateToLocalDateTime(startDate);
        LocalDateTime endLocalDate = dateToLocalDateTime(endDate);
        return ChronoUnit.MINUTES.between(startLocalDate, endLocalDate);
        // return (int) startLocalDate.until(endLocalDate, ChronoUnit.DAYS);
    }

    /**
     * 得到时差 java8
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static long getHourBetween(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw CoderException.normal("getSecondsBetween(startDate,endDate)传入的时间参数有空值");
        }
        LocalDateTime startLocalDate = dateToLocalDateTime(startDate);
        LocalDateTime endLocalDate = dateToLocalDateTime(endDate);
        return ChronoUnit.HOURS.between(startLocalDate, endLocalDate);
    }

    /**
     * 获取今天的数字型 日期格式化为int 。 yyyyMMdd
     *
     * @return
     */
    public static int getTodayInt() {
        return toDateInt(new Date());
    }

    /**
     * 获得时间对应的小时
     *
     * @param date
     * @return
     */
    public static int getHourOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获得时间对应的小时
     *
     * @param date
     * @return
     */
    public static int getMinute(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

    /**
     * 获得时间对应的日期
     *
     * @param date
     * @return
     */
    public static int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取年
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 根据描述获得时间字符串，形如6天12时；12时6分；6分8秒
     *
     * @param seconds
     * @return
     */
    public static String getDateTimeStr(int seconds) {
        String timeStr = "";
        int date = seconds / (24 * 3600);
        if (date > 0) {
            timeStr = date + "天" + (seconds % (24 * 3600)) / 3600 + "时";
        } else {
            int hour = seconds / 3600;
            if (hour > 0) {
                timeStr = hour + "时" + (seconds % 3600) / 60 + "分";
            } else {
                int minute = seconds / 60;
                timeStr = minute + "分" + seconds % 60 + "秒";
            }
        }
        return timeStr;
    }

    /**
     * 返回两个日期相差的天数
     *
     * @param begin
     * @param end
     * @return
     */
    public static long until(Date begin, Date end) {
        LocalDate lb = dateToLocalDate(begin);
        LocalDate le = dateToLocalDate(end);
        return lb.until(le, ChronoUnit.DAYS);
    }

    /**
     * 返回指定时间所在月份 有多少天
     *
     * @param date
     * @return
     */
    public static int getMonthDays(Date date) {
        Date begin = getMonthBegin(date, 0);
        Date end = getMonthEnd(date, 0);
        int days = getDaysBetween(begin, end) + 1;
        return days > 31 ? 31 : days;
    }

    /**
     * java.util.Date --> java.time.LocalDateTime
     *
     * @param date
     * @return
     */
    private static LocalDateTime dateToLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * java.util.Date --> java.time.LocalDate
     *
     * @param date
     * @return
     */
    private static LocalDate dateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone).toLocalDate();
    }

    /**
     * java.time.LocalDateTime -> java.util.Date
     *
     * @param localDateTime
     * @return
     */
    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * java.time.LocalDate --> java.util.Date
     *
     * @param localDate
     */
    private static Date localDateToDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    private static WeekFields getWeekFields() {
        return WeekFields.of(Locale.FRANCE);// 使用Locale.FRANCE，一周的第一天为星期一
    }

    /**
     * date1 和date2是否是在同一个星期
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isEqualWeek(Date date1, Date date2) {
        Date start1 = getWeekBeginDateTime(date1);
        Date start2 = getWeekBeginDateTime(date2);
        return getDaysBetween(start1, start2) == 0;
    }
}
