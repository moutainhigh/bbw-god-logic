package com.bbw.god.clear;

/**
 * 区服定时需要清理的资源
 * 
 * @author suhq
 * @date 2019-07-12 09:13:08
 */
public interface ServerClearService {
	/**
	 * 清理资源
	 */
	void clear(int sid);
}
