package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.SkillSection;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * 蚀月（3147）：每回合随机封印对方一张卡牌的一项非上场技能。（可被解封破解）
 *
 */
@Service
public class BattleSkill3147 extends BattleSkillService {
	private static final int SKILL_ID = 3147;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//蚀月（3147）：每回合随机封印对方一张卡牌的一项非上场技能。无法封印蚀月与解封技能。（可被解封破解）
		List<BattleCard> cards=psp.getOppoPlayingCards(true);
		if (ListUtil.isEmpty(cards)) {
			return ar;
		}
		BattleCard targetCard= PowerRandom.getRandomFromList(cards);
		BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(SKILL_ID, targetCard.getPos());
		effect.setLastRound(100);
		SkillSection skillSection=SkillSection.getDeploySection();
		//限制使用技能
		List<BattleSkill> battleSkillList=new ArrayList<>();
		for (BattleSkill skill : targetCard.excludeDerivativeAllSkills()) {
			if (skill.getTimesLimit().isForbid() || skillSection.contains(skill.getId()) || !canBan(skill.getId())){
				continue;
			}
			battleSkillList.add(skill);
		}
		if (!battleSkillList.isEmpty()){
			effect.forbid(PowerRandom.getRandomFromList(battleSkillList),getMySkillId());
			effect.setSequence(psp.getNextAnimationSeq());
			ar.addEffect(effect);
		}
		return ar;
	}

	/**
	 * 不能禁用禁术 蚀月、哮天、飞行、疾驰、影随
	 * @param skillID
	 * @return
	 */
	public static boolean  canBan(int skillID){
		if (skillID==CombatSkillEnum.FX.getValue()){
			return false;
		}
		if (skillID==CombatSkillEnum.JC.getValue()){
			return false;
		}
		if (skillID==CombatSkillEnum.YING_SUI.getValue()){
			return false;
		}
		if (skillID==CombatSkillEnum.JINS.getValue()){
			return false;
		}
		if (skillID==CombatSkillEnum.XIAO_TIAN.getValue()){
			return false;
		}
		if (skillID==CombatSkillEnum.JI_BIAN.getValue()){
			return false;
		}
		if (skillID==CombatSkillEnum.SY.getValue()){
			return false;
		}
		return true;
	}

	@Override
	public Action buildEffects(BattleCard target, PerformSkillParam psp){
		return buildEffects(target, psp,false);
	}

	public Action buildEffects(BattleCard target, PerformSkillParam psp,boolean includeSelf) {
		Action ar = new Action();
		//蚀月（3147）：每回合随机封印对方一张卡牌的一项非上场技能。无法封印蚀月与解封技能。（可被解封破解）
		BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(SKILL_ID, target.getPos());
		effect.setLastRound(100);
		SkillSection skillSection=SkillSection.getDeploySection();
		//限制使用技能
		List<BattleSkill> battleSkillList=new ArrayList<>();
		for (BattleSkill skill : target.getSkills()) {
			if ((!includeSelf && skill.getId()==getMySkillId()) || skill.getTimesLimit().isForbid() || skillSection.contains(skill.getId()) || !canBan(skill.getId())){
				continue;
			}
			battleSkillList.add(skill);
		}
		if (!battleSkillList.isEmpty()){
			effect.forbid(PowerRandom.getRandomFromList(battleSkillList),getMySkillId());
			effect.setSequence(psp.getNextAnimationSeq());
			ar.addEffect(effect);
		}
		return ar;
	}

	/**
	 * 对目标位置 释放 蚀月
	 * @return
	 */
	@Override
	public List<Effect> attackTargetPosByCopyEffects(PerformSkillParam fromPsp, int attkPos) {
		List<Effect> effects = new ArrayList<>();
		BattleCard battleCard = fromPsp.getCombat().getBattleCardByPos(attkPos);
		if (null == battleCard) {
			// 说明目标卡牌已死亡 或者 已被其他位置的反弹技能致死了
			return effects;
		}
		Action action = buildEffects(battleCard, fromPsp,true);
		effects.addAll(action.getEffects());
		for (Effect effect : effects) {
			effect.setSequence(fromPsp.getNextAnimationSeq());
		}
		return effects;
	}
}
