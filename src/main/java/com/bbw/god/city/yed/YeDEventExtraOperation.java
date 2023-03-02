package com.bbw.god.city.yed;

import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDAdvance;

/**
 * @author suchaobin
 * @description 野地事件拓展操作接口
 * @date 2020/6/10 14:23
 **/
public interface YeDEventExtraOperation {
	/**
	 * 额外操作（如：推荐特产，反抗逃跑等）
	 *
	 * @param gu        玩家对象
	 * @param arriveYeD 野地信息
	 */
	void extraOperation(GameUser gu, Integer num, RDArriveYeD arriveYeD, RDAdvance rd);
}
