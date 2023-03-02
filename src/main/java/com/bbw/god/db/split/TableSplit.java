package com.bbw.god.db.split;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.bbw.god.db.dao.InsUserDetailDao;

/**
 * 分表注解，在*Dao类上注解。
 * @see InsUserDetailDao
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-27 10:48
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface TableSplit {
	//是否分表
	public boolean split() default true;

	//需要分表的表名
	public String tableName() default "";

	//获取分表策略Id
	public String strategy();

}
