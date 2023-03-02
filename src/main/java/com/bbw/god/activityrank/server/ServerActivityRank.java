package com.bbw.god.activityrank.server;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.IActivityRank;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;
import com.bbw.god.server.redis.ServerRedisKey;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;

/**
 * 区服冲榜实例,提前生成
 *
 * @author suhq
 * @date 2018年10月12日 下午4:06:37
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class ServerActivityRank extends ServerData implements IActivityRank {
    private Integer type;// 活动类型
    private Integer openWeek;// 开服周数
    private String extraAward;// 第一名额外奖励
    private Date begin;
    private Date end;

    public static ServerActivityRank instance(int sId, int type, int openWeek, Date beginDate, Date endDate) {
        ServerActivityRank sar = new ServerActivityRank();
        sar.setId(ID.INSTANCE.nextId());
        sar.setSid(sId);
        sar.setType(type);
        sar.setOpenWeek(openWeek);
        sar.setBegin(beginDate);
        sar.setEnd(endDate);
        return sar;
    }

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.ACTIVITY_RANK;
    }

    @Override
    public Long gainId() {
        return id;
    }

    @Override
    public Integer gainSId() {
        return sid;
    }

    @Override
    public Integer gainType() {
        return type;
    }

    @Override
    public Integer gainOpenWeek() {
        return openWeek;
    }

    @Override
    public String gainExtraAward() {
        return extraAward;
    }

    @Override
    public Date gainBegin() {
        return begin;
    }

    @Override
    public Date gainEnd() {
        return end;
    }

    public String toDesString() {
        return ServerRedisKey.getServerDataKey(this) + "," + openWeek + ActivityRankEnum.fromValue(type).getName() + type + "," + DateUtil.toDateTimeString(begin) + "," + DateUtil.toDateTimeString(end);
    }

}
