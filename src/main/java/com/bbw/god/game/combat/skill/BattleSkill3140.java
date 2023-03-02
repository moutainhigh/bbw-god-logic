package com.bbw.god.game.combat.skill;

import com.bbw.common.CloneUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardStatus;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.skill.service.ISkillDefenseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 力盾（3140）：每回合随机指定1张我方非施术卡牌，本回合物理攻击对其无效。如无可生效目标，则恢复召唤师等同于施术者防御的血量。
 * <br>力盾新增效果：拥有力盾技能的卡牌无法成为力盾技能的目标。
 * @author lwb
 * @date 2020年02月19日
 * @version 1.0
 */
@Service
public class BattleSkill3140 extends BattleSkillService implements ISkillDefenseService {
	private static final int SKILL_ID = 3140;
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		List<BattleCard> myPlayingCards=psp.getMyPlayingCards(true);
		List<BattleCard> randomCards=myPlayingCards.stream().filter(p->!p.existSkillStatus(SKILL_ID)&&!p.existSkill(SKILL_ID)).collect(Collectors.toList());
		if (randomCards.isEmpty()){
			//如无可生效目标，则恢复召唤师等同于施术者防御的血量。
			CardValueEffect cardValueEffect=CardValueEffect.getSkillEffect(SKILL_ID,PositionService.getZhaoHuanShiPos(psp.getPerformPlayerId()));
			cardValueEffect.setSequence(psp.getNextAnimationSeq());
			cardValueEffect.setHp(psp.getPerformCard().getHp());
			ar.addEffect(cardValueEffect);
			return ar;
		}
		BattleCard targetCard= PowerRandom.getRandomFromList(randomCards);
		//有生效目标，本回合物理攻击对其无效
		BattleSkillEffect effect = BattleSkillEffect.getLastPerformSkillEffect(BattleCardStatus.StatusEffectType.ONE_ROUND_LASTING, SKILL_ID, targetCard.getPos());
		ar.addEffect(effect);
		return ar;
	}

	@Override
	public Action takeDefense(PerformSkillParam psp) {
		Action ar=new Action();
		if (psp.getReceiveEffect()==null){
			return ar;
		}
		Optional<BattleCard> sourceCard = psp.getEffectSourceCard();
		if (sourceCard.isPresent() && sourceCard.get().hasEffect(CombatSkillEnum.SHENJ)) {
			return ar;
		}
		if (!psp.getReceiveEffect().hasExtraSkillEffect(CombatSkillEnum.NORMAL_ATTACK.getValue())){
			return ar;
		}
		CardValueEffect effect= CloneUtil.clone(psp.getReceiveEffect().toValueEffect());
		effect.setRoundHp(0);
		effect.setHp(0);
		psp.setReceiveEffect(null);
		ar.addEffect(effect);
		return ar;
	}
}