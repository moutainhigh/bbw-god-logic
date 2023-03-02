package com.bbw.god.mall.lottery;

import com.bbw.god.game.data.GameDataService;
import com.bbw.god.mall.lottery.event.EPLotteryAwardSend;
import com.bbw.god.mall.lottery.event.LotteryAwardSendEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author suchaobin
 * @description 奖券奖励监听器
 * @date 2020/7/15 16:41
 **/
@Component
@Slf4j
public class LotteryAwardListener {
	@Autowired
	private LotteryService lotteryService;
	@Autowired
	private GameDataService gameDataService;

	private static final ScheduledThreadPoolExecutor EXECUTOR = new ScheduledThreadPoolExecutor(1);

	@Order(1000)
	@EventListener
	public void sendAward(LotteryAwardSendEvent event) {
		EPLotteryAwardSend ep = event.getEP();
		Integer group = ep.getGroup();
		EXECUTOR.schedule(new Runnable() {
			@Override
			public void run() {
				lotteryService.sendAward(group);
			}
		}, 60, TimeUnit.SECONDS);
		GameLottery gameLottery = lotteryService.getCurGameLottery(group);
		LotteryResult result = LotteryResult.getInstance(gameLottery.gainBoughtNumbers());
		gameLottery.setResult(result);
		gameLottery.setDrawing(true);
		gameDataService.updateGameData(gameLottery);
	}
}
