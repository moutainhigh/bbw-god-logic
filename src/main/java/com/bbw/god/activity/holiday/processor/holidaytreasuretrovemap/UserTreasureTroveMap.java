package com.bbw.god.activity.holiday.processor.holidaytreasuretrovemap;

import com.bbw.common.ListUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardStatus;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * 玩家藏宝图
 *
 * @author: huanghb
 * @date: 2022/2/8 10:09
 */
@Data
public class UserTreasureTroveMap implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 藏宝图等级 */
    private Integer level;
    /** 不同等级藏宝图碎片数量 */
    private Integer[] pieceNum;
    /** 总翻牌次数 */
    private Integer totalFlopTimes;
    /** 不同等级藏宝图奖励集齐次数 */
    private Integer[] collectAwardTimes;
    /** 藏宝图翻牌状态 */
    private Integer[] flopStatus;
    /** 连线奖励 */
    private List<ConnectionAward> connectionAwards = new ArrayList<>();
    /** String 是 目标id,Integet是目标翻牌次数 */
    private TreeMap<String, Integer> targets;

    protected static UserTreasureTroveMap instance() {
        UserTreasureTroveMap userTreasureTroveMap = new UserTreasureTroveMap();
        userTreasureTroveMap.setLevel(0);
        userTreasureTroveMap.setPieceNum(new Integer[3]);
        userTreasureTroveMap.setFlopStatus(new Integer[9]);
        userTreasureTroveMap.setCollectAwardTimes(new Integer[]{0, 0, 0});
        userTreasureTroveMap.initTargets();
        userTreasureTroveMap.setTotalFlopTimes(0);
        return userTreasureTroveMap;
    }

    /**
     * 初始化目标
     */
    private void initTargets() {
        targets = new TreeMap<>();
        List<CfgTreasureTroveMap.Target> flopTargets = TreasureTroveMapTool.getAllFlopTarget();
        flopTargets.forEach(tmp -> targets.put(tmp.getId().toString(), AwardStatus.UNAWARD.getValue()));
    }

    /**
     * 获得藏宝图轮次-即集体藏宝图次数加1
     *
     * @param treasureTroveMapLevelIndex
     * @return
     */
    protected Integer gainFindTreasureMapTurn(Integer treasureTroveMapLevelIndex) {
        Integer collectAwardsTime = this.getCollectAwardTimes()[treasureTroveMapLevelIndex];
        return collectAwardsTime + 1;
    }

    /**
     * 是否可以领取藏宝图奖励
     *
     * @param treasureTroveMapLevel
     * @return
     */
    protected boolean ifCanReceiveTreasureTroveMapAwards(int treasureTroveMapLevel) {
        int treasureTroveMapLevelIndex = TreasureTroveMapLevelEnum.fromValue(treasureTroveMapLevel).getLevelIndex();
        if (this.getPieceNum()[treasureTroveMapLevelIndex] != null && this.getPieceNum()[treasureTroveMapLevelIndex] <= 0) {
            return true;
        }
        return false;
    }

    /**
     * 领取藏宝图奖励
     *
     * @param treasureTroveMapLevel
     */
    protected void receiveTreasureTroveMapAwards(int treasureTroveMapLevel) {
        int treasureTroveMapLevelIndex = TreasureTroveMapLevelEnum.fromValue(treasureTroveMapLevel).getLevelIndex();
        this.pieceNum[treasureTroveMapLevelIndex] = TreasureTroveMapTool.getTreasureTroveMapPieceNumByLevel(treasureTroveMapLevel);
        this.collectAwardTimes[treasureTroveMapLevelIndex] += 1;
    }

    /**
     * 是否可以刷新翻牌跳战
     *
     * @return
     */
    protected boolean ifCanRefreshFlopChallenge() {
        if (ListUtil.isEmpty(this.connectionAwards)) {
            return true;
        }
        for (int i = 0; i < this.connectionAwards.size(); i++) {
            if (this.connectionAwards.get(i).getStatus() == AwardStatus.AWARDED.getValue()) {
                continue;
            }
            return false;
        }
        return true;
    }

    /**
     * 刷新翻牌跳战
     *
     * @return
     */
    protected void refreshFlopChallenge(Integer treasureTroveMapLevel) {
        this.flopStatus = new Integer[9];
        this.connectionAwards = ConnectionAward.getInstance(TreasureTroveMapTool.generateConnectionAwards());
        this.level = treasureTroveMapLevel;
        int levelIndex = TreasureTroveMapLevelEnum.fromValue(treasureTroveMapLevel).getLevelIndex();
        if (null != this.pieceNum[levelIndex] && this.pieceNum[levelIndex] > 0) {
            return;
        }
        if (null == this.pieceNum[levelIndex]) {
            this.pieceNum[levelIndex] = TreasureTroveMapTool.getTreasureTroveMapPieceNumByLevel(treasureTroveMapLevel);
            return;
        }
        if (this.pieceNum[levelIndex] <= 0) {
            this.pieceNum[levelIndex] = TreasureTroveMapTool.getTreasureTroveMapPieceNumByLevel(treasureTroveMapLevel);
            this.collectAwardTimes[levelIndex]++;
        }

    }

    /**
     * 翻牌参数检测
     *
     * @param flopIdex
     * @return
     */
    protected boolean ifValidFlopIndex(int flopIdex) {
        if (flopIdex < 0) {
            return false;
        }
        if (flopIdex >= this.flopStatus.length) {
            return false;
        }
        return true;
    }

    /**
     * 是否可以翻牌
     *
     * @param flopIndex
     * @return
     */
    protected boolean ifCanFlop(int flopIndex) {
        if (null == this.flopStatus[flopIndex]) {
            return true;
        }
        return false;
    }

    /**
     * 翻牌
     *
     * @param flopIdex
     */
    protected void flop(Integer flopIdex) {
        Integer flopPosLength = 3;
        this.flopStatus[flopIdex] = flopIdex + 1;
        //竖向连线初始位置
        int verticalConnectionInitPos = flopIdex % flopPosLength;
        //横向连线初始位置
        int horizontalConnectionInitPos = flopIdex - verticalConnectionInitPos;
        String verticalConnectionName = "";
        String horizontalConnectionName = "";
        for (int i = 0; i < flopPosLength; i++) {
            verticalConnectionName += this.flopStatus[verticalConnectionInitPos + flopPosLength * i];
            horizontalConnectionName += this.flopStatus[horizontalConnectionInitPos + i];
        }
        updataConnectionStatus(verticalConnectionName);
        updataConnectionStatus(horizontalConnectionName);
        this.totalFlopTimes++;
        updateTarget();
    }

    /**
     * 更新连线奖励状态
     *
     * @param connectionName
     */
    private void updataConnectionStatus(String connectionName) {
        CfgTreasureTroveMap.connectionAwardRule connectionAwardRule = TreasureTroveMapTool.getFlopConnectionAwardRule(connectionName);
        if (null != connectionAwardRule) {
            this.connectionAwards.get(connectionAwardRule.getConnectionAwardIndex()).setStatus(AwardStatus.ENABLE_AWARD.getValue());
        }
    }

    /**
     * 获得连线奖励
     *
     * @param connectionAwardId
     * @return
     */
    protected ConnectionAward gainConnectionAwards(int connectionAwardId) {
        return this.connectionAwards.stream().filter(tmp -> tmp.getConnectionAwardId() == connectionAwardId).findFirst().orElse(null);
    }

    /**
     * 更新连线奖励状态
     *
     * @param connectionAwardId
     */
    protected void updataConnectionStatus(int connectionAwardId) {
        for (int i = 0; i < this.connectionAwards.size(); i++) {
            if (this.connectionAwards.get(i).getConnectionAwardId() != connectionAwardId) {
                continue;
            }
            this.connectionAwards.get(i).setStatus(AwardStatus.AWARDED.getValue());
        }
        if (!ifCanRefreshFlopChallenge()) {
            return;
        }
        int levelIndex = TreasureTroveMapLevelEnum.fromValue(this.level).getLevelIndex();
        this.pieceNum[levelIndex] -= 1;
        this.level = 0;
    }


    /**
     * 获取所有翻牌目标进度
     *
     * @return
     */
    protected List<Target> gainAllTargets() {
        List<Target> rdTargets = new ArrayList<>();
        this.targets.entrySet().forEach(tmp -> {
            Target target = new Target(Integer.valueOf(tmp.getKey()), tmp.getValue());
            rdTargets.add(target);
        });
        return rdTargets;
    }

    /**
     * 根据目标id获得目标进度
     *
     * @param targetId
     * @return
     */
    protected Target gainTargetByTargetId(int targetId) {
        List<Target> targets = gainAllTargets();
        return targets.stream().filter(tmp -> tmp.getTargetId() == targetId).findFirst().orElse(null);
    }

    /**
     * 更新目标
     */
    private void updateTarget() {
        targets.entrySet().forEach(target -> {
            if (target.getValue() != AwardStatus.UNAWARD.getValue()) {
                return;
            }
            Integer targetId = Integer.valueOf(target.getKey());
            CfgTreasureTroveMap.Target flopTarget = TreasureTroveMapTool.getFlopTarget(targetId);
            if (flopTarget.getMapPieceNum() <= this.getTotalFlopTimes()) {
                target.setValue(AwardStatus.ENABLE_AWARD.getValue());
            }
        });
    }

    /**
     * 更新为已领取
     *
     * @param targetId
     */
    public void updateTargetToAwarded(int targetId) {
        targets.put(targetId + "", AwardStatus.AWARDED.getValue());
    }

    @Data
    public static class Target implements Serializable {
        private static final long serialVersionUID = 1L;
        private Integer targetId;
        private Integer status;

        public Target(int targetId, int awardStatus) {
            this.targetId = targetId;
            this.status = awardStatus;
        }
    }

    @Data
    public static class ConnectionAward {
        private Integer connectionAwardId;
        private Integer status;
        private List<Award> awards;

        public ConnectionAward(int connectionAwardId, int status, List<Award> awards) {
            this.connectionAwardId = connectionAwardId;
            this.status = status;
            this.awards = awards;
        }

        public static List<ConnectionAward> getInstance(List<CfgTreasureTroveMap.ConnectionAward> connectionAwards) {
            List<ConnectionAward> connectionAwardList = new ArrayList<>();
            for (CfgTreasureTroveMap.ConnectionAward connectionAward : connectionAwards) {
                ConnectionAward userConnectionAward = new ConnectionAward(
                        connectionAward.getId(),
                        AwardStatus.UNAWARD.getValue(),
                        connectionAward.getAwards());
                connectionAwardList.add(userConnectionAward);
            }
            return connectionAwardList;
        }
    }
}
