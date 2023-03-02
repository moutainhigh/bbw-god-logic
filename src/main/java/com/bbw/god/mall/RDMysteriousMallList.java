package com.bbw.god.mall;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDMysteriousMallList extends RDMallList implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer refreshGold = null;// 神秘更新消耗的元宝
	private Integer refreshTime = null;// 神秘下次自动更新时间
	private Integer limitRefreshTime = null;// 神秘刷新限制
	private Integer refreshTimes = null;// 神秘刷新次数
}
