package com.bbw.god.game.combat.data.skill;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 某种类型的技能ID限定
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-04 18:45
 */
@Data
public class SkillSection {
	private static Map<String,SkillSection> SKILL_SECTIONS = new HashMap<>();
	private int[] skills;
	private boolean autoAnimation = true;// 最大ID，包含
	private BattleSkillType belongTo=BattleSkillType.DEFAULT;

	public static SkillSection getInstance(SectionSkills sectionSkills) {
		if (!SKILL_SECTIONS.containsKey(sectionSkills.getSection())){
			SkillSection section = new SkillSection();
			section.setSkills(sectionSkills.getSkills());
			SKILL_SECTIONS.put(sectionSkills.getSection(), section);
		}
		return SKILL_SECTIONS.get(sectionSkills.getSection());
	}

	public static SkillSection getInstance(SectionSkills sectionSkills,BattleSkillType type) {
		if (!SKILL_SECTIONS.containsKey(sectionSkills.getSection())){
			SkillSection section = new SkillSection();
			section.setSkills(sectionSkills.getSkills());
			section.setBelongTo(type);
			SKILL_SECTIONS.put(sectionSkills.getSection(), section);
		}
		return SKILL_SECTIONS.get(sectionSkills.getSection());
	}

	public static SkillSection getInstance(SectionSkills sectionSkills,boolean autoAnimation) {
		if (!SKILL_SECTIONS.containsKey(sectionSkills.getSection())){
			SkillSection section = new SkillSection();
			section.setSkills(sectionSkills.getSkills());
			section.setAutoAnimation(autoAnimation);
			SKILL_SECTIONS.put(sectionSkills.getSection(), section);
		}
		return SKILL_SECTIONS.get(sectionSkills.getSection());
	}

	/**
	 * 在技能限制范围内
	 *
	 * @param skillId
	 * @return
	 */
	public boolean contains(int skillId) {
		for (int id : skills) {
			if (id == skillId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取组合技【2001-2099】
	 *
	 * @return
	 */
	public static SkillSection getGroupSkillSection() {
		return getInstance(SectionSkills.GROUP_SKILLS);
	}

	/**
	 * 上场技能[1001, 1099]
	 *
	 * @return
	 */
	public static SkillSection getDeploySection() {
		return getInstance(SectionSkills.DEPLOY_SKILLS);
	}

	/**
	 * 获取给别人的盾牌类技能
	 *
	 * @return
	 */
	public static SkillSection getShieldSection() {
		return getInstance(SectionSkills.SHIELD_SKILLS);
	}

	/**
	 * 共享的防御技能
	 *
	 * @return
	 */
	public static SkillSection getShareDefenseSection() {
		return getInstance(SectionSkills.SHARE_DEFENCE_SKILLS,BattleSkillType.DEFENSE);
	}

	/**
	 * 共享完全免疫防御
	 *
	 * @return
	 */
	public static SkillSection getSkillShareDefenseSection() {
		return getInstance(SectionSkills.SHARE_ABSOLUTE_DEFENCE_SKILLS, BattleSkillType.DEFENSE);
	}

	/**
	 * 回合布阵结束阶段0
	 *
	 * @return
	 */
	public static SkillSection getEndDeploySection0() {
		return getInstance(SectionSkills.DEPLOY_END_SKILLS_0);
	}

	/**
	 * 回合布阵结束后阶段1
	 *
	 * @return
	 */
	public static SkillSection getEndDeploySection1() {
		return getInstance(SectionSkills.DEPLOY_END_SKILLS_1);
	}

	/**
	 * 先制技能[2201,2299]
	 *
	 * @return
	 */
	public static SkillSection getPriority1Section() {
		return getInstance(SectionSkills.PRIORITY1_SKILLS);
	}

	/**
	 * 先制法术效果[3501,3599]
	 *
	 * @return
	 */
	public static SkillSection getPrioritySkillAttackSection() {
		return getInstance(SectionSkills.PRIORITY_SPELL_ATTACK_SKILLS);
	}

	/**
	 * 法术攻击[3101,3199]
	 *
	 * @return
	 */
	public static SkillSection getSkillAttackSection() {
		return getInstance(SectionSkills.SPELL_ATTACK_SKILLS);
	}

	/**
	 * 法术反击[3201,3299]
	 *
	 * @return
	 */
	public static SkillSection getFightBackSection() {
		return getInstance(SectionSkills.SPECLL_FIGHT_BACK_SKILLS);
	}

	/**
	 * 法术反制（受到法术效果后触发，该阶段技能可同时触发）
	 *
	 * @return
	 */
	public static SkillSection getSpecllCounterSection() {
		return getInstance(SectionSkills.SPECLL_COUNTER_SKILLS);
	}

	/**
	 * 法术防御[3001,3099]+法宝添加防御技能[8001,8099]
	 *
	 * @return
	 */
	public static SkillSection getSkillDefenseSection() {
		return getInstance(SectionSkills.SPELL_DEFENCE_SKILLS, BattleSkillType.DEFENSE);
	}


	/**
	 * 法术效果削弱
	 * @return
	 */
	public static SkillSection getSkillEffectWeakenSection(){
		return getInstance(SectionSkills.SPELL_EFFECT_WEAKEN_SKILLS,BattleSkillType.DEFENSE);
	}
	/**
	 * 死亡技能[1201,1299]
	 *
	 * @return
	 */
	public static SkillSection getDieSection() {
		return getInstance(SectionSkills.DIE_SKILLS);
	}

	/**
	 * 死亡时的攻击技能
	 *
	 * @return
	 */
	public static SkillSection getDyingAttakSection() {
		return getInstance(SectionSkills.DYING_ATTACK_SKILLS);
	}

	/**
	 * 死亡时的增益技能 如 长生
	 *
	 * @return
	 */
	public static SkillSection getDyingBenefitSection() {
		return getInstance(SectionSkills.DYING_BENEFIT_SKILLS);
	}

	/**
	 * 死亡后的增益技能 如 复活
	 * 进入坟场
	 * @return
	 */
	public static SkillSection getInToDiscardSection() {
		return getInstance(SectionSkills.INTO_DISCARD_SKILLS);
	}

	// --------------------------------------物理攻击----------------------------------------

	/**
	 * 物理攻击buff_0
	 *
	 * @return
	 */
	public static SkillSection getNormalBuffSection0() {
		return getInstance(SectionSkills.NORMAL_BUFF_SKILLS_0);
	}

	/**
	 * 物理攻击buff_1
	 *
	 * @return
	 */
	public static SkillSection getNormalBuffSection1() {
		return getInstance(SectionSkills.NORMAL_BUFF_SKILLS_1);
	}

	/**
	 * 物理buff防御[4001, 4099]
	 *
	 * @return
	 */
	public static SkillSection getNormalBuffDefenseSection() {
		return getInstance(SectionSkills.NORMAL_BUFF_DEFENCE_SKILLS,BattleSkillType.DEFENSE_NORMAL_BUFF);
	}

	/**
	 * 物理攻击之前，法术之后[3901, 3999]
	 *
	 * @return
	 */
	public static SkillSection getBeforeNormalSection() {
		return getInstance(SectionSkills.BEFORE_NORMAL_SKILLS);
	}

	/**
	 * 物理攻击先手[4301, 4399]
	 *
	 * @return
	 */
	public static SkillSection getNormalPriority1Section() {
		return getInstance(SectionSkills.NORMAL_PRIORITY1_SKILLS);
	}

	/**
	 * 物理攻击技能[4401, 4499]
	 *
	 * @return
	 */
	public static SkillSection getNormalAttackSection() {
		return getInstance(SectionSkills.NORMAL_ATTACK_SKILLS,false);
	}

	/**
	 * 第二个物理攻击阶段 ： 连击
	 *
	 * @return
	 */
	public static SkillSection getNormalAttackSection2() {
		return getInstance(SectionSkills.NORMAL_ATTACK2_SKILLS);
	}

	/**
	 * 物理防御前
	 *
	 * @return
	 */
	public static SkillSection getBeforeNormalDefenseSection() {
		return getInstance(SectionSkills.BEFORE_NORMAL_DEFENCE_SKILLS, false);
	}

	/**
	 * 物理防御
	 *
	 * @return
	 */
	public static SkillSection getNormalDefenseSection() {
		return getInstance(SectionSkills.NORMAL_DEFENCE_SKILLS, false);
	}

	/**
	 * 物理防御后
	 *
	 * @return
	 */
	public static SkillSection getNormalDefenseAfterSection() {
		return getInstance(SectionSkills.NORMAL_DEFENCE_AFTER_SKILLS, false);
	}

	/**
	 * 物理反制（受到物理攻击后触发，该阶段技能可同时触发）
	 *
	 * @return
	 */
	public static SkillSection getPhysicalCounterSection() {
		return getInstance(SectionSkills.PHYSICAL_COUNTER_SKILLS, false);
	}

	/**
	 * 物理反制后
	 *
	 * @return
	 */
	public static SkillSection getAfterPhysicalCounterSection() {
		return getInstance(SectionSkills.AFTER_PHYSICAL_COUNTER_SKILLS, false);
	}


	/**
	 * 被物理击中[4599]
	 *
	 * @return
	 */
	public static SkillSection getNormalBeHitSectionSection() {
		return getInstance(SectionSkills.NORMAL_HITTED_SKILLS,false);
	}

	/**
	 * 物理攻击后[4201,4299]
	 *
	 * @return
	 */
	public static SkillSection getNormalEndAttackSection() {
		return getInstance(SectionSkills.NORMAL_END_ATTACK_SKILLS);
	}

	/**
	 * 是否克制技能
	 * @param skillId
	 * @return
	 */
	public static boolean isAttributerestraint(int skillId) {
		SkillSection section = getInstance(SectionSkills.ATTRIBUTE_RESTRAINT_SKILLS);
		return section.contains(skillId);
	}

	/**
	 * 只执行一次的技能
	 *
	 * @return
	 */
	public static SkillSection getPerformOneTimeSection() {
		return getInstance(SectionSkills.PERFORM_ONE_SKILLS);
	}

	public static SkillSection getZhsSection() {
		return getInstance(SectionSkills.ZHS_SKILLS);
	}
	public static SkillSection getZhsAttakAtferSection() {
		return getInstance(SectionSkills.ZHS_ATTACK_AFTER_SKILLS);
	}
	/**
	 * 物理溅射
	 *
	 * @return
	 */
	public static SkillSection getPhysicalParticl() {
		return getInstance(SectionSkills.PHYSICAL_PARTICLE_SKILLS);
	}

	/**
	 * 所有溅射技能
	 * @return
	 */
	public static SkillSection getAllParticleSkills(){
		return getInstance(SectionSkills.ALL_PARTICLE_SKILLS);
	}

	/**
	 * 获取中毒效果的技能、法宝：瘟君、入痘、百毒豆
	 *
	 * @return
	 */
	public static SkillSection getZhongDuEffectSection() {
		return getInstance(SectionSkills.ZHONG_DU_EFFECT_SKILLS);
	}

	/**
	 * 所有剑的法宝
	 *
	 * @return
	 */
	public static SkillSection getAllJianWeapons() {
		return getInstance(SectionSkills.ALL_JIAN_SKILLS);
	}


	/**
	 * 护卫不能生效的BUFF
	 * @return
	 */
	public static SkillSection getHuWeiCantDefenseBuff(){
		return getInstance(SectionSkills.HU_WEI_CANT_DEFENCE_SKILLS);
	}

	/**
	 * 封禁类技能:禁术 封咒 蚀月 迷魂阵
	 */
	public static SkillSection getFenJinSkills(){
		return getInstance(SectionSkills.FENG_JIN_SKILLS);
	}
}