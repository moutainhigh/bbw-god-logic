package com.bbw.god.game.dfdj.rd;

import com.bbw.god.game.award.RDRankerAward;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 神仙大会排名奖励
 *
 * @author suhq
 * @date 2020-04-27 13:36
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDDfdjRankerAwardList extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<RDRankerAward> rankerAwards = new ArrayList<>();

}
