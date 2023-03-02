package com.bbw.god.city.chengc;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.city.chengc.RDTradeInfo.RDCitySpecial;
import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 刷新城池出售特产
 * 
 * @author suhq
 * @date 2019-05-24 16:30:21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDTradeRefresh extends RDCommon implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<RDCitySpecial> citySpecials = null;// 城市出售特产

}
