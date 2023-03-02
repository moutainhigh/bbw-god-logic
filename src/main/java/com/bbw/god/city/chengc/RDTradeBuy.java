package com.bbw.god.city.chengc;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.city.chengc.RDTradeInfo.RDSellingSpecial;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 城池交易买入特产
 * 
 * @author suhq
 * @date 2019年3月12日 下午2:35:33
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDTradeBuy extends RDCommon implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<RDSellingSpecial> boughtSpecials = null;// 交易购买特产给客户端的返回

}
