package com.bbw.god.server;

import com.bbw.common.ID;
import com.bbw.god.statistics.StatisticKeyEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 记录区服的统计数据
 *
 * @author suhq
 * @date 2019年3月7日 上午10:12:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Deprecated // TODO 2个月后去掉
public class ServerStatistic extends ServerData {
    private String key;
    private Object value;

    public static ServerStatistic instance(int sId, StatisticKeyEnum key, Object value) {
        ServerStatistic ss = new ServerStatistic();
        ss.setId(ID.INSTANCE.nextId());
        ss.setSid(sId);
        ss.setKey(key.getKey());
        ss.setValue(value);
        return ss;
    }

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.STATISTIC;
    }
}
