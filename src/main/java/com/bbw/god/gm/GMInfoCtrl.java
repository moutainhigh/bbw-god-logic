package com.bbw.god.gm;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.msg.ServerBroadcast;
import com.bbw.mc.broadcast.BroadcastAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 区服消息相关服务
 *
 * @author suhq
 * @date 2019年4月13日 下午1:49:30
 */
@Slf4j
@RestController
@RequestMapping("/gm")
public class GMInfoCtrl extends AbstractController {
    @Autowired
    private BroadcastAction broadcast;
    @Autowired
    private ServerService serverService;

    /**
     * 游戏内横幅广播
     *
     * @author suhq
     * @date 2020-08-27 16:33
     **/
    @GetMapping("info!broadcast")
    public Rst broadcast(String serverNames, String msg, int period) {
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        // 避免合服的区服多次发送
        servers = servers.stream().filter(tmp -> tmp.getId().intValue() == tmp.getMergeSid()).collect(Collectors.toList());
        for (CfgServerEntity server : servers) {
            broadcast.broadcast(server.getId(), msg, period);
        }

        return Rst.businessOK();
    }

    /**
     * 删除过期的广播消息
     *
     * @param serverNames
     * @return
     */
    @GetMapping("info!delExpiredBroadcastInfo")
    public Rst broadcast(String serverNames) {
        Date now = DateUtil.now();
        Rst rst = Rst.businessOK();
        List<CfgServerEntity> servers = ServerTool.getServers(serverNames);
        // 避免合服的区服多次发送
        servers = servers.stream().filter(tmp -> tmp.getId().intValue() == tmp.getMergeSid()).collect(Collectors.toList());
        for (CfgServerEntity server : servers) {
            List<ServerBroadcast> broadcasts = serverService.getServerDatas(server.getId(), ServerBroadcast.class);
            if (ListUtil.isNotEmpty(broadcasts)) {
                List<Long> toDels = broadcasts.stream().filter(tmp -> tmp.getBroadcast().getOverTime().before(now)).map(ServerData::getId).collect(Collectors.toList());
                if (ListUtil.isNotEmpty(toDels)) {
                    serverService.deleteServerDatas(server.getId(), toDels, ServerBroadcast.class);
                }
                rst.put(server.getName(), "过期数：" + toDels.size());
            }
        }

        return rst;
    }
}
