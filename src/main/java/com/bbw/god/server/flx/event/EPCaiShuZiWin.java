package com.bbw.god.server.flx.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * @author suchaobin
 * @description 猜数字事件参数
 * @date 2020/2/24 15:08
 */
@Data
public class EPCaiShuZiWin extends BaseEventParam {
	public EPCaiShuZiWin(BaseEventParam bep) {
		setValues(bep);
	}
}
