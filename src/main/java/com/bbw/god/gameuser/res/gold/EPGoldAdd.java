package com.bbw.god.gameuser.res.gold;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.res.ResAddInfo;
import com.bbw.god.gameuser.res.ResWayType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 获得的元宝信息
 *
 * @author suhq
 * @date 2019年3月5日 上午10:21:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPGoldAdd extends BaseEventParam {
	private List<ResAddInfo> addGolds = new ArrayList<>();

	public EPGoldAdd(BaseEventParam bep, int addGold) {
		setValues(bep);
		addGold(ResWayType.Normal, addGold);
	}

	public void addGold(ResWayType wayType, int value) {
		addGolds.add(new ResAddInfo(wayType, value));
	}

	public int gainAddGold() {
		return (int) addGolds.stream().mapToLong(ResAddInfo::getValue).sum();
	}

}
