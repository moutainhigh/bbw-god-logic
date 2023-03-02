package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * 增加法宝效果事件参数
 */
@Data
public class EPTreasureEffectAdd extends BaseEventParam {
    private Integer treasureId;
    private Integer addEffect;

    public EPTreasureEffectAdd(BaseEventParam bep,Integer treasureId,Integer addEffect){
        setValues(bep);
        this.treasureId = treasureId;
        this.addEffect = addEffect;
    }
}
