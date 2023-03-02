package com.bbw.god.gameuser.yuxg.Enum;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 符图类型枚举
 *
 * @author fzj
 * @date 2021/11/15 14:22
 */
@Getter
@AllArgsConstructor
public enum FuTuTypeEnum {
    ATTACK_FU_TU("攻击符图",10),
    DEFENSE_FU_TU("防御符图",20),
    BLOOD_FU_TU("血量符图",30),
    SKILLS_FU_TU("技能符图",40),
    ;

    private final String name;
    private final int type;

    public static FuTuTypeEnum fromValue(int type) {
        for (FuTuTypeEnum item : values()) {
            if (item.getType() == type) {
                return item;
            }
        }
        throw CoderException.high("无效的符图类型-" + type);
    }
}
