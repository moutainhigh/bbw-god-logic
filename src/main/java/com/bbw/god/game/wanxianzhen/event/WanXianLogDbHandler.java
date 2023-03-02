package com.bbw.god.game.wanxianzhen.event;

import com.bbw.god.db.entity.WanXianFightDetailEntity;
import com.bbw.god.db.entity.WanXianMatchEntity;
import com.bbw.god.db.entity.WanXianRankEntity;
import com.bbw.god.db.service.WanXianFightDetailService;
import com.bbw.god.db.service.WanXianMatchService;
import com.bbw.god.db.service.WanXianRankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 万仙阵明细处理器
 *
 * @author: suhq
 * @date: 2021/12/16 2:43 下午
 */
@Slf4j
@Async
@Component
public class WanXianLogDbHandler {
	@Autowired
	private WanXianMatchService wanXianMatchService;
	@Autowired
	private WanXianFightDetailService wanXianFightDetailService;
	@Autowired
	private WanXianRankService wanXianRankService;

	public void logFightLogs(List<WanXianFightDetailEntity> detailEntities) {
		if (detailEntities == null || detailEntities.isEmpty()) {
			return;
		}
		try {
			wanXianFightDetailService.insertBatch(detailEntities, detailEntities.size());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void logFightLog(WanXianFightDetailEntity detailEntity) {
		try {
			wanXianFightDetailService.insert(detailEntity);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void logMatchs(List<WanXianMatchEntity> entities) {
		if (entities == null || entities.isEmpty()) {
			return;
		}
		try {
			wanXianMatchService.insertBatch(entities, entities.size());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void logMatch(WanXianMatchEntity entity) {
		try {
			wanXianMatchService.insert(entity);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void logRank(WanXianRankEntity entity) {

		try {
			wanXianRankService.insert(entity);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void logRanks(List<WanXianRankEntity> entities) {
		try {
			wanXianRankService.insertBatch(entities, entities.size());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
