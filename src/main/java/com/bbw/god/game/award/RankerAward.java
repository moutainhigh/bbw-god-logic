package com.bbw.god.game.award;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 排名奖励
 *
 * @author: suhq
 * @date: 2021/9/10 9:42 上午
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RankerAward implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer maxRank = null;// 最大排名
    private Integer minRank = null;// 最小排名
    private List<Award> awards = null;
}
