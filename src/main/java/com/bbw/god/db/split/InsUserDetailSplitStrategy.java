package com.bbw.god.db.split;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.mapping.ParameterMapping;
import org.springframework.stereotype.Service;

import com.bbw.god.db.entity.InsUserDetailEntity;

@Service
public class InsUserDetailSplitStrategy implements Strategy {
	private static final String STRATEGY_KEY = "InsUserDetailDao.split.uid";

	/**
	 * 支持策略
	 * @param strategyName
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
		//用对象传值
		InsUserDetailEntity entity = (InsUserDetailEntity) dbEntity;
		Long uid = entity.getUid();
		uid = Optional.ofNullable(uid).orElse(0L) % 10;
		int seq = uid.intValue();
		StringBuilder sb = new StringBuilder();
		sb.append(tableName);
		sb.append("_");
		sb.append(seq);
		return sb.toString();
	}

}
