package com.bbw.god.game.chanjie.event;

import com.bbw.god.event.BaseEventParam;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EPChanjieLDFSFourWin extends BaseEventParam {
	private boolean firstPlayer;

	public static EPChanjieLDFSFourWin instance(BaseEventParam bep, boolean first) {
		EPChanjieLDFSFourWin ev = new EPChanjieLDFSFourWin();
		ev.setFirstPlayer(first);
		ev.setValues(bep);
		return ev;
	}
}
