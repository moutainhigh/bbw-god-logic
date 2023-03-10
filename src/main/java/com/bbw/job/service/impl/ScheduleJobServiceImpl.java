package com.bbw.job.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.bbw.common.utils.Constant;
import com.bbw.db.PageUtils;
import com.bbw.db.Query;
import com.bbw.job.dao.ScheduleJobDao;
import com.bbw.job.entity.ScheduleJobEntity;
import com.bbw.job.service.ScheduleJobService;
import com.bbw.job.utils.ScheduleUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("scheduleJobService")
public class ScheduleJobServiceImpl extends ServiceImpl<ScheduleJobDao, ScheduleJobEntity>
		implements ScheduleJobService {
	@Autowired
	private Scheduler scheduler;

	/**
	 * 项目启动时，初始化定时器
	 */
	@PostConstruct
	public void init() {
		log.info("初始化定时器...");
		List<ScheduleJobEntity> scheduleJobList = this.selectList(null);
		for (ScheduleJobEntity scheduleJob : scheduleJobList) {
			log.info("初始化定时器：【{}】-【{}】", scheduleJob.getBeanName(), scheduleJob.getMethodName());
			CronTrigger cronTrigger = ScheduleUtils.getCronTrigger(scheduler, scheduleJob.getJobId());
			// 如果不存在，则创建
			if (cronTrigger == null) {
				ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
			} else {
				ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);
			}
		}
		log.info("定时器初始化完成.");
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String beanName = (String) params.get("beanName");

		Page<ScheduleJobEntity> page = this.selectPage(new Query<ScheduleJobEntity>(params).getPage(),
				new EntityWrapper<ScheduleJobEntity>().like(StringUtils.isNotBlank(beanName), "bean_name", beanName));

		return new PageUtils(page);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(ScheduleJobEntity scheduleJob) {
		scheduleJob.setCreateTime(new Date());
		scheduleJob.setStatus(Constant.ScheduleStatus.NORMAL.getValue());
		this.insert(scheduleJob);

		ScheduleUtils.createScheduleJob(scheduler, scheduleJob);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(ScheduleJobEntity scheduleJob) {
		ScheduleUtils.updateScheduleJob(scheduler, scheduleJob);

		this.updateById(scheduleJob);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteBatch(Long[] jobIds) {
		for (Long jobId : jobIds) {
			ScheduleUtils.deleteScheduleJob(scheduler, jobId);
		}

		// 删除数据
		this.deleteBatchIds(Arrays.asList(jobIds));
	}

	@Override
	public int updateBatch(Long[] jobIds, int status) {
		Map<String, Object> map = new HashMap<>();
		map.put("list", jobIds);
		map.put("status", status);
		return baseMapper.updateBatch(map);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void run(Long[] jobIds) {
		for (Long jobId : jobIds) {
			ScheduleUtils.run(scheduler, this.selectById(jobId));
		}
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void pause(Long[] jobIds) {
		for (Long jobId : jobIds) {
			ScheduleUtils.pauseJob(scheduler, jobId);
		}

		updateBatch(jobIds, Constant.ScheduleStatus.PAUSE.getValue());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void resume(Long[] jobIds) {
		for (Long jobId : jobIds) {
			ScheduleUtils.resumeJob(scheduler, jobId);
		}

		updateBatch(jobIds, Constant.ScheduleStatus.NORMAL.getValue());
	}

}
