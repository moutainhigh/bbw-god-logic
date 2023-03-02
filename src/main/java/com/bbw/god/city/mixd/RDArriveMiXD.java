package com.bbw.god.city.mixd;

import com.bbw.god.city.RDCityInfo;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 到达迷仙洞
 * 
 * @author suhq
 * @date 2019年3月18日 下午3:52:23
 */
@Getter
@Setter
@ToString(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDArriveMiXD extends RDCityInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Integer> poss = null;// 仙人洞四个物品的位置
	private Integer nightmareMxd=0;//是否是梦魇迷仙洞
}
