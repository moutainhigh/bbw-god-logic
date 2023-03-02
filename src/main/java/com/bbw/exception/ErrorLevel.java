package com.bbw.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年8月22日 下午3:50:07
 */
@Getter
@AllArgsConstructor
public enum ErrorLevel implements Serializable {

    FATAL("严重", 4), HIGH("高", 2), NORMAL("一般", 1), NONE("非错误", 0);
    private String name;
    private int value;

    public static ErrorLevel fromValue(int value) {
        for (ErrorLevel model : values()) {
            if (model.getValue() == value) {
                return model;
            }
        }
        return null;
    }
}
