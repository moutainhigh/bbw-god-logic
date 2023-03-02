package com.bbw.job.config;

import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.scheduling.quartz.AdaptableJobFactory;
import org.springframework.stereotype.Component;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月6日 上午12:55:25
 */
@Component
public class SpringJobFactory extends AdaptableJobFactory {
	@Autowired
	private AutowireCapableBeanFactory capableBeanFactory;

	@Override
	protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
		// 调用父类的方法 
		Object jobInstance = super.createJobInstance(bundle);
		// 进行注入 
		capableBeanFactory.autowireBean(jobInstance);
		return jobInstance;
	}
}