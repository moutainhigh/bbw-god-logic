package com.bbw.god.game.combat.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 战斗胜利事件
 * 
 * @author suhq
 * @date 2019年4月18日 下午4:52:31
 */
public class CombatFightWinEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = -4084008628712398500L;

	public CombatFightWinEvent(EPFightEnd source) {
		super(source);
	}

	@Override
	public EPFightEnd getEP() {
		return (EPFightEnd)getSource();
	}
}
