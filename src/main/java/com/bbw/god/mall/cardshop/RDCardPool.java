package com.bbw.god.mall.cardshop;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 卡池数据
 *
 * @author suhq
 * @date 2019-05-08 08:56:01
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDCardPool extends RDSuccess {
	// 元宝十连抽次数
	private Integer goldTenDrawTimes = 0;
	// 当前许愿值
	private Integer curVow = 0;
	// 额外赠送的许愿值
	private Integer extraVow = 0;
	// 需要许愿值
	private Integer needVow = 0;
	// 许愿卡牌
	private Integer vowCardId = -1;
	// 新卡
	private Integer newCardId = -1;
	// 卡池内的卡牌
	private List<Integer> cards;

}
