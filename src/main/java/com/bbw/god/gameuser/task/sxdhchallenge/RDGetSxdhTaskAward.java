package com.bbw.god.gameuser.task.sxdhchallenge;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 神仙大会赛季挑战领取奖励
 *
 * @author suhq
 * @date 2020-04-27 11:15
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDGetSxdhTaskAward extends RDCommon {
    private static final long serialVersionUID = -7988084661952137081L;
    private Integer addedScore;
}
