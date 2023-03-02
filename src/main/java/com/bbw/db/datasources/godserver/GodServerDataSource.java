package com.bbw.db.datasources.godserver;

import com.alibaba.druid.pool.DruidDataSource;
import com.bbw.common.StrUtil;
import com.bbw.exception.CoderException;
import com.bbw.exception.ErrorLevel;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年6月7日 下午3:56:13
 */
public enum GodServerDataSource {
	I;
	//TODO:区服数据库链接，抽空改成从配置文件读取

	public DruidDataSource getDataSource(String url) {
		if (!isValidDbConnectUrl(url)) {
			String msg = url + "服务器链接配置错误！";
			CoderException ce = new CoderException(msg, ErrorLevel.HIGH);
			throw ce;
		}
		if (url.indexOf("useSSL") < 0) {
			url += "&useSSL=false";
		}
		DruidDataSource ds = new DruidDataSource();
		//设置名称会有异常
		//	ds.setName(name);
		//设置连接参数
		ds.setUrl(url);
		ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
		//配置初始化大小、最小、最大
		ds.setInitialSize(1);
		ds.setMinIdle(1);
		//TODO:最大数据库连接数，需要根据实际进行调节
		ds.setMaxActive(10);
		//连接泄漏监测
		ds.setRemoveAbandoned(true);
		ds.setRemoveAbandonedTimeout(30);
		//配置获取连接等待超时的时间
		ds.setMaxWait(60000);
		//配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
		ds.setTimeBetweenEvictionRunsMillis(60000);
		//防止过期
		ds.setValidationQuery("SELECT 'x'");
		ds.setTestWhileIdle(true);
		ds.setTestOnBorrow(false);
		return ds;
	}

	private boolean isValidDbConnectUrl(String url) {
		if (StrUtil.isNull(url)) {
			return false;
		}
		if (!url.trim().startsWith("jdbc")) {
			return false;
		}
		return true;
	}
}
