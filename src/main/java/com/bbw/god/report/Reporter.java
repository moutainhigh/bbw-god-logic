package com.bbw.god.report;

import com.bbw.god.db.entity.CfgChannelEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.gameuser.GameUser;
import lombok.Data;

import java.io.Serializable;

/**
 * 上报者实体
 *
 * @author: suhq
 * @date: 2021/8/17 5:57 下午
 */
@Data
public class Reporter implements Serializable {
    private static final long serialVersionUID = 3133606249120040894L;
    /** 账号 */
    private String account;
    /** 玩家ID */
    private String uid;
    /** 区服id */
    private Integer sid;
    /** 区服 */
    private String server;
    /** 渠道 */
    private String channel;

    /**
     * 构建上报者实体
     *
     * @param gu
     * @return
     */
    public static Reporter instance(GameUser gu) {
        Reporter reporter = new Reporter();
        reporter.setAccount(gu.getRoleInfo().getUserName());
        reporter.setUid(gu.getId().toString());
        reporter.setSid(gu.getServerId());
        reporter.setServer(ServerTool.getServer(gu.getServerId()).getName());
        CfgChannelEntity channel = Cfg.I.get(gu.getRoleInfo().getChannelId(), CfgChannelEntity.class);
        if (null != channel) {
            reporter.setChannel(channel.getName());
        }
        return reporter;
    }

}
