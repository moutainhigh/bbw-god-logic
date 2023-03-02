package com.bbw.god.server.fst.event;

import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.server.fst.server.FstServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FstListener {
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private FstServerService fstService;

//	@EventListener
//	@Order(1000)
//	public void levelUp(GuLevelUpEvent event) {
//		EPGuLevelUp ep = event.getEP();
//		GameUser gu = gameUserService.getGameUser(ep.getGuId());
//		// 解锁封神台
//		CfgFst cfgFst = Cfg.I.getUniqueConfig(CfgFst.class);
//		int limitLevel = cfgFst.getUnlockLevel();
//		if (gu.getLevel() == limitLevel) {
//			FstEventPublisher.pubIntoFstEvent(gu.getId());
//		}
//	}
//
//	@EventListener
//	public void intoFst(IntoFstEvent event) {
//		EventParam<Integer> ep = (EventParam<Integer>) event.getSource();
//		long guId = ep.getGuId();
//		fstService.intoFstRanking(gameUserService.getActiveSid(guId), guId);
//	}
}
