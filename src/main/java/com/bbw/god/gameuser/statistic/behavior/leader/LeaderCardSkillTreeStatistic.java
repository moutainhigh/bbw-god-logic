package com.bbw.god.gameuser.statistic.behavior.leader;

import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** 技能树统计
 * @author lzc
 * @description
 * @date 2021/4/15 11:28
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LeaderCardSkillTreeStatistic extends BehaviorStatistic {
	/** 已激活的完整技能树数量 */
	private int skillTreeNum = 0;

	public LeaderCardSkillTreeStatistic() {
		super(BehaviorType.LEADER_CARD_SKILL_TREE);
	}

	public LeaderCardSkillTreeStatistic(Integer today, Integer total, Integer date, Integer skillTreeNum) {
		super(today, total, date, BehaviorType.LEADER_CARD_SKILL_TREE);
		this.skillTreeNum = skillTreeNum;
	}
}
