package com.bbw.god.game.combat.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年11月25日 上午10:18:47 
* 类说明 战斗发起事件
*/
public class CombatInitiateEvent extends ApplicationEvent implements IEventParam{
	private static final long serialVersionUID = 1L;

	public CombatInitiateEvent(EPCombat source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPCombat getEP() {
		return (EPCombat)getSource();
	}

}
