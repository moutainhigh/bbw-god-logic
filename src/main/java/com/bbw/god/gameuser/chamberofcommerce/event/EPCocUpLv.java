package com.bbw.god.gameuser.chamberofcommerce.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EPCocUpLv extends BaseEventParam{
	private Integer level = 0;// 商会等级

	public static EPCocUpLv instance(BaseEventParam ep,int level) {
		EPCocUpLv ev = new EPCocUpLv();
		ev.setValues(ep);
		ev.setLevel(level);
		return ev;
	}
}
