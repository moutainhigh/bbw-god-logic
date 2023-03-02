package com.bbw.god.db.service;

import com.baomidou.mybatisplus.service.IService;
import com.bbw.god.db.entity.InsGamePveDetailEntity;

import java.util.Date;
import java.util.List;

/**
 * 玩家PVE竞技明细
 * 
 * @author suhq
 * @date 2019-07-25 11:46:11
 */
public interface InsGamePveDetailService extends IService<InsGamePveDetailEntity> {
	/**
	 * 根据玩家id和日期获取数据
	 *
	 * @param uid
	 * @param date
	 * @return
	 */
	List<InsGamePveDetailEntity> getByUidAndDate(Long uid, Date date);

	/**
	 * 获取成就13880所需数据
	 *
	 * @param uid
	 * @param sid
	 * @param date
	 * @return
	 */
	List<InsGamePveDetailEntity> getDataForAchievement13880(long uid, int sid, Date date);
}
