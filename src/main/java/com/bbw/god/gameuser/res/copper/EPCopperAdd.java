package com.bbw.god.gameuser.res.copper;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.res.ResAddInfo;
import com.bbw.god.gameuser.res.ResWayType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 获得的铜钱信息
 *
 * @author suhq
 * @date 2019年3月5日 上午10:21:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPCopperAdd extends BaseEventParam {
	private List<ResAddInfo> addCoppers = new ArrayList<>();
	private long weekCopper = 0;// 富豪榜铜钱加量

	public EPCopperAdd(BaseEventParam baseEP, int earnCopper) {
		setValues(baseEP);
		addCopper(earnCopper);
	}

	public EPCopperAdd(BaseEventParam baseEP, long earnCopper, long weekCopper) {
		setValues(baseEP);
		this.weekCopper = weekCopper;
		addCopper(earnCopper);
	}

	public void addCopper(long value) {
		addCoppers.add(new ResAddInfo(ResWayType.Normal, value));
	}

	public void addCopper(ResWayType wayType, long value) {
		if (value == 0) {
			return;
		}
		addCoppers.add(new ResAddInfo(wayType, value));
		if (wayType != ResWayType.Normal) {
			weekCopper += value;
		}
	}

	public long gainAddCopper() {
		return addCoppers.stream().mapToLong(ResAddInfo::getValue).sum();
	}

	public long gainCopper(ResWayType wayType) {
		ResAddInfo resAddInfo = addCoppers.stream().filter(tmp -> tmp.getWayType() == wayType).findFirst().orElse(null);
		if (resAddInfo != null) {
			return resAddInfo.getValue();
		}
		return 0;
	}

}
