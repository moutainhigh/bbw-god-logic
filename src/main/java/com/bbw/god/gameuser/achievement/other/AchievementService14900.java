package com.bbw.god.gameuser.achievement.other;

import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import org.springframework.stereotype.Service;

/**
 * @author suchaobin
 * @description 成就id=14900的service
 * @date 2020/10/30 10:56
 **/
@Service
public class AchievementService14900 extends BaseAchievementService {

    @Override
    public int getMyAchievementId() {
        return 14900;
    }

    @Override
    public int getMyProgress(long uid, UserAchievementInfo info) {
        return 0;
    }
}
