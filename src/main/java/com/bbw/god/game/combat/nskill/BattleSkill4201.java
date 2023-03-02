package com.bbw.god.game.combat.nskill;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 4201	穿刺	击退敌方卡牌后剩余攻击力攻击敌方召唤师。
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-15 02:43
 */
@Service
public class BattleSkill4201 extends BattleSkillService {
	private static final int SKILL_ID = 4201;//技能ID

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		BattleCard selfCard = psp.getPerformCard();
		//攻击对位卡牌后 存储的多余攻击力
		if (selfCard.getLeftAtk() < 1) {
			return ar;
		}
		//获取对手召唤师位置
		int targetPos=selfCard.getNormalAttackSkill().getTargetPos();
		int oppoZhsPos = PositionService.getZhaoHuanShiPos(psp.getOppoPlayer().getId());
		if (targetPos!=-1 && psp.getPerformPlayerId().getValue()==PositionService.getPlayerIdByPos(targetPos).getValue()){
			oppoZhsPos = PositionService.getZhaoHuanShiPos(psp.getPerformPlayerId());
		}
		CardValueEffect addtionEffeck =CardValueEffect.getSkillEffect(SKILL_ID,oppoZhsPos);
		addtionEffeck.setHp(-selfCard.getLeftAtk());
		selfCard.setLeftAtk(0);
		ar.addEffect(addtionEffeck);
		return ar;
	}
}
