package com.bbw.god.game.config.treasure;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 技能卷轴
 * 
 * @author suhq
 * @date 2019-10-12 11:25:26
 */
@Data
public class CfgSkillScrollLimitEntity implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id; //
	private String name; //
	private Integer skillId;
	private Integer limit; //
	private List<Integer> limitTypes; //
	private List<Integer> limitCards; //
	private List<Integer> limitSkills; //
	private List<Integer> limitLevels; //

	@Override
	public int getSortId() {
		return this.getId();
	}

	public boolean match(int skillId, int type, int cardId, int levelIndex) {
		boolean levelMatch = this.limitLevels.size() == 0 || this.limitLevels.contains(levelIndex);
		return match(skillId, type, cardId) && levelMatch;
	}

	public boolean match(int skillId, int type, int cardId) {
		boolean skillIdMatch = this.skillId == skillId;
		boolean typeMatch = this.limitTypes.size() == 0 || this.limitTypes.contains(type);
		boolean cardMatch = this.limitCards.size() == 0 || this.limitCards.contains(cardId);
		return skillIdMatch && typeMatch && cardMatch;
	}
}
