package com.bbw.god.event.common;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.EPBaseWithBroadcast;
import com.bbw.god.notify.rednotice.ModuleEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通用达成事件参数
 *
 * @author suhq
 * @date 2020-02-12 21:13:58
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class EPAccomplish extends EPBaseWithBroadcast {
    private ModuleEnum module;
    private Integer type;
    private Integer accomplishId;

    public EPAccomplish(BaseEventParam baseEP, ModuleEnum module, Integer type, Integer id) {
        setValues(baseEP);
        this.setModule(module);
        this.setType(type);
        this.setAccomplishId(id);
    }

}
