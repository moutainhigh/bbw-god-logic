package com.bbw.god.detail.async;

import com.bbw.god.db.entity.WanXianCPFightLogEntity;
import com.bbw.god.db.service.WanXianCPFightLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 记录点击预测界面中的选手战绩 查看视频的玩家流程
 *
 * @author: suhq
 * @date: 2021/12/16 2:02 下午
 */
@Slf4j
@Async
@Component
public class WanXianCPFightLogAsyncHandler {
	@Autowired
	private WanXianCPFightLogService wanXianCPFightLogService;

	/**
	 * 记录明细
	 */
	public void log(WanXianCPFightLogEntity detailData) {
		try {
			wanXianCPFightLogService.insert(detailData);
		} catch (Exception e) {
			log.error("明细数据保存失败！\n" + detailData);
			log.error(e.getMessage(), e);
		}
	}

}
