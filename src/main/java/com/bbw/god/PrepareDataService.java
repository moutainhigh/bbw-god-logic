package com.bbw.god;

import java.util.Date;

/**
 * 提前生成数据服务
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-20 11:38
 */
public interface PrepareDataService {

	/**
	 * 生成今天之后days天的数据
	 * @param days
	 */
	void prepareDatas(int days);

	boolean check(Date date);

}
