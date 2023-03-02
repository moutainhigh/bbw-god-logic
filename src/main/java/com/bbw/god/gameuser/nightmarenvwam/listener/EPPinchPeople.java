package com.bbw.god.gameuser.nightmarenvwam.listener;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 捏人事件参数
 *
 * @author: huanghb
 * @date: 2022/5/20 16:32
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPPinchPeople extends BaseEventParam {
    /** 捏人次数 */
    private Integer pinchPeoleTimes = 1;
    /** 泥人评分 */
    private List<Integer> soilScore;

    public EPPinchPeople(List<Integer> soilScore, BaseEventParam bep) {
        setValues(bep);
        this.soilScore = soilScore;
    }
}
