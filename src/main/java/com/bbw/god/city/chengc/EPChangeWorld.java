package com.bbw.god.city.chengc;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author suchaobin
 * @description 世界跳转世界参数
 * @date 2020/9/24 11:19
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class EPChangeWorld extends BaseEventParam {
    private Integer oldWorldType;
    private Integer newWorldType;

    public EPChangeWorld(Integer oldWorldType, Integer newWorldType, BaseEventParam bep) {
        this.oldWorldType = oldWorldType;
        this.newWorldType = newWorldType;
        setValues(bep);
    }
}
