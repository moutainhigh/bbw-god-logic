package com.bbw.god.game.combat.nskill;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect.AttackPower;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.runes.service.series.DuSeriesService;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 【逃遁】 4603：受到普通攻击后，无论自身是否死亡都将随机移动到我方场上任意空位并恢复自身受到的所有非永久伤害及封锁、中毒状态。
 *
 * @author longwh
 * @date 2022/10/25 11:50
 */
@Service
public class BattleSkill4603 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.TAO_DUN.getValue();

	@Autowired
	private DuSeriesService duSeriesService;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action junShiBuffAction(PerformSkillParam psp) {
		return attack(psp);
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action action = new Action();
		// 获取普通攻击效果
		if (null == psp.getReceiveEffect()){
			return action;
		}
		//非普通，不处理
		if (psp.getReceiveEffect().getSourceID() !=  CombatSkillEnum.NORMAL_ATTACK.getValue()){
			return action;
		}
		BattleCard card = psp.getPerformCard();
		// 永久血量小于0时 不触发逃遁
		if (card.getRoundHp() <= 0){
			return action;
		}
		boolean hasFeiXSkill = card.hasSkill(CombatSkillEnum.FX.getValue());
		// 获取当前的空余阵位（根据飞行技能判断是否包含云台）
		Player player = psp.getPerformPlayer();
		Integer[] emptyPosArr = ArrayUtils.toObject(player.getEmptyBattlePos(hasFeiXSkill));
		if (emptyPosArr.length == 0){
			// 无空余阵位 不触发技能
			return action;
		}
		int oldPos = card.getPos();
		int toPos = PowerRandom.getRandomFromArray(emptyPosArr);
		// 解除中毒状态
		boolean hasDued = card.getLastingEffects().stream().anyMatch(p -> duSeriesService.check(p.getPerformSkillID()));
		if (hasDued){
			List<CardValueEffect> newEffects = card.getLastingEffects().stream().filter(p -> !duSeriesService.check(p.getPerformSkillID())).collect(Collectors.toList());
			card.setLastingEffects(newEffects);
		}
		// 添加逃遁技能动画
		AnimationSequence amin = ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), getMySkillId(), oldPos, oldPos);
		action.addClientAction(amin);
		// 添加逃遁移动效果
		CardPositionEffect positionEffect = CardPositionEffect.getSkillEffectToTargetPos(getMySkillId(), oldPos);
		positionEffect.moveTo(PositionType.BATTLE, toPos);
		positionEffect.setSequence(psp.getNextAnimationSeq());
		action.addEffect(positionEffect);
		// 添加自愈效果
		CardValueEffect effect=CardValueEffect.getSkillEffect(getMySkillId(), toPos);
		//恢复自身受到的所有非永久性伤害与攻击值
		dealHp(effect, card, oldPos, toPos);
		effect.setAttackPower(AttackPower.getMaxPower());
		action.addEffect(effect);
		// 若逃遁位置为当前阵位位置的顺位之后，则可以再次进行普通攻击
		CardValueEffect effectN = CardValueEffect.getSkillEffect(getMySkillId(), toPos);
		effectN.setAtk(card.getNormalAttackPreAtk() - card.getAtk());
		action.addEffect(effectN);
		card.getNormalAttackSkill().getTimesLimit().setCurrentRoundTimes(1);
		return action;
	}

	/**
	 * 处理血量值
	 *
	 * @param effect
	 * @param card
	 * @param oldPos
	 * @param toPos
	 */
	private void dealHp(CardValueEffect effect, BattleCard card, int oldPos, int toPos){
		int hp=card.getRoundHp()-card.getHp();
		int roundHp = card.getRoundHp();
		// 判断当前的阵位是否为 中军
		if (PositionService.isZhongJunPos(oldPos)){
			int hpBuff = (int) (card.getInitHp() * 0.15);
			if (card.getRoundAtk() > card.getInitAtk()){
				hpBuff = (int) (card.getRoundAtk() * 0.15);
			}

			hp -= hpBuff;
			// 扣除阵位增益
			effect.setRoundHp(-hpBuff);
		}
		// 判断逃遁阵位是否为 中军
		if (PositionService.isZhongJunPos(toPos)){
			int hpBuff = (int) (card.getInitHp() * 0.15);
			if (card.getRoundAtk() > card.getInitAtk()){
				hpBuff = (int) (card.getRoundAtk() * 0.15);
			}
			// 增加阵位增益
			effect.setRoundHp(hpBuff);
		}
		hp = Math.min(hp, roundHp);
		effect.setHp(hp);
	}
}