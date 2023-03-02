package com.bbw.god.event;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-02-27 15:20
 */
public interface IEventParam {
	/**
	 * 获取事件参数
	 * 
	 * @return
	 */
	<T extends BaseEventParam> T getEP();
}
