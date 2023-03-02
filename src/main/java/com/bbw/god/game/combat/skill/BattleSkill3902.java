package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 狙杀：攻击前，如果敌方场上防御最少的卡牌低于160，则破除其160点永久防御值，无视回光。每阶判定与造成伤害提高50%，每升两阶可多狙杀一张卡牌。
 * 
 */
@Service
public class BattleSkill3902 extends BattleSkillService {
	private static final int SKILL_ID = 3902;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		List<BattleCard> cards = psp.getOppoPlayingCards(true);
		if (ListUtil.isEmpty(cards)) {
			return ar;
		}
		int hv = psp.getPerformCard().getHv();
		int maxNum = Math.min(1 + hv / 2, cards.size());
		int maxHp = getInt(160 * (1 + 0.5 * psp.getPerformCard().getHv()));
		// 刃纹符影响作用范围
		if (psp.getPerformPlayer().hasBuff(RunesEnum.REN_WEN)) {
			maxHp = (int) (maxHp * 1.25);
		}
		List<BattleCard> collects = cards.stream().sorted(Comparator.comparing(BattleCard::getHp)).collect(Collectors.toList());
		int seq = psp.getNextAnimationSeq();
		for (int i = 0; i < maxNum; i++) {
			BattleCard card = collects.get(i);
			if (card.getHp() <= maxHp) {
				CardValueEffect effect = CardValueEffect.getSkillEffect(getMySkillId(), card.getPos());
				effect.setSequence(seq);
				effect.setHp(-maxHp);
				ar.addEffect(effect);
			} else {
				break;
			}
		}
		return ar;
	}
}
