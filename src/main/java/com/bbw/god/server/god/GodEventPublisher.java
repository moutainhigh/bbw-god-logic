package com.bbw.god.server.god;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDAdvance;
import com.bbw.god.rd.RDCommon;

/**
 * 神仙事件发布器
 * 
 * @author suhq
 * @date 2018年11月24日 下午9:04:30
 */
public class GodEventPublisher {

	public static void pubGodAttachEvent(Long guId, Integer position, WayEnum way, RDAdvance rd) {
		SpringContextUtil.publishEvent(new GodAttachEvent(new EventParam<Integer>(guId, position, way, rd)));
	}

	/**
	 * 附体新的神仙
	 * 
	 * @param guId
	 * @param serverGod
	 * @param rd
	 */
	public static void pubAttachNewGodEvent(long guId, ServerGod serverGod, RDCommon rd) {
		SpringContextUtil.publishEvent(new AttachNewGodEvent(new EventParam<ServerGod>(guId, serverGod, rd)));
	}

	/**
	 * 附体新的卦象神仙
	 *
	 * @param guId
	 * @param serverGod
	 * @param rd
	 * @param effect 特别的效果
	 */
	public static void pubAttachHexagramGodEvent(long guId, ServerGod serverGod, RDCommon rd, Integer effect) {
		EventParam ep=new EventParam<ServerGod>(guId, serverGod, rd);
		ep.setWay(WayEnum.HEXAGRAM);
		AttachNewGodEvent event = new AttachNewGodEvent(ep);
		event.setEffect(effect);
		SpringContextUtil.publishEvent(event);
	}

	/**
	 * 附体新的卦象神仙
	 *
	 * @param guId
	 * @param serverGod
	 * @param rd
	 * @param effect 特别的效果
	 */
	public static void pubAttachCantUseSSFGodEvent(long guId, ServerGod serverGod, RDCommon rd, Integer effect) {
		EventParam ep=new EventParam<ServerGod>(guId, serverGod, rd);
		ep.setWay(WayEnum.HEXAGRAM);
		AttachNewGodEvent event = new AttachNewGodEvent(ep);
		event.setCanUseSSF(false);
		event.setEffect(effect);
		SpringContextUtil.publishEvent(event);
	}

	/**
	 * 附体新的卦象神仙
	 *
	 * @param guId
	 * @param serverGod
	 * @param rd
	 */
	public static void pubAttachHexagramGodEvent(long guId, ServerGod serverGod, RDCommon rd) {
		pubAttachHexagramGodEvent(guId, serverGod, rd,null);
	}
}
