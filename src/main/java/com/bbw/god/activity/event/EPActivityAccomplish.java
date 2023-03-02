package com.bbw.god.activity.event;

import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * @author suhq
 * @description: 活动达成参数
 * @date 2020-02-06 14:11
 **/
@Data
public class EPActivityAccomplish extends BaseEventParam {
    private CfgActivityEntity ca;

    public EPActivityAccomplish(BaseEventParam bep, CfgActivityEntity ca) {
        setValues(bep);
        this.ca = ca;
    }
}
