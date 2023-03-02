package com.bbw.god.gameuser.dice;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDGainDice extends RDSuccess {
    private Integer dice;
    /** 最近一次体力增长时间 */
    private Long lastDiceIncTime = null;

}
