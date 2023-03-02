package com.bbw.god.mall.cardshop.event;

import com.bbw.god.ConsumeType;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EPDraw {
	private Integer drawTimes;// 抽卡次数
	private ConsumeType costType;// 抽卡消耗类型
	private Integer cardPoolType;// 卡池类型，万物为60
	private Integer wishCard;// 心愿卡id
	private Integer wishValue;// 抽卡前的心愿值
	private List<Integer> addCardIds;// 抽到的卡牌id集合
}
