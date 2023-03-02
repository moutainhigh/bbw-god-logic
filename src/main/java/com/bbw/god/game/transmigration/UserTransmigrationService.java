package com.bbw.god.game.transmigration;

import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.transmigration.cfg.TransmigrationTool;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.game.transmigration.entity.UserTransmigration;
import com.bbw.god.gameuser.GameUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 玩家轮回信息
 *
 * @author: suhq
 * @date: 2021/10/18 10:55 上午
 */
@Service
public class UserTransmigrationService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GameTransmigrationService transmigrationService;

    /**
     * 获取轮回世界挑战信息
     *
     * @return
     */
    public UserTransmigration getTransmigration(long uid) {
        UserTransmigration userTransmigration = gameUserService.getSingleItem(uid, UserTransmigration.class);
        if (null == userTransmigration) {
            userTransmigration = UserTransmigration.getInstance(uid);
            gameUserService.addItem(uid, userTransmigration);
        }
        return userTransmigration;
    }

    /**
     * 更新新的记录
     *
     * @param uid
     * @param cityId
     * @param newScore
     */
    public void updateNewRecord(long uid, int cityId, int newScore) {
        UserTransmigration userTransmigration = getTransmigration(uid);
        userTransmigration.updateScore(cityId, newScore);
        gameUserService.updateItem(userTransmigration);
    }

    /**
     * 获取成功挑战过的数量
     *
     * @param uid
     * @return
     */
    public int getSuccessNum(long uid) {
        int sgId = ServerTool.getServerGroup(gameUserService.getActiveSid(uid));
        GameTransmigration curTransmigration = transmigrationService.getCurTransmigration(sgId);
        if (null == curTransmigration) {
            return 0;
        }
        return getTransmigration(uid).gainSuccessNum();
    }

    /**
     * 轮回增益buff
     *
     * @param uid
     * @return
     */
    public double getTranmigrationBuffAdd(long uid) {
        int num = getSuccessNum(uid);
        return TransmigrationTool.getTransmigrationAdd(num);
    }


}
