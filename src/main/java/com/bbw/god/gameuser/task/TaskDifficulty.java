package com.bbw.god.gameuser.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务难度
 *
 * @author: suhq
 * @date: 2021/8/5 5:42 下午
 */
@Getter
@AllArgsConstructor
public enum TaskDifficulty {

    FIRST_LEVEL("初级", 10),
    MIDDLE_LEVEL("中级", 20),
    HIGH_LEVEL("高级", 30),
    SUPER_LEVEL("史诗级", 40),

    ;

    private final String name;
    private final int value;

    public static TaskDifficulty fromValue(int value) {
        for (TaskDifficulty item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
