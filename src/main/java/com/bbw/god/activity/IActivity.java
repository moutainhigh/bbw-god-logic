package com.bbw.god.activity;

import java.util.Date;

/**
 * 活动实例接口
 * 
 * @author suhq
 * @date 2019年4月9日 上午12:10:37
 */
public interface IActivity {

	Long gainId();

	Integer gainSId();

	Integer gainParentType();

	Integer gainType();

	Date gainBegin();

	Date gainEnd();

	/**
	 * 是否在活动时间内
	 *
	 * @return
	 */
	Boolean ifTimeValid();

	/**
	 * 节日主题标识
	 *
	 * @return
	 */
	String gainSign();


}
