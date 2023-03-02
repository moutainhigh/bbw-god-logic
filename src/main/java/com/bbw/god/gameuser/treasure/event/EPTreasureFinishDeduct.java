package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 道具完成扣除事件参数
 *
 * @author fzj
 * @date 2022/4/7 9:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPTreasureFinishDeduct extends BaseEventParam {
    private EVTreasure deductTreasure;

    public EPTreasureFinishDeduct(BaseEventParam baseEP, EVTreasure deductTreasures) {
        setValues(baseEP);
        this.deductTreasure = deductTreasures;
    }
}
