package com.bbw.god.fight.fsfight;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 神仙大会战斗结果
 *
 * @author suhq
 * @date 2019-06-27 09:07:53
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDSxdhFightResult extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer winnerAddedScore = 0;
//    private Integer winnerExtraScore = 0;

    private Integer loserAddedScore = 0;
//    private Integer loserExtraScore = 0;

//    private Integer isSeasonEnd = 0;//是否赛季结束
}
