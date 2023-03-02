package com.bbw.god.gameuser.leadercard.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * @author suchaobin
 * @description 法外分身事件发布器
 * @date 2020/2/28 10:58
 */
public class LeaderCardEventPublisher {

	/**
	 * 升级
	 * @param ep
	 */
	public static void pubLeaderCardAddLvEvent(EPLeaderCardAddLv ep) {
		SpringContextUtil.publishEvent(new LeaderCardAddLvEvent(ep));
	}

	/**
	 * 升阶
	 * @param ep
	 */
	public static void pubLeaderCardUpHvEvent(EPLeaderCardUpHv ep) {
		SpringContextUtil.publishEvent(new LeaderCardUpHvEvent(ep));
	}

	/**
	 * 激活完整技能树
	 * @param ep
	 */
	public static void pubLeaderCardActiveSkillTreeEvent(BaseEventParam ep) {
		SpringContextUtil.publishEvent(new LeaderCardActiveSkillTreeEvent(ep));
	}

	/**
	 * 装备点满星图事件
	 * @param ep
	 */
	public static void pubLeaderEquipmentQualityFinishEvent(EPLeaderEquipmentQualityFinish ep) {
		SpringContextUtil.publishEvent(new LeaderEquipmentQualityFinishEvent(ep));
	}

	/**
	 * 装备强化事件
	 * @param ep
	 */
	public static void pubLeaderEquipmentAddLvEvent(EPLeaderEquipmentAddLv ep) {
		SpringContextUtil.publishEvent(new LeaderEquipmentAddLvEvent(ep));
	}
}
