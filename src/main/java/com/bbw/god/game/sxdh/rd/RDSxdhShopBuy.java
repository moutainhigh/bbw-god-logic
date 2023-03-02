package com.bbw.god.game.sxdh.rd;

import java.io.Serializable;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 商店购买
 * 
 * @author suhq
 * @date 2019-06-21 10:35:11
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDSxdhShopBuy extends RDCommon implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer addedBean = null;

}
