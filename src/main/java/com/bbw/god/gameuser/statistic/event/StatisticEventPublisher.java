package com.bbw.god.gameuser.statistic.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatistic;
import com.bbw.god.gameuser.statistic.behavior.login.LoginBehaviorStatisticEvent;
import com.bbw.god.gameuser.statistic.behavior.move.MoveBehaviorStatisticEvent;
import com.bbw.god.gameuser.statistic.behavior.snatchtreasure.SnatchBehaviorStatisticEvent;
import com.bbw.god.gameuser.statistic.resource.ResourceStatistic;
import com.bbw.god.gameuser.statistic.resource.copper.CopperResourceStatisticEvent;
import com.bbw.god.gameuser.statistic.resource.dice.DiceResourceStatisticEvent;
import com.bbw.god.gameuser.statistic.resource.ele.EleResourceStatisticEvent;
import com.bbw.god.rd.RDCommon;

/**
 * @author suchaobin
 * @description 统计事件发布器
 * @date 2020/4/18 10:27
 */
public class StatisticEventPublisher {
	/**
	 * 发布资源统计事件
	 *
	 * @param uid       玩家id
	 * @param way       途径
	 * @param rd        一些基础信息
	 * @param statistic 资源统计对象
	 */
	public static void pubResourceStatisticEvent(long uid, WayEnum way, RDCommon rd, ResourceStatistic statistic) {
		SpringContextUtil.publishEvent(new ResourceStatisticEvent(new EPResourceStatistic(uid, way, rd, statistic)));
	}

	/**
	 * 发布铜钱资源统计事件
	 *
	 * @param uid       玩家id
	 * @param way       途径
	 * @param rd        一些基础信息
	 * @param statistic 资源统计对象
	 */
	public static void pubCopperResourceEvent(long uid, WayEnum way, RDCommon rd, ResourceStatistic statistic) {
		SpringContextUtil.publishEvent(new CopperResourceStatisticEvent(new EPResourceStatistic(uid, way, rd, statistic)));
	}

	/**
	 * 发布体力资源统计事件
	 *
	 * @param uid       玩家id
	 * @param way       途径
	 * @param rd        一些基础信息
	 * @param statistic 资源统计对象
	 */
	public static void pubDiceResourceEvent(long uid, WayEnum way, RDCommon rd, ResourceStatistic statistic) {
		SpringContextUtil.publishEvent(new DiceResourceStatisticEvent(new EPResourceStatistic(uid, way, rd, statistic)));
	}

	/**
	 * 发布元素资源统计事件
	 *
	 * @param uid       玩家id
	 * @param way       途径
	 * @param rd        一些基础信息
	 * @param statistic 资源统计对象
	 */
	public static void pubEleResourceEvent(long uid, WayEnum way, RDCommon rd, ResourceStatistic statistic) {
		SpringContextUtil.publishEvent(new EleResourceStatisticEvent(new EPResourceStatistic(uid, way, rd, statistic)));
	}

	/**
	 * 发布行为统计事件
	 *
	 * @param uid       玩家id
	 * @param way       途径
	 * @param rd        一些基础信息
	 * @param statistic 资源统计对象
	 */
	public static void pubBehaviorStatisticEvent(long uid, WayEnum way, RDCommon rd, BehaviorStatistic statistic) {
		SpringContextUtil.publishEvent(new BehaviorStatisticEvent(new EPBehaviorStatistic(uid, way, rd, statistic)));
	}

	/**
	 * 发布夺宝行为统计事件
	 *
	 * @param uid       玩家id
	 * @param way       途径
	 * @param rd        一些基础信息
	 * @param statistic 资源统计对象
	 */
	public static void pubSnatchBehaviorEvent(long uid, WayEnum way, RDCommon rd, BehaviorStatistic statistic) {
		SpringContextUtil.publishEvent(new SnatchBehaviorStatisticEvent(new EPBehaviorStatistic(uid, way, rd, statistic)));
	}

	/**
	 * 发布移动行为统计事件
	 *
	 * @param uid       玩家id
	 * @param way       途径
	 * @param rd        一些基础信息
	 * @param statistic 资源统计对象
	 */
	public static void pubMoveBehaviorEvent(long uid, WayEnum way, RDCommon rd, BehaviorStatistic statistic) {
		SpringContextUtil.publishEvent(new MoveBehaviorStatisticEvent(new EPBehaviorStatistic(uid, way, rd, statistic)));
	}

	/**
	 * 发布登录行为统计事件
	 *
	 * @param uid       玩家id
	 * @param way       途径
	 * @param rd        一些基础信息
	 * @param statistic 资源统计对象
	 */
	public static void pubLoginBehaviorEvent(long uid, WayEnum way, RDCommon rd, BehaviorStatistic statistic) {
		SpringContextUtil.publishEvent(new LoginBehaviorStatisticEvent(new EPBehaviorStatistic(uid, way, rd, statistic)));
	}
}
