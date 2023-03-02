package com.bbw.god.gameuser.biyoupalace.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 秘传条件组合
 *
 * @author suhq
 * @date 2019-09-06 16:52:28
 */
@Data
public class CfgBYPalaceConditionEntity implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = -6331499741170594976L;
	private Integer id;
	private String name;
	private List<Integer> condition;
	private Integer value;

	@Override
	public int getSortId() {
		return id;
	}

}
