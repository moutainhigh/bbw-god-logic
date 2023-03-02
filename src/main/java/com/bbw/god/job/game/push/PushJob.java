package com.bbw.god.job.game.push;

import com.bbw.common.DateUtil;
import com.bbw.common.SetUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.online.GameOnlineService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.redis.GameUserRedisUtil;
import com.bbw.god.job.game.GameJob;
import com.bbw.god.notify.push.PushEnum;
import com.bbw.god.notify.push.UserPush;
import com.bbw.god.statistics.serverstatistic.GodServerStatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 给客户端推送定时器
 *
 * @author suhq
 * @date 2019-08-20 14:47:51
 */
@Slf4j
public abstract class PushJob extends GameJob {
    @Autowired
    GameUserRedisUtil userRedis;
    @Autowired
    private GameOnlineService gameOnlineService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GodServerStatisticService godServerStatisticService;

    @Override
    public void job() {
        List<Long> allUids = getUids();
        toPush(allUids);
    }

    /**
     * 获取要推送的对象uid
     *
     * @return
     */
    public List<Long> getUids() {
        Date now = DateUtil.now();
        List<CfgServerEntity> servers = ServerTool.getAvailableServers();
        List<Long> allUids = new ArrayList<Long>();
        for (CfgServerEntity server : servers) {
            Set<Long> todayUids = godServerStatisticService.getLoginUids(server.getMergeSid(), now);
            Set<Long> onlineUids = gameOnlineService.getLastUidList(server.getMergeSid());
            todayUids.removeAll(onlineUids);
            // 不在线的人数为0，跳过
            if (SetUtil.isEmpty(todayUids)) {
                continue;
            }
            for (Long uid : todayUids) {
                // 不在Redis的玩家说明玩家太久没登录，没办法进行推送，直接跳过
                if (!userRedis.existsUser(uid)) {
                    continue;
                }
                allUids.add(uid);
            }
        }
        return allUids;
    }

    /**
     * 推送
     *
     * @param uids
     */
    public abstract void toPush(List<Long> uids);


    /**
     * 筛选出能够推送的玩家id集合
     *
     * @param uids     需要筛选的玩家id集合
     * @param pushEnum 要推送的功能枚举类
     * @return
     */
    public List<Long> getAblePushUids(Collection<Long> uids, PushEnum pushEnum) {
        List<Long> ids = new ArrayList<>();
        for (Long uid : uids) {
            try {
                UserPush userPush = gameUserService.getSingleItem(uid, UserPush.class);
                if (userPush == null || userPush.ableToPush(pushEnum)) {
                    if (userPush == null) {
                        userPush = new UserPush(uid);
                        this.gameUserService.addItem(uid, userPush);
                    }
                    ids.add(uid);
                }
            } catch (Exception e) {
                log.error("异常玩家id={}", uid);
            }
        }
        return ids;
    }
}
