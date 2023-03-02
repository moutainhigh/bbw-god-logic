package com.bbw.god.game.config.card;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 卡牌技能
 * @author lwb
 */
@Data
public class CfgCardSkill  implements CfgEntityInterface, Serializable{

	private Integer id;
	private String name;
	private boolean defensible; //是否可被防御
	private Integer totalTimes=1000;//初始总使用次数
	private Integer roundTimes=1000;//初始回合内可用次数
	private Integer round=61;//可用回合数
	private Integer leaderCardSkillGroup=0;//LeaderCardSkillGroupEnum
	/**
	 * 额外自带的技能
	 */
	private List<Integer> ownSkills=new ArrayList<>();

	@Override
	public int getSortId() {
		return id;
	}
}