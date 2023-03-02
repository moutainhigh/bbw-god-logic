package com.bbw.god.game.config.city;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 城池振兴阵容
 */
@Data
public class CfgPromoteEntity implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id; //城池Id
	private String soliders;//守军阵容
	@Override
	public int getSortId() {
		return this.getId();
	}
}
