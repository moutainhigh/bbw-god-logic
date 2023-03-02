package com.bbw.god.game.combat.nskill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

/**
 * 芒刺（4502）：反弹受到的物理攻击50%的伤害。每升一阶增加5%的反弹伤害。（注：反弹伤害不超过自身血量。反弹无视灵动、销魂。即反弹的伤害直接接受）
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 10:25
 */
@Service
public class BattleSkill4502 extends BattleSkillService {
	private static final int SKILL_ID =4502;//技能ID
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
		CardValueEffect effect=CardValueEffect.getSkillEffect(SKILL_ID,cardValueEffect.getSourcePos());
		hp=getInt(hp*(0.5+0.05*psp.getPerformCard().getHv()));
		hp=Math.min(-hp,psp.getPerformCard().getHp());
		effect.setHp(-hp);
		ar.addEffect(effect);
		//触发 补充一个动画
		AnimationSequence amin= ClientAnimationService.getSkillAction(psp.getNextAnimationSeq(), SKILL_ID, psp.getPerformCard().getPos());
		ar.addClientAction(amin);
		return ar;
	}
}
