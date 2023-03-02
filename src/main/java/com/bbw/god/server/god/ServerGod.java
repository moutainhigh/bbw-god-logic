package com.bbw.god.server.god;

import com.bbw.common.ID;
import com.bbw.god.game.config.god.GodConfig;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 每日生成6+1神仙的相关记录,区服的前100个gamegod为系统备用（用于模拟神仙）
 *
 * @author suhq 2018年9月30日 上午10:11:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ServerGod extends ServerData {
    private Integer godId;
    private String name;
    private Integer position;// 位置
    private Integer dateInt;// 日期yyyyMMdd

    /**
     * @param sId
     * @param godId
     * @return
     */
    public static ServerGod instanceVirtualGod(int sId, int godId) {
        return instance(sId, GodConfig.bean().getVirtualDate(), godId, 0, true);
    }

    public static ServerGod instance(int sId, int dateInt, int godId, int pos) {
        return instance(sId, dateInt, godId, pos, false);
    }

    private static ServerGod instance(int sId, int dateInt, int godId, int pos, boolean isVirtual) {
        ServerGod serverGod = new ServerGod();
        serverGod.setId(ID.INSTANCE.nextId());
        serverGod.setSid(sId);
        GodEnum ge = GodEnum.fromValue(godId);
        serverGod.setGodId(godId);
        serverGod.setName(ge.getName());
        if (isVirtual) {
            serverGod.setDateInt(GodConfig.bean().getVirtualDate());
            serverGod.setPosition(0);
        } else {
            serverGod.setDateInt(dateInt);
            serverGod.setPosition(pos);
        }
        return serverGod;
    }

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.GOD;
    }

    @Override
    public String getLoopKey() {
        return String.valueOf(dateInt);
    }

    @Override
    public boolean isLoopData() {
        return true;
    }
}
