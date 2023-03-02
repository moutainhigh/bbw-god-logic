package com.bbw.god.game.chanjie.event;

import java.util.List;

import com.bbw.god.event.BaseEventParam;

import lombok.Getter;
import lombok.Setter;

/**
* @author lwb  
* @date 2019年7月8日  
* @version 1.0  
*/
@Getter
@Setter
public class EPChanjieGainHead extends BaseEventParam {
	private Integer headlv = 0;//头衔等级 外门弟子0    内门弟子1    真传弟子2   渡劫地仙3    大乘天仙4    大罗金仙5    护教法王6    掌教7
	private List<Long> uids;//获得的玩家

	public static EPChanjieGainHead instance(BaseEventParam bep, int level, List<Long> uids) {
		EPChanjieGainHead ev = new EPChanjieGainHead();
		ev.setHeadlv(level);
		ev.setUids(uids);
		ev.setValues(bep);
		return ev;
	}
}
