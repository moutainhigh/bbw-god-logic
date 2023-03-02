package com.bbw.god.gameuser.res.exp;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.gameuser.res.ResAddInfo;
import com.bbw.god.gameuser.res.ResWayType;
import com.bbw.god.rd.RDCommon;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 获得的经验信息
 *
 * @author suhq
 * @date 2019-09-28 17:03:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EPExpAdd extends BaseEventParam {
	private List<ResAddInfo> addExps = new ArrayList<>();

	public EPExpAdd(BaseEventParam bep, long addExp) {
		setValues(bep);
		addExp(ResWayType.Normal, addExp);
	}

	public void addExp(ResWayType wayType, long value) {
		addExps.add(new ResAddInfo(wayType, value));
	}

	public long gainAddExp() {
		return addExps.stream().mapToLong(ResAddInfo::getValue).sum();
	}

	public long gainNormalExp() {
		ResAddInfo rdAddInfo = addExps.stream().filter(tmp -> tmp.getWayType() == ResWayType.Normal).findFirst().orElse(null);
		if (rdAddInfo != null) {
			return rdAddInfo.getValue();
		}
		return 0;
	}

	public List<RDCommon.RDResAddInfo> buildRD(){
		List<RDCommon.RDResAddInfo> rd=new ArrayList<>();
		for (ResAddInfo addExp : addExps) {
			rd.add(new RDCommon.RDResAddInfo(addExp.getWayType().getValue(),addExp.getValue()));
		}
		return rd;
	}

}
