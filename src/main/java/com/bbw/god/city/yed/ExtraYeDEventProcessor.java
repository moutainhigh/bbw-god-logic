package com.bbw.god.city.yed;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDAdvance;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 野地事件拓展处理器
 * @date 2020/6/10 14:43
 **/
@Service
public abstract class ExtraYeDEventProcessor extends BaseYeDEventProcessor implements YeDEventExtraOperation {
	/**
	 * 额外操作（如：推荐特产，反抗逃跑等）
	 *
	 * @param gu        玩家对象
	 * @param arriveYeD 野地信息
	 */
	public abstract void extraOperation(GameUser gu, Integer num, RDArriveYeD arriveYeD, RDAdvance rd);
}
