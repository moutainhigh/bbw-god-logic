package com.bbw.god.mall.lottery;

import com.bbw.common.Rst;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author suchaobin
 * @description 奖券接口
 * @date 2020/7/6 10:25
 **/
@RestController
public class LotteryCtrl extends AbstractController {
	@Autowired
	private LotteryService lotteryService;

	/**
	 * 进入奖券界面
	 *
	 * @return
	 */
	@RequestMapping(CR.Lottery.ENTER_LOTTERY)
	public RDLotteryInfo enterLottery() {
		return lotteryService.enterLottery(getUserId());
	}

	/**
	 * 下注
	 *
	 * @param numbers
	 * @return
	 */
	@RequestMapping(CR.Lottery.BET)
	public Rst bet(String numbers) {
		return lotteryService.bet(getUserId(), numbers);
	}
}
