package com.bbw.god.gameuser.task.godtraining.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author suchaobin
 * @description 上仙试炼 试炼值增加事件基础参数
 * @date 2021/1/21 16:24
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class EPGodTrainingTaskAddPoint extends BaseEventParam {
    private Integer point;

    public EPGodTrainingTaskAddPoint(Integer point, BaseEventParam bep) {
        this.point = point;
        setValues(bep);
    }
}
