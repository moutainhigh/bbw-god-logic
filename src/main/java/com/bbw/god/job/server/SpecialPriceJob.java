package com.bbw.god.job.server;

import com.bbw.god.db.entity.CfgServerEntity;
import org.springframework.stereotype.Component;

/**
 * 以01:00为基准, 每20分钟执行特价价格增长
 *
 * @author suhq
 * @date 2019年3月11日 下午5:19:59
 */
@Component("specialPriceJob")
public class SpecialPriceJob extends ServerJob {

    @Override
    public void job(CfgServerEntity server) {
    }

    @Override
    public String getJobDesc() {
        return "特价价格变化";
    }

    // 必须重载，否则定时任务引擎认不到方法
    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }
}
