package com.bbw.god.city.yed;

import com.bbw.god.city.event.CityEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDAdvance;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 押押乐提示事件处理器
 * @date 2020/6/1 11:03
 **/
@Service
public class YaYaLeTipProcessor extends BaseYeDEventProcessor {
	/**
	 * 获取当前野地事件id
	 */
	@Override
	public int getMyId() {
		return YdEventEnum.YAYALE_TIP.getValue();
	}

	/**
	 * 野地事件生效
	 *
	 * @param gameUser
	 * @param rdArriveYeD
	 * @param rd
	 */
	@Override
	public void effect(GameUser gameUser, RDArriveYeD rdArriveYeD, RDAdvance rd) {
		Integer tip = yylTipsService.getTodayTip();
		if (tip == null) {
			//没有今日压压乐提示，则执行检查生成，本次事件改为无事事件
			yylTipsService.checkAndGenerateTips();
			rdArriveYeD.updateEvent(yeDProcessor.getYDEventById(YdEventEnum.NONE.getValue()));
			return;
		}
		rdArriveYeD.setTipEle(tip);
		BaseEventParam bep = new BaseEventParam(gameUser.getId(), WayEnum.YD, rd);
		CityEventPublisher.pubYeDTrigger(bep, EPYeDTrigger.fromIncome(YdEventEnum.YAYALE_TIP, tip));
	}
}
