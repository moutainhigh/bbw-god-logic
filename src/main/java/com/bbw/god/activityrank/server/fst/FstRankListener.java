package com.bbw.god.activityrank.server.fst;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.event.EventParam;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.server.fst.event.EVFstWin;
import com.bbw.god.server.fst.event.FstWinEvent;
import com.bbw.god.server.fst.event.IntoFstEvent;
import com.bbw.god.server.fst.server.FstServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Async
@Component
public class FstRankListener {
	private ActivityRankEnum rankType = ActivityRankEnum.FST_RANK;

	@Autowired
	private ActivityRankService activityRankService;
	@Autowired
	private FstServerService fstServerService;
	@Autowired
	private GameUserService gameUserService;
	@EventListener
	@Order(1000)
	public void fstWin(FstWinEvent event) {
		EventParam<EVFstWin> ep = (EventParam<EVFstWin>) event.getSource();
		long guId = ep.getGuId();
		long opponent = ep.getValue().getOppId();
		int sid = gameUserService.getActiveSid(guId);
		int myRank = fstServerService.getFstRank(guId);
		int opponentRank = fstServerService.getFstRank(opponent);
		activityRankService.setRankValue(guId, sid,myRank, rankType);
		activityRankService.setRankValue(opponent, sid,opponentRank, rankType);
	}

	@EventListener
	@Order(1000)
	public void intoFst(IntoFstEvent event) {
		EventParam<Integer> ep = (EventParam<Integer>) event.getSource();
		long guId = ep.getGuId();
		int sid = gameUserService.getActiveSid(guId);
		int myRank = fstServerService.getFstRankWithIntoRanking(guId);
		activityRankService.setRankValue(guId, sid, myRank, rankType);
	}
}
