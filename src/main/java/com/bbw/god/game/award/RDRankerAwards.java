package com.bbw.god.game.award;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 排名奖励
 *
 * @author suhq
 * @date 2020-04-27 13:35
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDRankerAwards extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<RDRankerAward> awards;
}
