package com.bbw.god.game.config;

import java.io.Serializable;

/**配置接口类
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-04 19:07
 */
public interface CfgInterface {
	public static final String FILE_DEFAULT_KEY = "默认";//文件配置，默认配置的ID值
	public static final String FILE_UNIQE_KEY = "唯一";//文件配置，有且仅有一份配置时，默认到ID值
	public static final int DB_DEFAULT_ID = 0;//数据库配置默认值的ID值
	public static final int DB_UNIQE_ID = 1;//数据库配置，有且仅有一份配置时，默认到ID值

	/**
	 * 获取配置项到ID值
	 * @return
	 */
	Serializable getId();

	/**
	 * 获取排序号
	 * @return
	 */
	int getSortId();

}
