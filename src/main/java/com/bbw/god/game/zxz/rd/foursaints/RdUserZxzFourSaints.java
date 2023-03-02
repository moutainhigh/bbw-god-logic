package com.bbw.god.game.zxz.rd.foursaints;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsInfo;
import com.bbw.god.game.zxz.enums.ZxzStatusEnum;
import com.bbw.god.rd.RDSuccess;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 进去四圣挑战
 * @author: hzf
 * @create: 2022-12-27 17:36
 **/
@Data
public class RdUserZxzFourSaints extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1366511178569740530L;
    /** 四圣挑战数据 */
    List<RdUserZxzFourSaint> zxzFourSaints;

    @Data
    public static class RdUserZxzFourSaint{
        /** 四圣挑战类型 */
        private Integer challengeType;
        /** 探索点 */
        private Integer exploratoryPoint;
        /** 诛仙阵：状态信息：参考 ZxzStatusEnum 枚举类 */
        private Integer status;
        /** 词条等级 */
        private Integer entryLv;
        /** 是否领取宝箱 */
        private Integer awarded;
        /** 免费刷新次数 */
        private Integer freeRefreshFrequency;
        /** 刷新时间 */
        private long refreshTime;




        public static RdUserZxzFourSaint getInstance(Integer challengeType,Integer exploratoryPoint,Integer status,Integer entryLv,Integer awarded,Integer freeRefreshFrequency){
            RdUserZxzFourSaint rd = new RdUserZxzFourSaint();
            rd.setChallengeType(challengeType);
            rd.setExploratoryPoint(exploratoryPoint);
            rd.setStatus(status);
            rd.setEntryLv(entryLv);
            rd.setAwarded(awarded);
            long timeToNextDayOfWeek = DateUtil.getTimeToDayWeek(1, 0);
            rd.setRefreshTime(timeToNextDayOfWeek);
            rd.setFreeRefreshFrequency(freeRefreshFrequency);
            return rd;
        }


        public static RdUserZxzFourSaint getInstance(Integer challengeType){
            RdUserZxzFourSaint rd = new RdUserZxzFourSaint();
            rd.setChallengeType(challengeType);
            rd.setExploratoryPoint(0);
            rd.setStatus(ZxzStatusEnum.NOT_OPEN.getStatus());
            rd.setEntryLv(0);
            rd.setAwarded(0);
            rd.setFreeRefreshFrequency(CfgFourSaintsTool.getCfg().getFreeRefreshFrequency());
            long timeToNextDayOfWeek = DateUtil.getTimeToDayWeek(1, 0);
            rd.setRefreshTime(timeToNextDayOfWeek);
            return rd;
        }
    }
    /**
     *
     * @param obtainTime 得到时间 如果是本周获取下周日23:59:59 剩余，不是本周 获取距离本周日23:59:59剩余的时间
     * @return
     */
    private static long getTimeToDayWeek(long obtainTime) {
        Date obtainDate = new Date(obtainTime);
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime midnight;
        //是否是本周
        if (DateUtil.isThisWeek(obtainDate)) {
            midnight = localDateTime.plusWeeks(1).with(TemporalAdjusters.next(DayOfWeek.of(DayOfWeek.SUNDAY.getValue()))).withHour(23).withMinute(59).withSecond(59).withNano(0);
        } else {
            midnight = localDateTime.plusWeeks(0).with(TemporalAdjusters.next(DayOfWeek.of(DayOfWeek.SUNDAY.getValue()))).withHour(23).withMinute(59).withSecond(59).withNano(0);
        }
        return ChronoUnit.MILLIS.between(localDateTime, midnight);
    }
}
