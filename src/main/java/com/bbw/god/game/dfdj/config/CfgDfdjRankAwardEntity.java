package com.bbw.god.game.dfdj.config;

import com.bbw.god.game.award.Award;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 神仙大会排名奖励
 *
 * @author suhq
 * @date 2020-04-23 14:42
 **/
@Data
public class CfgDfdjRankAwardEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer minRank;
    private Integer maxRank;
    private List<Award> awards;

}
