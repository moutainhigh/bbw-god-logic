package com.bbw.god.gameuser.businessgang.digfortreasure.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * 挖宝事件参数
 *
 * @author fzj
 * @date 2022/1/29 13:37
 */
@Data
public class EPDigTreasure extends BaseEventParam {
    /** 位置 */
    private int pos;

    public EPDigTreasure(int pos, BaseEventParam bep) {
        setValues(bep);
        this.pos = pos;
    }
}
