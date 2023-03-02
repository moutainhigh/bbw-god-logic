package com.bbw.god.gameuser.card.equipment.rd;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 穿戴情况WearingCondition
 *
 * @author: huanghb
 * @date: 2022/9/15 10:22
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RdWearingCondition extends RDSuccess {
    /** 是否装上 0表示未装备 1表示装备 */
    private Integer putedOn = 0;


    public static RdWearingCondition instance(Integer putedOn) {
        RdWearingCondition info = new RdWearingCondition();
        info.setPutedOn(putedOn);
        return info;

    }
}
