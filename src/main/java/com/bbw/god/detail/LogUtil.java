package com.bbw.god.detail;

import com.bbw.common.JSONUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.server.ServerData;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class LogUtil {

    /**
     * 获取区服日志的信息前缀
     *
     * @param server
     * @return
     */
    public static String getLogServerPart(CfgServerEntity server) {
        return String.format("区服【%d】【%s】", server.getMergeSid(), server.getName());
    }

    /**
     * 记录删除的玩家数据
     *
     * @param userData
     */
    public static void logDeletedUserData(UserData userData) {
        // 玩家数据明细
        log.info("删除{}数据类型{}的数据:{}", userData.getGameUserId(), userData.gainResType(), JSONUtil.toJson(userData));
    }

    /**
     * 记录删除的玩家数据
     *
     * @param way      用于区分操作
     * @param userData
     */
    public static void logDeletedUserData(String way, UserData userData) {
        // 玩家数据明细
        log.info("删除{}数据类型{}的数据【{}】:{}", userData.getGameUserId(), userData.gainResType(), way, JSONUtil.toJson(userData));
    }

    /**
     * 记录删除的玩家数据
     *
     * @param userDatas
     */
    public static <T extends UserData> void logDeletedUserDatas(List<T> userDatas, String prefixInfo) {
        if (ListUtil.isEmpty(userDatas)) {
            return;
        }
        // 玩家数据明细
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        for (T t : userDatas) {
            builder.append(JSONUtil.toJson(t) + "\n");
        }
        UserData userData = userDatas.get(0);
        log.info("{},删除{}数据类型{}的数据:{}", prefixInfo, userData.getGameUserId(), userData.gainResType(), builder.toString());
    }

    public static <T extends ServerData> void logDeletedServerDatas(List<T> serverDatas, String prefixInfo) {
        if (ListUtil.isEmpty(serverDatas)) {
            return;
        }
        // 玩家数据明细
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        for (T t : serverDatas) {
            builder.append(JSONUtil.toJson(t) + "\n");
        }
        ServerData serverData = serverDatas.get(0);
        log.info("{},删除{}数据类型{}的数据:{}", prefixInfo, serverData.getSid(), serverData.gainDataType(), builder.toString());
    }

}
