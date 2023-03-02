package com.bbw.god.gameuser.leadercard.equipment;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 装备星图升星
 *
 * @author suhq
 * @date 2021-03-26 17:57
 **/
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDEquipmentStarMapUpdate extends RDCommon {
    private static final long serialVersionUID = 6406202939136667369L;
    /** 强化结果-1扣性 0失败 1成功 */
    private Integer result = 0;
}
