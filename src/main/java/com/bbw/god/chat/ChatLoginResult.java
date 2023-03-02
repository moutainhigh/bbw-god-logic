package com.bbw.god.chat;

import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.gameuser.GameUser;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-05-23 14:54
 */
@Data
public class ChatLoginResult {
    private int res = 0;// 状态标识，必有0标识成功。其他为错误
    private int serverId;
    private int groupId;
    private String serverName;
    private long orgId;
    private String uid;
    private long rid;
    private String name;// 昵称
    private int level;// 等级
    private int vipLevel = 0;
    private int head = 1;// 头像
    private int headIcon = 31000;// 头像框
    private List<String> tokens = new ArrayList<>();// 令牌

    public static ChatLoginResult instance(GameUser usr, CfgServerEntity originServer, Set<String> tokens) {
        ChatLoginResult cr = new ChatLoginResult();
        cr.setRes(0);
        cr.setHead(usr.getRoleInfo().getHead());
        cr.setHeadIcon(usr.getRoleInfo().getHeadIcon());
        cr.setLevel(usr.getLevel());
        cr.setName(usr.getRoleInfo().getNickname());
        cr.setServerId(usr.getServerId());
        //原始区服信息
        cr.setServerName(originServer.getName());
        cr.setGroupId(originServer.getGroupId());

        cr.setRid(usr.getId());
        cr.getTokens().addAll(tokens);
        return cr;
    }
}
