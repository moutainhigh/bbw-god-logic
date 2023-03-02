package com.bbw.god.gameuser.res.diamond;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDCommon.RDResAddInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 钻石监听
 *
 * @author: huanghb
 * @date: 2022/6/15 17:16
 */
@Component
public class DiamondListener {
	@Autowired
	private GameUserService gameUserService;

	@EventListener
	public void addDiamond(DiamondAddEvent event) {
		EPDiamondAdd ep = event.getEP();
		int addDiamond = ep.gainAddDiamond();
		GameUser gameUser = gameUserService.getGameUser(ep.getGuId());
		gameUser.addDiamond(addDiamond);
		RDCommon rd = ep.getRd();
		if (ep.getWay() == WayEnum.GU_LEVEL_UP) {// 升级需要特殊处理
			rd.gainLevelAward().setAddedGold(addDiamond);
		} else {
			rd.addDiamond(addDiamond);
			List<RDResAddInfo> rdAddGoldInfos = ep.getAddDiamond().stream().map(tmp -> new RDResAddInfo(tmp.getWayType().getValue(), tmp.getValue())).collect(Collectors.toList());
			rd.setAddDiamonds(rdAddGoldInfos);
		}
	}

	@EventListener
	public void deductDiamond(DiamondDeductEvent event) {
		EPDiamondDeduct ep = event.getEP();
		GameUser gameUser = gameUserService.getGameUser(ep.getGuId());
		gameUser.deductDiamond(ep.getDeductDiamond());
		RDCommon rd = ep.getRd();
		rd.addDiamond(-ep.getDeductDiamond());
	}
}
