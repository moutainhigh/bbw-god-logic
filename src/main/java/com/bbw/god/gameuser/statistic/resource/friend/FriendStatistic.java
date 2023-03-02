package com.bbw.god.gameuser.statistic.resource.friend;

import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.gameuser.statistic.resource.ResourceStatistic;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author suchaobin
 * @description 好友统计
 * @date 2020/4/16 13:57
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FriendStatistic extends ResourceStatistic {
    public FriendStatistic(Integer today, Integer total, Integer date, int type) {
        super(today, total, date, AwardEnum.FRIEND, type);
    }

    public FriendStatistic(int type) {
        super(AwardEnum.FRIEND, type);
    }
}
