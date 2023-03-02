package com.bbw.god.mall.snatchtreasure;

import com.bbw.god.rd.RDCommon;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author suchaobin
 * @description 抽奖返回
 * @date 2020/7/15 10:16
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class RDSnatchTreasureDraw extends RDCommon {
	private Integer wishValue;
}
