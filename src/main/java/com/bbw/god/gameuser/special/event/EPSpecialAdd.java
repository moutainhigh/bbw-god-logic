package com.bbw.god.gameuser.special.event;

import java.util.List;

import com.bbw.god.event.BaseEventParam;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EPSpecialAdd extends BaseEventParam {
	private List<EVSpecialAdd> addSpecials;

	public EPSpecialAdd(BaseEventParam bep, List<EVSpecialAdd> addSpecials) {
		setValues(bep);
		this.addSpecials = addSpecials;
	}
}
