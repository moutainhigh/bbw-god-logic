package com.bbw.god.gameuser.special.event;

import java.util.ArrayList;
import java.util.List;

import com.bbw.god.event.BaseEventParam;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class EPPocketSpecial extends BaseEventParam {
	// 需要操作的特产基础ID
	private List<Long> dataIdList = new ArrayList<Long>();

	public EPPocketSpecial(BaseEventParam bep, List<Long> dataList) {
		setValues(bep);
		this.dataIdList = dataList;
	}

	public EPPocketSpecial(BaseEventParam bep, Long dataId) {
		setValues(bep);
		this.dataIdList.add(dataId);
	}
	
	public Long getLockSpecialId() {
		if (dataIdList.isEmpty()) {
			return null;
		}
		return dataIdList.get(0);
	}
	
	public List<Long> getUnLockSpecialIds(){
		return dataIdList;
	}
}
