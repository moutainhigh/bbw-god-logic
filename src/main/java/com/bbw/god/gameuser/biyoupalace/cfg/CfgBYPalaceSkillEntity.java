package com.bbw.god.gameuser.biyoupalace.cfg;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.game.config.CfgEntityInterface;

import lombok.Data;

/**
 * 碧游宫技能配置
 * 
 * @author suhq
 * @date 2019-09-06 16:55:16
 */
@Data
public class CfgBYPalaceSkillEntity implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String type;
	private Integer chapter;
	private List<String> skills;

	@Override
	public int getSortId() {
		return id;
	}

}
