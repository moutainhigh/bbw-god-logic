package com.bbw.god.activityrank.game.lovevalue;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 情缘榜服务
 *
 * @author fzj
 * @date 2022/2/8 14:19
 */
@Service
public class LoveValueRankService {
    @Autowired
    ActivityRankService activityRankService;

    /**
     * 加情缘值
     *
     * @param uid
     * @param addLoveValue
     */
    public void addLoveValue(long uid, int addLoveValue) {
        activityRankService.incrementRankValue(uid, addLoveValue, ActivityRankEnum.LOVE_VALUE_RANK);
        activityRankService.incrementRankValue(uid, addLoveValue, ActivityRankEnum.LOVE_VALUE_DAY_RANK);
    }
}
