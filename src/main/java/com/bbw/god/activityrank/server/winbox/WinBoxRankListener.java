package com.bbw.god.activityrank.server.winbox;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.event.EventParam;

/**
 * 宝箱榜监听器
 * 
 * @author suhq
 * @date 2019年3月4日 下午4:15:07
 */
@Component
public class WinBoxRankListener {
	private ActivityRankEnum rankType = ActivityRankEnum.WIN_BOX_RANK;

	@Autowired
	private ActivityRankService activityRankService;

	@EventListener
	public void openBox(ARWinBoxEvent event) {
		EventParam<Integer> ep = (EventParam<Integer>) event.getSource();
		long guId = ep.getGuId();
		activityRankService.incrementRankValue(guId, ep.getValue(), rankType);
	}

}
