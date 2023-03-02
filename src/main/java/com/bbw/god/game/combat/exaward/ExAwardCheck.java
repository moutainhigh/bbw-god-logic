package com.bbw.god.game.combat.exaward;

import com.bbw.god.game.combat.data.Combat;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-08-07 15:08
 */
public interface ExAwardCheck {

	public boolean support(int id);

	public String getDescriptor();

	public boolean check(Combat combat);

}
