package com.bbw.god.db.split;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-27 10:48
 */
import java.sql.Connection;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class, Integer.class }) })
public class TableSplitInterceptor implements Interceptor {

	private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
	private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
	private static final ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();
	@Autowired
	private StrategyFactory factory;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
		MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, DEFAULT_OBJECT_FACTORY, DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);

		BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
		doSplitTable(metaStatementHandler, boundSql);
		// 传递给下一个拦截器处理
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		// 当目标类是StatementHandler类型时，才包装目标类，否者直接返回目标本身,减少目标被代理的次数
		if (target instanceof StatementHandler) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	private void doSplitTable(MetaObject metaStatementHandler, BoundSql boundSql) throws ClassNotFoundException {

		String originalSql = (String) metaStatementHandler.getValue("delegate.boundSql.sql");
		if (originalSql != null && !originalSql.equals("")) {
			//System.out.println("分表前的SQL：" + originalSql);
			MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");
			String id = mappedStatement.getId();
			String className = id.substring(0, id.lastIndexOf("."));
			Class<?> classObj = Class.forName(className);
			// 根据配置自动生成分表SQL
			TableSplit tableSplit = classObj.getAnnotation(TableSplit.class);
			if (tableSplit != null && tableSplit.split()) {
				Strategy strategy = factory.getStrategy(tableSplit.strategy());//获取分表策略来处理分表
				String convertedSql = originalSql;
				try {
					convertedSql = originalSql.replaceAll(tableSplit.tableName(), strategy.convert(tableSplit.tableName(), boundSql.getParameterMappings(), boundSql.getParameterObject()));
				} catch (Exception e) {
					log.error("分表保存失败!" + e.getMessage(), e);
				}
				metaStatementHandler.setValue("delegate.boundSql.sql", convertedSql);
				//System.out.println("分表后的SQL：" + convertedSql);
			}
		}
	}

	@Override
	public void setProperties(Properties properties) {

	}
}