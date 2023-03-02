package com.bbw.god.game.combat.exaward;

import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import org.springframework.stereotype.Service;

/**
 * 前2回合不放置卡牌并赢得战斗
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-08-07 15:11
 */
@Service
public class ExAwardCheck5 implements ExAwardCheck {
	@Override
	public boolean support(int id) {
		return YeGExawardEnum.WIN_NO_CARD_BEFORE_2_ROUND.getVal() == id;
	}

	@Override
	public String getDescriptor() {
		return "前2回合不放置卡牌并赢得战斗";
	}

	@Override
	public boolean check(Combat combat) {
		Player me = combat.getP1();
		long flag = me.getStatistics().getDeployCardsFlag();
		//最后两位二进制数为0
		boolean b = 0 == (flag & 3);
		return b;
	}

}
