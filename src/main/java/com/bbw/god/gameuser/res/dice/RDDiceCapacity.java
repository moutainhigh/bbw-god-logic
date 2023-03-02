package com.bbw.god.gameuser.res.dice;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author：lwb
 * @date: 2021/1/7 15:42
 * @version: 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDDiceCapacity extends RDCommon {
    private Integer diceCapacity=null;//存储量
    private Integer maxDiceCapacity=null;//上限
}
