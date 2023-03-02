package com.bbw.god.city.yed;

import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDAdvance;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 无所事事处理器
 * @date 2020/6/16 11:14
 **/
@Service
public class DoNothingProcessor extends BaseYeDEventProcessor {
	@Override
	public int getMyId() {
		return YdEventEnum.NONE.getValue();
	}

	@Override
	public void effect(GameUser gameUser, RDArriveYeD rdArriveYeD, RDAdvance rd) {

	}
}
