package com.bbw.god.game.combat.data.card;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCardStatus.StatusEffectType;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.BattleSkillLog;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.config.TypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 战斗卡牌
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-04 18:38
 */
@Data
@NoArgsConstructor
public class BattleCard implements Serializable {
    private static final long serialVersionUID = 3449676024891768757L;
    public static final int MIN_MP = 1;
    private int id;// 本次战斗中的卡牌ID，不是卡牌id！！！
    // ---------卡牌初始化配置值------------
    private int imgId = 10;// 卡牌id
    private String name;// 卡牌名称
    private int lv = 0;// 等级
    private int hv = 0;// 阶级
    private TypeEnum type = TypeEnum.Gold;// 属性
    private int stars = 1;// 星级
    private int groupId = -1;// 组合ID
    private int initAtk = 0;// 卡牌初始化物理输出
    private int initHp = 0;// 卡牌初始化血量
    private Integer sex = null;
    private Integer fashion = null;
    // ---------回合开始值------------
    private int roundMpAddition = 0;// 需要消耗的法力值
    private int roundAtk = 0;// 回合初始物理攻击
    private int roundHp = 0;// 回合初始血量
    private int roundMaxHp = 0;//存活期间血量上限最大那次
    private int roundMaxAtk = 0;//存活期间攻击上限最大那次
    private int reduceRoundTempHp = 0;//回合扣除的血量  每回合结束或死亡需要清0
    private int reduceRoundTempAtk = 0;//回合扣除的攻击力  每回合结束或死亡需要清0
    // ---------当前值------------
    private int pos = -1;// 位置信息
    private int mp = 3;// 需要消耗的法力值
    private int atk = 0;// 当前物理攻击
    private int hp = 0;// 当前血量
    private Integer isUseSkillScroll = 0;//是否使用卷轴
    private List<CardValueEffect> lastingEffects = new ArrayList<>();// 持久性攻击
    private List<BattleSkill> skills = new ArrayList<>();// 法术
    private BattleSkill normalAttackSkill = BattleSkill.getNormalAttackSkill(); // 普通攻击
    private BattleSkill normalDefenseSkill = BattleSkill.getNormalDefenseSkill(); // 防御击中
    // ---------当前值------------
    private List<BattleSkillLog> skillLogs = new ArrayList<>();// 技能使用记录
    private int leftAtk = 0;// 击中后剩余多少攻击，每回合后重置
    private boolean behit = false;// 是否被击中，每回合后重置
    private boolean hit = false;// 是否击中对手，每回合后重置
    private List<CardValueEffect> roundDelayEffects = new ArrayList<>();// 回合临时加成
    private Set<BattleCardStatus> status = new HashSet<>();// 卡牌当前回合状态
    private int fuHuoSkillEffectTimes = 0;
    /** 生效期仅在一次技能释放到结束 */
    private List<Integer> banDieSkills = new ArrayList<>();
    private boolean alive = true;
    private Integer buff = 0;
    private Integer normalAttackPreAtk = 0;//物理攻击前的攻击力
    /** 该卡牌整场战斗中触发的技能,任何情況下都不會重置 */
    private List<BattleSkillLog> historySkillLogs = new ArrayList<>();

    /**
     * 攻击前更新普通攻击的攻击力。传0表示重置卡牌普通攻击为当前攻击力
     *
     * @param val
     */
    public void updateNormalAttackPreAtk(int val) {
        if (val == 0) {
            this.normalAttackPreAtk = atk;
        }
        this.normalAttackPreAtk += val;
    }

    public void setMp(int val) {
        mp = Math.max(val, BattleCard.MIN_MP);
    }

    public void clearTransientVal() {
        leftAtk = 0;
        behit = false;
        hit = false;
        roundDelayEffects.clear();
        banDieSkills.clear();
    }

    /**
     * 卡牌是否已存在指定的技能状态
     *
     * @param skillID
     * @return
     */
    public boolean existSkillStatus(int skillID) {
        if (status.isEmpty()) {
            return false;
        }
        for (BattleCardStatus cardStatus : status) {
            if (cardStatus.getSkillID() == skillID) {
                return true;
            }
        }
        return false;
    }

    /**
     * 卡牌是否已存在指定某类的技能状态
     *
     * @param skillID
     * @return
     */
    public boolean existSkillStatus(SkillSection section) {
        if (status.isEmpty()) {
            return false;
        }
        for (BattleCardStatus cardStatus : status) {
            if (section.contains(cardStatus.getSkillID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 卡牌是否已存在指定的技能
     *
     * @param skillID
     * @return
     */
    public boolean existSkill(int skillID) {
        if (skills.isEmpty()) {
            return false;
        }
        for (BattleSkill skill : skills) {
            if (skill.getId() == skillID) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取所有技能（含物理攻击）
     *
     * @return
     */
    public List<BattleSkill> getSkillsWithNormalAttack() {
        List<BattleSkill> total = new ArrayList<>();// 法术
        total.addAll(skills);
        total.add(normalAttackSkill);
        return total;
    }

    /**
     * 魅惑=》 获取 3101 到3199的法术效果技能+钻地
     *
     * @return
     */
    public List<BattleSkill> get31013199Skills() {
        List<BattleSkill> total = new ArrayList<>();
        SkillSection skillAttackSection = SkillSection.getSkillAttackSection();// 法术攻击技能
        SkillSection afterSkillAttackSection = SkillSection.getBeforeNormalSection();// 物理攻击之前，法术之后
        SkillSection skillNormalPriority1Section = SkillSection.getNormalPriority1Section();// 先手技能
        for (BattleSkill skill : skills) {
            if (skillAttackSection.contains(skill.getId()) || afterSkillAttackSection.contains(skill.getId()) || skillNormalPriority1Section.contains(skill.getId())) {
                total.add(skill);
            }
        }
        return total;
    }

    /**
     * 获取所有主动技能（只包含普攻、物理攻击先手、物理攻击加成、上场技能、法术攻击技能）
     * //外加 死斗 和暴击
     *
     * @return
     */
    public List<BattleSkill> getActiveAttackSkills() {
        List<BattleSkill> total = new ArrayList<>();
        SkillSection deploySection = SkillSection.getDeploySection();// 上场技能
        SkillSection skillAttackSection = SkillSection.getSkillAttackSection();// 法术攻击技能
        SkillSection afterSkillAttackSection = SkillSection.getBeforeNormalSection();// 物理攻击之前，法术之后
        SkillSection normalPriority1Section = SkillSection.getNormalPriority1Section();// 先手技能
        SkillSection priority1Section = SkillSection.getPriority1Section();// 先制技能
        SkillSection normalAttackSection = SkillSection.getNormalAttackSection();// 物理攻击技能
        //先制法术技能
        SkillSection prioritySkillAttackSection = SkillSection.getPrioritySkillAttackSection();
        SkillSection normalBuffSection_0 = SkillSection.getNormalBuffSection0();
        SkillSection normalBuffSection_1 = SkillSection.getNormalBuffSection1();
        SkillSection normalPriority = SkillSection.getNormalPriority1Section();
        //外加 死斗 和暴击
        for (BattleSkill skill : skills) {
            if (deploySection.contains(skill.getId()) || skillAttackSection.contains(skill.getId())
                    || afterSkillAttackSection.contains(skill.getId())
                    || normalPriority1Section.contains(skill.getId()) || priority1Section.contains(skill.getId())
                    || normalAttackSection.contains(skill.getId()) || skill.getId() == CombatSkillEnum.SID.getValue()
                    || skill.getId() == CombatSkillEnum.BJ.getValue() || normalBuffSection_0.contains(skill.getId())
                    || normalBuffSection_1.contains(skill.getId())
                    || normalPriority.contains(skill.getId()) || prioritySkillAttackSection.contains(skill.getId())) {
                total.add(skill);
            }
        }
        total.add(normalAttackSkill);
        return total;
    }

    /**
     * 技能使用次数
     *
     * @param skillId
     * @return
     */
    public long getSkillEffectTimes(int skillId) {
        return skillLogs.stream().filter(log -> log.getSkillId() == skillId).count();
    }

    /**
     * 整场战斗卡牌受到某个技能作用的次数
     * @param skillId
     * @return
     */
    public long getHistotySkillEffectTimes(int skillId) {
        return historySkillLogs.stream().filter(log -> log.getSkillId() == skillId).count();
    }

    /**
     * 记录技能使用日志
     *
     * @param skillId
     * @param round
     * @param targetPos
     */
    public void addSkillLog(int skillId, int round, int targetPos) {

        Optional<BattleSkillLog> one = skillLogs.stream().filter(log -> log.getSkillId() == skillId && log.getRound() == round).findAny();
        if (one.isPresent()) {
            one.get().getTargetsPos().add(targetPos);
        } else {
            BattleSkillLog skillLog = new BattleSkillLog(skillId, round, targetPos);
            skillLogs.add(skillLog);
            historySkillLogs.add(skillLog);
        }

    }

    public void addCardStatus(int round, int skillId) {
        BattleCardStatus cardStatus = new BattleCardStatus(round, skillId);
        status.add(cardStatus);
    }

    public void addCardStatus(StatusEffectType type, int round, int skillId) {
        BattleCardStatus cardStatus = new BattleCardStatus(round, skillId, type);
        status.add(cardStatus);
    }

    public void addCardStatus(StatusEffectType type, int round, int skillId, BattleCard card) {
        BattleCardStatus cardStatus = new BattleCardStatus();
        cardStatus.setRound(round);
        cardStatus.setSkillID(skillId);
        cardStatus.setStatusEffectType(type);
        cardStatus.setSouceCard(card);
        status.add(cardStatus);
    }

    public List<Integer> getCardStatusSkills(SkillSection section) {
        List<Integer> ids = new ArrayList<>();
        for (BattleCardStatus bs : status) {
            if (bs.isRoundvalid() && section.contains(bs.getSkillID())) {
                ids.add(bs.getSkillID());
            }
        }
        return ids;
    }

    /**
     * 重置卡牌状态 即所有状态持续减少1回合
     */
    public void resetRoundCardStatus() {
        status.forEach(p -> {
            if (p.getStatusEffectType() != StatusEffectType.ROUND_END) {
                p.incRound();
            }
        });
        status = status.stream().filter(p -> !p.isInvalid()).collect(Collectors.toSet());
    }

    public void incTimesCardStatus(int skillId) {
        status.forEach(p -> {
            if (p.getSkillID() == skillId) {
                p.incTimes();
            }
        });
        status = status.stream().filter(p -> !p.isInvalid()).collect(Collectors.toSet());
    }

    /**
     * 重置卡牌状态 即所有状态持续减少1回合
     */
    public void resetRoundEndCardStatus() {
        status.forEach(p -> {
            if (p.getStatusEffectType() == StatusEffectType.ROUND_END) {
                p.incRound();
            }
        });
        status = status.stream().filter(p -> !p.isInvalid()).collect(Collectors.toSet());
    }


    public Optional<CardValueEffect> getRoundDelayEffect(int buffId) {
        return roundDelayEffects.stream().filter(buff -> buff.getSourceID() == buffId).findAny();
    }

    public void removeRoundDelayEffect(int buffId) {
        roundDelayEffects = roundDelayEffects.stream().filter(buff -> buff.getSourceID() != buffId).collect(Collectors.toList());
    }

    /**
     * mp大于最小值
     *
     * @return
     */
    public boolean mpMoreThanMin() {
        return mp > MIN_MP;
    }

    /**
     * 重置数据
     */
    public void reset(boolean restGotoPlaying) {
        roundAtk = initAtk;
        roundHp = initHp;
        roundMaxAtk = roundAtk;
        roundMaxHp = roundHp;
        reduceRoundTempAtk = 0;
        reduceRoundTempHp = 0;
        atk = initAtk;
        hp = initHp;
        this.leftAtk = 0;
        this.roundMpAddition = 0;
        this.behit = false;
        this.hit = false;
        this.status.clear();
        this.skillLogs.clear();
        this.lastingEffects.clear();
        if (!skills.isEmpty()) {
            // 重置技能，留下卡牌自带的技能
            skills = skills.stream().filter(skill -> skill.isBornSkill()).collect(Collectors.toList());
        }
        if (restGotoPlaying) {
            // 设置卡牌技能可用性重置
            for (BattleSkill skill : skills) {
                skill.getTimesLimit().reset();
                ;
                skill.setPerformTimes(0);
            }
        } else {
            SkillSection gotoPlaying = SkillSection.getDeploySection();
            for (BattleSkill skill : skills) {
                if (skill.getId() != CombatSkillEnum.JINS.getValue() && !gotoPlaying.contains(skill.getId())) {
                    skill.getTimesLimit().reset();
                    skill.setPerformTimes(0);
                }
            }
        }
        // 重置普攻
        normalAttackSkill.getTimesLimit().reset();
        normalAttackSkill.setTargetPos(-1);
        if (banDieSkills == null) {
            banDieSkills = new ArrayList<>();
        }
        banDieSkills.clear();
        alive = true;
        clearTransientVal();
    }

    /**
     * 清理卡牌BUff
     * 不会重置上场技能
     */
    public void resetBuffStatus(boolean resetNormalAttack) {
        this.leftAtk = 0;
        this.roundMpAddition = 0;
        this.behit = false;
        this.hit = false;
        this.status.clear();
        this.skillLogs.clear();
        this.lastingEffects.clear();
        if (!skills.isEmpty()) {
            // 重置技能，留下卡牌自带的技能
            skills = skills.stream().filter(skill -> skill.isBornSkill()).collect(Collectors.toList());
        }
        // 设置卡牌技能可用性重置
        SkillSection gotoPlaying = SkillSection.getDeploySection();
        for (BattleSkill skill : skills) {
            if (skill.getId() != CombatSkillEnum.JINS.getValue() && !gotoPlaying.contains(skill.getId())) {
                skill.getTimesLimit().reset();
            }
        }
        // 重置普攻
        if (resetNormalAttack) {
            normalAttackSkill.getTimesLimit().reset();
        }
        normalAttackSkill.setTargetPos(-1);
        clearTransientVal();
    }

    /**
     * 获取当前环节有效的技能
     *
     * @param section
     * @return
     */
    public List<BattleSkill> getEffectiveSkills(SkillSection section) {
        // 留下此环节有效的技能
        List<BattleSkill> effectSkills = skills.stream()
                .filter(skill -> section.contains(skill.getId()) && skill.isEffective())
                .collect(Collectors.toList());
        if (section.contains(CombatSkillEnum.NORMAL_ATTACK.getValue()) && normalAttackSkill.isEffective()) {
            effectSkills.add(normalAttackSkill);
        }
        if (section.contains(CombatSkillEnum.NORMAL_DEFENSE.getValue())) {
            effectSkills.add(this.normalDefenseSkill);
        }
        return effectSkills;
    }

    /**
     * 判断技能是否有效
     *
     * @param skillID
     * @return
     */
    public boolean effectiveSkill(int skillID) {
        if (skillID == 4599 || SkillSection.isAttributerestraint(skillID)) {
            return true;
        }
        if (skillID == CombatSkillEnum.NORMAL_ATTACK.getValue()) {
            return normalAttackSkill.isEffective();
        }
        for (BattleSkill skill : skills) {
            if (skill.getId() == skillID) {
                return skill.isEffective();
            }
        }
        return false;
    }

    /**
     * 是否包含指定的技能
     *
     * @param skillId
     * @return
     */
    public boolean containsSkill(int skillId) {
        return getSkills().stream().filter(skill -> skill.getId() == skillId).findAny().isPresent();
    }

    public Optional<BattleSkill> getSkill(int skillId) {
        if (CombatSkillEnum.NORMAL_ATTACK.getValue() == skillId) {
            return Optional.of(this.normalAttackSkill);
        }
        if (CombatSkillEnum.NORMAL_DEFENSE.getValue() == skillId) {
            return Optional.of(this.normalDefenseSkill);
        }
        return getSkills().stream().filter(skill -> skill.getId() == skillId).findAny();
    }

    /**
     * 改变mp
     *
     * @param value 大于0 增加 小于0 减少
     */
    public void incMp(int value) {
        if (0 == value) {
            return;
        }
        mp += value;
        mp = Math.max(mp, 0);
    }

    /**
     * 改变hp
     *
     * @param value 大于0 增加 小于0 减少
     */
    public void incHp(int value) {
        if (0 == value) {
            return;
        }
        hp += value;
        hp = Math.max(hp, 0);
        alive = roundHp > 0 && hp > 0;
    }

    public void incReduceRoundTempHp(int value) {
        if (value < 0) {
            reduceRoundTempHp += Math.abs(value);
        }
    }

    public void incReduceRoundTempAtk(int value) {
        if (value < 0) {
            reduceRoundTempAtk += Math.abs(value);
        }
    }

    /**
     * 改变atk
     *
     * @param value 大于0 增加 小于0 减少
     */
    public void incAtk(int value) {
        if (0 == value) {
            return;
        }
        atk += value;
        atk = Math.max(atk, 0);

    }

    /**
     * 改变hp
     *
     * @param value 大于0 增加 小于0 减少
     */
    public void incRoundHp(int value) {
        if (0 == value) {
            return;
        }
        roundHp += value;
        roundHp = Math.max(roundHp, 0);
        if (roundHp > roundMaxHp) {
            roundMaxHp = roundHp;
        }
        alive = roundHp > 0 && hp > 0;
    }

    /**
     * 改变atk
     *
     * @param value 大于0 增加 小于0 减少
     */
    public void incRoundAtk(int value) {
        if (0 == value) {
            return;
        }
        roundAtk += value;
        roundAtk = Math.max(roundAtk, 0);
        if (roundAtk > roundMaxAtk) {
            roundMaxAtk = roundAtk;
        }
    }

    /**
     * 战败
     *
     * @return
     */
    public boolean isKilled() {
        return !alive;
    }

    /**
     * 追加技能
     *
     * @param skill
     */
    public void addSkill(BattleSkill skill) {
        this.skills.add(skill);
    }

    /**
     * 加到队首，优先执行
     *
     * @param skills
     */
    public void addSkillsToFront(List<BattleSkill> skills) {
        this.skills.addAll(0, skills);
    }

    /**
     * 是否有飞行技能
     *
     * @return
     */
    public boolean canFly() {
        return skills.stream().filter(skill -> skill.isFlySkill()).findAny().isPresent();
    }

    /**
     * 是否有[王者]技能
     *
     * @return
     */
    public boolean hasKingSkill() {
        return skills.stream().filter(skill -> skill.isKingSkill()).findAny().isPresent();
    }


    /**
     * 是否有最小MP的技能：疾驰或者飞闪
     *
     * @return
     */
    public boolean hasMinMpSkill() {
        return skills.stream().filter(skill -> skill.isJiChiSkill() || skill.getId() == CombatSkillEnum.FEI_SHAN.getValue()).findAny().isPresent();
    }

    /**
     * 是否有钻地技能
     *
     * @return
     */
    public boolean hasZuanDiSkill() {
        return skills.stream().filter(skill -> skill.isZuanDiSkill()).findAny().isPresent();
    }

    /**
     * 是否含有某技能不管是否 还有效
     *
     * @param skillId
     * @return
     */
    public boolean hasSkill(int skillId) {
        return skills.stream().filter(skill -> skill.getId() == skillId).findAny().isPresent();
    }

    /**
     * 是否含有有效的技能
     *
     * @param skillId
     * @return
     */
    public boolean hasEffectiveSkill(int skillId) {
        return skills.stream().filter(skill -> skill.getId() == skillId && skill.isEffective()).findAny().isPresent();
    }

    /**
     * 将玩家王者 替换成AI王者
     */
    public void changeAikingSKill() {
        for (BattleSkill skill : skills) {
            if (skill.getId() == CombatSkillEnum.WZ.getValue()) {
                skill.setId(CombatSkillEnum.WZAI.getValue());
            }
        }
    }

    public List<BattleCardStatus> getRoundEndCardStatus() {
        List<BattleCardStatus> res = new ArrayList<>();
        for (BattleCardStatus status : status) {
            if (status.getStatusEffectType() == StatusEffectType.ROUND_END) {
                res.add(status);
            }
        }
        return res;
    }

    /**
     * 重新初始化卡牌血量 包含hp,initHp,roundHp
     *
     * @param val
     */
    public void resetAllHp(int val) {
        hp = val;
        initHp = val;
        roundHp = val;
        roundMaxHp = val;
    }

    /**
     * 重新初始化卡牌攻击力 包含atk,initAtk,roundAtk
     *
     * @param val
     */
    public void resetAllAtk(int val) {
        atk = val;
        initAtk = val;
        roundAtk = val;
        roundMaxAtk = val;
    }

    /**
     * 判断该卡是否带有神剑效果
     *
     * @return
     */
    public boolean hasEffect(CombatSkillEnum skillEnum) {
        for (BattleCardStatus sta : status) {
            if (sta.getSkillID().equals(skillEnum.getValue())) {
                return true;
            }
        }
        return false;
    }

    public BattleSkill getFirstSkill(SkillSection section) {
        List<BattleSkill> skills = getSkills();
        if (skills == null || skills.isEmpty()) {
            return null;
        }
        for (BattleSkill skill : skills) {
            if (skill.isEffective() && section.contains(skill.getId())) {
                return skill;
            }
        }
        return null;
    }

    /**
     * 本次禁用某个死亡技能
     *
     * @param skillId
     */
    public void banDieSkill(int skillId) {
        banDieSkills.add(skillId);
    }

    /**
     * 本次禁用所有死亡技能
     */
    public void banAllDieSKill() {
        SkillSection section = SkillSection.getDieSection();
        for (int id : section.getSkills()) {
            banDieSkills.add(id);
        }
    }

    public List<Integer> getBanSkills() {
        List<Integer> ids = new ArrayList<Integer>();
        for (BattleSkill skill : skills) {
            if (!skill.isEffective()) {
                ids.add(skill.getId());
            }
        }
        if (normalAttackSkill.isEffective()) {
            ids.add(normalAttackSkill.getId());
        }
        if (!ListUtil.isEmpty(banDieSkills)) {
            ids.addAll(banDieSkills);
        }
        return ids;
    }

    /**
     * 获取所有排除技能自带的技能：如哮天：自带暗蛊 混元 禁术，则暗蛊 混元 禁术 不会包含在内
     *
     * @return
     */
    public List<BattleSkill> excludeDerivativeAllSkills() {
        List<BattleSkill> skills = new ArrayList<>();
        for (BattleSkill skill : getSkills()) {
            if (skill.getParent() == null) {
                skills.add(skill);
            }
        }
        return skills;
    }

    /**
     * 获取某回合的技能释放次数
     *
     * @param skillId 技能id
     * @param round 回合数
     * @return 释放次数
     */
    public int gainTimesOfSkillPerformInRound(int skillId, int round){
        // 获取技能释放次数
        return (int) getHistorySkillLogs().stream().filter(skillLog ->
                skillLog.getSkillId() == skillId && skillLog.getRound() == round).count();
    }
}