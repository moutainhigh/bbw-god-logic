package com.bbw.god.game.combat.data.skill;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.config.card.CfgCardSkill;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 战斗技能
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-04 18:45
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BattleSkill implements Serializable {
	private static final long serialVersionUID = 2934041238721769258L;
	private int id = 0;// 技能标识
	private Integer from = null;
	private Integer parent = null;
	private Integer performTimes = 0;
	/** 初始额外加成（装备技能引入） */
	private Double initPerformProbability = 0.0;
	/** 初始额外加成（装备技能引入） */
	private Double initExtraRate = 0.0;
	private boolean defensible = true; // 是否可被防御
	private TimesLimit timesLimit = TimesLimit.noLimit();// 在战斗中的可使用回合数量
	private transient int targetPos = -1;// 攻击目标,>0标识指定了攻击目标
	private transient List<Integer> changeTargetSkill = null;// 使攻击目标改变的技能

	/**
	 * 是否是自带的技能
	 *
	 * @return
	 */
	public boolean isBornSkill() {
		return from==null || from <0 || from>131000;
	}
	public boolean ifBornBuffSkill(){
		return from!=null && from>131000;
	}

	public BattleSkill(int id) {
		this.id = id;
	}


	/**
	 * 初始化自带技能
	 * @param cardSkill
	 * @return
	 */
	public static BattleSkill instanceBornSkill(CfgCardSkill cardSkill) {
		BattleSkill skill=new BattleSkill();
		skill.setId(cardSkill.getId());
		skill.setDefensible(cardSkill.isDefensible());
		skill.setTimesLimit(TimesLimit.instance(cardSkill));
		return skill;
	}

	/**
	 *
	 * 初始化 添加的技能
	 * @param from  来自什么地方添加的（技能ID、法宝ID、符文ID）
	 * @param cardSkill
	 * @return
	 */
	public static BattleSkill instanceSkill(int from, CfgCardSkill cardSkill) {
		BattleSkill skill=instanceBornSkill(cardSkill);
		skill.setFrom(from);
		return skill;
	}

	/**
	 *
	 * 法宝加成的技能
	 * @param from
	 * @param skillId
	 * @param isDefensible
	 * @param timesLimit
	 * @return
	 */
	public static BattleSkill instanceSkill(int from, int skillId, boolean isDefensible, TimesLimit timesLimit) {
		BattleSkill skill=new BattleSkill();
		skill.setId(skillId);
		skill.setFrom(from);
		skill.setDefensible(isDefensible);
		skill.setTimesLimit(timesLimit);
		return skill;
	}
	/**
	 *
	 * 初始化 属性克制技能
	 * @return
	 */
	public static BattleSkill instanceAttributeRestraintSkill(int id) {
		BattleSkill skill=new BattleSkill();
		skill.setId(id);
		skill.setDefensible(true);
		skill.setTimesLimit(TimesLimit.oneTimeLimit());
		return skill;
	}


	public BattleSkill(int id, String name, String caption) {
		this.id = id;
	}

	public static BattleSkill getNormalAttackSkill() {
		BattleSkill skill = new BattleSkill(CombatSkillEnum.NORMAL_ATTACK.getValue());
		return skill;
	}

	public static BattleSkill getNormalDefenseSkill() {
		BattleSkill skill = new BattleSkill(CombatSkillEnum.NORMAL_DEFENSE.getValue());
		return skill;
	}

	/**
	 * 技能攻击目标被改变
	 * 
	 * @return
	 */
	public boolean targetChanged() {
		return targetPos > 0;
	}

	public void setTargetPos(int target, int fromSkill) {
		targetPos = target;
		if (changeTargetSkill == null) {
			changeTargetSkill = new ArrayList<Integer>();
		}
		changeTargetSkill.add(fromSkill);
	}

	public boolean changeTargetPosFromSkill(int skillid) {
		if (changeTargetSkill == null) {
			return false;
		}
		return changeTargetSkill.contains(skillid);
	}
	public boolean isEffective() {
		// 普通防守技能永远有效
		if (id == CombatSkillEnum.NORMAL_DEFENSE.getValue()) {
			return true;
		}
		return timesLimit.isRoundEffective();
	}

	/**
	 * 是否被永久禁用
	 * 
	 * @return
	 */
	public boolean isForbid() {
		return timesLimit.isForbid();
	}

	public void roundReset() {
		timesLimit.roundReset();
	}

	/**
	 * 移除未使用的临时增加的单回合技能 <br>
	 * <font color="red">如紫金钵盂 只作用一回合，当回合结束未使用添加的飞行技能则清除</font>
	 */
	public void removeTempOneRoundSkill() {
		timesLimit.roundReset();
	}


	/**
	 * 是否是[飞行]技能
	 * 
	 * @return
	 */
	public boolean isFlySkill() {
		return CombatSkillEnum.FX.getValue() == id && timesLimit.hasPerformTimes();
	}

	/**
	 * 是否是[王者]技能
	 * 
	 * @return
	 */
	public boolean isKingSkill() {
		return id == CombatSkillEnum.WZAI.getValue() || id == CombatSkillEnum.WZ.getValue();
	}
	
	public boolean isAIKingSkill() {
		return id == CombatSkillEnum.WZAI.getValue();
	}

	/**
	 * 是否有[疾驰]技能
	 */
	public boolean isJiChiSkill() {
		return CombatSkillEnum.JC.getValue() == id;
	}

	/**
	 * 是否有[钻地4302]技能
	 */
	public boolean isZuanDiSkill() {
		return CombatSkillEnum.ZD.getValue() == id;
	}

	public void addPerformTimes(){
		this.performTimes++;
	}

	public boolean ifParent(int skillId){
		return parent!=null && parent.intValue()==skillId;
	}
}
