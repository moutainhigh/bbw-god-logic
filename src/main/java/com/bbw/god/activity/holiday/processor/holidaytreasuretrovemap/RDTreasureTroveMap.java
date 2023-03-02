package com.bbw.god.activity.holiday.processor.holidaytreasuretrovemap;

import com.bbw.god.game.award.RDAward;
import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 藏宝图信息
 *
 * @author: huanghb
 * @date: 2022/2/8 12:59
 */
@Data
public class RDTreasureTroveMap extends RDCommon implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 藏宝图等级 */
    private Integer level;
    /** 藏宝图翻牌id */
    private Integer[] flopStatus;
    /** 不同等级藏宝图碎片数量 */
    private Integer[] levelPieceNum;
    /** 总翻牌次数 */
    private Integer totalFlopTimes;
    /** 翻牌目标进度 */
    private List<RDTarget> targets;
    /** 连线奖励 */
    private List<RDConnectionAward> connectionAwards;
    /** 剩余时间 */
    private long remainTime;

    /**
     * 初始化藏宝图信息
     *
     * @param userTreasureTroveMap
     * @return
     */
    public static RDTreasureTroveMap instance(UserTreasureTroveMap userTreasureTroveMap) {
        RDTreasureTroveMap rd = new RDTreasureTroveMap();
        rd.setLevel(userTreasureTroveMap.getLevel());
        rd.setFlopStatus(userTreasureTroveMap.getFlopStatus());
        rd.setLevelPieceNum(userTreasureTroveMap.getPieceNum());
        List<RDTarget> rdTargets = RDTarget.getInstance(userTreasureTroveMap.gainAllTargets());
        rd.setTargets(rdTargets);
        List<RDConnectionAward> rdConnectionAwards = RDConnectionAward.getInstance(userTreasureTroveMap.getConnectionAwards());
        rd.setConnectionAwards(rdConnectionAwards);
        rd.setTotalFlopTimes(userTreasureTroveMap.getTotalFlopTimes());
        return rd;
    }

    @Data
    public static class RDTarget implements Serializable {
        private static final long serialVersionUID = 585390831831786753L;
        private Integer targetId;
        private Integer status;

        public RDTarget(UserTreasureTroveMap.Target target) {
            this.targetId = target.getTargetId();
            this.status = target.getStatus();
        }

        public static List<RDTarget> getInstance(List<UserTreasureTroveMap.Target> targets) {
            List<RDTarget> targetList = new ArrayList<>();
            for (UserTreasureTroveMap.Target target : targets) {
                RDTarget rdTarget = new RDTarget(target);
                targetList.add(rdTarget);
            }
            return targetList;
        }
    }

    @Data
    public static class RDConnectionAward implements Serializable {
        private static final long serialVersionUID = 585390831831786753L;
        private Integer targetId;
        private Integer status;
        private List<RDAward> awards;

        public RDConnectionAward(UserTreasureTroveMap.ConnectionAward connectionAward) {
            this.targetId = connectionAward.getConnectionAwardId();
            this.status = connectionAward.getStatus();
            this.awards = RDAward.getInstances(connectionAward.getAwards());
        }

        public static List<RDConnectionAward> getInstance(List<UserTreasureTroveMap.ConnectionAward> connectionAwards) {
            List<RDConnectionAward> connectionAwardList = new ArrayList<>();
            for (UserTreasureTroveMap.ConnectionAward connectionAward : connectionAwards) {
                RDConnectionAward rdConnectionAward = new RDConnectionAward(connectionAward);
                connectionAwardList.add(rdConnectionAward);
            }
            return connectionAwardList;
        }
    }
}
