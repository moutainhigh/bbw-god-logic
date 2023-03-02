package com.bbw.job.utils;

import com.bbw.common.IpUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.job.config.ScheduleConfig;
import com.bbw.job.entity.ScheduleJobEntity;
import com.bbw.job.entity.ScheduleJobLogEntity;
import com.bbw.job.service.ScheduleJobLogService;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.concurrent.Future;

/**
 * 定时任务
 */
public class ScheduleJob extends QuartzJobBean {
    private Logger logger = LoggerFactory.getLogger(getClass());
//	private ExecutorService service = Executors.newSingleThreadExecutor();

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        ScheduleJobEntity scheduleJob = (ScheduleJobEntity) context.getMergedJobDataMap().get(ScheduleJobEntity.JOB_PARAM_KEY);

        //获取spring bean
        ScheduleJobLogService scheduleJobLogService = (ScheduleJobLogService) SpringContextUtil.getBean("scheduleJobLogService");

        //数据库保存执行记录
        ScheduleJobLogEntity log = new ScheduleJobLogEntity();
        log.setJobId(scheduleJob.getJobId());
        log.setBeanName(scheduleJob.getBeanName());
        log.setMethodName(scheduleJob.getMethodName());
        log.setParams(scheduleJob.getParams());
        log.setCreateTime(new Date());

        //任务开始时间
        long startTime = System.currentTimeMillis();

        try {
            //执行任务
            logger.info("准备执行：" + scheduleJob.getBeanName());

            //String hm = DateUtil.toString(DateUtil.now(), "HHmm");
            //凌晨0点10分以内不执行定时任务。
            //			if (StrUtil.getInt(hm) < 10) {
            //				//logger.info("凌晨0点10分以内不执行定时任务！本次被忽略！");
            //				log.setStatus(1);
            //				log.setError("凌晨0点10分以内不执行定时任务！本次被忽略！");
            //			} else {

            ScheduleRunnable task = new ScheduleRunnable(scheduleJob.getBeanName(), scheduleJob.getMethodName(), scheduleJob.getParams());
            Future<?> future = ScheduleConfig.executorService.submit(task);

            future.get();
            //}
            //任务执行总时长
            long times = System.currentTimeMillis() - startTime;
            log.setTimes((int) times);
            //任务状态    0：成功    1：失败
            log.setStatus(0);
            String ip = IpUtil.getInet4Address();
            String logicName = ip;
            log.setParams(log.getParams() + "执行主机:" + logicName);
            logger.info("任务执行完毕，任务：" + scheduleJob.getBeanName() + "  总共耗时：" + times + "毫秒");
        } catch (Exception e) {
            logger.error("任务执行失败，任务ID：" + scheduleJob.getJobId(), e);

            //任务执行总时长
            long times = System.currentTimeMillis() - startTime;
            log.setTimes((int) times);

            //任务状态    0：成功    1：失败
            log.setStatus(1);
            log.setError(StringUtils.substring(e.toString(), 0, 2000));
        } finally {
            scheduleJobLogService.insert(log);
        }
    }
}
