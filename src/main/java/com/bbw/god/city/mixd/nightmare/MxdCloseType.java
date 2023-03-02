package com.bbw.god.city.mixd.nightmare;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * 说明：
 * 迷仙洞退出
 * @author lwb
 * date 2021-06-04
 */
@Getter
@AllArgsConstructor
public enum MxdCloseType implements Serializable {
    GIVE_UP(10,"放弃挑战"),
    GIVE_UP2(10,"放弃挑战");
    ;
    private int type;
    private String memo;
}
