package com.bbw.god.activity.holiday.processor.holidaytreasuretrovemap;

import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 寻藏宝图工具类
 *
 * @author: huanghb
 * @date: 2022/2/8 14:47
 */
public class TreasureTroveMapTool {

    /**
     * 获取配置类
     *
     * @return
     */
    public static CfgTreasureTroveMap getTreasureTroveMapCfg() {
        return Cfg.I.getUniqueConfig(CfgTreasureTroveMap.class);
    }

    /**
     * 生成连线奖励
     *
     * @return
     */
    public static List<CfgTreasureTroveMap.ConnectionAward> generateConnectionAwards() {
        List<CfgTreasureTroveMap.ConnectionAward> connectionAwardList = getTreasureTroveMapCfg().getConnectionAwards();
        Collections.shuffle(connectionAwardList);
        return connectionAwardList.subList(0, 6);
    }

    /**
     * 根据等级获得对g藏宝图碎片数量
     *
     * @return
     */
    public static Integer getTreasureTroveMapPieceNumByLevel(int treasureTroveMapLevel) {
        List<CfgTreasureTroveMap.TreasureTroveMap> treasureTroveMaps = getTreasureTroveMapCfg().getTreasureTroveMaps();
        CfgTreasureTroveMap.TreasureTroveMap treasureTroveMap = treasureTroveMaps.stream().filter(tmp -> tmp.getLevel() == treasureTroveMapLevel).findFirst().orElse(null);
        if (null == treasureTroveMap) {
            throw CoderException.high(String.format("treasureTroveMapLevel=%s范围的藏宝图", treasureTroveMapLevel));
        }
        return treasureTroveMap.getMapPieceNum();
    }

    /**
     * 获得翻牌需要的翻牌卡数量
     *
     * @return
     */
    public static Integer getFlopNeedFlopCardNum() {
        return getTreasureTroveMapCfg().getFlopNeedFlopCardNum();
    }

    /**
     * 根据连线名称返回对应连线奖励规则
     *
     * @return
     */
    public static CfgTreasureTroveMap.connectionAwardRule getFlopConnectionAwardRule(String connectionName) {
        List<CfgTreasureTroveMap.connectionAwardRule> connectionAwardRules = getTreasureTroveMapCfg().getConnectionAwardRules();
        return connectionAwardRules.stream().filter(tmp -> tmp.getConnectionName().equals(connectionName)).findFirst().orElse(null);
    }

    /**
     * 获得翻牌奖励
     *
     * @return
     */
    public static List<Award> gainFlopAwards() {
        List<CfgTreasureTroveMap.FlopAward> flopAwards = getTreasureTroveMapCfg().getFlopAwards();
        List<Integer> flopAwardProbs = flopAwards.stream().map(CfgTreasureTroveMap.FlopAward::getProb).collect(Collectors.toList());
        Integer flopAwardIndex = PowerRandom.hitProbabilityIndex(flopAwardProbs);
        return flopAwards.get(flopAwardIndex).getAwards();
    }

    /**
     * 获取最后一个目标
     *
     * @return
     */
    public static CfgTreasureTroveMap.Target getLastTarget() {
        List<CfgTreasureTroveMap.Target> targets = getTreasureTroveMapCfg().getTargets();
        return targets.get(targets.size() - 1);
    }

    /**
     * 获取所有目标
     *
     * @return
     */
    public static List<CfgTreasureTroveMap.Target> getAllFlopTarget() {
        return getTreasureTroveMapCfg().getTargets();
    }

    /**
     * 获取目标
     *
     * @param targetId
     * @return
     */
    public static CfgTreasureTroveMap.Target getFlopTarget(int targetId) {
        List<CfgTreasureTroveMap.Target> targets = getTreasureTroveMapCfg().getTargets();
        return targets.stream().filter(tmp -> tmp.getId() == targetId).findFirst().orElse(null);
    }

    /**
     * 获得藏宝图奖励
     *
     * @param treasureTroveMapLevel
     * @param turn
     * @return
     */
    public static List<Award> getTreasureTroveMapAwardByLevel(int treasureTroveMapLevel, int turn) {
        List<CfgTreasureTroveMap.TreasureTroveMap> treasureTroveMaps = getTreasureTroveMapCfg().getTreasureTroveMaps();
        CfgTreasureTroveMap.TreasureTroveMap treasureTroveMap = treasureTroveMaps.stream()
                .filter(tmp -> tmp.getLevel() == treasureTroveMapLevel && turn >= tmp.getMinTurn() && turn <= tmp.getMaxTurn())
                .findFirst().orElse(null);
        return treasureTroveMap.getCollectAwards();
    }

    /**
     * 获得寻宝道具id
     *
     * @return
     */
    public static Integer getTreasureHuntPropId() {
        return getTreasureTroveMapCfg().getTreasureHuntPropId();
    }
}
