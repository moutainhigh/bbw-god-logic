package com.bbw.god.mall;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 天灯工坊
 *
 * @author: huanghb
 * @date: 2022/2/10 17:41
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDSkyLanternWorkShopMallList extends RDMallList implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 购买数量 */
	private Integer buyNum = 0;
}
