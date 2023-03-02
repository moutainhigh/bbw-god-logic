package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 完成道具发放事件参数
 *
 * @author fzj
 * @date 2022/4/7 9:32
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPTreasureFinishAdd extends BaseEventParam {
    private List<EVTreasure> addTreasures;

    public EPTreasureFinishAdd(BaseEventParam baseEP, List<EVTreasure> addTreasures) {
        setValues(baseEP);
        this.addTreasures = addTreasures;
    }
}
