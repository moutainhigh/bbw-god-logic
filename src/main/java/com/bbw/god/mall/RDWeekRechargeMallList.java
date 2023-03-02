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
public class RDWeekRechargeMallList extends RDMallList implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long remainTime;// 剩余时间
}
