package com.bbw.god.game.combat.exaward;

import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import org.springframework.stereotype.Service;

/**
 * 胜利时我方墓地卡牌少于3张
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-08-07 15:11
 */
@Service
public class ExAwardCheck4 implements ExAwardCheck {
	@Override
	public boolean support(int id) {
		return YeGExawardEnum.WIN_LOSE_3_CARD.getVal() == id;
	}

	@Override
	public String getDescriptor() {
		return "胜利时我方坟场卡牌少于3张";
	}

	@Override
	public boolean check(Combat combat) {
		Player me = combat.getP1();
		return me.getDiscard().size() < 3;
	}

}
