package com.bbw.god.gameuser.card.equipment.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Getter;
import lombok.Setter;

/**
 * 仙诀激活
 *
 * @author: huanghb
 * @date: 2022/9/24 10:52
 */
@Getter
@Setter
public class EPXianJueActive extends BaseEventParam {
    /** 属性 */
    private Integer property;
    /** 星级 */
    private Integer star;

    public static EPXianJueActive instance(BaseEventParam ep, Integer property, Integer star) {
        EPXianJueActive ew = new EPXianJueActive();
        ew.setValues(ep);
        ew.setProperty(property);
        ew.setStar(star);
        return ew;
    }
}
