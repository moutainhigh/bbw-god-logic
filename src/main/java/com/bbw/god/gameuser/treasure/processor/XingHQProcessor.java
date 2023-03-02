package com.bbw.god.gameuser.treasure.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.fst.FstRanking;
import com.bbw.god.server.fst.FstTool;
import com.bbw.god.server.fst.game.FstGameRanking;
import com.bbw.god.server.fst.game.FstGameService;
import com.bbw.god.server.fst.server.FstServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 杏黄旗 TODO:待做
 * 
 * @author suhq
 * @date 2018年11月28日 下午5:01:13
 */
@Service
public class XingHQProcessor extends TreasureUseProcessor {
	@Autowired
	private FstGameService fstGameService;
	@Autowired
	private FstServerService fstServerService;
	@Autowired
	private ServerDataService serverDataService;
	@Autowired
	private GameDataService gameDataService;
	
	public XingHQProcessor() {
		this.treasureEnum = TreasureEnum.XHQ;
		this.isAutoBuy = true;
	}

	@Override
	public void check(GameUser gu, CPUseTreasure param) {
		if (param.getIsGameFst()==null||param.getIsGameFst()!=1){
			if (!fstServerService.hasJoinFst(gu.getId())) {
				throw new ExceptionForClientTip("fst.need.to.jion");
			}
		}else {
			if (!fstGameService.hasJoinFst(gu.getId())) {
				throw new ExceptionForClientTip("fst.not.join");
			}
		}
	}

	@Override
	public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
		Integer freeTimes = FstTool.getCfg().getFreeTimes();
		Integer fightTimes = 0;
		if (param.getIsGameFst()==null||param.getIsGameFst()!=1){
			FstRanking fst = fstServerService.getOrCreateFstRanking(gu.getId());
			fst.addChallengeNum();
			fightTimes=fst.getTodayFightTimes();
			serverDataService.updateServerData(fst);
		}else {
			FstGameRanking ranking = fstGameService.getOrCreateFstGameRanking(gu.getId());
			ranking.addChallengeNum();
			fightTimes=ranking.getTodayFightTimes();
			gameDataService.updateGameData(ranking);
		}
		rd.setPvpTimes(freeTimes-fightTimes);
	}
}
