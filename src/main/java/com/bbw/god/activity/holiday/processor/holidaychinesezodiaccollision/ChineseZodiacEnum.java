package com.bbw.god.activity.holiday.processor.holidaychinesezodiaccollision;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 生肖枚举
 *
 * @author: huanghb
 * @date: 2023/2/9 17:02
 */
@Getter
@AllArgsConstructor
public enum ChineseZodiacEnum {
    RAT("鼠", "01"),
    OX("牛", "02"),
    TIGER("虎", "03"),
    RABBIT("兔", "04"),
    DRAGON("龙", "05"),
    SNAKE("蛇", "06"),
    HORSE("马", "07"),
    GOAT("羊", "08"),
    MONKEY("猴", "09"),
    ROOSTER("鸡", "10"),
    DOG("狗", "11"),
    PIG("猪", "12"),
    ;
    /** 生肖名称 */
    private final String name;
    /** 生肖id */
    private final String id;

    public static ChineseZodiacEnum fromId(int id) {
        for (ChineseZodiacEnum item : values()) {
            if (Integer.valueOf(item.id) == id) {
                return item;
            }
        }
        throw CoderException.high("无效的生肖-" + id);
    }
}
