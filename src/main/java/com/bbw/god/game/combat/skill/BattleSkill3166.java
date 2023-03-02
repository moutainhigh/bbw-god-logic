package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardStatus;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.runes.service.series.DuSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 解毒：每回合，清除我方场上1张卡牌的中毒状态。
 *
 * @author: suhq
 * @date: 2021/12/3 11:00 上午
 */
@Service
public class BattleSkill3166 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.JIE_DU.getValue();// 技能ID
	@Autowired
	private DuSeriesService duSeriesService;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		List<BattleCard> cardsToChoose = new ArrayList<>();
		for (BattleCard card : psp.getPerformPlayer().getPlayingCards()) {
			if (card == null) {
				continue;
			}
			//是否中毒
			boolean hasDued = card.getLastingEffects().stream().anyMatch(p -> duSeriesService.check(p.getPerformSkillID()));
			if (!hasDued) {
				continue;
			}
			cardsToChoose.add(card);
		}
		if (ListUtil.isEmpty(cardsToChoose)) {
			return ar;
		}
		BattleCard targetCard = PowerRandom.getRandomFromList(cardsToChoose);
		List<CardValueEffect> newEffects = targetCard.getLastingEffects().stream().filter(p -> !duSeriesService.check(p.getPerformSkillID())).collect(Collectors.toList());
		targetCard.setLastingEffects(newEffects);

		AnimationSequence as = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), getMySkillId(), psp.getPerformCard().getPos(),targetCard.getPos());
		ar.addClientAction(as);
		return ar;

	}
}
