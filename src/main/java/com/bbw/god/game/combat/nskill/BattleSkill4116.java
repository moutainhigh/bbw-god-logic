package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.skill.service.ISkillNormalBuffDefenseService;
import com.bbw.god.game.config.TypeEnum;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * (4116）太极：战斗时无视自身属性，永远克制对位卡牌,免疫暴击技能。
 */
@Service
public class BattleSkill4116 extends BattleSkillService implements ISkillNormalBuffDefenseService {
	private static final int SKILL_ID = CombatSkillEnum.TJ.getValue();// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		BattleCard performCard = psp.getPerformCard();
		if (performCard==null){
			return ar;
		}
		Optional<BattleCard> op = psp.getFaceToFaceCard();
		if (!op.isPresent()){
			return ar;
		}
		if (restrain(performCard.getType(),op.get().getType().getValue())){
			ar.setTakeEffect(true);
		}
		AnimationSequence as=ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(),getMySkillId(),performCard.getPos());
		ar.addClientAction(as);
		return ar;
	}
	private boolean restrain(TypeEnum myType,int opType){
		switch (myType){
			case Gold:
				if (TypeEnum.Wood.getValue()==opType){
					return true;
				}
				break;
			case Wood:
				if (TypeEnum.Earth.getValue()==opType){
					return true;
				}
				break;
			case Water:
				if (TypeEnum.Fire.getValue()==opType){
					return true;
				}
				break;
			case Fire:
				if (TypeEnum.Gold.getValue()==opType){
					return true;
				}
				break;
			case Earth:
				if (TypeEnum.Water.getValue()==opType){
					return true;
				}
				break;
			default:
				break;
		}
		return false;
	}
}
