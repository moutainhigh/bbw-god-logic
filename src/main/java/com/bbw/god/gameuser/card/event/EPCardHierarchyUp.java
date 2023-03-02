package com.bbw.god.gameuser.card.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 卡牌进阶事件
 * 
 * @author suhq
 * @date 2019-06-29 09:56:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPCardHierarchyUp extends BaseEventParam {
	private Integer cardId;// 进阶的卡牌
	private Integer deductCardLingshi;// 扣除的卡牌灵石数量
	private Integer universalSoulId;// 万能灵石ID
	private Integer deductUniversalSoulId;// 扣除的万能灵石的数量
	private Integer deductneedHYXS;// 扣除的混沌仙石

	public EPCardHierarchyUp(BaseEventParam baseEP) {
		setValues(baseEP);
	}
}
