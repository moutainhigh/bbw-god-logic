package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.RunesEnum;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * 解封：上场时，解除我方全体卡牌封禁状态。
 * 
 */
@Service
public class BattleSkill1016 extends BattleSkillService {

	private static final int SKILL_ID = 1016;
	//可以被解封的：封咒，禁术，迷魂阵，蚀月
	private static final int[] CLEAR_SKILL_IDS = {3131, 1102, 2005, 3147, 1104};
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		SkillSection shangChangSkill = SkillSection.getDeploySection();
		for (BattleCard card:psp.getPerformPlayer().getPlayingCards()){
			if (card==null){
				continue;
			}
			card.setStatus(card.getStatus().stream().filter(p->!check(p.getSkillID())).collect(Collectors.toSet()));
			for (BattleSkill skill:card.getSkills()){
				int from = skill.getTimesLimit().getBanFrom();
				if (!check(from)) {
					continue;
				}
				if (!psp.getPerformPlayer().hasBuff(RunesEnum.TIAN_FU) && (shangChangSkill.contains(skill.getId()) || skill.getId() == CombatSkillEnum.JINS.getValue())) {
					//没有释放次数的上场技能 不能被解封
					continue;
				}
				if (skill.getPerformTimes() > 0 && skill.getId() == CombatSkillEnum.CS.getValue()) {
					continue;
				}
				if (check(from)) {
					skill.setTimesLimit(TimesLimit.noLimit());
				}
			}
		}
		AnimationSequence as = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), CombatSkillEnum.JIE_FEN.getValue(), psp.getPerformCard().getPos());
		ar.addClientAction(as);
		return ar;
	}

	private boolean check(int fromId){
		for (int id:CLEAR_SKILL_IDS){
			if (fromId==id){
				return true;
			}
		}
		return false;
	}
}
