package com.bbw.god.city.miaoy.hexagram.event;

import com.bbw.god.event.IEventParam;
import org.springframework.context.ApplicationEvent;

/** 64卦事件
* @author 作者 ：lzc
* @version 创建时间：2021年04月13日
* 类说明 
*/
public class HexagramEvent extends ApplicationEvent implements IEventParam{

	private static final long serialVersionUID = 1L;

	public HexagramEvent(EPHexagram source) {
		super(source);
	}

	@SuppressWarnings("unchecked")
	@Override
	public EPHexagram getEP() {
		return (EPHexagram)getSource();
	}
}
