package com.bbw.god.gameuser.buddy.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.EventParam;
import com.bbw.god.rd.RDCommon;

public class BuddyEventPublisher {

	public static void pubAcceptEvent(long guId, long buddyId, RDCommon rd) {
		SpringContextUtil.publishEvent(new BuddyAcceptEvent(new EventParam<Long>(guId, buddyId, rd)));
	}

}
