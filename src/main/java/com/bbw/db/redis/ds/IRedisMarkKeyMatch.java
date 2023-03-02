package com.bbw.db.redis.ds;

/**
 * key -> redis服务器 的映射接口
 *
 * @author suhq
 * @date 2020-06-19 16:05
 **/
public interface IRedisMarkKeyMatch {
    /**
     * key到库的规则
     *
     * @param key
     * @return multi-ds里的定义的数据源，eg:common
     */
    String getRedisMark(String key);
}
