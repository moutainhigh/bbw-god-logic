package com.bbw.god.activity.config;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.activity.cfg.CfgBrocadeGiftConfig;
import com.bbw.god.activity.cfg.CfgPrayerSkyLanternConfig;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 元宵活动工具类
 *
 * @author fzj
 * @date 2022/2/9 9:17
 */
public class LanternFestivalTool {

    /**
     * 获取祈福天灯配置
     *
     * @return
     */
    public static CfgPrayerSkyLanternConfig getPrayerSkyLanternConfig() {
        return Cfg.I.getUniqueConfig(CfgPrayerSkyLanternConfig.class);
    }

    /**
     * 获取祈福天灯目标及奖励
     *
     * @param target
     * @return
     */
    public static CfgPrayerSkyLanternConfig.SkyLanternTarget getTargetAndAward(Integer target) {
        return getPrayerSkyLanternConfig().getTargetWithAwards().stream().filter(t -> target >= t.getSkyLanternTimes()).findFirst().orElse(null);
    }

    /**
     * 获取锦礼配置
     *
     * @return
     */
    public static CfgBrocadeGiftConfig getBrocadeGiftConfig() {
        return Cfg.I.getUniqueConfig(CfgBrocadeGiftConfig.class);
    }

    /**
     * 获取轮次及奖励
     *
     * @param type
     * @return
     */
    public static CfgBrocadeGiftConfig.TurnAndAwards getTurnAndAwardByType(Integer type) {
        List<CfgBrocadeGiftConfig.TurnAndAwards> turnAndAwards = getBrocadeGiftConfig().getTurns()
                .stream().filter(t -> type.equals(t.getType())).collect(Collectors.toList());
        return turnAndAwards.stream().filter(t -> {
            Date beginDraw = DateUtil.fromDateTimeString(t.getBeginDraw());
            Date end = DateUtil.fromDateTimeString(t.getEnd());
            return DateUtil.isBetweenIn(DateUtil.now(), beginDraw, end);
        }).findFirst().orElse(null);
    }


    /**
     * 获取需要开奖轮次及奖励
     *
     * @param betTime
     * @return
     */
    public static CfgBrocadeGiftConfig.TurnAndAwards getNeedDrawPrizeTurnAward(Date betTime) {
        return getBrocadeGiftConfig().getTurns().stream().filter(t -> {
            Date beginDraw = DateUtil.fromDateTimeString(t.getBeginDraw());
            Date end = DateUtil.fromDateTimeString(t.getEnd());
            return DateUtil.isBetweenIn(betTime, beginDraw, end) && DateUtil.now().after(end);
        }).findFirst().orElse(null);
    }

    /**
     * 获取当前轮次及奖励
     *
     * @return
     */
    public static List<CfgBrocadeGiftConfig.TurnAndAwards> getCurrentTurnAndAwards() {
        List<CfgBrocadeGiftConfig.TurnAndAwards> turnAndAwardsList = new ArrayList<>();
        for (Map.Entry<Integer, List<CfgBrocadeGiftConfig.TurnAndAwards>> turnAndAwards : groupingByType().entrySet()) {
            //获得当前轮
            CfgBrocadeGiftConfig.TurnAndAwards currentTurn = turnAndAwards.getValue().stream().filter(t -> {
                Date begin = DateUtil.fromDateTimeString(t.getBegin());
                Date end = DateUtil.fromDateTimeString(t.getEnd());
                boolean isBegin = DateUtil.addMinutes(begin, 1).before(DateUtil.now());
                boolean isEnd = DateUtil.now().before(DateUtil.addMinutes(end, 1));
                return isBegin && isEnd;
            }).findFirst().orElse(null);
            if (null == currentTurn) {
                continue;
            }
            Date drawTime = DateUtil.fromDateTimeString(currentTurn.getBeginDraw());
            if (DateUtil.now().before(drawTime)) {
                continue;
            }
            turnAndAwardsList.add(currentTurn);
        }
        return turnAndAwardsList;
    }

    /**
     * 获取上一轮次及奖励
     *
     * @return
     */
    public static List<CfgBrocadeGiftConfig.TurnAndAwards> getLastTurnAndAwards() {
        List<CfgBrocadeGiftConfig.TurnAndAwards> turnAndAwardsList = new ArrayList<>();
        for (Map.Entry<Integer, List<CfgBrocadeGiftConfig.TurnAndAwards>> turnAndAwards : groupingByType().entrySet()) {
            List<CfgBrocadeGiftConfig.TurnAndAwards> turnList = turnAndAwards.getValue();
            //获得当前轮
            CfgBrocadeGiftConfig.TurnAndAwards currentTurn = turnList.stream().filter(t -> {
                Date begin = DateUtil.fromDateTimeString(t.getBegin());
                return DateUtil.addMinutes(begin, 1).before(DateUtil.now());
            }).findFirst().orElse(null);
            if (null == currentTurn) {
                continue;
            }
            //判断是否有下一轮
            Integer turn = currentTurn.getTurn();
            CfgBrocadeGiftConfig.TurnAndAwards nextTurnAndAward = getTurnAndAwardByTurn(turn + 1, currentTurn.getType());
            Date end = DateUtil.fromDateTimeString(currentTurn.getEnd());
            boolean currentTurnEnd = DateUtil.now().after(DateUtil.addMinutes(end, 1));
            if (null == nextTurnAndAward && currentTurnEnd) {
                turnAndAwardsList.add(currentTurn);
                continue;
            }
            CfgBrocadeGiftConfig.TurnAndAwards lastTurn = getTurnAndAwardByTurn(turn - 1, currentTurn.getType());
            if (null == lastTurn || currentTurnEnd) {
                continue;
            }
            turnAndAwardsList.add(lastTurn);
        }
        return turnAndAwardsList;
    }

    /**
     * 获取某个类型的某轮数据
     *
     * @param turn
     * @param type
     * @return
     */
    public static CfgBrocadeGiftConfig.TurnAndAwards getTurnAndAwardByTurn(int turn, int type) {
        List<CfgBrocadeGiftConfig.TurnAndAwards> turnAndAwardsList = getBrocadeGiftConfig().getTurns()
                .stream().filter(t -> t.getType() == type).collect(Collectors.toList());
        return turnAndAwardsList.stream().filter(t -> t.getTurn() == turn).findFirst().orElse(null);
    }

    /**
     * 获得本轮参与奖奖励
     *
     * @return
     */
    public static List<Award> getRandomParticipateAwards() {
        //获得参与奖所有奖励集合
        List<CfgBrocadeGiftConfig.ParticipateAwards> participateAwards = getBrocadeGiftConfig().getParticipateAwards();
        //奖励概率集合
        List<Integer> awardprobs = participateAwards.stream().map(CfgBrocadeGiftConfig.ParticipateAwards::getProb).collect(Collectors.toList());
        //根据概率随机获得奖励下标
        int awardIndex = PowerRandom.hitProbabilityIndex(awardprobs);
        //返回本轮参与奖奖励
        return participateAwards.get(awardIndex).getAwards();
    }

    /**
     * 根据类型分组
     *
     * @return
     */
    public static Map<Integer, List<CfgBrocadeGiftConfig.TurnAndAwards>> groupingByType() {
        return getBrocadeGiftConfig().getTurns().stream().collect(Collectors.groupingBy(CfgBrocadeGiftConfig.TurnAndAwards::getType));
    }
}
