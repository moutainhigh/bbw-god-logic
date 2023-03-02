package com.bbw.god.server.fst.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * 封神台守卫成功事件
 * 
 * @author lzc
 * @date 2021-07-12 14:25:01
 */
public class FstGuardWinEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public FstGuardWinEvent(BaseEventParam source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public BaseEventParam getEP() {
		return (BaseEventParam)getSource();
	}

}
