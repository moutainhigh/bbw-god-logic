package com.bbw.god.activity.worldcup;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 物品类别
 *
 * @author suhq
 * @version 创建时间：2018年9月21日 上午9:04:07
 */
@Getter
@AllArgsConstructor
public enum WorldCupTypeEnum {

    SUPER16("Super16", 13430),
    DROIYAN8("Droiyan8", 13440),
    PROPHET("Prophet", 13450),
    QUIZKING("QuizKing", 13460)
    ;

    private final String name;
    private final int activityType;

    public static WorldCupTypeEnum fromActivityType(int value) {
        for (WorldCupTypeEnum item : values()) {
            if (item.getActivityType() == value) {
                return item;
            }
        }
        return null;
    }

    public static WorldCupTypeEnum fromName(String name) {
        for (WorldCupTypeEnum model : values()) {
            if (model.getName().equals(name)) {
                return model;
            }
        }
        return null;
    }
}
