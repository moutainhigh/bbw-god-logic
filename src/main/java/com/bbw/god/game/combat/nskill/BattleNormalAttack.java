package com.bbw.god.game.combat.nskill;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardStatus;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.SkillSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lwb
 * @date 2019年8月24日
 * @version 1.0
 */
public abstract class BattleNormalAttack extends BattleSkillService {
	public void getNormalAttackEffect(PerformSkillParam psp, Action ar) {
		// 执行卡牌延迟 攻击buff
		List<Integer> extraSkillEffectskillIds = getLastingNormalAttackBuff(psp);
		// 执行普攻
		extraSkillEffectskillIds.add(getMySkillId());
		BattleCard performCard = psp.getPerformCard();
		int targetPos = -1;
		Optional<BattleSkill> skill = performCard.getSkill(CombatSkillEnum.NORMAL_ATTACK.getValue());
		if (skill.isPresent() && skill.get().targetChanged()) {
			targetPos = skill.get().getTargetPos();
		}
		Optional<BattleCard> targetCard;
		//嘲讽吸引目标 不包括云台
		List<BattleCard> chaoFengCards = psp.getOppoPlayingCards(CombatSkillEnum.CHAO_FENG.getValue(), false);
		if (ListUtil.isNotEmpty(chaoFengCards)) {
			targetCard = Optional.of(chaoFengCards.get(chaoFengCards.size() - 1));
			//修正技能目标位置，保证拥有钻地+龙息的卡牌可以正常发动
			skill.get().setTargetPos(targetCard.get().getPos());
			targetPos = -1;
			//触发 补充动画
			for (BattleCard chaoFengCard : chaoFengCards) {
				AnimationSequence amin = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), CombatSkillEnum.CHAO_FENG.getValue(), chaoFengCard.getPos());
				ar.addClientAction(amin);
			}
			//修正没有弹出钻地和地劫没有弹字体动画的问题
			if (getMySkillId() == CombatSkillEnum.ZD.getValue() || getMySkillId() == CombatSkillEnum.DJ.getValue()) {
				AnimationSequence amin = ClientAnimationService.getSkillAction(psp.getCombat().getAnimationSeq(),
						getMySkillId(), performCard.getPos());
				ar.addClientAction(amin);
			}

		} else {
			targetCard = psp.getFaceToFaceCard();
		}
		if (CombatSkillEnum.TIAO_BO.getValue()==targetPos){
			//挑拨随机选择一个不含云台的友方目标
			List<BattleCard> cards = psp.getMyPlayingCards(false);
			cards = cards.stream().filter(p -> p.getPos() != performCard.getPos()).collect(Collectors.toList());
			if (!cards.isEmpty()) {
				targetPos = PowerRandom.getRandomFromList(cards).getPos();
			} else {
				targetPos = -1;
			}
			performCard.getNormalAttackSkill().setTargetPos(targetPos);
		}
		if (targetPos < 0 || (targetPos != 10 && targetPos != 1010 && psp.getCombat().getBattleCardByPos(targetPos) == null)) {
			// 确认攻击目标
			int attackPos = -1;
			if (targetCard.isPresent()) {
				attackPos = targetCard.get().getPos();
			} else {
				attackPos = PositionService.getZhaoHuanShiPos(psp.getOppoPlayer().getId());
			}
			targetPos = attackPos;
		}
		// 开始进行伤害计算
		int seq = psp.getNextAnimationSeq();
		CardValueEffect addtionEffeck = CardValueEffect.getNormalAttackEffect(skill.get().getId(), targetPos);
		addtionEffeck.setSourcePos(psp.getPerformCard().getPos());
		addtionEffeck.setSequence(seq);
		int totalAtk = psp.getPerformCard().getAtk();
		addtionEffeck.addExtraSkillEffects(extraSkillEffectskillIds);
		// 开始执行攻击
		if (isForeverHarm(performCard)) {
			addtionEffeck.setRoundHp(-totalAtk);
		} else {
			addtionEffeck.setHp(-totalAtk);
		}
		addtionEffeck.setParticleSkill(true);
		ar.addEffect(addtionEffeck);
		// 限制物理攻击技能
		SkillSection normalAttackSection = SkillSection.getNormalAttackSection();
		List<BattleSkill> normalAttackSkills = psp.getPerformCard().getEffectiveSkills(normalAttackSection);
		for (BattleSkill normalAttack : normalAttackSkills) {
			normalAttack.getTimesLimit().forbidOneRound(-1);
		}
	}
	/**
	 * 是否携带销魂buff=》攻击造成的伤害 为永久伤害
	 *
	 * @return
	 */
	public boolean isForeverHarm(BattleCard card) {
		for (CardValueEffect effect : card.getRoundDelayEffects()) {
			if (CombatSkillEnum.XH.getValue() == effect.getSourceID()) {
				return true;
			}
		}
		return false;
	}

	public List<Integer> getLastingNormalAttackBuff(PerformSkillParam psp) {
		Set<BattleCardStatus> cardStatus = psp.getPerformCard().getStatus();
		List<Integer> skillIds = new ArrayList<Integer>();
		skillIds.add(CombatSkillEnum.NORMAL_ATTACK.getValue());
		for (BattleCardStatus status : cardStatus) {
			if (status.isNormalAttackType()) {
				skillIds.add(status.getSkillID());
			}
		}
		return skillIds;
	}
}