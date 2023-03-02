package com.bbw.god.activityrank.game.guess;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.activityrank.game.GameActivityRankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 竞猜榜服务
 *
 * @author longwh
 * @date 2022/11/11 10:30
 */
@Service
public class GuessCompetitionRankService extends GameActivityRankService {
    @Autowired
    ActivityRankService activityRankService;



    /**
     * 加竞猜币值
     *
     * @param uid
     * @param addGuessingValue
     */
    @Async
    public void addGuessingValue(long uid, int addGuessingValue) {
        activityRankService.incrementRankValue(uid, addGuessingValue, ActivityRankEnum.GUESSING_COMPETITION_RANK);
        activityRankService.incrementRankValue(uid, addGuessingValue, ActivityRankEnum.GUESSING_COMPETITION_DAY_RANK);
    }
}