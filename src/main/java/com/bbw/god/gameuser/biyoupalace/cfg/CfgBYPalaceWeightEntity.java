package com.bbw.god.gameuser.biyoupalace.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 碧游宫权重
 */
@Data
public class CfgBYPalaceWeightEntity implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 2559980044477175286L;
	private Integer id;
	private Integer chapterType;
	private String name;
	private Integer weight;

	@Override
	public int getSortId() {
		return id;
	}

}
