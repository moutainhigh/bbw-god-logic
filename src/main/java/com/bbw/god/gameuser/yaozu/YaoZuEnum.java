package com.bbw.god.gameuser.yaozu;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 妖族来犯枚举类
 *
 * @author fzj
 * @date 2021/9/6 14:38
 */
@Getter
@AllArgsConstructor
public enum YaoZuEnum {

    YE_ZHU_YAO("野猪妖",100),
    GOU_DA_XIAN("狗大仙",200),
    PI_PA_JING("琵琶精",300),
    ZHI_JI_JING("稚鸡精",400),
    YAO_HU_XIAN("妖狐仙",500),
    ;
    private final String name;
    private final int type;

    public static YaoZuEnum fromValue(int type) {
        for (YaoZuEnum item : values()) {
            if (item.getType() == type) {
                return item;
            }
        }
        throw CoderException.high("无效的途径-" + type);
    }
}
