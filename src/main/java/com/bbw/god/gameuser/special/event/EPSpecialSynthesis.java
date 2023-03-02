package com.bbw.god.gameuser.special.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author suchaobin
 * @description 特产合成事件基础参数
 * @date 2020/11/12 17:13
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class EPSpecialSynthesis extends BaseEventParam {

    public static EPSpecialSynthesis getInstance(BaseEventParam bep) {
        EPSpecialSynthesis ep = new EPSpecialSynthesis();
        ep.setValues(bep);
        return ep;
    }
}
