package com.bbw.god.chat;

import com.alibaba.fastjson.JSON;
import com.bbw.App;
import com.bbw.common.HttpClientUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.guild.GuildInfo;
import com.bbw.god.server.guild.UserGuild;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-23 11:37
 */
@Slf4j
@Service
public class ChatService {
    @Autowired
    private GameUserService userService;
    @Autowired
    private App app;
    @Value("${chat-server-room-max-user:2000}")
    private int serverRoomMaxUser;
    @Value("${chat-org-room-max-user:50}")
    private int orgRoomMaxUser;
    @Autowired
    private ServerDataService serverDataService;

    /**
     * 获取所有的聊天室配置
     *
     * @return
     */
    public ChatRoomSetting getChatRoomSetting() {
        ChatRoomSetting crs = new ChatRoomSetting();
        List<CfgServerEntity> servers = ServerTool.getAvailableServers();
        for (CfgServerEntity server : servers) {
            crs.getServerRoomList().add(getServerChatRoomName(server.getId()));
            // TODO:行会聊天室
            List<GuildInfo> guilds = serverDataService.getServerDatas(server.getId(), GuildInfo.class);
            guilds.forEach(p -> {
                crs.getGuildRoomList().add(getGuildChatRoomName(server.getId(), p.getId()));
            });
        }
        // 区服组
        List<String> serverGroups = servers.stream().collect(Collectors.groupingBy(CfgServerEntity::getGroupId)).keySet().stream().map(tmp -> getServerGroupChatRoomName(tmp)).collect(Collectors.toList());
        crs.setServerGroupRoomList(serverGroups);

        return crs;
    }

    private String getServerGroupChatRoomName(int groupId) {
        return String.valueOf(groupId);
    }

    private String getServerChatRoomName(int sid) {
        return "s_" + sid;
    }

    private String getGuildChatRoomName(int sid, long guildId) {
        return "o_" + sid + "_" + guildId;
    }

    /**
     * 行会创建的时候创建聊天室
     *
     * @param uid
     * @return
     */
    public boolean createGuildChatRoom(long uid) {
        String param = "rid=%s&roomName=%s&maxUser=" + orgRoomMaxUser;
        return queryChatServer(uid, "chat!creatChatRoom", param);
    }

    /**
     * 区服创建聊天室
     *
     * @param sid
     * @return
     */
    public boolean createServerChatRoom(int sid) {
        if (app.runAsDev()) {
            return true;
        }
        String param = "roomName=%s&maxUser=" + serverRoomMaxUser;
        String chatRoomName = getServerChatRoomName(sid);
        String url = ServerTool.getServerGroupInfo(sid).getWsUrl() + "chat!creatChatRoom" + "?" + String.format(param, chatRoomName);
        String json = HttpClientUtil.doGet(url);
        ChatRequestResult rst = JSON.parseObject(json, ChatRequestResult.class);
        if (!rst.success()) {
            log.error("区服聊天室创建请求错误！" + rst.getMessage() + "\n" + url);
        }
        return rst.success();
    }

    /**
     * 加入到行会聊天室
     *
     * @param uid
     * @return
     */
    public boolean joinGuildChatRoom(long uid) {
        String param = "rid=%s&roomName=%s";
        return queryChatServer(uid, "chat!joinChatRoom", param);
    }

    /**
     * 退出行会聊天室
     *
     * @param uid
     * @return
     */
    public boolean leaveGuildChatRoom(long uid) {
        String param = "rid=%s&roomName=%s";
        return queryChatServer(uid, "chat!leaveChatRoom", param);
    }

    /**
     * 移除行会聊天室
     *
     * @param uid
     * @return
     */
    public boolean removeGuildChatRoom(long uid) {
        String param = "rid=%s&roomName=%s";
        return queryChatServer(uid, "chat!removeRoom", param);
    }

    public void reBuildGuildChatRoom(long uid) {
        createGuildChatRoom(uid);
        UserGuild userGuild = userService.getSingleItem(uid, UserGuild.class);
        GuildInfo guildInfo = serverDataService.getServerData(userService.getGameUser(uid).getServerId(), GuildInfo.class, userGuild.getGuildId());
        if (null == guildInfo) {

        }
        guildInfo.getMembers().forEach(p -> {
            joinGuildChatRoom(p);
        });

    }

    private boolean queryChatServer(long uid, String path, String param) {
        // TODO:获取玩家所在行会的ID
        UserGuild userGuild = userService.getSingleItem(uid, UserGuild.class);
        if (userGuild == null || userGuild.getGuildId().longValue() == 0) {
            log.error("行会聊天室请求错误！玩家【" + uid + "】未加入行会！");
            return false;
        }
        int sid = userService.getActiveSid(uid);
        long guildId = userGuild.getGuildId();
        String chatRoomName = getGuildChatRoomName(userService.getGameUser(uid).getServerId(), guildId);
        String url = ServerTool.getServerGroupInfo(sid).getWsUrl() + path + "?" + String.format(param, uid, chatRoomName);
        try {
            String json = HttpClientUtil.doGet(url);
            ChatRequestResult rst = JSON.parseObject(json, ChatRequestResult.class);
            if (!rst.success()) {
                log.error("行会聊天室请求错误！" + rst.getMessage() + "\n" + url);
            }
            return rst.success();
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

}
