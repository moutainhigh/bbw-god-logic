package com.bbw.god.mall;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDMallList extends RDCommon implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<RDMallInfo> mallGoods = null;// 返回的商品
	private Integer mallType = null;// 商品类型
	private List<Integer> userAuthList = null; // 玩家权限集合
}
