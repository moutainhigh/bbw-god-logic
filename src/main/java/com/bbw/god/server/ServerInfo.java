package com.bbw.god.server;

import lombok.Getter;
import lombok.Setter;

/**
 * 一个区服对应一个ServerInfo
 *
 * @author suhq
 * @date 2018年11月28日 下午2:40:59
 */
@Getter
@Setter
public class ServerInfo extends ServerData {

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.INFO;
    }
}
