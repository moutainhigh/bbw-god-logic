package com.bbw.god.game.sxdh;

import com.bbw.common.DateUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.sxdh.config.CfgSxdh;
import com.bbw.god.game.sxdh.config.SxdhTool;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 神仙大会时间服务
 *
 * @author suhq
 * @date 2020-04-24 17:46
 **/
@Service
public class SxdhDateService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private SxdhZoneService sxdhZoneService;

    /**
     * 获得神仙大会每日结束时间
     *
     * @param dayIndex -1 昨日，0 今日
     * @return
     */
    public Date getSxdhDateEnd(int dayIndex) {
        int endHour = SxdhTool.getSxdh().getSeasonEndHour();
        Calendar now = Calendar.getInstance();
        Date date = null;
        if (now.get(Calendar.HOUR_OF_DAY) >= endHour) {
            date = DateUtil.addDays(now.getTime(), dayIndex);
        } else {
            date = DateUtil.addDays(now.getTime(), dayIndex - 1);

        }
        Date awardDateTime = DateUtil.toDate(date, endHour, 0, 0);
        return awardDateTime;
    }

    /**
     * 获得当前赛季阶段
     *
     * @return
     */
    public CfgSxdh.SeasonPhase getCurSeasonPhase() {
        return getSeasonPhase(DateUtil.now());
    }

    /**
     * 获得指定时间对应的赛季阶段
     *
     * @param date
     * @return
     */
    public CfgSxdh.SeasonPhase getSeasonPhase(Date date) {
        List<CfgSxdh.SeasonPhase> seasonPhases = SxdhTool.getSxdh().getSeasonPhases();
        //赛季结束阶段返回最后一个阶段
        if (sxdhZoneService.getZones(date).size() == 0) {
            return seasonPhases.get(seasonPhases.size() - 1);
        }
        int endHour = SxdhTool.getSxdh().getSeasonEndHour();
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        int dateHour = dateCalendar.get(Calendar.HOUR_OF_DAY);
        int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
        if (dateHour >= endHour) {
            day += 1;
        }
        final int dayFinal = day;
        CfgSxdh.SeasonPhase seasonPhase = seasonPhases.stream().filter(tmp -> tmp.getBegin() <= dayFinal && tmp.getEnd() >= dayFinal).findFirst().orElse(null);
        return seasonPhase;
    }

    /**
     * 神仙大会商店购买次数重置时间
     *
     * @return
     */
    public Date getSxdhBuyResetDate() {
        CfgSxdh cfgSxdh = SxdhTool.getSxdh();
        Date now = DateUtil.now();
        int monthInt = DateUtil.toMonthInt(now);
        String dateStr = monthInt + cfgSxdh.getResetMallRecordDate();
        Date thisMonthResetDate = DateUtil.fromDateLong(Long.valueOf(dateStr));
        if (now.before(thisMonthResetDate)) {
            return thisMonthResetDate;
        } else {
            Date nextMonthDate = DateUtil.addMonths(now, 1);
            int nextMonthInt = DateUtil.toMonthInt(nextMonthDate);
            return DateUtil.fromDateLong(Long.valueOf(nextMonthInt + cfgSxdh.getResetMallRecordDate()));
        }
    }

    /**
     * 仙豆过期时间
     *
     * @return
     */
    public Date getBeanExpireDate() {
        CfgSxdh cfgSxdh = SxdhTool.getSxdh();
        Date now = DateUtil.now();
        Date nextMonthDate = DateUtil.addMonths(now, 1);
        int nextMonthInt = DateUtil.toMonthInt(nextMonthDate);
        return DateUtil.fromDateLong(Long.valueOf(nextMonthInt + cfgSxdh.getBeanExpireDate()));
//        int monthInt = DateUtil.toMonthInt(now);
//        String dateStr = monthInt + cfgSxdh.getBeanExpireDate();
//        Date thisMonthResetDate = DateUtil.fromDateLong(Long.valueOf(dateStr));
//        if (now.before(thisMonthResetDate)) {
//            return thisMonthResetDate;
//        } else {
//
//        }
    }

    /**
     * 获得当月对应的仙豆过期时间
     *
     * @return
     */
    public Date getBeanExpireDateAsThisMonth() {
        CfgSxdh cfgSxdh = SxdhTool.getSxdh();
        Date now = DateUtil.now();
        int nextMonthInt = DateUtil.toMonthInt(now);
        return DateUtil.fromDateLong(Long.valueOf(nextMonthInt + cfgSxdh.getBeanExpireDate()));
    }

    /**
     * 头像过期时间
     *
     * @param uid
     * @return
     */
    public Date getHeadIconExpireDate(long uid) {
        CfgServerEntity serverEntity = gameUserService.getOriServer(uid);
        SxdhZone sxdhZone = sxdhZoneService.getZoneByServer(serverEntity);
        if (sxdhZone != null) {
            return sxdhZone.getEndDate();
        }
        Date now = DateUtil.now();
        Date monthEnd = DateUtil.getMonthEnd(now, 0);
        int monthIndex = 0;
        if (DateUtil.getDaysBetween(now, monthEnd) == 0) {
            monthIndex = 1;
        }
        Date endDate = DateUtil.getMonthEnd(now, monthIndex);
        endDate = DateUtil.getDateBegin(endDate);
        endDate = DateUtil.addHours(endDate, SxdhTool.getSxdh().getSeasonEndHour());
        return endDate;
    }

}
