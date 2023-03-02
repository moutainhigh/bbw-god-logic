package com.bbw.god.db.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.WanXianRankEntity;

import java.util.List;

public interface WanXianRankService extends IService<WanXianRankEntity> {
	/**
	 * 获取所有的赛季
	 *
	 * @return
	 */
	List<Integer> getAllSeasons();

	/**
	 * 获取常规赛数据
	 *
	 * @param season 赛季
	 * @return
	 */
	List<WanXianRankEntity> getNormalDataBySeason(int season);

	/**
	 * 获取特色赛数据
	 *
	 * @param season 赛季
	 * @return
	 */
	List<WanXianRankEntity> getSpecialDataBySeason(int season);
}
