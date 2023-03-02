package com.bbw.god.gameuser.special.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDCommon;

import java.util.List;

/**
 * 特产事件发布器
 * 
 * @author suhq
 * @date 2018年11月24日 下午9:51:19
 */
public class SpecialEventPublisher {

	public static void pubSpecialAddEvent(long guId, List<EVSpecialAdd> specials, WayEnum way, RDCommon rd) {
		BaseEventParam bep = new BaseEventParam(guId, way, rd);
		EPSpecialAdd ep = new EPSpecialAdd(bep, specials);
		SpringContextUtil.publishEvent(new SpecialAddEvent(ep));
	}

	public static void pubSpecialDeductEvent(EPSpecialDeduct ep) {
		SpringContextUtil.publishEvent(new SpecialDeductEvent(ep));
	}

	public static void pubSpecialLockEvent(EPPocketSpecial ep) {
		SpringContextUtil.publishEvent(new SpecialLockEvent(ep));
	}

	public static void pubSpecialUnLockEvent(EPPocketSpecial ep) {
		SpringContextUtil.publishEvent(new SpecialUnLockEvent(ep));
	}

	public static void pubSpecialSynthesisEvent(BaseEventParam bep) {
		SpringContextUtil.publishEvent(new SpecialSynthesisEvent(EPSpecialSynthesis.getInstance(bep)));
	}
}
