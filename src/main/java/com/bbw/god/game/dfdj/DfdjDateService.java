package com.bbw.god.game.dfdj;

import com.bbw.common.DateUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.dfdj.config.CfgDfdj;
import com.bbw.god.game.dfdj.config.DfdjTool;
import com.bbw.god.game.dfdj.zone.DfdjZone;
import com.bbw.god.game.dfdj.zone.DfdjZoneService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author suchaobin
 * @description 巅峰对决时间服务
 * @date 2021/1/5 14:35
 **/
@Service
@Slf4j
public class DfdjDateService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private DfdjZoneService dfdjZoneService;
    @Value("${bbw-god.chanjie.beginTime:20:45:00}")//阐截斗法 战斗开启时间
    private String cjdfBeginTime;
    @Value("${bbw-god.chanjie.endTime:22:00:00}") //阐截斗法 战斗结束时间
    private String cjdfEndTime;

    /**
     * 获得巅峰对决每日结束时间
     *
     * @param dayIndex -1 昨日，0 今日
     * @return
     */
    public Date getDfdjDateEnd(int dayIndex) {
        int endHour = DfdjTool.getDfdj().getSeasonEndHour();
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
    public CfgDfdj.SeasonPhase getCurSeasonPhase() {
        return getSeasonPhase(DateUtil.now());
    }

    /**
     * 获得指定时间对应的赛季阶段
     *
     * @param date
     * @return
     */
    public CfgDfdj.SeasonPhase getSeasonPhase(Date date) {
        List<CfgDfdj.SeasonPhase> seasonPhases = DfdjTool.getDfdj().getSeasonPhases();
        //赛季结束阶段返回最后一个阶段
        if (dfdjZoneService.getZones(date).size() == 0) {
            return seasonPhases.get(seasonPhases.size() - 1);
        }
        int endHour = DfdjTool.getDfdj().getSeasonEndHour();
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        int dateHour = dateCalendar.get(Calendar.HOUR_OF_DAY);
        int day = dateCalendar.get(Calendar.DAY_OF_MONTH);
        if (dateHour >= endHour) {
            day += 1;
        }
        final int dayFinal = day;
        CfgDfdj.SeasonPhase seasonPhase = seasonPhases.stream().filter(tmp -> tmp.getBegin() <= dayFinal && tmp.getEnd() >= dayFinal).findFirst().orElse(null);
        return seasonPhase;
    }

    /**
     * 巅峰对决商店购买次数重置时间
     *
     * @return
     */
    public Date getDfdjBuyResetDate() {
        CfgDfdj CfgDfdj = DfdjTool.getDfdj();
        Date now = DateUtil.now();
        int monthInt = DateUtil.toMonthInt(now);
        String dateStr = monthInt + CfgDfdj.getResetMallRecordDate();
        Date thisMonthResetDate = DateUtil.fromDateLong(Long.valueOf(dateStr));
        if (now.before(thisMonthResetDate)) {
            return thisMonthResetDate;
        } else {
            Date nextMonthDate = DateUtil.addMonths(now, 1);
            int nextMonthInt = DateUtil.toMonthInt(nextMonthDate);
            return DateUtil.fromDateLong(Long.valueOf(nextMonthInt + CfgDfdj.getResetMallRecordDate()));
        }
    }

    /**
     * 仙豆过期时间
     *
     * @return
     */
    public Date getBeanExpireDate() {
        CfgDfdj cfgDfdj = DfdjTool.getDfdj();
        Date now = DateUtil.now();
        Date nextMonthDate = DateUtil.addMonths(now, 1);
        int nextMonthInt = DateUtil.toMonthInt(nextMonthDate);
        return DateUtil.fromDateLong(Long.parseLong(nextMonthInt + cfgDfdj.getBeanExpireDate()));
    }

    /**
     * 获得当月对应的仙豆过期时间
     *
     * @return
     */
    public Date getBeanExpireDateAsThisMonth() {
        CfgDfdj cfgDfdj = DfdjTool.getDfdj();
        Date now = DateUtil.now();
        int nextMonthInt = DateUtil.toMonthInt(now);
        return DateUtil.fromDateLong(Long.valueOf(nextMonthInt + cfgDfdj.getBeanExpireDate()));
    }

    /**
     * 头像过期时间
     *
     * @param uid
     * @return
     */
    public Date getHeadIconExpireDate(long uid) {
        CfgServerEntity serverEntity = gameUserService.getOriServer(uid);
        DfdjZone zone = dfdjZoneService.getZoneByServer(serverEntity);
        if (zone != null) {
            return zone.getEndDate();
        }
        Date now = DateUtil.now();
        Date monthEnd = DateUtil.getMonthEnd(now, 0);
        int monthIndex = 0;
        if (DateUtil.getDaysBetween(now, monthEnd) == 0) {
            monthIndex = 1;
        }
        Date endDate = DateUtil.getMonthEnd(now, monthIndex);
        endDate = DateUtil.getDateBegin(endDate);
        endDate = DateUtil.addHours(endDate, DfdjTool.getDfdj().getSeasonEndHour());
        return endDate;
    }

    public boolean isOpenDfdj(long uid) {
        CfgDfdj dfdj = DfdjTool.getDfdj();
        // 判断等级
        GameUser gu = gameUserService.getGameUser(uid);
        if (gu.getLevel() < dfdj.getPvpUnlockLevel()) {
            return false;
        }
        // 未开启
        if (!dfdj.getIfOpen()) {
            return false;
        }
        //超过开放时间，不可进行匹配
        if (DateUtil.fromDateTimeString(dfdj.getOpenEnd()).getTime() < System.currentTimeMillis()) {
            return false;
        }
        // 阐截斗法期间
        Date cjdfBegin = DateUtil.toDate(new Date(), cjdfBeginTime);
        Date cjdfEnd = DateUtil.toDate(new Date(), cjdfEndTime);
        Date now = DateUtil.now();
        if (now.after(cjdfBegin) && now.before(cjdfEnd)) {
            return false;
        }
        Integer openBeginHour = dfdj.getOpenBeginHour();
        Integer openEndHour = dfdj.getOpenEndHour();
        int hourOfDay = DateUtil.getHourOfDay(DateUtil.now());
        return hourOfDay <= openBeginHour || hourOfDay >= openEndHour;
    }

    public boolean ifOpenDfdj(long uid) {
        try {
            return isOpenDfdj(uid);
        } catch (Exception e) {
            return false;
        }
    }
}
