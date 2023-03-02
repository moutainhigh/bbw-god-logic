package com.bbw.god.game.chanjie.event;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.event.BaseEventParam;

/**
 * 
* @author lwb  
* @date 2019年6月24日  
* @version 1.0
 */
public class ChanjieEventPublisher {

	public static void pubFightEvent(EPChanjieFight ev) {
		SpringContextUtil.publishEvent(new ChanjieFightEvent(ev));
	}
	
	public static void pubGainHeadEvent(EPChanjieGainHead ev) {
		SpringContextUtil.publishEvent(new ChanjieGainHeadEvent(ev));
	}
	
	public static void pubSpecailHonorEvent(EPChanjieSpecailHonor ev) {
		SpringContextUtil.publishEvent(new ChanjieSpecailHonorEvent(ev));
	}
	
	public static void pubReligionSelectEvent(EPChanjieReligionSelect ev) {
		SpringContextUtil.publishEvent(new ChanjieReligionSelectEvent(ev));
	}

	public static void pubLDFSFourWinEvent(EPChanjieLDFSFourWin ev) {
		SpringContextUtil.publishEvent(new ChanjieLDFSFourWinEvent(ev));
	}

	public static void pubLdfsInvitationEvent(long uid) {
		EPChanjieLdfsInvitation ep=new EPChanjieLdfsInvitation();
		ep.setValues(new BaseEventParam(uid));
		SpringContextUtil.publishEvent(new ChanjieLdfsInvitationEvent(ep));
	}
}
