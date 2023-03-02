package com.bbw.god.gameuser.card.event;

import java.util.List;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.card.UserCard;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 卡牌删除参数
 * 
 * @author suhq
 * @date 2019-05-24 09:13:58
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPCardDel extends BaseEventParam {
	private List<UserCard> delCards;

	public EPCardDel(BaseEventParam baseEP, List<UserCard> delCards) {
		setValues(baseEP);
		this.setDelCards(delCards);
	}
}
