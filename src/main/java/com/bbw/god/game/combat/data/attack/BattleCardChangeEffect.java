package com.bbw.god.game.combat.data.attack;

import java.util.ArrayList;
import java.util.List;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.card.BattleCard;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年9月23日 下午2:06:34 类说明 卡牌转换效果
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class BattleCardChangeEffect extends Effect {
	private static final long serialVersionUID = 1L;
	private List<BattleCard> changes = null;// 待转换的新卡
	private PlayerId targetPlayerId = null;// 被转换的玩家
	@Override
	public EffectResultType getResultType() {
		return EffectResultType.CARD_CHANGE_TO_CARD;
	}

	private BattleCardChangeEffect(EffectSourceType type, int atkId) {
		this.setSourceType(type);
		this.setSourceID(atkId);
	}

	/**
	 * 获取卡牌变更
	 * 
	 * @param SkillId
	 * @param newCard
	 * @return
	 */
	public static BattleCardChangeEffect getCardChangeEffect(int SkillId, BattleCard newCard, PlayerId playerId) {
		BattleCardChangeEffect effect = new BattleCardChangeEffect(EffectSourceType.SKILL, SkillId);
		effect.changes = new ArrayList<BattleCard>();
		effect.changes.add(newCard);
		effect.setTargetPos(newCard.getPos());
		effect.targetPlayerId = playerId;
		return effect;
	}

	public static BattleCardChangeEffect getCardChangeEffect(int SkillId, List<BattleCard> newCards,
			PlayerId playerId) {
		BattleCardChangeEffect effect = new BattleCardChangeEffect(EffectSourceType.SKILL, SkillId);
		effect.changes = newCards;
		effect.setTargetPos(PositionService.getZhaoHuanShiPos(playerId));
		effect.targetPlayerId = playerId;
		return effect;
	}
}
