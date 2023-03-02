package com.bbw.god.db.split;

import java.util.List;

import org.apache.ibatis.mapping.ParameterMapping;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-27 10:44
 */
//用接口定义拦截后返回新的表名
public interface Strategy {
	/**
	 * 支持策略
	 * @param strategyKey
	 * @return
	 */
	public boolean support(String strategyKey);

	/**
	 * 传入一个需要分表的表名，返回一个处理后的表名
	 * @param tableName:要分表的原始表名
	 * @param paramsMapping:SQL语句中的参数映射集合。
	 * @param dbEntity:和数据库映射的实体对象。如：InsUserEntity
	 * @return
	 */
	public String convert(String tableName, List<ParameterMapping> paramsMapping, Object dbEntity);
}
