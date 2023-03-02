package com.bbw.god.mall.snatchtreasure;

import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.snatchtreasure.SnatchBehaviorStatisticEvent;
import com.bbw.god.gameuser.statistic.behavior.snatchtreasure.SnatchTreasureStatistic;
import com.bbw.god.gameuser.statistic.event.EPBehaviorStatistic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author suchaobin
 * @description 夺宝监听器
 * @date 2020/7/2 13:54
 **/
@Component
@Async
@Slf4j
public class SnatchTreasureListener {
	@Autowired
	private GameUserService gameUserService;

	@Order(1000)
	@EventListener
	public void snatchTreasureDraw(SnatchBehaviorStatisticEvent event) {
		try {
			EPBehaviorStatistic ep = event.getEP();
			Long uid = ep.getGuId();
			BehaviorStatistic behaviorStatistic = ep.getBehaviorStatistic();
			if (!(behaviorStatistic instanceof SnatchTreasureStatistic)) {
				return;
			}
			SnatchTreasureStatistic statistic = (SnatchTreasureStatistic) behaviorStatistic;
			Integer weekDrawTimes = statistic.getWeekDrawTimes();
			List<CfgSnatchTreasureBox> boxes = SnatchTreasureTool.getSnatchTreasureBoxes();
			UserSnatchTreasureBox snatchTreasureBox = gameUserService.getSingleItem(uid, UserSnatchTreasureBox.class);
			if (null == snatchTreasureBox) {
				snatchTreasureBox = UserSnatchTreasureBox.getInstance(uid);
				gameUserService.addItem(uid, snatchTreasureBox);
			}
			for (CfgSnatchTreasureBox box : boxes) {
				if (weekDrawTimes >= box.getValue()) {
					snatchTreasureBox.accomplish(box.getId());
				}
			}
			gameUserService.updateItem(snatchTreasureBox);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
