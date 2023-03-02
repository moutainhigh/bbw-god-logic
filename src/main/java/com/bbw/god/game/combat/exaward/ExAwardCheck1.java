package com.bbw.god.game.combat.exaward;

import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import org.springframework.stereotype.Service;

/**
 * 己方不损血赢得战斗
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-08-07 15:11
 */
@Service
public class ExAwardCheck1 implements ExAwardCheck {

	@Override
	public boolean support(int id) {
		return YeGExawardEnum.WIN_NOT_LOSE_BLOOD.getVal() == id;
	}

	@Override
	public String getDescriptor() {
		return "己方不损血赢得战斗";
	}

	@Override
	public boolean check(Combat combat) {
		Player me = combat.getP1();
		return me.getHp() == me.getMaxHp();
	}

}
