package com.bbw.god.city.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.city.lut.LtBackEvent;
import com.bbw.god.city.lut.LtTributeEvent;
import com.bbw.god.city.miaoy.EPMiaoYDrawEnd;
import com.bbw.god.city.miaoy.MiaoYDrawEndEvent;
import com.bbw.god.city.mixd.EPOutMxd;
import com.bbw.god.city.mixd.OutMxdEvent;
import com.bbw.god.city.nvwm.EPNvWMDonate;
import com.bbw.god.city.nvwm.NwmDonateEvent;
import com.bbw.god.city.taiyf.TyfFillEvent;
import com.bbw.god.city.yed.EPYeDTrigger;
import com.bbw.god.city.yed.YeDTriggerEvent;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.EventParam;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;

import java.util.List;

/**
 * 城市事件发布类
 * 
 * @author suhq
 * @date 2018年11月24日 下午7:54:14
 */
public class CityEventPublisher {

	public static void publCityArriveEvent(Long guId, Integer position, WayEnum way, RDAdvance rd) {
		SpringContextUtil.publishEvent(new CityArriveEvent(new EventParam<Integer>(guId, position, way, rd)));
	}

	public static void pubUserCityAddEvent(Long guId, EPCityAdd param, RDFightResult rd) {
		SpringContextUtil.publishEvent(new UserCityAddEvent(new EventParam<EPCityAdd>(guId, param, rd)));
	}

	public static void pubOutMxdEvent(List<Integer> guAwardPos, BaseEventParam bep) {
		SpringContextUtil.publishEvent(new OutMxdEvent(new EPOutMxd(guAwardPos, bep)));
	}

	public static void pubMiaoYDrawEndEvent(long guId, EPMiaoYDrawEnd ep, RDCommon rd) {
		SpringContextUtil.publishEvent(new MiaoYDrawEndEvent(new EventParam<EPMiaoYDrawEnd>(guId, ep, rd)));
	}

	public static void pubTyfFillEvent(long guId, RDCommon rd) {
		SpringContextUtil.publishEvent(new TyfFillEvent(new EventParam<Integer>(guId, rd)));
	}

	public static void pubLtTributeEvent(long guId, RDCommon rd) {
		SpringContextUtil.publishEvent(new LtTributeEvent(new EventParam<Integer>(guId, rd)));
	}

	public static void pubLtBackEvent(long guId, RDCommon rd) {
		SpringContextUtil.publishEvent(new LtBackEvent(new EventParam<Integer>(guId, rd)));
	}

	public static void pubNwmDonateEvent(int satisfaction, BaseEventParam bep) {
		SpringContextUtil.publishEvent(new NwmDonateEvent(new EPNvWMDonate(satisfaction, bep)));
	}

	/**
	 * 发布野地事件
	 *
	 * @param bep
	 * @param ep
	 */
	public static void pubYeDTrigger(BaseEventParam bep, EPYeDTrigger ep) {
		SpringContextUtil.publishEvent(new YeDTriggerEvent(new EventParam<EPYeDTrigger>(bep, ep)));
	}

	/**
	 * 通过城池关卡事件
	 * @param param
	 */
	public static void pubUserPassCityLevelEvent(EPPassCityLevel param) {
		SpringContextUtil.publishEvent(new UserPassCityLevelEvent(param));
	}
}
