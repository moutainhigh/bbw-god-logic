package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 战刺（4504）：反弹受到的物理攻击50%的伤害，反弹伤害无法超过自身防御，击退卡牌后，溢出的攻击力将直接作用于召唤师。每升一阶增加5%的反弹伤害。
 *（攻击与反击伤害都有穿刺效果：即普攻溢出伤害也加到反弹伤害中）
 */
@Service
public class BattleSkill4504 extends BattleSkillService {
	private static final int SKILL_ID =4504;//技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	public Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		if (psp.getReceiveEffect()==null || !psp.getReceiveEffect().isValueEffect()){
			return ar;
		}
		CardValueEffect cardValueEffect=psp.getReceiveEffect().toValueEffect();
		int hp=cardValueEffect.getHp()+cardValueEffect.getRoundHp();
		if (hp>=0){
			return ar;
		}
		int sourcePos=cardValueEffect.getSourcePos();
		BattleCard performCard = psp.getPerformCard();
		int atk=0;
		for (Effect effect:psp.getReceiveEffects()){
			if (!effect.isValueEffect() || !(effect.getSourcePos()==performCard.getPos() && effect.getTargetPos()==sourcePos)){
				continue;
			}
			CardValueEffect valueEffect=effect.toValueEffect();
			atk+=valueEffect.getHp()+valueEffect.getRoundHp();
		}

		//反弹伤害 到的物理攻击50%的伤害+每升一阶增加5%的反弹伤害
		int reboundAtk=getInt(hp*(0.5+0.05*psp.getPerformCard().getHv()));
		// 反弹伤害无法超过自身防御
		reboundAtk = - Math.min(-reboundAtk, performCard.getRoundHp());
		BattleCard targetCard = psp.getCombat().getBattleCardByPos(cardValueEffect.getSourcePos());
		//对方卡收到普攻后的 剩余血量
		int targetCardInTimeHp=targetCard.getHp()-Math.abs(atk);
		CardValueEffect effect=CardValueEffect.getSkillEffect(SKILL_ID,cardValueEffect.getSourcePos());
		int attackZHS=0;
		int seq=psp.getNextAnimationSeq();
		effect.setSequence(seq);
		if (targetCardInTimeHp>0){
			//作用在召唤师的：反弹伤害-剩余血量
			attackZHS=Math.abs(reboundAtk)-targetCardInTimeHp;
			if (attackZHS<=0){
				//打不死对面,则反弹的伤害全作用在卡牌上
				effect.setHp(reboundAtk);
			}else {
				//打的死，则计算卡牌收到的部分伤害
				effect.setHp(-targetCardInTimeHp);
			}
		}else {
			//卡牌已经被普攻击打死了,则战刺反弹的伤害+普攻溢出都作用在召唤师
			attackZHS=Math.abs(reboundAtk)+Math.abs(targetCardInTimeHp);
			//卡牌收到的伤害就是剩余伤害
			effect.setHp(0);
		}
		if (attackZHS>0){
			//作用再召唤师
			CardValueEffect zhsEffect=CardValueEffect.getSkillEffect(SKILL_ID,psp.getOppoZhsPos());
			zhsEffect.setSourcePos(psp.getPerformCard().getPos());
			zhsEffect.setHp(-attackZHS);
			zhsEffect.setSequence(seq);
			ar.addEffect(zhsEffect);
		}
		if (effect.getHp()<0){
			ar.addEffect(effect);
		}
		if (ar.existsEffect()){
			//触发 补充一个动画
			AnimationSequence amin= ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), SKILL_ID, psp.getPerformCard().getPos());
			ar.addClientAction(amin);
		}
		return ar;
	}
}