package com.bbw.god.gameuser.res.dice;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 体力事件监听
 * 
 * @author suhq
 * @date 2019-06-19 09:26:35
 */
@Component
public class DiceListener {
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private UserDiceCapacityService userDiceCapacityService;
	@EventListener
	public void addDice(DiceAddEvent event) {
		EPDiceAdd ep = event.getEP();
		GameUser gu = gameUserService.getGameUser(ep.getGuId());

		gu.addDice(ep.getAddDice());
		RDCommon rd = ep.getRd();
		if (ep.getWay() == WayEnum.GU_LEVEL_UP) {// 升级需要特殊处理
			rd.gainLevelAward().setAddedDices(ep.getAddDice());
		} else {
			rd.addDice(ep.getAddDice());
		}
	}

	@EventListener
	public void deductDice(DiceDeductEvent event) {
		EPDiceDeduct ep = event.getEP();
		GameUser gu = gameUserService.getGameUser(ep.getGuId());
		gu.deductDice(ep.getDeductDice());
		ep.getRd().setDeductedDices(ep.getDeductDice());
	}

	@Async
	@EventListener
	public void diceFull(DiceFullEvent event){
		EPDiceFull ep = event.getEP();
		int dice = ep.getAddDice();
		if (dice>0){
			userDiceCapacityService.changeUserDice(ep.getGuId(), dice);
		}
	}
}
