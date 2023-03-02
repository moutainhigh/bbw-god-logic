package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

@Data
public class EPTreasureEffectSet extends BaseEventParam {
    private Integer treasureId;
    private Integer treasureEffect;

    public EPTreasureEffectSet(BaseEventParam bep, Integer treasureId, Integer treasureEffect) {
        setValues(bep);
        this.treasureId = treasureId;
        this.treasureEffect = treasureEffect;
    }
}
