package com.bbw.god.gameuser.treasure;

import lombok.Data;

/**
 * 升级符箓
 *
 * @author suhq
 * @date 2019-10-08 14:45:28
 */
@Data
public class CPUpdateSymbol {
	/** 符箓ID */
	private Integer symbol;
	/** 卡牌ID */
	private Integer cardId = 0;
	/** 是否使用万能符箓 */
	private Integer useWanNFL = 0;
	/** 批量升级符箓，1，5，10 */
	private Integer upgradeTimes = 1;

	/**
	 * 是否可以使用万能符箓
	 *
	 * @return
	 */
	public boolean isUseWanNFL() {
		return this.useWanNFL == 1;
	}

}
