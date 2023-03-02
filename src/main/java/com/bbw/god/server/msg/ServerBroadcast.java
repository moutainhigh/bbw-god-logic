package com.bbw.god.server.msg;

import com.bbw.common.ID;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;
import com.bbw.mc.broadcast.BroadcastMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-09 17:16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ServerBroadcast extends ServerData {
    private BroadcastMsg broadcast;

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.broadcast;
    }

    public static ServerBroadcast fromBroadcastMsg(int sid, BroadcastMsg msg) {
        ServerBroadcast sb = new ServerBroadcast();
        sb.setId(ID.INSTANCE.nextId());
        sb.setSid(sid);
        sb.setBroadcast(msg);
        return sb;
    }
}
