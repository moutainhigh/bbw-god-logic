package com.bbw.god.job.server;

import com.bbw.coder.CoderNotify;
import com.bbw.common.DateUtil;
import com.bbw.common.StrUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.config.server.ServerTool;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 区服定时任务
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-31 21:14
 */
@Slf4j
public abstract class ServerJob extends CoderNotify {

    /**
     * 获取任务描述
     *
     * @return
     */
    public abstract String getJobDesc();

    /**
     * 具体的任务
     */
    public abstract void job(CfgServerEntity server);

    /**
     * 执行定时任务。 子类必须重载，否则定时任务认不到方法。
     *
     * @param sendMail：是否发送结果邮件
     */
    public void doJob(String sendMail) {
        String title = getJobDesc() + DateUtil.nowToString();
        StringBuilder sb = new StringBuilder();
        List<CfgServerEntity> servers = ServerTool.getAvailableServers();
        for (CfgServerEntity server : servers) {
            try {
                String msg = DateUtil.nowToString() + "开始执行" + LogUtil.getLogServerPart(server) + getJobDesc();
                sb.append(msg + "\n");
                log.info(msg);

                job(server);

                msg = DateUtil.nowToString() + "完成" + LogUtil.getLogServerPart(server) + getJobDesc();
                log.info(msg);
                sb.append(msg + "\n");

            } catch (Exception e) {
                log.error(e.getMessage(), e);
                String logInfo = String.format("%s%s任务出错", LogUtil.getLogServerPart(server), getJobDesc());
                log.error(logInfo);
                sb.append(logInfo + "\n");
                notifyCoderHigh(logInfo, e);
            }
        }
        if (StrUtil.isNotNull(sendMail) && !"0".equals(sendMail)) {
            log.info(title + "\n" + sb.toString());
//            notifyCoderInfo(title, sb.toString());
        }
    }

}
