package com.bbw.god.game.combat.video;

import com.bbw.oss.OSSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 玩家录像保存明细处理器
 *
 * @author: suhq
 * @date: 2021/12/16 2:27 下午
 */
@Slf4j
@Async
@Component
public class CombatVideoSaveAsyncHandler {
	/**
	 * 记录明细
	 */
	public void save(CombatVideo video, String ossPath) {
		try {
			OSSService.uploadVideo(video, ossPath);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

}
