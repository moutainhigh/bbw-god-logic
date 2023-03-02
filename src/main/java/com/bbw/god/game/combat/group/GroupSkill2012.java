package com.bbw.god.game.combat.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;

/**
 * <pre> 
 * 四海龙王	三人以上在场，每回合每张卡对对位卡牌施加威风效果，无视金刚。（无视金刚效果，可被刚毅、回光）
 * </pre>
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 01:39
 */
@Service
public class GroupSkill2012 extends GroupSkillService {
	private static final int GROUP_ID = 2012;//组合ID
	private static final int MINIMUM_CARDS = 3;//至少需要多少张才能形成组合

	@Override
	public boolean match(int groupId) {
		return GROUP_ID == groupId;
	}

	@Override
	protected int getMinimumCards() {
		return MINIMUM_CARDS;
	}

	@Override
	protected List<Effect> groupAttack(Combat combat, PlayerId playing, List<BattleCard> groupCards) {
		//四海龙王	三人以上在场，每回合每张卡对对位卡牌施加威风效果，无视金刚。
		List<Effect> atks = new ArrayList<>(groupCards.size());
		int seq = combat.getAnimationSeq();
		for (BattleCard card : groupCards) {
			int cardIndex = PositionService.getBattleCardIndex(card.getPos());
			Optional<BattleCard> oppoCard = combat.getFaceToFaceCard(playing, cardIndex);
			if (oppoCard.isPresent()) {
				CardPositionEffect atk = CardPositionEffect.getSkillEffectToTargetPos(GROUP_ID,oppoCard.get().getPos());
				atk.moveTo(PositionType.DRAWCARD);
				atk.setSourcePos(card.getPos());
				atk.setSequence(seq);
				atks.add(atk);
			}
		}
		return atks;
	}
	
}
