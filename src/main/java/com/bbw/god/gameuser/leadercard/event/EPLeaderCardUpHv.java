package com.bbw.god.gameuser.leadercard.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * @author lzc
 * @description 法外分身升阶事件(主要用于广播)
 * @date 2021/4/14 10:53
 */
@Data
public class EPLeaderCardUpHv extends BaseEventParam {
	private Integer leaderCardHv; //法外分身当前等级

	public static EPLeaderCardUpHv instance(BaseEventParam ep, int leaderCardHv) {
		EPLeaderCardUpHv ew =new EPLeaderCardUpHv();
		ew.setValues(ep);
		ew.setLeaderCardHv(leaderCardHv);
		return ew;
	}
}
