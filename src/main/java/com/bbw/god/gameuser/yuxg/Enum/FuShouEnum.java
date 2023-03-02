package com.bbw.god.gameuser.yuxg.Enum;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 符首枚举
 *
 *
 * @author fzj
 * @date 2021/11/2 15:15
 */
@Getter
@AllArgsConstructor
public enum  FuShouEnum {
    XUAN_JIE("玄阶符首", 50130,3),
    DI_JIE("地阶符首",50131,4),
    TIAN_JIE("天阶符首",50132,5),
            ;

    private final String name;
    private final int type;
    private final int fuTan;

    public static FuShouEnum fromValue(int type) {
        for (FuShouEnum item : values()) {
            if (item.getType() == type) {
                return item;
            }
        }
        throw CoderException.high("无效的符首-" + type);
    }
}
