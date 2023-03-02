package com.bbw.god.gameuser.businessgang.Enum;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商帮类型npc枚举
 *
 * @author fzj
 * @date 2022/1/17 15:28
 */
@Getter
@AllArgsConstructor
public enum BusinessNpcTypeEnum {
    ZHANG_DUO_REN("掌舵人", 10),
    ZHANG_LAO("长老", 20),
    ZHI_SHI("执事", 30),
    XIAO_SHI_DI("小师弟",40),
    XIAO_SHI_MEI("小师妹",50),
    ;

    private final String name;
    private final int type;

    public static BusinessNpcTypeEnum fromValue(int type) {
        for (BusinessNpcTypeEnum item : values()) {
            if (item.getType() == type) {
                return item;
            }
        }
        throw CoderException.high("无效的npc类型-" + type);
    }
}
