package com.bbw.god.server.flx.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 押押乐下注事件
 * @date 2020/4/23 10:14
 */
public class YaYaLeBetEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 7886932628933418675L;

	public YaYaLeBetEvent(EPYaYaLeBet source) {
		super(source);
	}

	/**
	 * 获取事件参数
	 *
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public EPYaYaLeBet getEP() {
		return (EPYaYaLeBet) getSource();
	}
}
