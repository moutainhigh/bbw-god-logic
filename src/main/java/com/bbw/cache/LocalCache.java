package com.bbw.cache;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 * 本地内存缓存，JVM重启后失效。
 * 算法：为每一种类型的对象构建独立的缓存池。如果没有指定类型，则数据存入类型为LocalCache.NO_TYPE的池中。（空模式）
 * 主要参数说明：
 *  type:缓存对象类型，相同类型的数据存在同一个缓冲池，可以根据getTypeList方法获取指定类型的所有缓存对象。
 *  key:缓存对象的键值。相同类型的数据必须保证唯一。
 *  timeoutSecond:缓存时间。单位为秒。指定长久缓存使用LocalCache.NO_TIMEOUT作为参数。
 *  updateTimeout。如果为true，则缓存时间从最后一次被命中后重新计时
 *
 *  【未指定类型】(没有type参数)的缓存对象，默认缓存30分钟。
 *  【指定类型】的缓存对象，默认长久缓存。
 *
 *   定时器默认10分钟执行清理一次。
 * </pre>
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-04 16:27
 */
@Slf4j
public class LocalCache {
    private static LocalCache localCache;// 单例
    // 定时器清理间隔（默认10分钟执行一次）
    private static Long defaultTaskDelay = 1000 * 60 * 10L;
    // 存储有效期,默认存储30分钟。最好和session周期保持一致
    private static Long defaultTimeOut = 1000 * 60 * 30L;
    // 用于防止定时任务叠加执行
    private static boolean isRun = false;
    // 同步锁
    private static byte[] lock = new byte[0];
    // 长久缓存，不过期
    public static final Long NO_TIMEOUT = 0L;
    // 一天24小时
    public static final Long ONE_DAY = 1000 * 60 * 60 * 24L;
    // 一周
    public static final Long ONE_WEEK = 1000 * 60 * 60 * 24 * 7L;
    // 未指定类型的数据缓存池名
    private static final String NO_TYPE = "no_type";
    // 定时清理超时缓存
    private static Timer timer;
    // 缓存对象池
    private static Map<String, TypeListCache<?>> typeCachePools = new ConcurrentHashMap<>();

    public static LocalCache getInstance() {
        synchronized (lock) {
            if (localCache == null) {
                localCache = new LocalCache();
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        clearTimeOutCache();
                    }
                }, 0, defaultTaskDelay);
            }
        }
        return localCache;
    }

    /**
     * 设置默认清理时间
     *
     * @param delay
     */
    public static void setDefaultClearDelay(Long delay) {
        if (delay >= 500) {
            defaultTaskDelay = delay;
        }
    }

    /**
     * 设置默认缓存时间
     *
     * @param timeout
     */
    public static void setDefaultTimeout(Long timeout) {
        if (timeout > 0) {
            defaultTimeOut = timeout;
        }
    }

    /**
     * 在【未指定类型】的缓存池中查找键值
     *
     * @param key
     * @return
     */
    public boolean containsKey(String key) {
        TypeListCache<?> lc = getCacheTypeList(NO_TYPE);
        return lc.containsKey(key);
    }

    /**
     * 在【type类型】的缓存池中查找键值
     *
     * @param type
     * @param key
     * @return
     */
    public boolean containsKey(String type, String key) {
        TypeListCache<?> lc = getCacheTypeList(type);
        return lc.containsKey(key);
    }

    /**
     * 在【未指定类型】的缓存池中获取对象。
     *
     * @param key
     * @return
     */
    public <T> T get(String key) {
        return get(NO_TYPE, key);
    }

    /**
     * 在【type类型】的缓存池中获取对象。
     *
     * @param type
     * @param key
     * @return
     */
    public <T> T get(String type, String key) {
        TypeListCache<T> lc = getCacheTypeList(type);
        CacheValue<T> cv = lc.get(key);
        if (null == cv) {
            return null;
        }
        return cv.getValue();
    }

    @SuppressWarnings("unchecked")
    private <T> TypeListCache<T> getCacheTypeList(String type) {
        if (typeCachePools.containsKey(type)) {
            return (TypeListCache<T>) typeCachePools.get(type);
        }
        TypeListCache<T> lc = new TypeListCache<T>(type);
        typeCachePools.put(type, lc);
        return lc;
//        Optional<TypeListCache<?>> listcache = typeList.stream().filter(value -> {
//            System.out.println("stream:" + Thread.currentThread().getId());
//            return value.getType().equals(type);
//        }).findFirst();
//        if (listcache.isPresent()) {
//            return (TypeListCache<T>) listcache.get();
//        }
//        TypeListCache<T> lc = new TypeListCache<T>(type);
//        typeList.add(lc);
//        return lc;
    }

    /**
     * 获取【type类型】的所有缓存对象。
     *
     * @param type
     * @return 不返回NULL
     */
    public <T> List<T> getTypeList(String type) {
        TypeListCache<T> lc = getCacheTypeList(type);
        ArrayList<T> list = new ArrayList<>(lc.size());
        for (CacheValue<T> cv : lc.values()) {
            list.add(cv.getValue());
        }
        return list;
    }

    /**
     * 获取【type类型】的缓存对象的数量。
     *
     * @param type
     * @return
     */
    public int getTypeCount(String type) {
        TypeListCache<?> lc = getCacheTypeList(type);
        return lc.size();
    }

    /**
     * 将对象缓存到【type类型】的缓存池。默认永久缓存。
     *
     * @param type
     * @param key
     * @param value
     */
    public <T> void put(String type, String key, T value) {
        put(type, key, value, NO_TIMEOUT);
    }

    /**
     * 批量缓存数据
     *
     * @param type
     * @param values
     * @param <T>
     */
    public <T> void put(String type, Map<String, T> values) {
        TypeListCache<T> lc = getCacheTypeList(type);
        Set<String> keys = values.keySet();
        for (String key : keys) {
            CacheValue<T> cacheValue = new CacheValue<>(key, values.get(key), NO_TIMEOUT, false);
            lc.put(key, cacheValue);
        }
    }

    /**
     * 非读取再更新，而是直接以新对象更新缓存
     *
     * @param type
     * @param values
     * @param <T>
     */
    public <T> void putNew(String type, Map<String, T> values) {
        TypeListCache<T> lc = new TypeListCache<T>(type);
        Set<String> keys = values.keySet();
        for (String key : keys) {
            CacheValue<T> cacheValue = new CacheValue<>(key, values.get(key), NO_TIMEOUT, false);
            lc.put(key, cacheValue);
        }
        typeCachePools.put(type, lc);
    }

    /**
     * 将对象缓存到【type类型】的缓存池。
     *
     * @param type
     * @param key
     * @param value
     * @param timeoutSecond。缓存时间,单位秒。传入LocalCache.NO_TIMEOUT则永久有效。
     */
    public <T> void put(String type, String key, T value, long timeoutSecond) {
        if (null == value) {
            log.error("不能缓存NULL对象!");
            return;
        }
        TypeListCache<T> lc = getCacheTypeList(type);
        CacheValue<T> cacheValue = new CacheValue<>(key, value, timeoutSecond, false);
        lc.put(key, cacheValue);
    }

    /**
     * 将对象缓存到【type类型】的缓存池。
     *
     * @param type
     * @param key
     * @param value
     * @param timeoutSecond。缓存时间，单位秒。传入LocalCache.NO_TIMEOUT则永久有效。
     * @param updateTimeout。如果为true，则缓存时间从最后一次被命中后重新计时
     */
    public <T> void put(String type, String key, T value, long timeoutSecond, boolean updateTimeout) {
        if (null == value) {
            return;
        }
        CacheValue<T> cacheValue = new CacheValue<>(key, value, timeoutSecond, updateTimeout);
        TypeListCache<T> lc = getCacheTypeList(type);
        lc.put(key, cacheValue);
    }

    /**
     * 将对象缓存到【未指定类型】的缓存池。默认缓存30分钟。
     *
     * @param key
     * @param value
     */
    public <T> void put(String key, T value) {
        put(NO_TYPE, key, value, defaultTimeOut);
    }

    /**
     * 将对象缓存到【未指定类型】的缓存池。
     *
     * @param key
     * @param value
     * @param timeoutSecond：缓存时间。以秒为单位。传入LocalCache.NO_TIMEOUT则永久有效。
     */
    public <T> void put(String key, T value, long timeoutSecond) {
        put(NO_TYPE, key, value, timeoutSecond);
    }

    public void removeTimeOutCache() {
        clearTimeOutCache();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void clearTimeOutCache() {
        if (!isRun) {
            synchronized (lock) {
                try {
                    isRun = true;
                    long start = System.currentTimeMillis();
                    if (!typeCachePools.isEmpty()) {
                        for (TypeListCache typeCache : typeCachePools.values()) {
                            long typeStart = System.currentTimeMillis();
                            ArrayList<String> removeList = new ArrayList<>();
                            for (Iterator<Entry<String, CacheValue>> iterator = typeCache.entrySet().iterator(); iterator.hasNext(); ) {
                                Entry<String, CacheValue> e = iterator.next();
                                if (e.getValue() == null) {
                                    removeList.add(e.getKey());
                                } else {
                                    if (e.getValue().isTimeout()) {
                                        removeList.add(e.getKey());
                                    }
                                }
                            }
                            for (String key : removeList) {
                                typeCache.remove(key);
                            }
                            int clearSize = removeList.size();
                            removeList.clear();
                            log.debug("本次清理：[" + typeCache.getType() + "]超时缓存对象" + clearSize + "个,当前剩余：" + typeCache.size() + ",清理用时：" + (System.currentTimeMillis() - typeStart) + "毫秒");
                        }
                    }
                    log.info("本次清理本地缓存超时对象共耗时：" + (System.currentTimeMillis() - start) + "毫秒");
                } finally {
                    isRun = false;
                }
            }
        }
    }

    /**
     * 清除所有缓存
     */
    public void clear() {
        if (!isRun) {
            synchronized (lock) {
                try {
                    isRun = true;
                    long start = System.currentTimeMillis();
                    if (!typeCachePools.isEmpty()) {
                        for (TypeListCache<?> typeCache : typeCachePools.values()) {
                            typeCache.clear();
                        }
                    }
                    log.info("清空对象共耗时：" + (System.currentTimeMillis() - start) + "毫秒");
                } finally {
                    isRun = false;
                }
            }
        }
    }

    /**
     * 清除指定类型的缓存
     *
     * @param type
     */
    public void clear(String type) {
        TypeListCache<?> cache = getCacheTypeList(type);
        cache.clear();
    }

    /**
     * 清除指定类型的缓存
     *
     * @param type
     */
    public void remove(String type, String key) {
        TypeListCache<?> cache = getCacheTypeList(type);
        cache.remove(key);
//		cache.clear();
    }

    /**
     * 清除未指定类型的缓存
     *
     * @param key
     */
    public void remove(String key) {
        TypeListCache<?> cache = getCacheTypeList(NO_TYPE);
        cache.remove(key);
//		cache.clear();
    }

    @Getter
    private class CacheValue<T> {
        // private static final long serialVersionUID = 800800964682488492L;
        private Long createTime;
        private String key;
        private Long timeout;
        private boolean updateTimeout;
        private T value;

        private CacheValue(String key, T value, long timeoutSecond, boolean updateTimeout) {
            this.key = key;
            this.value = value;
            this.updateTimeout = updateTimeout;
            // 将创建时间提前1秒。为了及时清除缓存而设计。这个时间由清除一次缓存的最长时间来定。
            createTime = System.currentTimeMillis() - 1000 * 1;
            timeout = timeoutSecond > 0 ? timeoutSecond * 1000L : NO_TIMEOUT;
        }

        private T getValue() {
            if (updateTimeout) {
                createTime = System.currentTimeMillis() - 1000 * 1;
            }
            return value;
        }

        /**
         * 判断对象是否已经失效
         *
         * @return 返回 true 失效 false 没有失效
         */
        private boolean isTimeout() {
            boolean ret = true;
            if (timeout.longValue() == NO_TIMEOUT.longValue()) {
                return false;
            } else {
                ret = System.currentTimeMillis() - createTime >= timeout;
                if (ret) {
                    value = null;// 缓存过期来，将对象指向NULL
                }
            }
            return ret;
        }
    }

    private class TypeListCache<T> extends ConcurrentHashMap<String, CacheValue<T>> {
        private static final long serialVersionUID = -2198081315131125221L;
        @Getter
        private String type = NO_TYPE;

        private TypeListCache(String type) {
            this.type = type;
        }
    }
}
