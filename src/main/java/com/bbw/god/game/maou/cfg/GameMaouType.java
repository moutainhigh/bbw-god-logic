package com.bbw.god.game.maou.cfg;

import com.bbw.god.activity.config.ActivityEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 跨服魔王类型
 *
 * @author: suhq
 * @date: 2022/1/7 9:57 上午
 */
@Getter
@AllArgsConstructor
public enum GameMaouType {
    DEVIL("恶魔", 1000, ActivityEnum.RESIST_DEVIL.getValue()),
    YEAR_BEAST("年兽", 2000, ActivityEnum.YEAR_BEAST.getValue()),
    GUAN_ZU("冠族", 3000, ActivityEnum.GUAN_ZU.getValue()),
    ANTI_HEAT("祛暑", 4000, ActivityEnum.COOL_SUMMER.getValue()),
    LITTLE_REINDEER("小小驯鹿", 5000, ActivityEnum.LITTLE_REINDEER.getValue()),
    ;
    /** 跨服魔王 */
    private final String name;
    /** 跨服魔王类型 */
    private final int value;
    /** 魔王对应互动类型 */
    private final int activityType;

    public static GameMaouType fromValue(int value) {
        for (GameMaouType item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }

    /**
     * 获取活动对应的跨服魔王类型
     *
     * @param activityType
     * @return
     */
    public static GameMaouType fromActivity(int activityType) {
        for (GameMaouType item : values()) {
            if (item.getActivityType() == activityType) {
                return item;
            }
        }
        return null;
    }

    /**
     * 获取魔王对应的活动类型
     *
     * @return
     */
    public static List<Integer> getMaouActivities() {
        List<Integer> activities = new ArrayList<>();
        for (GameMaouType item : values()) {
            activities.add(item.getActivityType());
        }
        return activities;
    }
}
