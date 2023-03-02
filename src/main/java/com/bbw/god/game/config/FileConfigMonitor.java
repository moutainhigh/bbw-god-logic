package com.bbw.god.game.config;

import java.io.File;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.stereotype.Service;

/**
 * 监控配置目录
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-04 15:35
 */
@Service
public class FileConfigMonitor {
	public static String configDir = "config";//resources目录下的config

	public void start() throws Exception {
		URL url = this.getClass().getClassLoader().getResource(configDir);
		// 监控目录
		String rootDir = url.getPath();
		// 轮询间隔 5 秒
		long interval = TimeUnit.SECONDS.toMillis(5);
		// 创建过滤器
		IOFileFilter directories = FileFilterUtils.and(FileFilterUtils.directoryFileFilter(), HiddenFileFilter.VISIBLE);
		IOFileFilter files = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), FileFilterUtils.suffixFileFilter(".yml"));
		IOFileFilter filter = FileFilterUtils.or(directories, files);
		// 使用过滤器
		FileAlterationObserver observer = new FileAlterationObserver(new File(rootDir), filter);
		//不使用过滤器
		//FileAlterationObserver observer = new FileAlterationObserver(new File(rootDir));
		observer.addListener(new FileListener());
		//创建文件变化监听器
		FileAlterationMonitor monitor = new FileAlterationMonitor(interval, observer);
		// 开始监控
		monitor.start();
	}
}
