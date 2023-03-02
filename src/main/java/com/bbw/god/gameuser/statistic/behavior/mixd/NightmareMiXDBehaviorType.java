package com.bbw.god.gameuser.statistic.behavior.mixd;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 梦魇迷仙洞行为统计枚举
 * @author lzc
 * @description
 * @date 2021/06/04 9:49
 */
@Getter
@AllArgsConstructor
public enum NightmareMiXDBehaviorType {
    BEAT_PATROL("击败巡使", 1),//0：普通巡使，1：巡使头领
    DRINK_WATER("饮用泉水", 2),//0：生命值没到11，1：饮用后生命值为11
    STEP_TRAP("踩到陷阱", 3),
    OPEN_SPECIAL_BOX("巡使驻地中打开特殊宝箱", 4),
    BEAT_DEFIER("作为层主击败挑战者", 5),
    PASS_OF_ONE_HP("1点生命值状态通过一层", 6),
    BITE_THE_DUST("作为层主没有击败挑战者被取代", 7),
    SMELT_FAIL("熔炼失败", 8),
    SMELT_SUCCEED("熔炼成功", 9),
    FULL_LIFE_PASS("不损失生命值通过一层", 10)
    ;
    private final String name;
    private final Integer value;

    public static NightmareMiXDBehaviorType fromName(String name) {
        for (NightmareMiXDBehaviorType behaviorType : values()) {
            if (behaviorType.getName().equals(name)) {
                return behaviorType;
            }
        }
		return null;
	}

	public static NightmareMiXDBehaviorType fromValue(Integer value) {
		for (NightmareMiXDBehaviorType behaviorType : values()) {
			if (behaviorType.getValue().equals(value)) {
				return behaviorType;
			}
		}
		return null;
	}
}
