package com.bbw.god.city.lut;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 鹿台进宫参数
 * 
 * @author suhq
 * @date 2018年11月20日 下午5:29:14
 */
@Getter
@Setter
@ToString
public class CPLtTribute {
	/** 卡牌ID */
	private int cardId;
	/** 10铜钱进贡；20元素进贡 */
	private int tributeType;
	/** 10卡牌升级；20重置等级 */
	private int type;

	public CPLtTribute(int cardId, int tributeType, int type) {
		this.cardId = cardId;
		this.tributeType = tributeType;
		this.type = type;
	}

}
