package com.bbw.god.gameuser.yuxg.Enum;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 符图相关枚举
 *
 * @author fzj
 * @date 2021/11/4 17:49
 */
@Getter
@AllArgsConstructor
public enum FuTuEnum {
    ONE_FU_TU("一阶符图", 1),
    TWO_FU_TU("二阶符图", 2),
    THREE_FU_TU("三阶符图", 3),
    FOUR_FU_TU("四阶符图",4),
    FIVE_FU_TU("五阶符图",5),
    ;

    private final String name;
    private final int type;

    public static FuTuEnum fromValue(int type) {
        for (FuTuEnum item : values()) {
            if (item.getType() == type) {
                return item;
            }
        }
        throw CoderException.high("无效的符图品阶-" + type);
    }
}
