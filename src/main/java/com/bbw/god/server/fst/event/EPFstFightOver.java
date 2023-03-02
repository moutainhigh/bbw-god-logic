package com.bbw.god.server.fst.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * 封神台跨服挑战基础数据
 *
 * @author fzj
 * @date 2021/8/16 10:27
 */
@Data
public class EPFstFightOver extends BaseEventParam {
    /** 是否成功 */
    private boolean isWin;

    public EPFstFightOver(BaseEventParam baseEP, boolean isWin) {
        setValues(baseEP);
        this.isWin = isWin;
    }
}
