package com.bbw.god.game.combat.nskill;

import org.springframework.stereotype.Service;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.param.PerformSkillParam;

/**
 * 物理防御
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-19 01:51
 */
@Service
public abstract class NormalDefenseSkillService extends BattleSkillService {
	//责任链中的下一个元素
	private NormalDefenseSkillService nextDefenseSkill = null;

	public NormalDefenseSkillService getNext() {
		return nextDefenseSkill;
	}

	@Override
    public abstract Action attack(PerformSkillParam psp);

	/**
	 * 设置下一个责任链策略，返回下一个责任链策略
	 * @param next
	 * @return
	 */
	public NormalDefenseSkillService setNext(NormalDefenseSkillService next) {
		this.nextDefenseSkill = next;
		return nextDefenseSkill;
	}
}
