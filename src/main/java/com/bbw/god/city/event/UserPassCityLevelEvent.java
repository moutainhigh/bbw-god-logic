package com.bbw.god.city.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/** 
* @author 作者 ：lwb
* 类说明 通过城池关卡事件
*/
public class UserPassCityLevelEvent extends ApplicationEvent implements IEventParam{
	private static final long serialVersionUID = 1L;

	public UserPassCityLevelEvent(EPPassCityLevel source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPPassCityLevel getEP() {
		return (EPPassCityLevel)getSource();
	}

}
