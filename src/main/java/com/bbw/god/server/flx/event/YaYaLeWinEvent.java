package com.bbw.god.server.flx.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/**
 * @author suchaobin
 * @description 押押乐头奖事件
 * @date 2020/2/24 15:09
 */
public class YaYaLeWinEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1218431879263760260L;

	public YaYaLeWinEvent(EPYaYaLeWin source) {
		super(source);
	}

	@Override
	public EPYaYaLeWin getEP() {
		return (EPYaYaLeWin) getSource();
	}
}
