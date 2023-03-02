package com.bbw.god.gameuser.res.diamond;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.res.ResAddInfo;
import com.bbw.god.gameuser.res.ResWayType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 获得的钻石信息
 *
 * @author: huanghb
 * @date: 2022/6/15 17:06
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPDiamondAdd extends BaseEventParam {
	private List<ResAddInfo> addDiamond = new ArrayList<>();

	public EPDiamondAdd(BaseEventParam bep, int addDiamond) {
		setValues(bep);
		addDiamond(ResWayType.Normal, addDiamond);
	}

	public void addDiamond(ResWayType wayType, int value) {
		addDiamond.add(new ResAddInfo(wayType, value));
	}

	public int gainAddDiamond() {
		return (int) addDiamond.stream().mapToLong(ResAddInfo::getValue).sum();
	}

}
