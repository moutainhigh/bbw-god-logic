package com.bbw.db.datasources;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * 动态数据源
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017/8/19 1:03
 */
public class DynamicDataSource extends AbstractRoutingDataSource {
	private static DynamicDataSource instance;
	private static byte[] lock = new byte[0];
	private static Map<Object, Object> dataSourceMap = new HashMap<Object, Object>();

	@Override
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		super.setTargetDataSources(targetDataSources);
		dataSourceMap.putAll(targetDataSources);
		super.afterPropertiesSet();// 必须添加该句，否则新添加数据源无法识别到
	}

	public Map<Object, Object> getDataSourceMap() {
		return dataSourceMap;
	}

	public boolean contains(String dsKeyName) {
		return dataSourceMap.containsKey(dsKeyName);
	}

	public void addDataSource(String dsKeyName, DataSource ds) {
		dataSourceMap.put(dsKeyName, ds);
		setTargetDataSources(dataSourceMap);
	}

	public static synchronized DynamicDataSource getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new DynamicDataSource();
				}
			}
		}
		return instance;
	}

	public void removeDataSource(String dsKeyName) {
		if (dataSourceMap.containsKey(dsKeyName)) {
			DruidDataSource ds = (DruidDataSource) dataSourceMap.get(dsKeyName);
			if (null != ds) {
				ds.close();
			}
			dataSourceMap.remove(dsKeyName);
			setTargetDataSources(dataSourceMap);
		}
	}

	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceContextHolder.getServer();
	}

}
