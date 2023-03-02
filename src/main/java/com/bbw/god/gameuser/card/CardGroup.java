package com.bbw.god.gameuser.card;

import com.bbw.god.game.config.card.CardEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 卡牌升级参数
 *
 * @author suhq
 * @date 2018年11月6日 下午2:08:07
 */
@Data
@NoArgsConstructor
public class CardGroup {
	private Integer fuCeId = 0;
	private List<Integer> cardIds = new ArrayList<>();

	public CardGroup(Integer fuCeId,List<Integer> cardIds){
		this.fuCeId = fuCeId;
		this.cardIds = cardIds;
	}

	/**
	 * 是否包含主角卡
	 *
	 * @return
	 */
	public boolean hasLeaderCard() {
		return cardIds.contains(CardEnum.LEADER_CARD.getCardId());
	}
}
