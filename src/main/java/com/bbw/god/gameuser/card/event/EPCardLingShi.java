package com.bbw.god.gameuser.card.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.EPBaseWithBroadcast;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 卡牌添加参数
 * 
 * @author suhq
 * @date 2019-05-24 09:13:58
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EPCardLingShi extends BaseEventParam {
	private List<LingShiInfo> lingShiInfos;

	public static EPCardLingShi getInstance( List<LingShiInfo> lingShiInfos,BaseEventParam bep){
		EPCardLingShi lingShi=new EPCardLingShi();
		lingShi.setLingShiInfos(lingShiInfos);
		lingShi.setValues(bep);
		return lingShi;
	}
	@Data
	public static class LingShiInfo{
		private int cardId;
		private int num;

		public static LingShiInfo getInstance(int cardId,int num){
			LingShiInfo lingShiInfo=new LingShiInfo();
			lingShiInfo.setCardId(cardId);
			lingShiInfo.setNum(num);
			return lingShiInfo;
		}
	}
}
