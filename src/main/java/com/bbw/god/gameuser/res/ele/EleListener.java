package com.bbw.god.gameuser.res.ele;

import com.bbw.god.game.config.WayEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;

@Component
public class EleListener {
	@Autowired
	private GameUserService gameUserService;

	@EventListener
	public void addEle(EleAddEvent event) {
		EPEleAdd ep = event.getEP();
		GameUser gameUser = gameUserService.getGameUser(ep.getGuId());
		ep.getAddEles().stream().forEach(ele -> {
			gameUser.addEle(ele.getType(), ele.getNum(),ep.getWay());
			ep.getRd().addEle(ele.getType(), ele.getNum());
		});
	}

	@EventListener
	public void deductEle(EleDeductEvent event) {
		EPEleDeduct ep = event.getEP();
		GameUser gu = gameUserService.getGameUser(ep.getGuId());
		ep.getDeductEles().stream().forEach(ele -> {
			gu.deductEle(ele.getType(), ele.getNum());
			ep.getRd().addEle(ele.getType(), -ele.getNum());
		});
	}
}
