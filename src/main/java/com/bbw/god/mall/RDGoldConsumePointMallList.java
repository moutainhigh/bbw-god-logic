package com.bbw.god.mall;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.game.award.Award;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDGoldConsumePointMallList extends RDMallList implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer activityId;
	private Integer point = 0;// 元宝消费积分数
	private Long remainTime;// 剩余时间
	private List<Award> awards;
	private Integer progress;
	private Integer awardIndex;
	private Integer status;
}
