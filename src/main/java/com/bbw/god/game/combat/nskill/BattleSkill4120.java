package com.bbw.god.game.combat.nskill;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 突击：直接攻击召唤师时，施放1次【突袭】，并以相同攻击值随机攻击敌方1张地面卡牌。
 * （1）随机攻击卡牌时将会触发：物理攻击BUFF、物理攻击后两个类别的技能。
 * （2）攻击判定为普通攻击，即受到攻击的卡牌会触发攻击攻击防御类别或受到普攻时触发的效果。
 * （3）【突击】只会进行1次是否触发【连击】的判定：若只攻击召唤师，则在攻击召唤师后进行判定；若攻击召唤师且攻击卡牌，则在攻击卡牌后进行判定。
 *
 * @author: suhq
 * @date: 2022/5/9 11:00 上午
 */
@Service
public class BattleSkill4120 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.TU_JI.getValue();
	@Autowired
	private BattleSkill4112 battleSkill4112;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		Optional<BattleCard> faceOptional = psp.getFaceToFaceCard();
		//是否攻击召唤师
		boolean isToAttackPlayer = !faceOptional.isPresent() || faceOptional.get().isKilled();
		if (!isToAttackPlayer) {
			return ar;
		}
		// 施放1次【突袭】
		battleSkill4112.buildEffect(ar, psp);
		if (!ar.existsEffect()) {
			return ar;
		}
		//以相同攻击值随机攻击敌方1张地面卡牌
		int atk = ((CardValueEffect) ar.getEffects().get(0)).getAtk();
		List<BattleCard> oppoPlayingCards = psp.getOppoPlayingCards(false);
		if (ListUtil.isEmpty(oppoPlayingCards)) {
			return ar;
		}
		BattleCard targetCard = PowerRandom.getRandomFromList(oppoPlayingCards);
		//攻击对方目标卡牌
		CardValueEffect effect = CardValueEffect.getSkillEffect(SKILL_ID, targetCard.getPos());
		int hp = psp.getPerformCard().getAtk() + atk;
		effect.setHp(-hp);
		ar.addEffect(effect);
		return ar;
	}
}
