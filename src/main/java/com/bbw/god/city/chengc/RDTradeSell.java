package com.bbw.god.city.chengc;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.city.chengc.RDTradeInfo.RDSellingSpecial;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 城池交易出售特产
 * 
 * @author suhq
 * @date 2019年3月12日 下午2:35:16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDTradeSell extends RDCommon implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<RDSellingSpecial> sellingSpecials = null;// 玩家卖出特产后返回剩余特产的价格信息

}
