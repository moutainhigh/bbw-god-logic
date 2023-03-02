package com.bbw.god.detail.async;

import com.bbw.god.db.entity.InsAccessLogEntity;
import com.bbw.god.db.service.InsAccessLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 请求访问明细处理器
 *
 * @author lwb
 * @date 2019-10-25 10:31:58
 */
@Slf4j
@Async
@Component
public class AccessDetailAsyncHandler {
	@Autowired
	private InsAccessLogService insAccessLogService;

	/**
	 * 记录明细
	 *
	 * @param detailData
	 */
	public void log(InsAccessLogEntity detailData) {
		try {
			insAccessLogService.insert(detailData);
		} catch (Exception e) {
			log.error("访问日志明细数据保存失败！\n" + detailData);
			log.error(e.getMessage(), e);
		}
	}

}
