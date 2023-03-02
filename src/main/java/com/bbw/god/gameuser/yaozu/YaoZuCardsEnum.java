package com.bbw.god.gameuser.yaozu;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 妖族镜像卡组枚举
 *
 * @author fzj
 * @date 2021/9/14 11:42
 */
@Getter
@AllArgsConstructor
public enum YaoZuCardsEnum {

    MIRRORING_CARDS("镜像卡组",0),
    ONTOLOGY_CARDS("本体卡组",1),
    ;

    private final String name;
    private final int type;

    public static YaoZuCardsEnum fromValue(int type) {
        for (YaoZuCardsEnum item : values()) {
            if (item.getType() == type) {
                return item;
            }
        }
        throw CoderException.high("无效的卡组-" + type);
    }
}
