package com.bbw.sys.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-05 15:51
 */
@WebListener
public class AppContextListener implements ServletContextListener {
	public void contextDestroyed(ServletContextEvent event) {
		System.out.println("tomcat已经关闭！开始关闭quartz！");
		try {
			SchedulerFactory sf = new StdSchedulerFactory();//创建新的调度器工厂
			Scheduler scheduler = sf.getScheduler();//获取当前进程的所有定时器线程数据
			scheduler.shutdown(false);//关闭定时器线程
			System.out.println("关闭定时器线程成功！");
		} catch (Exception e) {
			System.out.println("关闭定时器线程失败！");
			e.printStackTrace();
		}
	}

	//https://stackoverflow.com/questions/23936162/register-shutdownhook-in-web-application
	public void contextInitialized(ServletContextEvent event) {
		//ServletContext context = event.getServletContext();        
		//System.setProperty("rootPath", context.getRealPath("/"));
		//logContext ctx = (logContext) LogManager.getContext(false);
		//ctx.reconfigure();
		/*
		 * log.info("global setting,rootPath:{}",rootPath);
		 * log.info("deployed on architecture:{},operation System:{},version:{}", System.getProperty("os.arch"),
		 * System.getProperty("os.name"), System.getProperty("os.version")); Debugger.dump();
		 * log.info("app startup completed....");
		 */
	}
}