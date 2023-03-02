package com.bbw.god.game.zxz.cfg;

import com.bbw.common.CloneUtil;
import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.Cfg;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 诛仙阵工具类
 * @author: hzf
 * @create: 2022-09-14 16:00
 **/
public class ZxzTool {

    /**
     * 获取配置类
     * @return
     */
    public static CfgZxzEntity getCfg() {
        return CloneUtil.clone(Cfg.I.getUniqueConfig(CfgZxzEntity.class));
    }

    /**
     * 获取手动刷新配置
     * @return
     */
    public static List<CfgManualRefreshLevel> getManualRefreshLevels(){
        return getCfg().getManualRefreshLevels();
    }

    /**
     * 根据难度获取复阵图的配置
     * @param difficulty
     * @return
     */
    public static CfgManualRefreshLevel getManualRefreshLevel(Integer difficulty, Integer needTreasure){
       return getManualRefreshLevels().stream()
               .filter(rule -> rule.getNeedTreasure().equals(needTreasure) && rule.getDifficulty().equals(difficulty))
               .findFirst().orElse(null);
    }

    /**
     * 获取难度数据
     * @return
     */
    public static List<CfgZxzLevel> getZxzLevels() {
        return getCfg().getLevels();
    }

    /**
     * 根据难度类型获取对应的配置
     *
     * @param difficulty 难度类型
     * @return
     */
    public static CfgZxzLevel getZxzLevel(Integer difficulty) {
        return  getZxzLevels().stream().
                filter(zxzLevel -> zxzLevel.getDifficulty().equals(difficulty))
                .findFirst().orElse(null);
    }
    /**
     * 获取全部关卡配置数据
     * @return
     */
    public static List<CfgZxzDefenderCardRule> getZxzDefenderCardRules() {
        return getCfg().getDefenderCardRules();
    }

    /**
     * 根据难度类型获取关卡配置
     *
     * @param difficulty 难度类型
     * @return
     */
    public static List<CfgZxzDefenderCardRule> getZxzDefenderCards(Integer difficulty) {
        return getZxzDefenderCardRules().stream()
                .filter(zxz -> zxz.getDifficulty().equals(difficulty))
                .collect(Collectors.toList());
    }

    /**
     * 根据关卡ID获取对应的配置信息
     * @param defenderId
     * @return
     */
    public static  CfgZxzDefenderCardRule getZxzDefenderCard(Integer defenderId) {
        Integer difficulty =  Integer.parseInt(String.valueOf(defenderId).substring(0, 2));
        Integer defender =  Integer.parseInt(String.valueOf(defenderId).substring(3, 4));
        return getZxzDefenderCardRules()
                .stream().filter(tmp -> tmp.getDifficulty().equals(difficulty) && tmp.getDefender().equals(defender))
                .findFirst().orElse(null);
    }

    /**
     * 获取区域配置
     * @return
     */
    public static  CfgRegionConfig getRegionConfig(){
        return getCfg().getRegionConfig().get(0);
    }

    /**
     * 获取自动刷新时间配置
     *
     * @return
     */
    public static List<CfgAutoRefreshRule> getAutoRefreshRules() {
        return getCfg().getAutoRefreshRules();
    }

    /**
     * 获取攻防比例
     * @return
     */
    public static List<CfgRegionDefenseProport> getRegionDefenseProports(){
        return getCfg().getRegionDefenseProports();
    }
    /**
     * 获取攻防比例
     * @return
     */
    public static CfgRegionDefenseProport getRegionDefenseProport(Integer difficulty){
        return getRegionDefenseProports().stream()
                .filter(rule -> rule.getDifficulty().equals(difficulty))
                .findFirst().orElse(null);
    }

    /**
     * 根据难度获取刷新时间表配置
     *
     * @param difficulty 难度
     * @return
     */
    public static List<CfgAutoRefreshRule.CfgRefreshTime> getRefreshTime(Integer difficulty) {
        List<CfgAutoRefreshRule.CfgRefreshTime> cfgRefreshTimes = new ArrayList<>();
        for (CfgAutoRefreshRule cfgAutoRefreshRule : getAutoRefreshRules()) {
            if (cfgAutoRefreshRule.getDifficulty().equals(difficulty)) {
                cfgRefreshTimes.addAll(cfgAutoRefreshRule.getRefreshTime());
            }
        }
        return cfgRefreshTimes;
    }

    /**
     * 计算难度的刷新时间
     * @param difficulty
     * @return 返回距离下一次刷新剩余的秒数
     */
    public static long getRemainRefreshSeconds(Integer difficulty) {
        List<CfgAutoRefreshRule.CfgRefreshTime> byDifficultyRefreshTime = getRefreshTime(difficulty);
        List<Long> timeLong = new ArrayList<>();
        for (CfgAutoRefreshRule.CfgRefreshTime cfgRefreshTime : byDifficultyRefreshTime) {

            List<Integer> weekDays = cfgRefreshTime.getWeekDays();
            for (Integer weekDay : weekDays) {
                //判断是不是每天刷新
                if (weekDay == 0) {
                    long time = DateUtil.getTimeToNextDay();
                    timeLong.add(time);
                } else {
                    //计算距离下个周几的时间
                    long timeToNextDayOfWeek = DateUtil.getTimeToDayWeek(weekDay, cfgRefreshTime.getHour());
                    timeLong.add(timeToNextDayOfWeek);
                }
            }
        }

      return Collections.min(timeLong);
    }

    /**
     * 区域id 换 难度
     * @param regionId
     * @return
     */
    public static Integer getDifficulty(Integer regionId){
        return Integer.parseInt(String.valueOf(regionId).substring(0, 2));
    }

    /**
     * 关卡id 换 区域id
     * @param defenderId
     * @return
     */
    public static Integer getRegionId(Integer defenderId){
        return Integer.parseInt(String.valueOf(defenderId).substring(0, 3));
    }

    /**
     * 判断难度
     * @param difficulty
     * @return
     */
    public static void ifDifficulty(Integer difficulty){
        List<Integer> difficultys = getZxzLevels().stream().map(CfgZxzLevel::getDifficulty).collect(Collectors.toList());
        if (!difficultys.contains(difficulty)){
            throw new ExceptionForClientTip("zxz.difficulty.no.exist");
        }
    }

    /**
     * 判断区域
     * @param regionId
     * @return
     */
    public static void ifRegion(Integer regionId){
        if (null == regionId) {
            throw new ExceptionForClientTip("zxz.region.no.exist");
        }

        Integer difficulty = ZxzTool.getDifficulty(regionId);
        CfgZxzLevel zxzLevel = getZxzLevel(difficulty);
        if (null == zxzLevel) {
            throw new ExceptionForClientTip("zxz.difficulty.no.exist");
        }
        List<Integer> regions = zxzLevel.getRegions();
        if (!regions.contains(regionId)) {
            throw new ExceptionForClientTip("zxz.region.no.exist");
        }
    }
    //判断当前是否还在维护中
    public static void ifMaintain(){
        //开始维护时间
        String beginMaintain = getCfg().getBeginMaintainDate();
        Date beginMaintainDate = DateUtil.fromDateTimeString(beginMaintain);

        //结束维护时间
        String endMaintain = getCfg().getEndMaintainDate();
        Date endMaintainDate = DateUtil.fromDateTimeString(endMaintain);
        //当前时间
        Date newDate = new Date();
        boolean ifMaintain = DateUtil.millisecondsInterval(newDate,beginMaintainDate) > 0 && DateUtil.millisecondsInterval(endMaintainDate,newDate) > 0 ;
        //为真：还在维护中
        if (ifMaintain) {
           throw new ExceptionForClientTip("zxz.currently.maintain");
        }
    }
}
