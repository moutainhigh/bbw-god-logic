package com.bbw.god.activityrank.game.carefree;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.activityrank.game.GameActivityRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 逍遥榜服务
 *
 * @author: huanghb
 * @date: 2023/2/21 17:55
 */
@Service
public class CareFreeRankService extends GameActivityRankService {
    @Autowired
    ActivityRankService activityRankService;


    /**
     * 加竞猜币值
     *
     * @param uid
     * @param addCareFreeValue
     */
    @Async
    public void addCareFreeValue(long uid, int addCareFreeValue) {
        activityRankService.incrementRankValue(uid, addCareFreeValue, ActivityRankEnum.CAREFREE_RANK);
        activityRankService.incrementRankValue(uid, addCareFreeValue, ActivityRankEnum.CAREFREE_DAY_RANK);
    }
}