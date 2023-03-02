package com.bbw.god.game.combat;

import com.bbw.exception.CoderException;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.AnimationSequence.Animation;
import com.bbw.god.game.combat.data.attack.*;
import com.bbw.god.game.combat.data.attack.Effect.EffectResultType;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.CardMovement;
import com.bbw.god.game.combat.data.param.PerformWeaponParam;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户端动画
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-05 10:27
 */
public class ClientAnimationService {

	public static List<AnimationSequence> getEffectAnimations(List<Effect> effect) {
		List<AnimationSequence> ans = new ArrayList<>();
		if (null == effect || effect.isEmpty()) {
			return ans;
		}
		for (int i = 0; i < effect.size(); i++) {
			AnimationSequence tmp = getEffectAnimation(effect.get(i));
			ans.add(tmp);
		}
		return ans;
	}

	public static AnimationSequence getEffectAnimation(Effect effect) {
		switch (effect.getResultType()) {
		case CARD_VALUE_CHANGE://属性
			return getValueEffectAction(effect.toValueEffect());
		case CARD_POSITION_CHANGE://位置
			return getPositionEffectAction(effect.toPositionEffect());
		case SKILL_STATUS_CHANGE://卡牌技能
			return getStautsEffectAction(effect.toBattleSkillEffect());
		case CARD_CHANGE_TO_CARD:
			return getCardChangeAnimationSequence(effect.toBattleCardChangeEffect());
		default:
			throw CoderException.high("无法处理[" + effect.getResultType() + "]类型的行动效果！");
		}
	}

	/**
	 * 卡牌状态动画
	 */
	public static AnimationSequence getStautsEffectAction(Effect effect) {
		AnimationSequence as = new AnimationSequence(effect.getSequence(), EffectResultType.SKILL_STATUS_CHANGE);
		Animation action = new Animation();
		action.setPos(effect.getTargetPos());
		action.setStatus(effect.getSourceID());
		if (effect.getSourceID() == CombatSkillEnum.SHENG_GUANG.getValue()) {
			action.setStatus(effect.getPerformSkillID());
		} else if (effect.getSourceID() == CombatSkillEnum.MHZ_G.getValue()) {
			action.setStatus(CombatSkillEnum.FZ.getValue());
		}
		as.add(action);
		return as;
	}

	/**
	 * 卡牌 数值变化 效果动画
	 */
	private static AnimationSequence getValueEffectAction(CardValueEffect effect) {
		int atk = effect.getAtk() + effect.getRoundAtk();
		int hp = effect.getHp();
		int roundHp=effect.getRoundHp();
		if (roundHp!=0 && !PositionService.isZhaoHuanShiPos(effect.getTargetPos())){
			hp=hp+roundHp;
			roundHp=0;
		}
		int mp = effect.getMp() + effect.getRoundMp();
		AnimationSequence as = new AnimationSequence(effect);
		Animation action = new Animation();
		action.setPos(effect.getTargetPos());
		as.setSeq(effect.getSequence());
		if (atk != 0) {
			action.setAtk(atk);
		}
		if (0 != hp) {
			action.setHp(hp);
		}
		if (0 != roundHp) {
			action.setRoundHp(roundHp);
			if (0 == hp && PositionService.isZhaoHuanShiPos(effect.getTargetPos())){
				action.setHp(roundHp);
			}
		}
		if (0 != mp) {
			action.setMp(mp);
		}
		as.add(action);
		return as;
	}

	/**
	 * 卡牌 移动 效果动画
	 */
	private static AnimationSequence getPositionEffectAction(CardPositionEffect effect) {
		AnimationSequence as = new AnimationSequence(effect);
		Animation action = new Animation();
		action.setPos1(effect.getFromPos());
		action.setPos2(effect.getToPos());
		as.add(action);
		return as;
	}
	public static AnimationSequence getPositionEffectAction(int resultType,int seq,int fromPos,int toPos) {
		AnimationSequence as = new AnimationSequence();
		as.setType(resultType);
		as.setSeq(seq);
		Animation action = new Animation();
		action.setPos1(fromPos);
		action.setPos2(toPos);
		as.add(action);
		return as;
	}
	/**
	 * 卡牌技能释放动画
	 */
	public static AnimationSequence getSkillAction(int sequence, int skillId, int fromPos, int... toPos) {
		AnimationSequence as = new AnimationSequence(sequence, EffectResultType.PLAY_ANIMATION);
		Animation action = new Animation();
		action.setSkill(skillId);
		action.setPos1(fromPos);
		if (toPos.length > 0) {
			action.setPos2(toPos[0]);
		} else {
			action.setPos2(-1);
		}
		as.add(action);
		return as;
	}

	public static Animation getSkillAnimation(int skillId, int fromPos, int... toPos) {
		Animation action = new Animation();
		action.setSkill(skillId);
		action.setPos1(fromPos);
		if (toPos.length > 0) {
			action.setPos2(toPos[0]);
		} else {
			action.setPos2(-1);
		}
		return action;
	}

	public static AnimationSequence getStatusEffectAnimation(int sequence, int skillId, int pos) {
		AnimationSequence as = new AnimationSequence(sequence, EffectResultType.SKILL_STATUS_CHANGE);
		Animation action = new Animation();
		action.setSkill(skillId);
		action.setPos(pos);
		as.add(action);
		return as;
	}

	/**
	 * 卡牌物理攻击动画
	 */
	public static AnimationSequence getNormalAttackAction(int sequence, int cardPos) {
		AnimationSequence as = new AnimationSequence(sequence, EffectResultType.PLAY_ANIMATION);
		Animation action = new Animation();
		action.setPos1(cardPos);
		action.setSkill(CombatSkillEnum.NORMAL_ATTACK.getValue());
		as.add(action);
		return as;
	}

	/**
	 * 卡牌物理攻击动画
	 */
	public static List<AnimationSequence> getCardMovementActions(int sequence, List<CardMovement> moves) {
		List<AnimationSequence> actions = new ArrayList<>();
		for (int i = 0; i < moves.size(); i++) {
			AnimationSequence as = new AnimationSequence(sequence + i, EffectResultType.CARD_POSITION_CHANGE);
			Animation action = new Animation();
			action.setPos1(moves.get(i).getFromPos());
			action.setPos2(moves.get(i).getToPos());
			as.add(action);
			actions.add(as);
		}
		return actions;
	}
	
	/**
	 * 法宝默认释放动画
	 * @param action
	 * @param combat
	 * @param pwp
	 */
	public static void getWeaponEffectAnimation(Action action,Combat combat,PerformWeaponParam pwp) {
		if (!action.getClientActions().isEmpty()) {
			combat.addAnimations(action.getClientActions());
			return;
		}
		//技能没有发动 不生成动画
		if (!action.getTakeEffect()) {
			return;
		}
		//法宝释放默认播放动画 只需一个即可
		int seq=pwp.getNextAnimationSeq();
		AnimationSequence as = new AnimationSequence(seq, EffectResultType.PLAY_ANIMATION);
 		for(Integer pos:pwp.getMultiplePos()) {
 			//如果法宝作用对象是战场或者手牌上的卡牌 则不实现该Effect
			if (PositionService.posCardHasKingSkill(combat, pos)) {
				continue;
			}
 			Animation animSeq=getSkillAnimation(pwp.getWeaponId(),PositionService.getZhaoHuanShiPos(pwp.getPerformPlayerId()), pos);
 			as.add(animSeq);
 		}
 		combat.addAnimation(as);
	}
	public static AnimationSequence getCardChangeAnimationSequence(BattleCardChangeEffect effect) {
		AnimationSequence as = new AnimationSequence(effect);
		List<BattleCard> cards = effect.getChanges();
		String cardStr = CombatCardTools.getCardStrs(cards, PlayerId.P1);
		Animation at = new Animation();
		if (cardStr != null && cardStr.indexOf("N") == 0) {
			cardStr = cardStr.substring(1);
		}
		at.setCards(cardStr);
		as.add(at);
		return as;
	}

}
