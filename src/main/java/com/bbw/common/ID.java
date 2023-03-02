package com.bbw.common;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-04 14:56
 */
public enum ID {
    INSTANCE;
    private static long workerId = 0L;
    private static IdGenerate idGenerate = null;
    private static AtomicLong detailSeq = null;

    static {
        //获取本机的ip地址
        String ip = IpUtil.getInet4Address();
        long ipLong = IpUtil.ipToLong(ip);
        workerId = ipLong % IdGenerate.maxWorkerId;
        idGenerate = new IdGenerate(0, workerId);
        detailSeq = new AtomicLong(getInitSeq());
    }

    /**
     * 获取机器标识
     *
     * @return
     */
    public long getMachineId() {
        return workerId;
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     *
     * @return
     */
    public long nextId() {
        return idGenerate.nextId();
    }

    public long[] nextId(int nums) {
        return idGenerate.nextId(nums);
    }

    public String nextString() {
        return idGenerate.nextString();
    }

    /**
     * 指定后缀
     *
     * @param suffix
     * @return
     */
    public String nextCode(String suffix) {
        return idGenerate.nextCode(suffix);
    }

    /**
     * ID格式：8位时间(yyMMddHH)+8位毫秒序列+2位机器标识
     *
     * @return
     */
    public static long getNextDetailId() {
        Date now = DateUtil.now();
        //8位时间
        String nowString = DateUtil.toString(now, "yyMMddHH");
        //seq
        long seq = detailSeq.incrementAndGet();
        //2位机器标识
        String tpl = "%s%08d%02d";
        String id = String.format(tpl, nowString, seq, workerId);
        return Long.valueOf(id);
    }

    private static long getInitSeq() {
        Date now = DateUtil.now();
        //8位毫秒序列,一天最多86400000毫秒
        Date todayBegin = DateUtil.getDateBegin(now);
        return now.getTime() - todayBegin.getTime();
    }
}
