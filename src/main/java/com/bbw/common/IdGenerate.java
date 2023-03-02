package com.bbw.common;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 毫秒级别时间截+机器id+毫秒级内可增长的序列号
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月26日 上午10:16:37
 */
public class IdGenerate {
    //==============================Fields===========================================
    /** 开始时间截 (2019-03-08) */
    private final long twepoch = 1551974400000L;

    /** 机器id所占的位数，4位最多能部署16台机器 */
    //private final long workerIdBits = 8L;
    private static final long workerIdBits = 4L;

    /** 序列在id中占的位数，6位则每毫米可产生64个 */
    //private final long sequenceBits = 12L;
    private final long sequenceBits = 6L;

    /** 毫秒级别时间截占的位数,41位可以用69年 */
    private final long timestampBits = 41L;

    /** 生成发布方式所占的位数 */
    private final long getMethodBits = 1L;

    /** 支持的最大机器id，结果是 */
    public static final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /** 生成序列向左移workerIdBits位 */
    private final long sequenceShift = workerIdBits;

    /** 时间截向左移位 */
    private final long timestampShift = sequenceBits + workerIdBits;

    /** 生成发布方式向左移位 */
    private final long getMethodShift = timestampBits + sequenceBits + workerIdBits;

    /** 工作机器ID */
    private long workerId = 0L;

    /** 生成序列的掩码 */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /** 毫秒内序列 */
    private long sequence = 0L;

    /** 上次生成ID的时间截 */
    private long lastTimestamp = -1L;

    /** 1位生成发布方式，0代表嵌入式发布、1代表中心服务器发布模式 */
    private long getMethod = 0L;

    /** 成发布方式的掩码，这里为3 (0b11=0x3=3) */
    private long maxGetMethod = -1L ^ (-1L << getMethodBits);
    /** 重入锁 */
    private Lock lock = new ReentrantLock();

    //==============================Constructors=====================================

    /**
     * 构造函数
     *
     * @param 发布方式     0代表嵌入式发布、1代表中心服务器发布模式
     * @param workerId 工作ID
     */
    public IdGenerate(long getMethod, long workerId) {
        if (getMethod > maxGetMethod || getMethod < 0) {
            throw new IllegalArgumentException(String.format("getMethod can't be greater than %d or less than 0", maxGetMethod));
        }
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        this.getMethod = getMethod;
        this.workerId = workerId;
    }

    public long[] nextId(int nums) {
        long[] ids = new long[nums];
        for (int i = 0; i < nums; i++) {
            ids[i] = nextId();
        }

        return ids;
    }

    // ==============================Methods==========================================

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return SnowflakeId
     */
    public long nextId() {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("系统时钟回退。 在未来 %d 毫秒内将无法生成新的ID!", lastTimestamp - timestamp));
        }
        lock.lock();
        try {
            //如果是同一时间生成的，则进行毫秒内序列
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & sequenceMask;
                //毫秒内序列溢出
                if (sequence == 0) {
                    //阻塞到下一个毫秒,获得新的时间戳
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                //时间戳改变，毫秒内序列重置
                sequence = 0L;
            }
            //上次生成ID的时间截
            lastTimestamp = timestamp;
        } finally {
            lock.unlock();
        }

        //移位并通过或运算拼到一起组成64位的ID
        return (getMethod << getMethodShift) // 生成方式占用1位，左移61位
                | ((timestamp - twepoch) << timestampShift) // 时间差占用41位，最多69年，左移20位
                | (sequence << sequenceShift) // 毫秒内序列，取值范围0-4095
                | workerId; // 工作机器，取值范围0-255
    }

    public String nextString() {
        return Long.toString(nextId());
    }

    public String[] nextString(int nums) {
        String[] ids = new String[nums];
        for (int i = 0; i < nums; i++) {
            ids[i] = nextString();
        }
        return ids;
    }

    /**
     * 后缀
     *
     * @param suffix
     * @return
     */
    public String nextCode(String suffix) {
        StringBuilder sb = new StringBuilder();
        long id = nextId();
        sb.append(id);
        sb.append(suffix);
        return sb.toString();
    }

    /**
     * 此方法可以在后缀上增加业务标志
     *
     * @param suffix
     * @param nums
     * @return
     */
    public String[] nextCode(String suffix, int nums) {
        String[] ids = new String[nums];
        for (int i = 0; i < nums; i++) {
            ids[i] = nextCode(suffix);
        }
        return ids;
    }

    public String nextHexString() {
        return Long.toHexString(nextId());
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }
}
