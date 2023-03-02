package com.bbw.god.gameuser.treasure.event;

import com.bbw.common.DateUtil;
import com.bbw.god.event.BaseEventParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author suchaobin
 * @description 法宝过期事件参数
 * @date 2020/6/7 21:10
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class EPTreasureExpired extends BaseEventParam {
    /**
     * 法宝id
     */
    private Integer treasureId;
    /**
     * 过期数量
     */
    private long expiredNum = 0;
    /**
     * 过期时间
     */
    private Long expiredTime = DateUtil.toDateTimeLong();

    public EPTreasureExpired(Integer treasureId, long expiredNum, BaseEventParam bep) {
        this.treasureId = treasureId;
        this.expiredNum = expiredNum;
        setValues(bep);
    }

    public EPTreasureExpired(Integer treasureId, long expiredNum, Long expiredTime, BaseEventParam bep) {
        this.treasureId = treasureId;
        this.expiredNum = expiredNum;
        this.expiredTime = expiredTime;
        setValues(bep);
    }
}
