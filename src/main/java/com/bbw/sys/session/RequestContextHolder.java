package com.bbw.sys.session;

/**
 * 请求上下文数据,用于实现不同渠道的本地化
 *
 * @author suhq
 * @date 2020-11-06 14:47
 **/
public class RequestContextHolder {
    private static final ThreadLocal<String> CONTEXT_HOLDER = new ThreadLocal<String>();

    public static synchronized void setValue(String channelId) {
        CONTEXT_HOLDER.set(channelId);
    }

    public static String getValue() {
        return CONTEXT_HOLDER.get();
    }

    public static void clear() {
        CONTEXT_HOLDER.remove();
    }
}
