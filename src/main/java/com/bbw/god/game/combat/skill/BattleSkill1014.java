package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 让位（1014）：上场时，与手牌中攻防总和最高的卡牌交换，替换上场卡牌可发动上场技能。（如在云台位，不能替换无飞行技能卡牌。）
 * 
 */
@Service
public class BattleSkill1014 extends BattleSkillService {
	private static final int SKILL_ID = 1014;// 技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		List<BattleCard> cards=psp.getMyHandCards();
		List<BattleCard> drawCards=psp.getPerformPlayer().getDrawCards();
		List<BattleCard> allCards=new ArrayList<>();
		allCards.addAll(cards);
		allCards.addAll(drawCards);
		allCards=allCards.stream().filter(p->!p.getSkill(CombatSkillEnum.RW.getValue()).isPresent()).collect(Collectors.toList());
		if (PositionService.isYunTaiPos(psp.getPerformCard().getPos())){
			//云台位 必须要飞行卡
			allCards=allCards.stream().filter(p->p.hasEffectiveSkill(CombatSkillEnum.FX.getValue())).collect(Collectors.toList());
		}
		if (ListUtil.isEmpty(allCards)){
			//无手牌 不执行
			return ar;
		}
		int maxHpAtk=0;
		BattleCard targetCard = null;
		for (BattleCard card:allCards){
			int temp=card.getAtk()+card.getHp();
			if (temp>maxHpAtk){
				maxHpAtk=temp;
				targetCard=card;
			}
		}
		if (targetCard==null){
			return ar;
		}
		CardPositionEffect positionEffect=CardPositionEffect.getSkillEffectToTargetPos(getMySkillId(),psp.getPerformCard().getPos());
		positionEffect.setToPos(targetCard.getPos());
		positionEffect.setExchange(true);
		ar.addEffect(positionEffect);
		return ar;
	}
}
