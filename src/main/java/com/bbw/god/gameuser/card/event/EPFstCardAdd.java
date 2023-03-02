package com.bbw.god.gameuser.card.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 封神台卡牌事件参数
 * 
 * @author lzc
 * @date 2021-07-12 10:18:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPFstCardAdd extends BaseEventParam {
	private Integer fstCardNum = 0;// 用户封神台卡牌数量

	public EPFstCardAdd(BaseEventParam baseEP, int fstCardNum) {
		setValues(baseEP);
		this.fstCardNum = fstCardNum;
	}
}
