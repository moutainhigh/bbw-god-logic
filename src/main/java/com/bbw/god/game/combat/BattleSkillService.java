package com.bbw.god.game.combat;

import com.bbw.common.CloneUtil;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.attack.Effect.AttackPower;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.skill.service.ISkillBaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 技能攻击
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 09:04
 */
@Service
public abstract class BattleSkillService implements ISkillBaseService {
	protected Logger log = LoggerFactory.getLogger(this.getClass());
	public Action productAction(PerformSkillParam psp) {
		// 军师位置技能加成
		if (PositionService.isJunShiPos(psp.getPerformCard().getPos())) {
			SkillSection deploySection = SkillSection.getDeploySection();// 上场技能
			SkillSection skillSection = SkillSection.getSkillAttackSection();// 攻击技能
            SkillSection fightBackSection=SkillSection.getFightBackSection();
			if (deploySection.contains(getMySkillId()) || skillSection.contains(getMySkillId()) || fightBackSection.contains(getMySkillId())) {
				return junShiBuffAction(psp);
			}
		}
		return attack(psp);
	}

	/**
	 * 回合延迟效果，如怨灵
	 * @param psp
	 * @return
	 */
	public List<Effect> attakRoundLasting(PerformSkillParam psp) {
		return null;
	}


	/**
	 * 溅射效果，成功攻击主目标后产生的额外伤害
	 * 如 雷电，龙息
	 * @param psp
	 * @return
	 */
	public List<Effect> attakParticleffects(PerformSkillParam psp) {
		return null;
	}

	/**
	 * 军师位加成
	 * 
	 * @param psp
	 * @return
	 */
	protected Action junShiBuffAction(PerformSkillParam psp) {
		Action action = attack(psp);
		if (null != action && action.existsEffect()) {
			for (Effect effect : action.getEffects()) {
				if (effect.isValueEffect()) {
					CardValueEffect ve = effect.toValueEffect();
					ve.setHp(this.getInt(ve.getHp() * 1.5));
					ve.setRoundHp(this.getInt(ve.getRoundHp() * 1.5));
				}
			}
		}
		return action;
	}

	/**
	 * 技能攻击
	 * 
	 * @param psp
	 * @return
	 */
	protected abstract Action attack(PerformSkillParam psp);

	/**
	 * 复制本身受到的效果复制到目标上 <br>
	 * 出现场景 ：回光 和 刚毅 <br>
	 * 该方法默认实现为 拷贝Effect 交换攻击和被攻击的目标 <br>
	 * 特殊技能需要重写该方法 如 魅惑、枷锁
	 * 
	 * @return
	 */
	public List<Effect> attackTargetPosByCopyEffects(PerformSkillParam fromPsp, int attkPos) {
		List<Effect> effects = new ArrayList<>();
		if (null == fromPsp.getCombat().getBattleCard(attkPos)) {
			// 说明目标卡牌已死亡 或者 已被其他位置的反弹技能致死了
			return effects;
		}
		Effect fightBackEffect = CloneUtil.clone(fromPsp.getReceiveEffect());
		fightBackEffect.setSourcePos(fromPsp.getPerformCard().getPos());
		fightBackEffect.setTargetPos(attkPos);
		fightBackEffect.setAttackPower(AttackPower.L2);
		fightBackEffect.setSequence(fromPsp.getNextAnimationSeq());
		effects.add(fightBackEffect);
		return effects;
	}

	/**
	 * 对指定目标施放技能
	 * @param target
	 * @param psp
	 * @return
	 */
	public Action buildEffects(BattleCard target, PerformSkillParam psp){
		return new Action();
	}
}
