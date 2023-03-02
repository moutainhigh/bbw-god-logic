package com.bbw.god.job.tomysql;

import com.bbw.coder.CoderNotify;
import com.bbw.common.DateUtil;
import com.bbw.common.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据库任务
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-31 21:14
 */
@Slf4j
public abstract class DbJob extends CoderNotify {

    /**
     * 获取任务描述
     *
     * @return
     */
    public abstract String getJobDesc();

    /**
     * 具体的任务
     */
    public abstract void job();

    /**
     * 执行定时任务。
     * 子类必须重载，否则定时任务认不到方法。
     *
     * @param sendMail：是否发送结果邮件
     */
    public void doJob(String sendMail) {
        long begin = System.currentTimeMillis();
        String title = getJobDesc() + DateUtil.nowToString();
        StringBuilder sb = new StringBuilder();
        try {
            String msg = DateUtil.nowToString() + "开始执行" + getJobDesc();
            sb.append(msg + "\n");
            log.debug(msg);

            job();

            msg = DateUtil.nowToString() + "完成" + getJobDesc();
            log.debug(msg);
            sb.append(msg + "\n");

        } catch (Exception e) {
            String logInfo = String.format("%s任务出错", getJobDesc());
            log.error(e.getMessage(), e);
            sb.append(logInfo + "\n");
            notifyCoderHigh(logInfo, e);
        }
        long end = System.currentTimeMillis();
        if (end - begin > 15000) {
            log.error("[" + this.getJobDesc() + "]到数据库耗时:{}", (end - begin));
        }
        if (StrUtil.isNotNull(sendMail) && !"0".equals(sendMail)) {
            notifyCoderInfo(title, sb.toString());
        }
    }

}
