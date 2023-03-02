package com.bbw.god.game.combat.exaward;

import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.card.BattleCard;
import org.springframework.stereotype.Service;

/**
 * 打掉对方的所有卡牌
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-08-07 15:11
 */
@Service
public class ExAwardCheck3 implements ExAwardCheck {

	@Override
	public boolean support(int id) {
		return YeGExawardEnum.WIN_KILLED_ALL_CARDS.getVal() == id;
	}

	@Override
	public String getDescriptor() {
		return "打掉对方的所有卡牌";
	}

	@Override
	public boolean check(Combat combat) {
		Player oppo = combat.getP2();
		//牌堆没有牌
		if (!oppo.getDrawCards().isEmpty()) {
			return false;
		}
		//阵位没有牌
		for (BattleCard card : oppo.getPlayingCards()) {
			if (null != card) {
				return false;
			}
		}
		//手牌没有牌
		for (BattleCard card : oppo.getHandCards()) {
			if (null != card) {
				return false;
			}
		}
		return true;
	}

}
