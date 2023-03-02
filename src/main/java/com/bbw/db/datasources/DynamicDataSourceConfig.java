package com.bbw.db.datasources;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

/**
 * 配置多数据源
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017/8/19 0:41
 */
@Configuration
public class DynamicDataSourceConfig {

	@Bean
	@ConfigurationProperties("spring.datasource.druid")
	public DataSource firstDataSource() {
		return DruidDataSourceBuilder.create().build();
	}

	@Bean
	@Primary
	public DynamicDataSource dataSource(@Qualifier("firstDataSource") DataSource firstDataSource) {
		DynamicDataSource dynamicDS = DynamicDataSource.getInstance();
		Map<Object, Object> targetDataSources = dynamicDS.getDataSourceMap();
		targetDataSources.put(DataSourceNames.FIRST, firstDataSource);
		dynamicDS.setDefaultTargetDataSource(firstDataSource);
		dynamicDS.setTargetDataSources(targetDataSources);
		return dynamicDS;
	}
}
