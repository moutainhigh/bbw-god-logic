package com.bbw.god.activityrank.game;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.IActivityRank;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Date;
import java.util.Objects;

/**
 * 跨服冲榜实例,提前生成
 *
 * @author suhq
 * @date 2018年10月12日 下午4:06:37
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GameActivityRank extends GameData implements IActivityRank {
    private Integer type;// 活动类型
    private Integer serverGroup;// 区服组
    private String extraAward;// 第一名额外奖励
    private Date begin;
    private Date end;

    public static GameActivityRank instance(int serverGroup, int type, Date beginDate, Date endDate) {
        GameActivityRank gar = new GameActivityRank();
        gar.setId(ID.INSTANCE.nextId());
        gar.setServerGroup(serverGroup);
        gar.setType(type);
        gar.setBegin(beginDate);
        gar.setEnd(endDate);
        return gar;
    }

    @Override
    public GameDataType gainDataType() {
        return GameDataType.ACTIVITY_RANK;
    }

    @Override
    public Long gainId() {
        return id;
    }

    @Override
    public Integer gainSId() {
        return 0;
    }

    @Override
    public Integer gainType() {
        return type;
    }

    @Override
    public Integer gainOpenWeek() {
        return 0;
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
        return id + "," + Objects.requireNonNull(ActivityRankEnum.fromValue(type)).getName() + type + "," + DateUtil.toDateTimeString(begin) + "," + DateUtil.toDateTimeString(end);
    }
}
