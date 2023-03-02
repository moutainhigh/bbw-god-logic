package com.bbw.god.gameuser.yaozu;

import com.bbw.exception.CoderException;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 妖族进度
 *
 * @author fzj
 * @date 2021/9/14 13:33
 */
@Getter
@AllArgsConstructor
public enum YaoZuProgressEnum {
    /** */
    NOT_ATTACKED("未攻打",0),
    BEAT_MIRRORING("击败镜像",1),
    BEAT_ONTOLOGY("击败本体",2),
    ;
    /** */
    private final String name;
    /** */
    private final int type;

    public static YaoZuProgressEnum fromValue(int type) {
        for (YaoZuProgressEnum item : values()) {
            if (item.getType() == type) {
                return item;
            }
        }
        throw CoderException.high("无效的途径-" + type);
    }
}
