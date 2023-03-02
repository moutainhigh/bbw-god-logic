package com.bbw.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

/**
 * Spring Context 工具类
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-02 15:36
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
	}

	public static Object getBean(String name) {
		return applicationContext.getBean(name);
	}

	public static <T> T getBean(Class<T> requiredType, Object... args) {
		return applicationContext.getBean(requiredType, args);
	}

	public static Object getBean(String name, Object... args) {
		return applicationContext.getBean(name, args);
	}

	public static <T> T getBean(String name, Class<T> requiredType) {
		return applicationContext.getBean(name, requiredType);
	}

	public static <T> List<T> getBeans(Class<T> type) {
		Map<String, T> beanMap = applicationContext.getBeansOfType(type);
		List<T> beans = new ArrayList<T>();
		for (String beanName : beanMap.keySet()) {
			beans.add(beanMap.get(beanName));
		}
		return beans;
	}

	public static boolean containsBean(String name) {
		return applicationContext.containsBean(name);
	}

	public static boolean isSingleton(String name) {
		return applicationContext.isSingleton(name);
	}

	public static Class<? extends Object> getType(String name) {
		return applicationContext.getType(name);
	}

	public static void publishEvent(ApplicationEvent event) {
		applicationContext.publishEvent(event);
	}

}