package com.bbw.god.gameuser.card;

import com.bbw.common.ListUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 卡牌参数解析
 *
 * @author: suhq
 * @date: 2021/11/17 11:13 上午
 */
public class CardParamParser {

	/**
	 * cardId,cardId,cardId#符图ID
	 *
	 * @param cardGroup
	 */
	public static List<Integer> parseGroupParam(String cardGroup) {
		List<Integer> cardIds = ListUtil.parseStrToInts(cardGroup);
		//去重
		cardIds = cardIds.stream().distinct().collect(Collectors.toList());
		return cardIds;
	}
}
