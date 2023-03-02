package com.bbw.god.activity.server;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.server.ServerData;
import com.bbw.god.server.ServerDataType;
import com.bbw.god.server.redis.ServerRedisKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 区服活动实例，如充值活动、攻城略地等，提前生成
 *
 * @author suhq
 * @date 2018年10月12日 下午4:06:37
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ServerActivity extends ServerData implements IActivity {
    private static final String UNLIMITED_DATE = "2099-12-31 23:59:59";
    private Integer parentType;// 活动父类型
    private Integer type;// 活动类型
    private String name;
    private Date begin;
    private Date end;

    public static ServerActivity fromActivity(CfgActivityEntity activity, int sId) {
        Date beginDate = DateUtil.now();
        Date endDate = DateUtil.fromDateTimeString(UNLIMITED_DATE);
        return fromActivity(activity, beginDate, endDate, sId);
    }

    public static ServerActivity fromActivity(CfgActivityEntity activity, Date beginDate, Date endDate, int sId) {
        ServerActivity sa = new ServerActivity();
        sa.setId(ID.INSTANCE.nextId());
        sa.setSid(sId);
        sa.setParentType(activity.getParentType());
        sa.setType(activity.getType());
        sa.setName(activity.getName());
        sa.setBegin(beginDate);
        sa.setEnd(endDate);
        return sa;
    }

    @Override
    public Boolean ifTimeValid() {
        return DateUtil.isBetweenIn(DateUtil.now(), begin, end);
    }

    public Boolean ifBefore(Date beginBefore, Date endBefore) {
        return begin.before(beginBefore) && end.before(endBefore);
    }

    @Override
    public ServerDataType gainDataType() {
        return ServerDataType.ACTIVITY;
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
    public Integer gainParentType() {
        return parentType;
    }

    @Override
    public Integer gainType() {
        return type;
    }

    @Override
    public Date gainBegin() {
        return begin;
    }

    @Override
    public Date gainEnd() {
        return end;
    }

    @Override
    public String gainSign() {
        return "";
    }

    public String toDesString() {
        return ServerRedisKey.getServerDataKey(this) + "," + ActivityEnum.fromValue(type).getName() + type + "," + DateUtil.toDateTimeString(begin) + "," + DateUtil.toDateTimeString(end);
    }

}
