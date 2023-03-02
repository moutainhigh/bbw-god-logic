package com.bbw.god.game.maou;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.maou.cfg.CfgGameMaou;
import com.bbw.god.game.maou.cfg.GameMaouTool;
import com.bbw.god.game.maou.cfg.GameMaouType;
import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 * 魔王攻打信息
 *
 * @author: suhq
 * @date: 2021/12/17 10:22 上午
 */
@Data
public class GameMaouAttacker implements Serializable {
    private static final long serialVersionUID = -2703643819461277402L;
    private Long uid;
    /** 魔王类型 */
    private Integer maouType;
    /** 参战卡牌 */
    private List<Integer> cards;
    /** 总攻击次数 */
    private Integer totalAttackTimes;
    /** 剩余攻击次数 */
    private Integer remainAttackTimes;
    /** 最近一次攻打时间 */
    private Date lastAttackTime;
    /** 打掉的血量 */
    private Integer attackBlood;
    /** 目标奖励状态 */
    private TreeMap<String, Integer> targets;

    public static GameMaouAttacker getInstance(long uid, GameMaouType maouType, int attackTimes) {
        GameMaouAttacker attackInfo = new GameMaouAttacker();
        attackInfo.setUid(uid);
        attackInfo.setMaouType(maouType.getValue());
        attackInfo.setTotalAttackTimes(attackTimes);
        attackInfo.setRemainAttackTimes(attackTimes);
        attackInfo.setLastAttackTime(DateUtil.now());
        attackInfo.setAttackBlood(0);
        attackInfo.initTargets();
        return attackInfo;
    }

    /**
     * 更新攻击信息
     *
     * @param addAttackBlood
     */
    public void updateAttackInfo(int addAttackBlood) {
        this.attackBlood += addAttackBlood;
        this.remainAttackTimes--;
        this.lastAttackTime = DateUtil.now();
        updateTarget(this.attackBlood);

    }

    /**
     * 添加额外攻击次数
     *
     * @param newTotalAttackTimes
     */
    public void addExtraAttackTimes(int newTotalAttackTimes) {
        int addTimes = newTotalAttackTimes - this.getTotalAttackTimes();
        this.setTotalAttackTimes(newTotalAttackTimes);
        this.setRemainAttackTimes(this.getRemainAttackTimes() + addTimes);
    }

    /**
     * 获取当前目标
     *
     * @return
     */
    public Target gainCurTarget() {
        Optional<Map.Entry<String, Integer>> op = targets.entrySet().stream()
                .filter(tmp -> tmp.getValue() != AwardStatus.AWARDED.getValue())
                .findFirst();
        if (op.isPresent()) {
            Integer targetId = Integer.valueOf(op.get().getKey());
            return new Target(targetId, op.get().getValue());
        }
        Integer lastTargetId = GameMaouTool.getMaouConfig(GameMaouType.fromValue(maouType)).getLastTarget().getId();
        return new Target(lastTargetId, AwardStatus.AWARDED.getValue());
    }

    /**
     * 更新目标
     *
     * @param attackBlood
     */
    private void updateTarget(int attackBlood) {
        CfgGameMaou maouConfig = GameMaouTool.getMaouConfig(GameMaouType.fromValue(maouType));
        targets.entrySet().forEach(target -> {
            if (target.getValue() != AwardStatus.UNAWARD.getValue()) {
                return;
            }
            Integer targetId = Integer.valueOf(target.getKey());
            CfgGameMaou.GameMaouTarget maouTarget = maouConfig.getMaouTarget(targetId);
            if (maouTarget.getBlood() <= attackBlood) {
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

    /**
     * 初始化目标
     */
    private void initTargets() {
        targets = new TreeMap<>();
        CfgGameMaou maouConfig = GameMaouTool.getMaouConfig(GameMaouType.fromValue(maouType));
        maouConfig.getTargets().forEach(tmp -> targets.put(tmp.getId().toString(), AwardStatus.UNAWARD.getValue()));
    }


    @Data
    public static class Target implements Serializable {
        private static final long serialVersionUID = 585390831831786753L;
        private Integer targetId;
        private Integer status;

        public Target(int targetId, int awardStatus) {
            this.targetId = targetId;
            this.status = awardStatus;
        }
    }
}
