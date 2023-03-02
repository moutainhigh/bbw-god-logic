package com.bbw.god.game.combat.nskill;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardStatus;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * 龙息 攻击敌方卡牌的同时，破除其左右卡牌本次攻击伤害50%的防御值，每升一阶破除左右卡牌增加5%。
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-09 11:14
 */
@Service
public class BattleSkill4111 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.LX.getValue();// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action junShiBuffAction(PerformSkillParam psp) {
		// 军师位 不加成
		return attack(psp);
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		BattleCard performCard = psp.getPerformCard();

		//嘲讽吸引目标
		List<BattleCard> chaoFengCards = psp.getOppoPlayingCards(CombatSkillEnum.CHAO_FENG.getValue(), true);
		if (ListUtil.isNotEmpty(chaoFengCards)) {
			Optional<BattleCard> targetCard = Optional.of(chaoFengCards.get(chaoFengCards.size() - 1));
			//触发 补充动画
//			for (BattleCard chaoFengCard : chaoFengCards) {
//				AnimationSequence amin = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), CombatSkillEnum.CHAO_FENG.getValue(), chaoFengCard.getPos());
//				ar.addClientAction(amin);
//			}
			List<CardValueEffect> effects = getAttackEffects(targetCard.get(), psp.getOppoPlayer());
			if (effects.isEmpty()) {
				// 目标卡左右都没有卡牌
				return ar;
			}
			BattleSkillEffect effect = BattleSkillEffect.getLastPerformSkillEffect(BattleCardStatus.StatusEffectType.NORMAL_ATTACK, SKILL_ID, psp.getPerformCard().getPos());
			// 在这里不设置溅射，防止龙息对左右牌的作用触发2次
//			effect.setParticleSkill(true);
			ar.addEffect(effect);
			return ar;
		}

		// 攻击目标被改变
		Optional<BattleSkill> skill = performCard.getSkill(CombatSkillEnum.NORMAL_ATTACK.getValue());
		if (skill.isPresent() && skill.get().targetChanged()) {
			int targetPos = skill.get().getTargetPos();
			if (targetPos != CombatSkillEnum.TIAO_BO.getValue()) {
				List<CardValueEffect> effects = new ArrayList<>();
				if (!PositionService.isZhaoHuanShiPos(targetPos)) {
					BattleCard card = psp.getCombat().getBattleCard(targetPos);
					if (card == null) {
						return ar;
					}
					Player targetPlayer = psp.getOppoPlayer();
					if (psp.getPerformPlayerId().equals(PositionService.getPlayerIdByPos(targetPos))) {
						targetPlayer = psp.getPerformPlayer();
					}
					effects = getAttackEffects(card, targetPlayer);
				}
				if (effects.isEmpty()) {
					// 目标卡左右都没有卡牌
					return ar;
				}
			}
			BattleSkillEffect effect = BattleSkillEffect.getLastPerformSkillEffect(BattleCardStatus.StatusEffectType.NORMAL_ATTACK, SKILL_ID, psp.getPerformCard().getPos());
			ar.addEffect(effect);
			return ar;
		}
		// 钻地4302、地劫4303 可以无视自己的对位，率先攻击敌方当前防御最弱的卡牌。
		if (performCard.hasZuanDiSkill() || performCard.hasEffectiveSkill(CombatSkillEnum.DJ.getValue())) {
			List<BattleCard> oppoPlayingCards = psp.getOppoPlayingCards(false);
			Optional<BattleCard> minHp = oppoPlayingCards.stream().min(Comparator.comparing(BattleCard::getHp));
			if (!minHp.isPresent()) {
				return ar;
			}
			List<CardValueEffect> effects = getAttackEffects(minHp.get(), psp.getOppoPlayer());
			if (effects.isEmpty()) {
				// 目标卡左右都没有卡牌
				return ar;
			}
			BattleSkillEffect effect = BattleSkillEffect.getLastPerformSkillEffect(BattleCardStatus.StatusEffectType.NORMAL_ATTACK, SKILL_ID, psp.getPerformCard().getPos());
			ar.addEffect(effect);
			return ar;
		}
		// 对位卡牌不存在 则不发生溅射效果
		if (!psp.getFaceToFaceCard().isPresent()) {
			return ar;
		}
		BattleSkillEffect effect = BattleSkillEffect.getLastPerformSkillEffect(BattleCardStatus.StatusEffectType.NORMAL_ATTACK, SKILL_ID, psp.getPerformCard().getPos());
		ar.addEffect(effect);
		return ar;
	}

	@Override
	public List<Effect> attakParticleffects(PerformSkillParam psp) {
		if (psp.getPerformCard() == null) {
			return new ArrayList<>();
		}
		//攻击敌方卡牌的同时，破除其左右卡牌本次攻击伤害50%的防御值，每升一阶破除左右卡牌增加5%。
		Optional<BattleSkill> skill = psp.getPerformCard().getSkill(CombatSkillEnum.NORMAL_ATTACK.getValue());
		int targetPos = psp.getReceiveEffect().getTargetPos();
		Player targetPlayer = psp.getOppoPlayer();
		if (skill.isPresent() && skill.get().targetChanged()) {
			targetPos = skill.get().getTargetPos();
			if (psp.getPerformPlayerId().equals(PositionService.getPlayerIdByPos(targetPos))) {
				targetPlayer = psp.getPerformPlayer();
			}
		}
		List<CardValueEffect> effects = getAttackEffects(psp.getCombat().getBattleCard(targetPos), targetPlayer);
		// 计算伤害
		// 获取增益减益后的攻击力加成
//		int addtionAtk = psp.getPerformCard().getRoundDelayEffects().stream().collect(Collectors.summingInt(CardValueEffect::getAtk));
		// 攻击力算法：（卡牌自身攻击力+额外加成）*（0.5+0.05*卡牌阶数）
		int totalHp = -this.getInt((psp.getPerformCard().getAtk()) * (0.5 + 0.05 * psp.getPerformCard().getHv()));
		boolean isForeverHarm = psp.isForeverHarm();
		List<Effect> res = new ArrayList<>();
		int seq=psp.getNextAnimationSeq();
		for (CardValueEffect ef : effects) {
			ef.setSequence(seq);
			ef.setExtraSkillEffect(psp.getAttackEffect().getExtraSkillEffect());
			ef.setSourcePos(psp.getPerformCard().getPos());
			if (isForeverHarm) {
				ef.setRoundHp(totalHp);
			} else {
				ef.setHp(totalHp);
			}
			res.add(ef);
		}
		return res;
	}

	private List<CardValueEffect> getAttackEffects(BattleCard targetCard, Player player) {
		List<CardValueEffect> effects = new ArrayList<CardValueEffect>();
		if (targetCard==null){
			return effects;
		}
		int index = PositionService.getBattleCardIndex(targetCard.getPos());
		switch (index) {
			case 0 :
				break;
			case 1 :
				if (null != player.getPlayingCards(2)) {
					effects.add(getValueEffect(player, 2));
				}
				break;
			case 2 :
				if (null != player.getPlayingCards(1)) {
					effects.add(getValueEffect(player, 1));
				}
				if (null != player.getPlayingCards(3)) {
					effects.add(getValueEffect(player, 3));
				}
				break;
			case 3 :
				if (null != player.getPlayingCards(2)) {
					effects.add(getValueEffect(player, 2));
				}
				if (null != player.getPlayingCards(4)) {
					effects.add(getValueEffect(player, 4));
				}
				break;
			case 4 :
				if (null != player.getPlayingCards(3)) {
					effects.add(getValueEffect(player, 3));
				}
				if (null != player.getPlayingCards(5)) {
					effects.add(getValueEffect(player, 5));
				}
				break;
			case 5 :
				if (null != player.getPlayingCards(4)) {
					effects.add(getValueEffect(player, 4));
				}
				break;
		}
		return effects;
	}
	private CardValueEffect getValueEffect(Player player, int index) {
		int targetPos = PositionService.getBattleCardPos(player.getId(), index);
		CardValueEffect addtionEffeck = CardValueEffect.getSkillEffect(SKILL_ID, targetPos);
		return addtionEffeck;
	}
}
