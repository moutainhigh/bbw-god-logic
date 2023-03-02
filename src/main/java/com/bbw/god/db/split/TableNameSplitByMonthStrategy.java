package com.bbw.god.db.split;

import com.bbw.common.DateUtil;
import org.apache.ibatis.mapping.ParameterMapping;
import org.springframework.stereotype.Service;

import java.util.List;

/***
 * 战斗日志分表
 * @author liuwenbin
 */
@Service
public class TableNameSplitByMonthStrategy implements Strategy {
	private static final String STRATEGY_KEY = "tableName.split.month";

	/**
	 * 支持策略
	 * @return
	 */
	@Override
	public boolean support(String strategyKey) {
		return STRATEGY_KEY.equals(strategyKey);
	}

	/**
	 * 传入一个需要分表的表名，返回一个处理后的表名
	 * @param tableName:要分表的原始表名
	 * @param paramsMapping:SQL语句中的参数映射集合。
	 * @param dbEntity:和数据库映射的实体对象。如：InsUserEntity。或者是参数结果，如 "uid=190212,sid=12"
	 * @return
	 */
	@Override
	public String convert(String tableName, List<ParameterMapping> paramsMapping, Object dbEntity) {
		int yearMonthInt = DateUtil.toMonthInt(DateUtil.now());
		StringBuilder sb = new StringBuilder();
		sb.append(tableName);
		sb.append("_");
		sb.append(yearMonthInt);
		return  sb.toString();
	}

}
