package com.bbw.god.job.game.push;

import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.dice.IncDiceService;
import com.bbw.god.gameuser.redis.GameUserRedisUtil;
import com.bbw.god.notify.push.PushEnum;
import com.bbw.god.notify.push.UserPush;
import com.bbw.god.server.ServerUserService;
import com.bbw.mc.push.PushAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 给客户端推送体力定时器
 *
 * @author suhq
 * @date 2019-08-20 14:47:51
 */
@Component("dicePushJob")
public class DicePushJob extends PushJob {
    @Autowired
    GameUserRedisUtil userRedis;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private IncDiceService incDiceService;
    @Autowired
    private PushAction pushAction;
    @Autowired
    private GameUserService gameUserService;

    // 必须重载，否则定时任务引擎认不到方法
    @Override
    public String getJobDesc() {
        return "客户端体力推送";
    }

    @Override
    public void doJob(String sendMail) {
        super.doJob(sendMail);
    }

    @Override
    public void job() {
        List<Long> allUids = getUids();
        toPush(allUids);
    }

    @Override
    public void toPush(List<Long> uids) {
        for (Long uid : uids) {
            GameUser gu = serverUserService.getGameUser(uid);
            int maxDice = IncDiceService.maxDiceLimitByLevel(gu.getLevel());
            // 体力已满无需推送，跳过
            if (gu.getDice() >= maxDice) {
                continue;
            }
            // 增长体力
            incDiceService.limitIncDice(gu);
            UserPush userPush = gameUserService.getSingleItem(uid, UserPush.class);
            if (userPush == null || !userPush.ableToPush(PushEnum.DIACE_FULL)) {
                continue;
            }
            // 体力刚满且订阅了，推送
            if (gu.getDice() >= maxDice) {
                String title = ServerTool.getServer(gu.getServerId()).getShortName() + "【" + gu.getRoleInfo().getNickname() + "】体力已满";
                String content = "您的体力已满，请及时消耗，以免溢出！";
                pushAction.push(uid, title, content);
            }
        }

    }

}
