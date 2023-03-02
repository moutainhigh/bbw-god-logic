package com.bbw.god.game.combat.skill;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.TimesLimit;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 祭鞭：上场时，优先于其他上场技能发动，该回合封禁敌方全体卡牌所有技能，一场战斗触发一次，对拥有【王者】的卡牌无效。自带【拦截】。
 * （1）该技能为复合技能。参考【火枪】、【解封】。
 * （2）该技能为封禁类技能，会被【乾坤】、【金身】、【法身】所防御。
 * （3）该技能的上场效果将会优先于【禁术】释放。最高优先级释放顺序：AI王者（1101）＞祭鞭（1104）＞禁术（1102）。
 * 前段效果在战斗中只会触发一次，若卡牌上场时未能正常施法（技能被封禁）则不会将其判定为触发。
 *
 * @author: suhq
 * @date: 2021/12/3 3:46 下午
 */
@Service
public class BattleSkill1104 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.JI_BIAN.getValue();// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		int jiBianEffectTimes = psp.getPerformPlayer().getStatistics().gainSkillEffectTime(CombatSkillEnum.JI_BIAN);
		if (jiBianEffectTimes >= 1) {
			return ar;
		}
		psp.getPerformPlayer().getStatistics().addSkillEffectTime(CombatSkillEnum.JI_BIAN);
		List<BattleCard> oppPlayingCards = psp.getOppoPlayingCards(true);
		if (ListUtil.isEmpty(oppPlayingCards)) {
			return ar;
		}
		AnimationSequence as = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), getMySkillId(), psp.getPerformCard().getPos());
		ar.addClientAction(as);
		int seq = psp.getNextAnimationSeq();
		for (BattleCard card : oppPlayingCards) {
			List<BattleSkill> skills = card.getSkills();
			boolean hasWZ = card.hasKingSkill();
			// 对拥有【王者】的卡牌无效
			if (skills.isEmpty() || hasWZ) {
				continue;
			}
			BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(getMySkillId(), card.getPos());
			for (BattleSkill skill : skills) {
				TimesLimit limt = CloneUtil.clone(skill.getTimesLimit());
				effect.forbidOneRound(skill.getId(), limt, getMySkillId());
				effect.setSequence(seq);
			}
			ar.addEffect(effect);
			AnimationSequence statusEffectAction = ClientAnimationService.getStautsEffectAction(effect);
			statusEffectAction.setSeq(seq);
			ar.addClientAction(statusEffectAction);
		}
		return ar;
	}
}
