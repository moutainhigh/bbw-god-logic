package com.bbw.god.game.maou.cfg;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 跨服魔王配置
 *
 * @author: suhq
 * @date: 2021/12/15 4:07 下午
 */
@Data
public class CfgGameMaou implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = -926676412865385950L;
    private String key;
    private Integer cardLimit = 0;
    private Integer freeTimes = 0;
    private Integer extraTimesForDiLY = 0;
    private Integer extraTimesForTingLY = 0;
    private Integer intervalSeconds;
    /** 等级达到多少级的参与者可获得击杀奖励。!!!如果与其他特殊条件判定，由具体业务特殊处理。 */
    private Integer killAwardsNeedLevel;
    private List<GameMaouInfo> maous;
    private List<GameMaouTarget> targets;
    // 生效的技能
    private List<Integer> effectSkills = new ArrayList<>();
    // 生效的组合
    private List<Integer> effectGroups = new ArrayList<>();

    /**
     * 获取第一个目标
     *
     * @return
     */
    public GameMaouTarget getFirstTarget() {
        return targets.get(0);
    }

    /**
     * 获取最后一个目标
     *
     * @return
     */
    public GameMaouTarget getLastTarget() {
        return targets.get(targets.size() - 1);
    }

    /**
     * 获取目标
     *
     * @param targetId
     * @return
     */
    public GameMaouTarget getMaouTarget(int targetId) {
        return targets.stream().filter(tmp -> tmp.getId() == targetId).findFirst().orElse(null);
    }

    /**
     * 获取魔王信息
     *
     * @param turn
     * @return
     */
    public GameMaouInfo getMaouInfo(int turn) {
        GameMaouInfo gameMaouInfo = maous.stream()
                .filter(tmp -> tmp.getMinTurn() <= turn && tmp.getMaxTurn() >= turn)
                .findFirst().get();
        return gameMaouInfo;
    }


    @Override
    public String getId() {
        return this.key;
    }

    @Override
    public int getSortId() {
        return 1;
    }

    @Data
    public static class GameMaouInfo implements Serializable {
        private static final long serialVersionUID = -5207682094090019L;
        private Integer id;
        /** 初始血量 */
        private Integer initBlood;
        /** 使用该魔王数据的最小轮次 */
        private Integer minTurn;
        /** 使用该魔王数据的最大轮次。999999相当于无限轮次 */
        private Integer maxTurn;
        /** 在轮次期间，每轮的血量加值 */
        private Integer extraBloodPerTurn;
        /** 击杀奖励 */
        private List<Award> killAwards;

        /**
         * 获取特定轮次的血量
         *
         * @param turn
         * @return
         */
        public int getInitBlood(int turn) {
            int tmp = turn - minTurn;
            return initBlood + tmp * extraBloodPerTurn;
        }
    }


    @Data
    public static class GameMaouTarget implements Serializable {
        private static final long serialVersionUID = 6283485026406890074L;
        private Integer id;
        private Integer blood;
        private List<Award> awards;
    }
}
