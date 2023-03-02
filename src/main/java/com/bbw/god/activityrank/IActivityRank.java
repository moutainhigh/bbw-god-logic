package com.bbw.god.activityrank;

import java.util.Date;

/**
 * 活动实例接口
 * 
 * @author suhq
 * @date 2019年4月9日 上午12:10:37
 */
public interface IActivityRank {

	Long gainId();

	Integer gainSId();

	Integer gainType();

	Integer gainOpenWeek();

	String gainExtraAward();

	Date gainBegin();

	Date gainEnd();

}
