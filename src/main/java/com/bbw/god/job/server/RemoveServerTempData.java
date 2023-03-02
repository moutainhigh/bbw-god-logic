package com.bbw.god.job.server;

import com.bbw.common.DateUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.server.DelHistoryServerDataService;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.flx.FlxCaiShuZiBet;
import com.bbw.god.server.flx.FlxYaYaLeBet;
import com.bbw.god.server.flx.ServerFlxResult;
import com.bbw.god.server.flx.ServerFlxResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

/**
 * 删除区服临时数据
 * 10天前的魔王、福临轩投注数据
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-24 15:54
 */
@Component("removeServerTempData")
public class RemoveServerTempData extends ServerJob {
    // 移除超过多少天的数据
    private static final int TIME_OUT_DAYS = 10;
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private ServerFlxResultService serverFlxResultService;
    @Autowired
    private DelHistoryServerDataService delHistoryServerDataService;

    // 必须重载，否则定时任务引擎认不到方法
    @Override
    public void job(CfgServerEntity server) {
        int days = 20;
        Date today = DateUtil.now();
        for (int i = 0; i < days; i++) {
            Date removeDate = DateUtil.addDays(today, -(TIME_OUT_DAYS + i));
            int dateInt = DateUtil.toDateInt(removeDate);
            String loopKey = String.valueOf(dateInt);
            // 福临轩
            Optional<ServerFlxResult> dateResult = serverFlxResultService.getSingleResultByDate(server.getId(), dateInt);
            if (dateResult.isPresent()) {
                serverDataService.deleteServerData(dateResult.get());//开奖结果
                serverDataService.deleteServerDatas(server.getId(), FlxYaYaLeBet.class, loopKey);//压压乐投注数据
                serverDataService.deleteServerDatas(server.getId(), FlxCaiShuZiBet.class, loopKey);//猜数字投注数据
            }
        }
        delHistoryServerDataService.delExpiredServerData(server);
    }

    @Override
    public String getJobDesc() {
        return "删除" + TIME_OUT_DAYS + "天前的数据";
    }

    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }

}
