package com.bbw.god.game.zxz.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 诛仙阵难度配置
 *
 * @author: hzf
 * @create: 2022-09-14 17:16
 **/
@Data
public class ZxzDifficulty implements Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    /** 难度类型 */
    private Integer difficulty;
    /** 区域数据 */
    private List<ZxzRegion> regions;


    public static ZxzDifficulty getInstance(Integer difficulty, List<ZxzRegion> regions) {
        ZxzDifficulty zxzDifficulty = new ZxzDifficulty();
        zxzDifficulty.setDifficulty(difficulty);
        zxzDifficulty.setRegions(regions);
        return zxzDifficulty;
    }
}
