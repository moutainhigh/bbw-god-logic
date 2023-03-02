package com.bbw.god.login.repairdata;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.db.pool.PlayerPool;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.redis.GameUserRedisUtil;
import com.bbw.mc.mail.MailAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.bbw.god.login.repairdata.RepairDataConst.REPAIR_DICE_DATE;

/**
 * @author suchaobin
 * @description 修复体力service
 * @date 2020/7/7 14:44
 **/
@Service
public class RepairDiceService implements BaseRepairDataService {
    @Autowired
    private MailAction mailAction;

    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (lastLoginDate.before(REPAIR_DICE_DATE)) {
            gu.repairDice();
        }
        repairDiceOutOfLimit(gu);
    }

    private void repairDiceOutOfLimit(GameUser gu) {
        // 登录时检查体力，体力异常给运营发邮件
        CfgGame cfgGame = Cfg.I.getUniqueConfig(CfgGame.class);
        // 白名单账号直接跳过
        if (cfgGame.isWhiteAccount(gu.getRoleInfo().getUserName())) {
            return;
        }
        if (gu.getDice() > 2500) {
            int oldDice = gu.getDice();
            gu.setDice(2500);
            GameUserRedisUtil userRedis = SpringContextUtil.getBean(GameUserRedisUtil.class);
            userRedis.incDice(gu.getId(), 2500 - gu.getDice());
            PlayerPool updatePool = SpringContextUtil.getBean(PlayerPool.class);
            updatePool.addToUpdatePool(gu.getId());

            String serverName = ServerTool.getServer(gu.getServerId()).getName();
            String title = "玩家数据异常";
            String content = String.format("【%s】的【%s】玩家体力达【%s】,溢出，请检查数据", serverName, gu.getRoleInfo().getNickname(),
                    oldDice);
            mailAction.notifyOperator(title, content);
        }
    }
}
