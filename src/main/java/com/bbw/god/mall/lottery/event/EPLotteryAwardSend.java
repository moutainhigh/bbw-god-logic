package com.bbw.god.mall.lottery.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author suchaobin
 * @description 奖券奖励发放事件参数
 * @date 2020/7/15 16:34
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class EPLotteryAwardSend extends BaseEventParam {
	private Integer group;

	public EPLotteryAwardSend(Integer group, BaseEventParam bep) {
		this.group = group;
		setValues(bep);
	}
}
