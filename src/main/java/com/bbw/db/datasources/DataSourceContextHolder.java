package com.bbw.db.datasources;

/**
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月11日 下午6:31:50
 */
public class DataSourceContextHolder {
	private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<String>();

	public static synchronized void setServer(String server) {
		CONTEXT_HOLDER.set(server);
	}

	public static String getServer() {
		return CONTEXT_HOLDER.get();
	}

	public static void clearServer() {
		CONTEXT_HOLDER.remove();
	}
}
