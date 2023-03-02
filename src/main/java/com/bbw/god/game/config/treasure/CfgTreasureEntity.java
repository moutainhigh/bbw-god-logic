package com.bbw.god.game.config.treasure;

import java.io.Serializable;

import com.bbw.god.game.config.CfgEntityInterface;

import lombok.Data;

/**
 * 
 * 
 * @author shaojun
 * @email lsj@bamboowind.cn
 * @date 2018-11-27 21:57:48
 */
@Data
public class CfgTreasureEntity implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id; //
	private String name; //
	private Integer type; //
	private Integer star; //
	private String memo; //

	@Override
	public int getSortId() {
		return this.getId();
	}

	public boolean ifSkillScroll() {
		return type == 56;
	}
}
