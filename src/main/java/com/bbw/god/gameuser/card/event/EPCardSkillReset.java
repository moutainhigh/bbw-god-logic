package com.bbw.god.gameuser.card.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

import java.util.List;

/**
 * @author suchaobin
 * @description 卡牌技能重置事件参数
 * @date 2020/2/24 16:16
 */
@Data
public class EPCardSkillReset extends BaseEventParam {
	/**重置前的技能*/
	private List<Integer> oldSkillList;
	/**卡牌初始化的技能*/
	private List<Integer> initialSkillList;
	/** 装配技能次数 */
	private Integer useSkillScrollTimes;

	public EPCardSkillReset(List<Integer> oldSkillList, List<Integer> initialSkillList, Integer useSkillScrollTimes, BaseEventParam bep) {
		this.oldSkillList = oldSkillList;
		this.initialSkillList = initialSkillList;
		this.useSkillScrollTimes = useSkillScrollTimes;
		setValues(bep);
	}
}
