package com.bbw.god.game.monitor;

import com.bbw.App;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.pay.UserReceipt;
import com.bbw.mc.mail.MailAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户数据监控
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-27 10:35
 */
@Slf4j
@Service
public class MonitorUser {
    @Autowired
    private App app;
    @Autowired
    private GameUserService usrService;
    @Autowired
    private MailAction mailAction;
    //忽略
    private static long[] ignoreUid = {190429207101684L, 190429207100002L, 190516000321548L, 190429207101713L, 190516000321513L};

    public void monitor(GameUser usr) {
        if (!app.runAsProd()) {
            return;
        }
        if (ignore(usr.getId())) {
            return;
        }
        int max = 30 * 10000;
        if (usr.getGold() < max) {
            return;
        }
        max = 35 * 10000;
        List<UserReceipt> recipts = usrService.getMultiItems(usr.getId(), UserReceipt.class);

        if (usr.getGold() > max && recipts.size() <= 10) {
            CfgServerEntity server = Cfg.I.get(usr.getServerId(), CfgServerEntity.class);
            String title = String.format("区服=%s usr=%s nickname=%s 的元宝数量超过%s 充值单据数量%s", server.getName(), usr.getId(), usr.getRoleInfo().getNickname(), max, recipts.size());
            log.error(title);
            mailAction.notifyOperator(title, title);
            return;
        }
        max = 40 * 10000;
        if (usr.getGold() > max) {
            CfgServerEntity server = Cfg.I.get(usr.getServerId(), CfgServerEntity.class);
            String title = String.format("区服=%s usr=%s nickname=%s 的元宝数量超过%s ", server.getName(), usr.getId(), usr.getRoleInfo().getNickname(), max);
            log.error(title);
            mailAction.notifyOperator(title, title);
            return;
        }
    }

    private boolean ignore(long uid) {
        for (long id : ignoreUid) {
            if (id == uid) {
                return true;
            }
        }
        return false;
    }
}
