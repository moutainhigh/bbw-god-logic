package com.bbw.god.gameuser.businessgang.digfortreasure;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 挖宝奖励
 *
 * @author: huanghb
 * @date: 2023/2/14 9:13
 */
@Data
public class FloorAward {
    /** 楼层 */
    private Integer floorId;
    /** 楼层奖励id集合 */
    private List<Integer> floorAwardIds = new ArrayList<>();

    /**
     * 初始化
     *
     * @param floorId       楼层id
     * @param floorAwardIds 楼层奖励ids
     * @return
     */
    public static FloorAward instance(int floorId, List<Integer> floorAwardIds) {
        FloorAward floorAward = new FloorAward();
        floorAward.setFloorId(floorId);
        floorAward.setFloorAwardIds(floorAwardIds);
        return floorAward;
    }
}
