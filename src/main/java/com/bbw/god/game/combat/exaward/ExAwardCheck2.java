package com.bbw.god.game.combat.exaward;

import com.bbw.god.game.combat.data.Combat;
import org.springframework.stereotype.Service;

/**
 * 6回合内赢得战斗
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-08-07 15:11
 */
@Service
public class ExAwardCheck2 implements ExAwardCheck {
	@Override
	public boolean support(int id) {
		return YeGExawardEnum.WIN_6_ROUND.getVal() == id;
	}

	@Override
	public String getDescriptor() {
		return "6回合内赢得战斗";
	}

	@Override
	public boolean check(Combat combat) {
		// 结算时会多加1回合 所以需要-1
		return (combat.getRound() - 1) <= 6;
	}

}
