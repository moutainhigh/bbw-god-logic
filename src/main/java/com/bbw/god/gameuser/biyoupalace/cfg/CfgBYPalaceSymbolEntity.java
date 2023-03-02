package com.bbw.god.gameuser.biyoupalace.cfg;

import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;

/**
 * 碧游宫符箓
 * 
 * @author suhq
 * @date 2019-09-06 16:47:57
 */
@Data
public class CfgBYPalaceSymbolEntity implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 7358352044663386391L;
	private Integer id;
	private String name;
	private Integer type;
	private String color;
	private Integer effect;
	private Integer copperToNext;

	/**
	 * 是否满级
	 *
	 * @return
	 */
	public boolean isTopSymbol() {
		return copperToNext == 0;
	}

	@Override
	public int getSortId() {
		return id;
	}

}
