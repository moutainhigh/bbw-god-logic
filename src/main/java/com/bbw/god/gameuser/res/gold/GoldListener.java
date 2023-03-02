package com.bbw.god.gameuser.res.gold;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDCommon.RDResAddInfo;

@Component
public class GoldListener {
	@Autowired
	private GameUserService gameUserService;

	@EventListener
	public void addGold(GoldAddEvent event) {
		EPGoldAdd ep = event.getEP();
		int addGold = ep.gainAddGold();
		GameUser gameUser = gameUserService.getGameUser(ep.getGuId());
		gameUser.addGold(addGold);
		RDCommon rd = ep.getRd();
		if (ep.getWay() == WayEnum.GU_LEVEL_UP) {// 升级需要特殊处理
			rd.gainLevelAward().setAddedGold(addGold);
		} else {
			rd.addGold(addGold);
			List<RDResAddInfo> rdAddGoldInfos = ep.getAddGolds().stream().map(tmp -> new RDResAddInfo(tmp.getWayType().getValue(), tmp.getValue())).collect(Collectors.toList());
			rd.setAddGolds(rdAddGoldInfos);
		}
	}

	@EventListener
	public void deductGold(GoldDeductEvent event) {
		EPGoldDeduct ep = event.getEP();
		GameUser gameUser = gameUserService.getGameUser(ep.getGuId());
		gameUser.deductGold(ep.getDeductGold());
		RDCommon rd = ep.getRd();
		rd.addGold(-ep.getDeductGold());
	}
}
