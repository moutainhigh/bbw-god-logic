package com.bbw.god.activity.config;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.bbw.common.ListUtil;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.config.Cfg;

public class ActivityTool {

	public static CfgActivityEntity getActivity(int caId) {
		return Cfg.I.get(caId, CfgActivityEntity.class);
	}

	/**
	 * 根据活动父类型获得活动
	 * 
	 * @param parentType
	 * @return
	 */
	public static List<CfgActivityEntity> getActivitiesByParentType(ActivityParentTypeEnum parentType) {
		int parentTypeInt = parentType.getValue();
		return getActivities().stream().filter(a -> a.getParentType() == parentTypeInt).collect(Collectors.toList());
	}

	/**
	 * 根据活动类型获取所有的活动
	 * 
	 * @param type
	 * @return
	 */
	public static List<CfgActivityEntity> getActivitiesByType(ActivityEnum type) {
		int typeInt = type.getValue();
		List<CfgActivityEntity> activities = getActivities().stream().filter(a -> a.getType() == typeInt).collect(Collectors.toList());
		activities.sort(Comparator.comparing(CfgActivityEntity::getId));
		return activities;
	}

	/**
	 * 根据活动类型获取第一个活动
	 * 
	 * @param type
	 * @return
	 */
	public static CfgActivityEntity getActivityByType(ActivityEnum type) {
		List<CfgActivityEntity> cas = getActivitiesByType(type);
		if (ListUtil.isEmpty(cas)) {
			return null;
		}
		return cas.get(0);
	}

	/**
	 * 获取所有活动
	 * 
	 * @return
	 */
	public static List<CfgActivityEntity> getActivities() {
		return Cfg.I.get(CfgActivityEntity.class).stream().filter(a -> a.getStatus()).collect(Collectors.toList());
	}

}
