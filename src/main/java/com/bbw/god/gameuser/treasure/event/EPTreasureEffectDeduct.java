package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

@Data
public class EPTreasureEffectDeduct extends BaseEventParam {
    private Integer treasureId;
    private Integer deductEffect;

    public EPTreasureEffectDeduct(BaseEventParam bep, Integer treasureId, Integer deductEffect) {
        setValues(bep);
        this.treasureId = treasureId;
        this.deductEffect = deductEffect;
    }
}
