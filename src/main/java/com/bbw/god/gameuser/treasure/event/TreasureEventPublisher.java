package com.bbw.god.gameuser.treasure.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.yuxg.UserFuTu;
import com.bbw.god.rd.RDCommon;

import java.util.ArrayList;
import java.util.List;

/**
 * 法宝事件发布器
 *
 * @author suhq
 * @date 2018年11月24日 下午9:38:26
 */
public class TreasureEventPublisher {

	public static void pubTAddEvent(long guId, int treasureId, int treasureNum, WayEnum way, RDCommon rd) {
		EVTreasure ev = new EVTreasure(treasureId, treasureNum);
		List<EVTreasure> evs = new ArrayList<>();
		evs.add(ev);
		pubTAddEvent(guId, evs, way, rd);
	}

	public static void pubTAddEvent(long guId, List<EVTreasure> treasures, WayEnum way, RDCommon rd) {
		BaseEventParam bep = new BaseEventParam(guId, way, rd);
		EPTreasureAdd ep = new EPTreasureAdd(bep, treasures);
		SpringContextUtil.publishEvent(new TreasureAddEvent(ep));
	}

	public static void pubTDeductEvent(long guId, int treasureId, int treasureNum, WayEnum way, RDCommon rd) {
		if (treasureNum <= 0) {
			return;
		}
		EVTreasure ev = new EVTreasure(treasureId, treasureNum);
		BaseEventParam bep = new BaseEventParam(guId, way, rd);
		EPTreasureDeduct ep = new EPTreasureDeduct(bep, ev);
		SpringContextUtil.publishEvent(new TreasureDeductEvent(ep));
	}

	public static void pubTEffectAddEvent(long guId, Integer treasureId, Integer addEffect, WayEnum way, RDCommon rd) {
		BaseEventParam bep = new BaseEventParam(guId, way, rd);
		SpringContextUtil.publishEvent(new TreasureEffectAddEvent(new EPTreasureEffectAdd(bep, treasureId, addEffect)));
	}

	/**
	 * 符图添加事件
	 *
	 * @param guId
	 * @param fuTuId
	 * @param num
	 * @param way
	 * @param rd
	 */
	public static void pubTAddFuTuEvent(long guId, int fuTuId, int num, WayEnum way, RDCommon rd){
		BaseEventParam bep = new BaseEventParam(guId, way, rd);
		EPFuTuAdd ep = new EPFuTuAdd(bep, fuTuId, num);
		SpringContextUtil.publishEvent(new FuTuAddEvent(ep));
	}

	/**
	 * 符图扣除事件
	 *
	 * @param guId
	 * @param userFuTu
	 * @param num
	 * @param way
	 * @param rd
	 */
	public static void pubTDeductFuTuEvent(long guId, UserFuTu userFuTu, int num, WayEnum way, RDCommon rd){
		BaseEventParam bep = new BaseEventParam(guId, way, rd);
		EPFuTuDeduct ep = new EPFuTuDeduct(bep, userFuTu, num);
		SpringContextUtil.publishEvent(new FuTuDeductEvent(ep));
	}

	/**
	 * 漫步鞋专用
	 *
	 * @param guId
	 * @param remainEffect -9999关闭漫步鞋；-1路口直接选择方向；0不可选择方向；>0剩余步数
	 * @param way
	 */
	public static void pubMBXEffectSetEvent(long guId, int remainEffect, WayEnum way) {
		BaseEventParam bep = new BaseEventParam(guId, way);
		int treasureId = TreasureEnum.MBX.getValue();
		SpringContextUtil.publishEvent(new TreasureEffectSetEvent(new EPTreasureEffectSet(bep, treasureId, remainEffect)));
	}

	public static void pubTEffectDeductEvent(long guId, Integer treasureId, Integer deductEffect, WayEnum way) {
		BaseEventParam bep = new BaseEventParam(guId, way);
		SpringContextUtil.publishEvent(new TreasureEffectDeductEvent(new EPTreasureEffectDeduct(bep, treasureId, deductEffect)));
	}

	public static void pubTRecordAddEvent(long guId, int treasureId, WayEnum way) {
		BaseEventParam bep = new BaseEventParam(guId, way);
		EPTreasureRecordAdd ep = new EPTreasureRecordAdd(bep, treasureId);
		SpringContextUtil.publishEvent(new TreasureRecordAddEvent(ep));
	}

	public static void pubTRecordDelEvent(long guId, int treasureId, WayEnum way) {
		if (0 == treasureId) {
			return;
		}
		BaseEventParam bep = new BaseEventParam(guId, way);
		EPTreasureRecordDel ep = new EPTreasureRecordDel(bep, treasureId);
		SpringContextUtil.publishEvent(new TreasureRecordDelEvent(ep));
	}

	public static void pubTRecordResetEvent(long guId, int treasureId, WayEnum way) {
		BaseEventParam bep = new BaseEventParam(guId, way);
		EPTreasureRecordReset ep = new EPTreasureRecordReset(bep, treasureId);
		SpringContextUtil.publishEvent(new TreasureRecordResetEvent(ep));
	}

	public static void pubTreasureExpiredEvent(int treasureId, Long expiredNum, BaseEventParam bep) {
		SpringContextUtil.publishEvent(new TreasureExpiredEvent(new EPTreasureExpired(treasureId, expiredNum, bep)));
	}

	public static void pubTUserDeifyTokenEvent(BaseEventParam bep, int cardId) {
		SpringContextUtil.publishEvent(new TreasureUseDeifyTokenEvent(new EPCardDeify(bep, cardId)));
	}

	/**
	 * 道具完成发放事件
	 *
	 * @param guId
	 * @param treasureId
	 * @param treasureNum
	 * @param way
	 * @param rd
	 */
	public static void pubTFinishAddEvent(long guId, int treasureId, int treasureNum, WayEnum way, RDCommon rd) {
		EVTreasure ev = new EVTreasure(treasureId, treasureNum);
		List<EVTreasure> evs = new ArrayList<>();
		evs.add(ev);
		BaseEventParam bep = new BaseEventParam(guId, way, rd);
		EPTreasureFinishAdd ep = new EPTreasureFinishAdd(bep, evs);
		SpringContextUtil.publishEvent(new TreasureFinishAddEvent(ep));
	}

	/**
	 * 道具完成扣除事件
	 *
	 * @param guId
	 * @param treasureId
	 * @param treasureNum
	 * @param way
	 * @param rd
	 */
	public static void pubTFinishDeductEvent(long guId, int treasureId, int treasureNum, WayEnum way, RDCommon rd) {
		if (treasureNum <= 0) {
			return;
		}
		EVTreasure ev = new EVTreasure(treasureId, treasureNum);
		BaseEventParam bep = new BaseEventParam(guId, way, rd);
		EPTreasureFinishDeduct ep = new EPTreasureFinishDeduct(bep, ev);
		SpringContextUtil.publishEvent(new TreasureFinishDeductEvent(ep));
	}
}
