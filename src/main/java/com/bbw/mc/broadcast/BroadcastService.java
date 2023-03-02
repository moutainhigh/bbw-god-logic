package com.bbw.mc.broadcast;

import com.bbw.App;
import com.bbw.cache.LocalCache;
import com.bbw.common.DateUtil;
import com.bbw.common.HttpClientUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.msg.ServerBroadcast;
import com.bbw.mc.Msg;
import com.bbw.mc.MsgType;
import com.bbw.mc.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 广播服务
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-29 10:57
 */
@Slf4j
@Service("broadcastService")
public class BroadcastService extends NotifyService {
    private static final int LIMIT_COUNT = 30;
    private static final String USER_BROADCAST_KEY = "UserBroadcast";
    @Autowired
    private App app;
    @Autowired
    private ServerDataService serverDataService;

    public BroadcastService() {
        this.type = MsgType.BROADCAST;
    }

    @Override
    public void notify(Msg msg) {
        if (msg instanceof BroadcastMsg) {
            if (StrUtil.isBlank(msg.getContent())) {
                log.error("空广播信息" + msg.toString());
                return;
            }
            BroadcastMsg broadcastMsg = (BroadcastMsg) msg;
            StringBuilder url = new StringBuilder();
            url.append(ServerTool.getServerGroupInfo(broadcastMsg.getSid()).getWsUrl());
            if (broadcastMsg.ifToServer()) {
                broadcast(broadcastMsg);
                url.append("banner!serverNotify?sid=");
                url.append(broadcastMsg.getSid());
                url.append("&info=");
                url.append(HttpClientUtil.urlEncode(broadcastMsg.getContent()));
//                String url = app.getWsBaseUrl() + "banner!serverNotify?sid=" + broadcastMsg.getSid() + "&info=" + HttpClientUtil.urlEncode(broadcastMsg.getContent());
                HttpClientUtil.doGet(url.toString());
            } else if (broadcastMsg.ifToAllServer()) {
                broadcastAllServer(broadcastMsg);
            } else if (broadcastMsg.ifToGroup()) {
                broadcastToGroup(broadcastMsg);
                url.append("banner!serverGroupNotify?group=");
                url.append(broadcastMsg.getGroup());
                url.append("&info=");
                url.append(HttpClientUtil.urlEncode(broadcastMsg.getContent()));
                HttpClientUtil.doGet(url.toString());
            }
        } else {
            log.error("无效的广播数据{}", msg.toString());
        }

    }

    private void broadcast(BroadcastMsg msg) {
        ServerBroadcast sb = ServerBroadcast.fromBroadcastMsg(msg.getSid(), msg);
        serverDataService.addServerData(sb);
    }

    /**
     * 在所有服务器广播消息
     *
     * @param msg
     */
    private void broadcastAllServer(BroadcastMsg msg) {
        List<CfgServerEntity> servers = ServerTool.getAvailableServers();
        List<ServerBroadcast> sbs = servers.stream().map(tmp -> ServerBroadcast.fromBroadcastMsg(tmp.getId(), msg)).collect(Collectors.toList());
        serverDataService.addServerData(sbs);

    }

    /**
     * 在指定服务器广播消息
     *
     * @param msg
     */
    private void broadcastToGroup(BroadcastMsg msg) {
        List<CfgServerEntity> servers = ServerTool.getGroupServers(msg.getGroup());
        List<ServerBroadcast> sbs = servers.stream().map(tmp -> ServerBroadcast.fromBroadcastMsg(tmp.getId(), msg)).collect(Collectors.toList());
        serverDataService.addServerData(sbs);
    }

    /**
     * 获得广播信息
     *
     * @param sid
     * @return
     */
    public List<String> getBroadcastInfo(int sid, Long uid) {
        List<ServerBroadcast> broadcasts = serverDataService.getServerDatas(sid, ServerBroadcast.class);
        if (ListUtil.isEmpty(broadcasts)) {
            return new ArrayList<>(0);
        }

        // 按照时间倒序
        broadcasts.sort(Comparator.comparing(ServerBroadcast::getId).reversed());
        long maxId = 0;
        // 获取当前玩家已经拿走到最大的广播ID
        Long maxBroadcastId = LocalCache.getInstance().get(USER_BROADCAST_KEY, uid.toString());
        if (null != maxBroadcastId) {
            maxId = maxBroadcastId.longValue();
        }
        Date now = DateUtil.now();
        List<String> msgs = new ArrayList<>();
        List<Long> toRemove = new ArrayList<>();
        int count = 0;
        for (ServerBroadcast b : broadcasts) {
            // 清除失效的
            if (b.getBroadcast().getOverTime().before(now)) {
                toRemove.add(b.getId());
                continue;
            }
            // 添加有效的，最多添加LIMIT_COUNT
            if (b.getBroadcast().getId() > maxId) {
                msgs.add(b.getBroadcast().getContent());
                count++;
                if (count >= LIMIT_COUNT) {
                    break;
                }
            }
        }
        if (!toRemove.isEmpty()) {
            serverDataService.deleteServerDatas(sid, toRemove, ServerBroadcast.class);
        }
        LocalCache.getInstance().put(USER_BROADCAST_KEY, uid.toString(), broadcasts.get(0).getId());
        return msgs;
    }

}
