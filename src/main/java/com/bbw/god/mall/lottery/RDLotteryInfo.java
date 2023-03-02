package com.bbw.god.mall.lottery;

import com.bbw.god.rd.RDCommon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author suchaobin
 * @description 进入奖券界面
 * @date 2020/7/6 10:28
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class RDLotteryInfo extends RDCommon {
	private static final long serialVersionUID = -7229660661590756075L;
	private Long remainTime;// 距离开奖剩余时间
	private List<Integer> boughtNumbers;// 已经被购买的数字集合（不包括自己的）
	private List<Integer> myNumbers;// 我购买的数字集合
}
