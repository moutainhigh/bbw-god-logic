package com.bbw.god.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 自定义上报事件类型
 *
 * @author: suhq
 * @date: 2021/8/17 5:54 下午
 */
@Getter
@AllArgsConstructor
public enum ReportEventType {

    BUSINESS_REQUEST("#business_request", 10),
    BUSINESS_FINISH("#business_finish", 20),
    ;

    private final String name;
    private final int value;

    public static ReportEventType fromValue(int value) {
        for (ReportEventType item : values()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }

    public static ReportEventType fromName(String name) {
        for (ReportEventType model : values()) {
            if (model.getName().equals(name)) {
                return model;
            }
        }
        return null;
    }
}
