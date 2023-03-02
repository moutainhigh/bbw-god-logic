package com.bbw.job.utils;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.jdbc.core.JdbcTemplate;

import com.bbw.common.SpringContextUtil;
import com.bbw.common.utils.Constant;
import com.bbw.exception.CoderException;

/**
 * 定时任务工具类
 * com.bbw.job.entity.ScheduleJobEntity 这个类必须以完整包名的方式调用
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-16 23:22
 */
public class ScheduleUtils {
	private final static String JOB_NAME = "TASK_";

	/**
	 * 获取触发器key
	 */
	public static TriggerKey getTriggerKey(Long jobId) {
		return TriggerKey.triggerKey(JOB_NAME + jobId);
	}

	/**
	 * 获取jobKey
	 */
	public static JobKey getJobKey(Long jobId) {
		return JobKey.jobKey(JOB_NAME + jobId);
	}

	/**
	 * 获取表达式触发器
	 */
	public static CronTrigger getCronTrigger(Scheduler scheduler, Long jobId) {
		try {
			return (CronTrigger) scheduler.getTrigger(getTriggerKey(jobId));
		} catch (SchedulerException e1) {
			//log.error(e.getMessage(), e);
			JdbcTemplate jdbc = SpringContextUtil.getBean(JdbcTemplate.class);
			jdbc.execute("DELETE  FROM QRTZ_SCHEDULER_STATE");
			jdbc.execute("DELETE  FROM QRTZ_LOCKS");
			jdbc.execute("DELETE  FROM QRTZ_CRON_TRIGGERS");
			jdbc.execute("DELETE  FROM QRTZ_TRIGGERS");
			jdbc.execute("DELETE  FROM QRTZ_JOB_DETAILS");
			try {
				return (CronTrigger) scheduler.getTrigger(getTriggerKey(jobId));
			} catch (SchedulerException e2) {
				throw CoderException.high("获取定时任务CronTrigger出现异常!请再启动一次!", e2);
			}
		}
	}

	/**
	 * 创建定时任务
	 */
	public static void createScheduleJob(Scheduler scheduler, com.bbw.job.entity.ScheduleJobEntity scheduleJob) {
		try {
			//构建job信息
			JobDetail jobDetail = JobBuilder.newJob(ScheduleJob.class).withIdentity(getJobKey(scheduleJob.getJobId())).build();

			//表达式调度构建器
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression()).withMisfireHandlingInstructionDoNothing();

			//按新的cronExpression表达式构建一个新的trigger
			CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(scheduleJob.getJobId())).withSchedule(scheduleBuilder).build();

			//放入参数，运行时的方法可以获取
			jobDetail.getJobDataMap().put(com.bbw.job.entity.ScheduleJobEntity.JOB_PARAM_KEY, scheduleJob);

			scheduler.scheduleJob(jobDetail, trigger);

			//暂停任务
			if (scheduleJob.getStatus() == Constant.ScheduleStatus.PAUSE.getValue()) {
				pauseJob(scheduler, scheduleJob.getJobId());
			}
		} catch (SchedulerException e) {
			throw CoderException.normal("创建定时任务失败", e);
		}
	}

	/**
	 * 更新定时任务
	 */
	public static void updateScheduleJob(Scheduler scheduler, com.bbw.job.entity.ScheduleJobEntity scheduleJob) {
		try {
			TriggerKey triggerKey = getTriggerKey(scheduleJob.getJobId());

			//表达式调度构建器
			CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(scheduleJob.getCronExpression()).withMisfireHandlingInstructionDoNothing();

			CronTrigger trigger = getCronTrigger(scheduler, scheduleJob.getJobId());

			//按新的cronExpression表达式重新构建trigger
			trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();

			//参数
			trigger.getJobDataMap().put(com.bbw.job.entity.ScheduleJobEntity.JOB_PARAM_KEY, scheduleJob);

			scheduler.rescheduleJob(triggerKey, trigger);

			//暂停任务
			if (scheduleJob.getStatus() == Constant.ScheduleStatus.PAUSE.getValue()) {
				pauseJob(scheduler, scheduleJob.getJobId());
			}

		} catch (SchedulerException e) {
			throw CoderException.normal("更新定时任务失败", e);
		}
	}

	/**
	 * 立即执行任务
	 */
	public static void run(Scheduler scheduler, com.bbw.job.entity.ScheduleJobEntity scheduleJob) {
		try {
			//参数
			JobDataMap dataMap = new JobDataMap();
			dataMap.put(com.bbw.job.entity.ScheduleJobEntity.JOB_PARAM_KEY, scheduleJob);

			scheduler.triggerJob(getJobKey(scheduleJob.getJobId()), dataMap);
		} catch (SchedulerException e) {
			throw CoderException.normal("立即执行定时任务失败", e);
		}
	}

	/**
	 * 暂停任务
	 */
	public static void pauseJob(Scheduler scheduler, Long jobId) {
		try {
			scheduler.pauseJob(getJobKey(jobId));
		} catch (SchedulerException e) {
			throw CoderException.normal("暂停定时任务失败", e);
		}
	}

	/**
	 * 恢复任务
	 */
	public static void resumeJob(Scheduler scheduler, Long jobId) {
		try {
			scheduler.resumeJob(getJobKey(jobId));
		} catch (SchedulerException e) {
			throw CoderException.normal("暂停定时任务失败", e);
		}
	}

	/**
	 * 删除定时任务
	 */
	public static void deleteScheduleJob(Scheduler scheduler, Long jobId) {
		try {
			scheduler.deleteJob(getJobKey(jobId));
		} catch (SchedulerException e) {
			throw CoderException.normal("删除定时任务失败", e);
		}
	}
}
