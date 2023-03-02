package com.bbw.god.game.combat.video;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.video.service.CombatVideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 玩家录像明细处理器
 *
 * @author suhq
 * @date 2019-07-25 17:31:58
 */
@Slf4j
@Async
@Component
public class CombatVideoAsyncHandler {

	/**
	 * 记录战斗明细
	 *
	 * @param detailId
	 * @param combat
	 */
	public void monitor(Long detailId, Combat combat) {
		try {
			CombatVideoService service = SpringContextUtil.getBean(CombatVideoService.class);
			service.save4LvCityWar(combat, detailId);
		} catch (Exception e) {
			log.error("保存监听战战失败：对应PVE明细ID：" + detailId);
			log.error(e.getMessage(), e);
		}
	}

}
