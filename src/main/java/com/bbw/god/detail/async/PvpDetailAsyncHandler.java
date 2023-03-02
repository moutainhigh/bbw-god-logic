package com.bbw.god.detail.async;

import com.bbw.god.db.entity.InsGamePvpDetailEntity;
import com.bbw.god.db.service.InsGamePvpDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 玩家竞技明细异步处理器
 *
 * @author: suhq
 * @date: 2021/12/16 11:24 上午
 */
@Slf4j
@Async
@Component
public class PvpDetailAsyncHandler {
	@Autowired
	private InsGamePvpDetailService pvpDetailService;

	/**
	 * 记录明细
	 *
	 * @param detailData
	 */
	public void log(InsGamePvpDetailEntity detailData) {
		try {
			pvpDetailService.insert(detailData);
		} catch (Exception e) {
			log.error("访问日志明细数据保存失败！\n" + detailData);
			log.error(e.getMessage(), e);
		}
	}

}
