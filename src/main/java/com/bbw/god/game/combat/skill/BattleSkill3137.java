package com.bbw.god.game.combat.skill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 雷电（3137）：每回合破除敌方随机1张卡牌（不含云台）最低100点防御值，
 * 所有与该卡相连的卡牌（不包含云台，最多5张）受到50%的伤害。每级增加最高值上限50点，每阶增加50%效果。
 * 
 * @author lwb
 * @date 2020年03月09日
 * @version 1.0
 */
@Service
public class BattleSkill3137 extends BattleSkillService {
	private static final int SKILL_ID = 3137;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		// 雷电（3137）：每回合破除敌方随机1张卡牌（不含云台）最低100点防御值，所有与该卡相连的卡牌（不包含云台，最多5张）受到50%的伤害。
		// 每级增加最高值上限50点，每阶增加50%效果。
		Action ar = new Action();
		List<BattleCard> playingCards = psp.getOppoPlayingCards(false);
		if (playingCards.isEmpty()) {
			return ar;
		}
		// 伤害计算
		double low = 100 * (1 + psp.getPerformCard().getHv() * 0.5);// 下限
		double hight = (100 + psp.getPerformCard().getLv() * 50) * (1 + psp.getPerformCard().getHv() * 0.5);// 上限
		int max = getInt(hight);
		int min = low > hight ? max : getInt(low);// 下限超过上限 则最小值就为上限值
		int hp = PowerRandom.getRandomBetween(min, max);
		// 目标卡牌
		BattleCard targetCard = PowerRandom.getRandomFromList(playingCards);
		int seq = psp.getNextAnimationSeq();
		CardValueEffect targetCardeffect = CardValueEffect.getSkillEffect(SKILL_ID, targetCard.getPos());
		targetCardeffect.setHp(-hp);
		targetCardeffect.setSequence(seq);
		targetCardeffect.setParticleSkill(true);
		ar.addEffect(targetCardeffect);

//		// 技能释放动画
		AnimationSequence action = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), SKILL_ID, psp.getPerformCard().getPos(), targetCard.getPos());
		ar.addClientAction(action);
	    return ar;
	}

	@Override
	public List<Effect> attakParticleffects(PerformSkillParam psp) {
		// 获取相连目标 位置标识，依次为：0云台、1先锋、2前军、3中军、4后军、5军师
		List<Effect> subEffects = new ArrayList<Effect>();
		List<BattleCard> linkCards = new ArrayList<BattleCard>();
		int index = PositionService.getBattleCardIndex(psp.getAttackEffect().getTargetPos());
		BattleCard[] cards = psp.getOppoPlayer().getPlayingCards();
		// 左边相连
		for (int i = index - 1; i > 0; i--) {
			BattleCard card = cards[i];
			if (card == null) {
				break;
			}
			linkCards.add(card);
		}
		// 右边相连
		for (int i = index + 1; i < 6; i++) {
			BattleCard card = cards[i];
			if (card == null) {
				break;
			}
			linkCards.add(card);
		}
		int seq=psp.getNextAnimationSeq();
		int hp=psp.getAttackEffect().toValueEffect().getHp();
		if (hp==0){
			hp=psp.getAttackEffect().toValueEffect().getRoundHp();
		}
		for (BattleCard card : linkCards) {
			CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, card.getPos());
			effect.setHp(getInt(hp * 0.5));
			effect.setSourcePos(psp.getReceiveEffect().getSourcePos());
			effect.setSequence(seq);
			subEffects.add(effect);
		}
		return subEffects;
	}
}
