package com.bbw.god.gameuser.card.equipment.rd;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 装备星图升星
 *
 * @author: huanghb
 * @date: 2022/9/19 14:40
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RdXianJueStarMapUpdate extends RDCommon {
    private static final long serialVersionUID = 6406202939136667369L;
    /** 强化结果-1扣性 0失败 1成功 */
    private Integer result = 0;
}
