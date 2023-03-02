package com.bbw.god.detail.async;

import com.bbw.god.db.entity.InsErrorLogEntity;
import com.bbw.god.db.service.InsErrorLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 错误明细异步处理器
 *
 * @author: suhq
 * @date: 2021/12/16 11:46 上午
 */
@Slf4j
@Async
@Component
public class ErrorDetailAsyncHandler {
	@Autowired
	private InsErrorLogService detailService;

	/**
	 * 记录明细
	 *
	 * @param detailData
	 */
	public void log(InsErrorLogEntity detailData) {
		try {
			detailService.insert(detailData);
		} catch (Exception e) {
			log.error("访问日志明细数据保存失败！\n" + detailData);
			log.error(e.getMessage(), e);
		}
	}

}
