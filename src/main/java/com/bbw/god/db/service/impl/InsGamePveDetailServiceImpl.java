package com.bbw.god.db.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.common.DateUtil;
import com.bbw.god.db.dao.InsGamePveDetailDao;
import com.bbw.god.db.entity.InsGamePveDetailEntity;
import com.bbw.god.db.service.InsGamePveDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class InsGamePveDetailServiceImpl extends ServiceImpl<InsGamePveDetailDao, InsGamePveDetailEntity> implements InsGamePveDetailService {
	@Autowired
	private InsGamePveDetailDao insGamePveDetailDao;

	@Override
	public List<InsGamePveDetailEntity> getByUidAndDate(Long uid, Date date) {
		long start = DateUtil.toDateTimeLong(DateUtil.toDate(date, "00:00:00"));
		long end = DateUtil.toDateTimeLong(DateUtil.toDate(date, "23:59:59"));
		EntityWrapper<InsGamePveDetailEntity> wrapper = new EntityWrapper<>();
		wrapper.eq("uid", uid).between("recording_time", start, end);
		return this.selectList(wrapper);
	}

	/**
	 * 获取成就13880所需数据
	 *
	 * @param uid
	 * @param sid
	 * @param date
	 * @return
	 */
	@Override
	public List<InsGamePveDetailEntity> getDataForAchievement13880(long uid, int sid, Date date) {
		EntityWrapper<InsGamePveDetailEntity> wrapper = new EntityWrapper<>();
		wrapper.eq("uid", uid).eq("sid", sid);
		wrapper.gt("recording_time", DateUtil.toDateTimeLong(date));
		wrapper.orderBy("recording_time", true);
		Page<InsGamePveDetailEntity> myPage = new Page<>(0, 3);
		return insGamePveDetailDao.selectPage(myPage, wrapper);
	}
}
