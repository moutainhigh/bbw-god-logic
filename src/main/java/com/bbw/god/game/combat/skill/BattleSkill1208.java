package com.bbw.god.game.combat.skill;

import com.bbw.common.CloneUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleCardLocalCacheService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardStatus.StatusEffectType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 殇咒：死亡时，破除致死的卡牌50%永久攻防，并减少敌方召唤师50%当前血量，每阶增加3%数值。
 * （1）破除致死卡牌的永久攻防时，选取的参数对象与【死咒】一致。
 *
 * @author: suhq
 * @date: 2022/5/9 2:23 下午
 */
@Service
public class BattleSkill1208 extends BattleDieSkill {
	private static final int SKILL_ID = CombatSkillEnum.SHANG_ZHOU.getValue();

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action performSkill(PerformSkillParam psp) {
		Action action = new Action();
		// 被击退后，致死的卡牌的攻防永久减半，每升一阶攻防再降3%。
		BattleCard targetCard = null;
		// 组合技致死则 随机反弹到一张卡牌
		if (psp.getReceiveEffect().isFromGroupSkill()) {
			int groupSkillId = psp.getReceiveEffectSkillId();
			List<BattleCard> cards = psp.getOppoPlayer().getPlayingCardsByGroupSkillId(groupSkillId);
			BattleCard target = PowerRandom.getRandomFromList(cards);
		} else {
			// 单体致死
			Optional<BattleCard> sourceCard = psp.getEffectSourceCard();
			if (!sourceCard.isPresent()) {
				// 伤害来源卡不存在 则不发动技能
				return action;
			}
			targetCard = sourceCard.get();
		}
		if (null == targetCard) {
			return action;
		}
		int targetPos = targetCard.getPos();
		BattleSkillEffect effect = BattleSkillEffect.getLastPerformSkillEffect(StatusEffectType.ROUND_END, SKILL_ID, targetPos);
		BattleCard performCardClone = CloneUtil.clone(psp.getPerformCard());
		effect.setSouceCard(performCardClone);
		action.addEffect(effect);
		//加入临时本地缓存，便于后续目标卡牌阵亡后仍然能触发对召唤师的效果
		BattleCard cardToCache = CloneUtil.clone(targetCard);
		cardToCache.addCardStatus(StatusEffectType.ROUND_END, effect.getLastRound(), SKILL_ID, performCardClone);
		BattleCardLocalCacheService.cacheCardToDoRoundEnd(psp.getCombat(), psp.getOppoPlayer(), cardToCache);
		return action;
	}

	@Override
	public List<Effect> attakRoundLasting(PerformSkillParam psp) {
		List<Effect> effects = new ArrayList<>();
		BattleCard performCard = psp.getPerformCard();
		Optional<BattleCard> faceToFaceCard = psp.getFaceToFaceCard();
		if (faceToFaceCard.isPresent()) {
			BattleCard targetCard = faceToFaceCard.get();
			Double atkValue = targetCard.getRoundAtk() * (0.5 + 0.03 * performCard.getHv());
			int roundAtk = this.getInt(atkValue);
			Double hpValue = targetCard.getRoundHp() * (0.5 + 0.03 * performCard.getHv());
			int roundHp = this.getInt(hpValue);
			CardValueEffect cardEffect = CardValueEffect.getSkillEffect(SKILL_ID, targetCard.getPos());
			cardEffect.setRoundAtk(-roundAtk);
			cardEffect.setRoundHp(-roundHp);
			effects.add(cardEffect);
		}

		int zhsPos = PositionService.getZhaoHuanShiPos(psp.getOppoPlayer().getId());
		CardValueEffect playerEffect = CardValueEffect.getSkillEffect(SKILL_ID, zhsPos);
		double playerRoundHp = psp.getOppoPlayer().getHp() * (0.5 + 0.03 * performCard.getHv());
		playerEffect.setHp((int) -playerRoundHp);
		playerEffect.setSequence(psp.getNextAnimationSeq());
		effects.add(playerEffect);

		return effects;
	}
}
