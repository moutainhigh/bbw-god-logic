package com.bbw.god.gameuser.yaozu.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * 妖族来犯事件参数
 *
 * @author fzj
 * @date 2021/9/7 8:43
 */
@Data
public class EPYaoZu extends BaseEventParam {
    private int yaoZuId;

    public EPYaoZu(int yaoZuId, BaseEventParam baseEventParam) {
        setValues(baseEventParam);
        this.yaoZuId = yaoZuId;
    }
}
