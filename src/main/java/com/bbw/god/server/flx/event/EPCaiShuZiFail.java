package com.bbw.god.server.flx.event;

import com.bbw.god.event.BaseEventParam;
import lombok.Data;

/**
 * @author suchaobin
 * @description 押押乐下竞猜失败事件参数
 * @date 2020/2/28 10:06
 */
@Data
public class EPCaiShuZiFail extends BaseEventParam{
	public EPCaiShuZiFail(BaseEventParam bep) {
		setValues(bep);
	}
}
