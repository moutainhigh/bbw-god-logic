package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 符图事件信息
 *
 * @author fzj
 * @date 2022/1/4 16:52
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPFuTuAdd extends BaseEventParam {
    private Integer id;
    private Integer num;

    public EPFuTuAdd(BaseEventParam baseEP, int fuTuId, int num) {
        setValues(baseEP);
        this.id = fuTuId;
        this.num = num;
    }
}
