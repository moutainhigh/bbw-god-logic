package com.bbw.god.activity.rd;

import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.util.List;

/**
 * 玩家信息
 *
 * @author fzj
 * @date 2022/2/8 15:17
 */
@Data
public class RDUserListInfos extends RDSuccess {
    /** 好友列表 */
    private List<RDBuddyUser> buddyUsers;

    @Data
    public static class RDBuddyUser {
        private long userId;
        private int head;
        /** 头像框 */
        private Integer iconId;
        /** 昵称 */
        private String nickName;
        /** 等级 */
        private int level;
    }

    public static RDBuddyUser fromInsUserEntity(GameUser gameUser) {
        GameUser.RoleInfo roleInfo = gameUser.getRoleInfo();
        RDBuddyUser rd = new RDBuddyUser();
        rd.setUserId(gameUser.getId());
        rd.setHead(roleInfo.getHead());
        rd.setIconId(roleInfo.getHeadIcon());
        rd.setNickName(ServerTool.getServerShortName(gameUser.getServerId()) + "·" + roleInfo.getNickname());
        rd.setLevel(gameUser.getLevel());
        return rd;
    }
}
