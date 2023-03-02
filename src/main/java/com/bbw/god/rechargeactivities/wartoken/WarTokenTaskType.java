package com.bbw.god.rechargeactivities.wartoken;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-03
 */
@Getter
@AllArgsConstructor
public enum WarTokenTaskType implements Serializable {
    LOGIN_TASK(10,"随机任务"),
    NORMAL_TASK(20,"常规任务"),
    RANDOM_TASK(30,"随机任务"),
    RANDOM_UNIQUE_TASK(40,"唯一完成任务"),
    ;
    private int type;
    private String memo;
}
