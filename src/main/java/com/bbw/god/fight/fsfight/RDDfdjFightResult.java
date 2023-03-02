package com.bbw.god.fight.fsfight;

import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 巅峰对决战斗结果
 *
 * @author suhq
 * @date 2019-06-27 09:07:53
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDDfdjFightResult extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer winnerAddedScore = 0;
    private RDCommon winnerExtraAward;
    private RDCommon loserExtraAward;
    private Integer loserAddedScore = 0;
}
