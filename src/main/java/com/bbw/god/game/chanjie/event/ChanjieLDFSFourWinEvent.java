package com.bbw.god.game.chanjie.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.IEventParam;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年3月2日 下午3:46:01 类说明 阐截斗法四胜事件
 */
public class ChanjieLDFSFourWinEvent extends ApplicationEvent implements IEventParam {
	private static final long serialVersionUID = 1L;

	public ChanjieLDFSFourWinEvent(EPChanjieLDFSFourWin source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPChanjieLDFSFourWin getEP() {
		return (EPChanjieLDFSFourWin) getSource();
	}

}
