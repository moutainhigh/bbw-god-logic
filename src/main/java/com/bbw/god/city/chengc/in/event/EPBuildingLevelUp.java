package com.bbw.god.city.chengc.in.event;

import com.bbw.god.city.chengc.UserCity;
import lombok.Data;

import java.util.List;

/**
 * 建筑升级
 *
 * @author suhq
 * @date 2019-05-23 18:04:43
 */
@Data
public class EPBuildingLevelUp {
    private Long ucId;
    private Integer cityId;
    private List<Integer> levelUpBuildings;

    public EPBuildingLevelUp(UserCity uc, List<Integer> levelUpBuildings) {
        this.ucId = uc.getId();
        this.cityId = uc.getBaseId();
        this.levelUpBuildings = levelUpBuildings;
    }

}
