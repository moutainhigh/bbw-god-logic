package com.bbw.god.gameuser.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务状态
 *
 * @author suhq
 * @date 2019年2月21日 上午11:18:46
 */
@Getter
@AllArgsConstructor
public enum TaskStatusEnum {

    QUEUING("排队中", -4),
    FAIL("失败", -3),
    TIME_OUT("过期", -2),
    WAITING("未进行", -1),
    DOING("进行中", 0),
    ACCOMPLISHED("已达成", 1),
    AWARDED("已领取", 2);

    private final String name;
    private final int value;

    public static TaskStatusEnum fromValue(int value) {
        for (TaskStatusEnum item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }
}
