package com.bbw.god.mall.skillscroll;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author suchaobin
 * @description 合成类型
 * @date 2021/2/2 17:26
 **/
@AllArgsConstructor
@Getter
public enum SynthesisType {
    TYPE_1("随机本篇卷轴", 10),
    TYPE_2("指定本篇同属性卷轴", 20),
    TYPE_3("随机高篇卷轴或随机2本本篇卷轴", 30),
    TYPE_4("指定密传", 40),
    TYPE_JINSHEN_RANDOM("金身随机密传", 50),
    TYPE_JINSHEN_DESIGNATE("金身指定密传", 51),
    ;

    private final String name;
    private final Integer value;

    public static SynthesisType fromValue(int value) {
        for (SynthesisType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return null;
    }
}
