package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 吸血	每次击中敌方卡牌后防御永久+60，每升一阶增加40%的效果。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-18 23:12
 */
@Service
public class BattleSkill4202 extends BattleSkillService {
	private static final int SKILL_ID = 4202;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		//Optional<BattleCard> faceCardObj = psp.getFaceToFaceCard();
		//对面没有卡牌
//		if (!faceCardObj.isPresent()) {
//			return ar;
//		}
//		BattleCard faceCard = faceCardObj.get();
//		//没有被击中
//		if (!faceCard.isBehit()) {
//			return ar;
//		}
		if (!psp.getPerformCard().isHit()) {
			return ar;
		}
		//吸血	每次击中敌方卡牌后防御永久+60，每升一阶增加40%的效果。
		CardValueEffect selfEffeck = CardValueEffect.getSkillEffect(CombatSkillEnum.XIX.getValue(), psp.getPerformCard().getPos());
		Double roundHp = 60 * psp.getPerformCard().getStars() * (1 + 0.4 * psp.getPerformCard().getHv());
		selfEffeck.setRoundHp(this.getInt(roundHp));
		selfEffeck.setSequence(psp.getNextAnimationSeq());
		ar.addEffect(selfEffeck);
		return ar;
	}
}
