package com.bbw.god.gameuser.card.event;

import java.util.List;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.EPBaseWithBroadcast;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 卡牌添加参数
 * 
 * @author suhq
 * @date 2019-05-24 09:13:58
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EPCardAdd extends EPBaseWithBroadcast {
	private List<CardAddInfo> addCards;
	private boolean isRandom;

	public EPCardAdd(boolean isRandom, BaseEventParam baseEP, List<CardAddInfo> cardAddInfos, String broadcastWayInfo) {
		this.isRandom = isRandom;
		this.setAddCards(cardAddInfos);
		this.setBroadcastWayInfo(broadcastWayInfo);
		setValues(baseEP);
	}

	public EPCardAdd(BaseEventParam baseEP, List<CardAddInfo> cardAddInfos, String broadcastWayInfo) {
		this.setAddCards(cardAddInfos);
		this.setBroadcastWayInfo(broadcastWayInfo);
		setValues(baseEP);
	}

	@Data
	@AllArgsConstructor
	public static class CardAddInfo {
		private Integer cardId;
		private boolean isNew;
	}
}
