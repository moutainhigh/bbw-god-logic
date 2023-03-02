package com.bbw.god.game.zxz.rank;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 排行者
 *
 * @author: suhq
 * @date: 2022/9/26 2:38 下午
 */
@Data
@AllArgsConstructor
public class ZxzRanker {
    /** 玩家ID */
    private Long uid;
    /** 排名 */
    private int rank;
    /** 区域等级 */
    private Integer regionLevel;
    /** 区域等级 精确值 */
    private double accurateRegionLevel;
}
