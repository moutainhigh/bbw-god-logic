package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author suchaobin
 * @description 法宝记录删除时间参数
 * @date 2020/11/3 10:42
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class EPTreasureRecordDel extends BaseEventParam {
    private Integer treasureId;

    public EPTreasureRecordDel(BaseEventParam bep, Integer treasureId) {
        setValues(bep);
        this.treasureId = treasureId;
    }
}
