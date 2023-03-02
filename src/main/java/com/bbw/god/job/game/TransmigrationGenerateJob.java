package com.bbw.god.job.game;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.game.config.WorldType;
import com.bbw.god.game.transmigration.GameTransmigrationService;
import com.bbw.god.game.transmigration.TransmigrationEnterService;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.server.ServerUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 轮回世界生成定时器
 *
 * @author: suhq
 * @date: 2021/10/18 10:08 上午
 */
@Slf4j
@Component("transmigrationGenerateJob")
public class TransmigrationGenerateJob extends GameJob {
    @Autowired
    private GameTransmigrationService gameTransmigrationService;
    @Autowired
    private TransmigrationEnterService enterService;
    @Autowired
    private ServerUserService serverUserService;
    /** 定时器执行时，用于获得指定分钟前的战区 */
    private static final int ADVANCE_MINUTE = -30;

    @Override
    public void job() {
        Date sendDate = DateUtil.addMinutes(DateUtil.now(), ADVANCE_MINUTE);
        List<GameTransmigration> transmigrations = gameTransmigrationService.getTransmigrations(sendDate);
        if (ListUtil.isEmpty(transmigrations)) {
            log.info("当前没有开启的轮回世界");
            return;
        }
        // 获取当天要结算的轮回
        transmigrations = transmigrations.stream().filter(tmp -> DateUtil.toDateInt(tmp.getEnd()) == DateUtil.toDateInt(sendDate)).collect(Collectors.toList());
        if (ListUtil.isEmpty(transmigrations)) {
            log.info("轮回世界尚未结束，无需生成");
            return;
        }
        //提前生成新的一轮
        gameTransmigrationService.createNewTransmigrations();
        // 将当前还在轮回世界的玩家跳转到前一次的世界
        for (GameTransmigration transmigration : transmigrations) {
            Set<Long> uids = enterService.getUids(transmigration);
            List<GameUser> users = serverUserService.getGameUser(uids);
            for (GameUser user : users) {
                if (user.getStatus().getCurWordType() == WorldType.TRANSMIGRATION.getValue()) {
                    user.getStatus().setCurWordType(user.getStatus().getPreWordType());
                    user.updateStatus();
                }
            }
        }

    }

    @Override
    public String getJobDesc() {
        return "轮回世界生成";
    }

    // 必须重载，否则定时任务引擎认不到方法
    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }

}
