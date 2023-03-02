package com.bbw.god.road;

import com.bbw.god.game.config.city.CfgRoadEntity;
import lombok.Data;

/**
 * 路径格子
 *
 * @author suhq
 * @date 2020-10-14 12:40
 **/
@Data
public class PathRoad {
    private CfgRoadEntity road;
    private Integer dir;
    private Integer belongToPath;
    private Integer indexInPath;

    public PathRoad(CfgRoadEntity road, int dir) {
        this.road = road;
        this.dir = dir;
    }
}
