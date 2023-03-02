package com.bbw.god.city.yed;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author suchaobin
 * @description 野地相关接口
 * @date 2020/6/1 13:51
 **/
@RestController
public class YeDController extends AbstractController {
	@Autowired
	private YeDEventProcessorFactory processorFactory;
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private YeDProcessor yeDProcessor;

	@GetMapping(CR.YeD.EXTRA_OPERATION)
	public RDAdvance extraOperation(int eventId, Integer num) {
		GameUser gu = gameUserService.getGameUser(getUserId());
		RDArriveYeD arriveYeD = TimeLimitCacheUtil.getArriveCache(gu.getId(), RDArriveYeD.class);
		if (arriveYeD.getIsAlreadyExtraOperation()) {
			throw new ExceptionForClientTip("yeD.event.already.extra.operation");
		}
		ExtraYeDEventProcessor processor = processorFactory.getExtraProcessorById(eventId);
		RDAdvance rd = new RDAdvance();
		processor.extraOperation(gu, num, arriveYeD, rd);
		arriveYeD.setIsAlreadyExtraOperation(true);
		rd.setArriveYeD(arriveYeD);
		TimeLimitCacheUtil.setArriveCache(getUserId(), arriveYeD);
		return rd;
	}

	@GetMapping(CR.YeD.LIST_ADVENTURES)
	public RDAdventures listAdventures() {
		return yeDProcessor.listAdventures(getUserId());
	}

	@GetMapping(CR.YeD.GET_ADVENTURE_INFO)
	public RDArriveYeD getAdventureInfo(long dataId) {
		return yeDProcessor.getAdventureInfo(getUserId(), dataId);
	}

	@GetMapping(CR.YeD.GAIN_CARD_EXP)
	public RDCommon gainCardExp(long dataId) {
		return yeDProcessor.gainCardExp(getUserId(), dataId, new RDCommon());
	}
}
