package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleCardChangeEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.pve.CombatPVEInitService;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 超度：上场时，使敌方坟场的全体卡牌变成0级鬼兵。
* @author lwb  
* @date 2019年8月1日  
* @version 1.0
 */
@Service
public class BattleSkill1010 extends BattleSkillService {
	private static final int SKILL_ID = 1010;// 技能ID
	@Autowired
	private CombatRunesPerformService runesPerformService;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		// 获取敌对坟地所有卡牌
		Player player = psp.getOppoPlayer();
		List<BattleCard> disCards = player.getDiscard();
		List<BattleCard> nBattleCards = new ArrayList<BattleCard>();
		for (BattleCard card : disCards) {
			if (card == null) {
				continue;
			}
			nBattleCards.add(getNewCard(card, player.getCardInitId(),psp));
		}
		if (!nBattleCards.isEmpty()) {
			BattleCardChangeEffect effect = BattleCardChangeEffect.getCardChangeEffect(SKILL_ID, nBattleCards,
					player.getId());
			effect.setSequence(psp.getNextAnimationSeq());
			ar.addEffect(effect);
		}
		return ar;
	}

	private BattleCard getNewCard(BattleCard card, int id,PerformSkillParam psp) {
		// 0级0阶级 424 鬼兵
		CCardParam bcip = CCardParam.init(424, 0, 0,null);
		BattleCard ncard = CombatPVEInitService.initBattleCard(bcip, id);
		ncard.setPos(card.getPos());
		runesPerformService.runInitCardRunes(psp.getOppoPlayer(),ncard);
		return ncard;
	}
}
