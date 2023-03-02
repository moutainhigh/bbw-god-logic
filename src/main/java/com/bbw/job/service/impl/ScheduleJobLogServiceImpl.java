package com.bbw.job.service.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.db.PageUtils;
import com.bbw.db.Query;
import com.bbw.job.dao.ScheduleJobLogDao;
import com.bbw.job.entity.ScheduleJobLogEntity;
import com.bbw.job.service.ScheduleJobLogService;

@Service("scheduleJobLogService")
public class ScheduleJobLogServiceImpl extends ServiceImpl<ScheduleJobLogDao, ScheduleJobLogEntity> implements ScheduleJobLogService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String jobId = (String) params.get("jobId");

		Page<ScheduleJobLogEntity> page = this.selectPage(new Query<ScheduleJobLogEntity>(params).getPage(), new EntityWrapper<ScheduleJobLogEntity>().like(StringUtils.isNotBlank(jobId), "job_id", jobId));

		return new PageUtils(page);
	}

}
