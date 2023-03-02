package com.bbw.god.activityrank.server.eleconsume;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.gameuser.res.ele.EPEleDeduct;
import com.bbw.god.gameuser.res.ele.EVEle;
import com.bbw.god.gameuser.res.ele.EleDeductEvent;

/**
 * 元素消耗榜监听器
 * 
 * @author suhq
 * @date 2019年3月4日 下午4:15:07
 */
@Component
public class EleConsumeRankListener {
	private ActivityRankEnum rankType = ActivityRankEnum.ELE_CONSUME_RANK;

	@Autowired
	private ActivityRankService activityRankService;

	@EventListener
	@Order(1000)
	public void deductEle(EleDeductEvent event) {
		EPEleDeduct ep = event.getEP();
		// 元素消耗榜
		// 本次总消耗
		int sumEle = ep.getDeductEles().stream().mapToInt(EVEle::getNum).sum();
		activityRankService.incrementRankValue(ep.getGuId(), sumEle, rankType);
	}
}
