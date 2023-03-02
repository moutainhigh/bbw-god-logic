package com.bbw.god.detail.async;

import com.bbw.god.db.entity.WanXianUserCardsEntity;
import com.bbw.god.db.service.WanXianUserCardsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 玩家竞技明细处理器
 *
 * @author suhq
 * @date 2019-07-25 17:31:58
 */
@Slf4j
@Async
@Component
public class WanXianUserCardsAsyncHandler {
	@Autowired
	private WanXianUserCardsService wanXianUserCardsService;

	/**
	 * 记录明细
	 */
	public void log(WanXianUserCardsEntity detailData) {
		try {
			wanXianUserCardsService.insert(detailData);
		} catch (Exception e) {
			log.error("明细数据保存失败！\n" + detailData);
			log.error(e.getMessage(), e);
		}
	}

}
