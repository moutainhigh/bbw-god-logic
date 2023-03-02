package com.bbw.god.game.combat.data.skill;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 技能使用记录
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-12 04:15
 */
@Data
@NoArgsConstructor
public class BattleSkillLog implements Serializable {
	private static final long serialVersionUID = 6810310747873886956L;
	private int skillId = 0;//技能ID
	private int round = 0;//回合数
	private List<Integer> targetsPos = new ArrayList<>();//技能ID使用的对象位置

	public BattleSkillLog(int skillId, int round, int targetPos) {
		this.skillId = skillId;
		this.round = round;
		this.targetsPos.add(targetPos);
	}
}
