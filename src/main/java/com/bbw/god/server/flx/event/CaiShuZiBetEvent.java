package com.bbw.god.server.flx.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 猜数字下注事件
 * @date 2020/4/23 10:11
 */
public class CaiShuZiBetEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = -5259813670340215856L;

	public CaiShuZiBetEvent(EPCaiShuZiBet source) {
		super(source);
	}

	/**
	 * 获取事件参数
	 *
	 * @return
	 */
	@Override
	public EPCaiShuZiBet getEP() {
		return (EPCaiShuZiBet) getSource();
	}
}
