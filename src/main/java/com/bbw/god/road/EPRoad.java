package com.bbw.god.road;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author suhq
 * @description: 走过格子
 * @date 2019-12-10 15:01
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class EPRoad extends BaseEventParam {
    private Integer roadId;

    public EPRoad(BaseEventParam bep,int roadId){
        setValues(bep);
        this.roadId = roadId;
    }
}
