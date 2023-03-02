package com.bbw.god.game.combat.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年11月25日 上午10:23:13 类说明
 */
public class CombatEventPublisher {

	public static void pubCombatInitEvent(EPCombat ep) {
		SpringContextUtil.publishEvent(new CombatInitiateEvent(ep));
	}

	public static void pubCombatAchievement(EPCombatAchievement ep) {
		SpringContextUtil.publishEvent(new CombatAchievementEvent(ep));
	}

	/**
	 * 麒麟相关成就
	 * @param
	 */
	public static void pubCombatQiLinKillZhsEvent(long uid){
		SpringContextUtil.publishEvent(new CombatQiLinKillZhsEvent(new BaseEventParam(uid)));
	}

	/**
	 * 主角卡相关成就
	 * @param ep
	 */
	public static void pubCombatLeaderCardEvent(EPCombatLeaderCardParam ep){
		SpringContextUtil.publishEvent(new CombatLeaderCardEvent(ep));
	}

	/**
	 * 发布战斗胜利事件
	 *
	 * @param ev
	 */
	public static void pubWinEvent(EPFightEnd ev) {
		SpringContextUtil.publishEvent(new CombatFightWinEvent(ev));
	}

	/**
	 * 发布战斗失败事件
	 *
	 * @param ev
	 */
	public static void pubFailEvent(EPFightEnd ev) {
		SpringContextUtil.publishEvent(new CombatFailEvent(ev));
	}

	public static void pubEliteYeGuaiFightWinEvent(EPEliteYeGuaiFightWin ep) {
		SpringContextUtil.publishEvent(new EliteYeGuaiFightWinEvent(ep));
	}

	/**
	 * 发布战斗结算数据
	 * @param
	 */
	public static void pubResultDataEvent(long uid, int fightType, int killCardsNum, int useTreasureNum) {
		EPCombatResultData data=EPCombatResultData.getInstance(uid, fightType, killCardsNum, useTreasureNum);
		SpringContextUtil.publishEvent(new CombatResultDataEvent(data));
	}


	/**
	 * 神仙大会加积分
	 * @param
	 */
	public static void pubSxdhAddPointEvent(long uid, int addPoint) {
		if (addPoint<=0){
			return;
		}
		EPCombatSxdhAddPoint data=new EPCombatSxdhAddPoint();
		data.setValues(new BaseEventParam(uid));
		data.setPoint(addPoint);
		SpringContextUtil.publishEvent(new CombatSxdhAddPointEvent(data));
	}
}
