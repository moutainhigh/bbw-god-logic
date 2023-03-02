package com.bbw.god.gameuser.task.dfdjchallenge;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 巅峰对决赛季挑战领取奖励
 *
 * @author suhq
 * @date 2020-04-27 11:15
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDGetDfdjTaskAward extends RDCommon {
    private Integer addedScore;
}
