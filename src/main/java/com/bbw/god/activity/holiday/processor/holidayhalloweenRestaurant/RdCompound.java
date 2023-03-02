package com.bbw.god.activity.holiday.processor.holidayhalloweenRestaurant;

import com.bbw.god.rd.RDCommon;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 万圣节餐厅合成
 *
 * @author: huanghb
 * @date: 2022/10/11 15:31
 */
@Data
public class RdCompound extends RDCommon implements Serializable {
    private static final long serialVersionUID = 1L;
    /** 盘子上食物信息 */
    private List<Integer> plateFoodInfos;

    public static RdCompound instance(Map<Integer, Integer> plateFoodInfos) {
        RdCompound rd = new RdCompound();
        rd.setPlateFoodInfos(plateFoodInfos.values().stream().collect(Collectors.toList()));
        return rd;
    }
}
