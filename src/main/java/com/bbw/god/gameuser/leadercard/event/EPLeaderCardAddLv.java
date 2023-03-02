package com.bbw.god.gameuser.leadercard.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * @author lzc
 * @description 法外分身升级事件(主要用于广播)
 * @date 2021/4/14 10:53
 */
@Data
public class EPLeaderCardAddLv extends BaseEventParam {
	private Integer leaderCardLv; //法外分身当前等级

	public static EPLeaderCardAddLv instance(BaseEventParam ep,int leaderCardLv) {
		EPLeaderCardAddLv ew =new EPLeaderCardAddLv();
		ew.setValues(ep);
		ew.setLeaderCardLv(leaderCardLv);
		return ew;
	}
}
