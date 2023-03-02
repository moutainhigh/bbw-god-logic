package com.bbw.god.game.combat.exaward;

import com.bbw.god.game.combat.data.Combat;
import org.springframework.stereotype.Service;

/**
 * 击败精英怪
 * 
 *
 */
@Service
public class ExAwardCheck6 implements ExAwardCheck {
	@Override
	public boolean support(int id) {
		return YeGExawardEnum.WIN_ELITE.getVal() == id;
	}

	@Override
	public String getDescriptor() {
		return YeGExawardEnum.WIN_ELITE.getMemo();
	}

	@Override
	public boolean check(Combat combat) {
		return true;
	}

}
