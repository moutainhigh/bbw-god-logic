package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.SectionSkills;
import com.bbw.god.game.config.card.CardEnum;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 隐藏伏虎 3502：每回合，优先于其他回合技能发动，若该回合与玄坛黑虎同场，则本回合获得其所有非上场技能。
 * （1）该技能为隐藏技能，通常情况下无法直接获得，也不进行展示。作为【伏虎 3501】的自带技能存在。
 * （2）该技能为新的施法阶段“先制法术效果”：该阶段的在上场技能之后，法术效果之前。
 * （3）该技能需要在玄坛黑虎与神·赵公明同场时才会触发，触发后会将玄坛黑虎上的非上场技能复制给神·赵公明。复制的技能会在回合结束时消失。
 * （4）若场上同时存在多张玄坛黑虎，则会将所有玄坛黑虎的技能复制给神·赵公明。复制的技能无法重复。
 *
 * @author: suhq
 * @date: 2022/1/17 4:08 下午
 */
@Service
public class BattleSkill3502 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.YIN_CANG_FU_HU.getValue();
	/** 针对该技能的非上场技 */
	private static final List<Integer> NOT_DEPLOY_SKILLS = new ArrayList<>();

	static {
		SectionSkills[] sectionSkills = {
				SectionSkills.DEPLOY_END_SKILLS_0, SectionSkills.DEPLOY_END_SKILLS_1,
				SectionSkills.PRIORITY1_SKILLS, SectionSkills.DEPLOY_SKILLS,
				SectionSkills.ATTRIBUTE_RESTRAINT_SKILLS, SectionSkills.NORMAL_ATTACK_SKILLS,
				SectionSkills.NORMAL_HITTED_SKILLS};
		for (SectionSkills sectionSkill : sectionSkills) {
			for (int skill : sectionSkill.getSkills()) {
				NOT_DEPLOY_SKILLS.add(skill);
			}
		}
	}

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action action = new Action();
		List<BattleCard> playingCards = psp.getMyPlayingCards(true);
		Optional<BattleCard> xianTHHOp = playingCards.stream().filter(tmp -> CardEnum.XIAN_THH.getCardId() == tmp.getImgId()).findFirst();
		if (!xianTHHOp.isPresent()) {
			return action;
		}
		List<BattleSkill> skills = xianTHHOp.get().getSkills();
		List<BattleSkill> skillsToAdd = skills.stream().filter(tmp -> !NOT_DEPLOY_SKILLS.contains(tmp.getId())).collect(Collectors.toList());
		if (ListUtil.isEmpty(skillsToAdd)) {
			return action;
		}
		int seq = psp.getNextAnimationSeq();
		BattleCard performCard = psp.getPerformCard();
		BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(getMySkillId(), performCard.getPos());
		effect.setSequence(seq);
		for (BattleSkill skill : skillsToAdd) {
			if (performCard.existSkill(skill.getId())) {
				continue;
			}
			effect.addSkill(skill.getId(), TimesLimit.oneTimeLimit());
		}
		if (ListUtil.isNotEmpty(effect.getEffectLimits())){
			action.addEffect(effect);
		}
		return action;
	}
}
