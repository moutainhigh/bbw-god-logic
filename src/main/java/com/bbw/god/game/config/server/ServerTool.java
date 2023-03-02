package com.bbw.god.game.config.server;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgServerGroup;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class ServerTool {
    public static String ALL_SERVER = "所有";

    public static List<Integer> getServerGroups() {
        return getAvailableServers().stream().map(CfgServerEntity::getGroupId).distinct().collect(Collectors.toList());
    }

    public static int getServerGroup(int sid) {
        return getServer(sid).getGroupId();
    }

    public static CfgServerEntity getServer(int sid) {
        return Cfg.I.get(sid, CfgServerEntity.class);
    }

    public static List<CfgServerEntity> getServers(String serverNames) {
        List<CfgServerEntity> servers = getAvailableServers();
        if (!ALL_SERVER.equals(serverNames)) {
            servers = servers.stream().filter(s -> serverNames.contains(s.getName())).collect(Collectors.toList());
        }
        return servers;
    }

    /**
     * 获得区服
     *
     * @param serverName
     * @return
     */
    @NotNull
    public static CfgServerEntity getServer(String serverName) {
        List<CfgServerEntity> allEnableServers = getServers();
        Optional<CfgServerEntity> optionalServer = allEnableServers.stream().filter(s -> s.getName().equals(serverName)).findFirst();
        if (!optionalServer.isPresent()) {
            throw ExceptionForClientTip.fromMsg("无效的区服");
        }
        return optionalServer.get();
    }

    /**
     * 获得区服组
     *
     * @param serverGroup
     * @return
     */
    @NotNull
    public static List<CfgServerEntity> getGroupServers(int serverGroup) {
        List<CfgServerEntity> allEnableServers = getAvailableServers();
        List<CfgServerEntity> groupServers = allEnableServers.stream().filter(server -> server.getGroupId() == serverGroup).collect(Collectors.toList());
        if (ListUtil.isEmpty(groupServers)) {
            throw ExceptionForClientTip.fromMsg("没有对应的组区服");
        }
        return groupServers;
    }

    /**
     * 获得活跃的区服
     *
     * @return
     */
    public static List<CfgServerEntity> getAvailableServers() {
        // 获得所有的区服
        List<CfgServerEntity> servers = getServers();
        List<CfgServerEntity> availableServers = servers.stream().filter(s -> s.getId() == s.getMergeSid()).collect(Collectors.toList());
        return availableServers;
    }

    public static List<CfgServerEntity> getServers() {
        // 获得所有的区服
        return Cfg.I.get(CfgServerEntity.class);
    }

    /**
     * 获取区服简称
     *
     * @param sid
     * @return
     */
    public static String getServerShortName(int sid) {
        CfgServerEntity serverEntity = getServer(sid);
        return serverEntity.getShortName();
    }

    /**
     * 获取区服组配置
     *
     * @param sid
     * @return
     */
    public static CfgServerGroup getServerGroupInfo(int sid) {
        int serverGroup = getServerGroup(sid);
        return Cfg.I.get(serverGroup, CfgServerGroup.class);
    }

    /**
     * 获取角色当前区服ID
     *
     * @param uid
     * @return
     */
    public static int getActiveSid(long uid) {
        CfgServerEntity oriServer = getOriServer(uid);
        if (null == oriServer) {
            log.error("!!!!!!!!!!!!" + uid + "获取区服为null", new Exception("区服ID获取失败"));
            return -1;
        }
        return oriServer.getMergeSid();
    }

    /**
     * 获取角色原始区服ID
     *
     * @param uid
     * @return
     */
    public static CfgServerEntity getOriServer(long uid) {
        Long oriSid = uid / 100000 % 10000;
        int sidInt = oriSid.intValue();
        sidInt = Math.abs(sidInt);
        return ServerTool.getServer(sidInt);
    }
}
