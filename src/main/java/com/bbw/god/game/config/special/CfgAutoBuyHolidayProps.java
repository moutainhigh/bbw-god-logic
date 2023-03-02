package com.bbw.god.game.config.special;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 节日道具特产参数配置
 *
 * @author suhq
 * @date 2019-07-19 17:54:28
 */
@Data
public class CfgAutoBuyHolidayProps implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 1L;
	/** 节日道具id */
	private Integer id;
	/** 节日道具名称 */
	private String name;


	@Override
	public int getSortId() {
		return this.getId();
	}
}
