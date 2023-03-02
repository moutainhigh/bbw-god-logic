package com.bbw.god.gameuser.treasure.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 卡牌封神
 * @author lwb
 * @date 2020/7/30 10:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPCardDeify extends BaseEventParam {
    private int cardId;

    public EPCardDeify(BaseEventParam baseEP, int cardId) {
        setValues(baseEP);
        this.cardId = cardId;
    }
}
