package com.bbw.god.gameuser.achievement.behavior.fight;

import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import org.springframework.stereotype.Service;

/** 领悟绝技Ⅲ
 * @author lzc
 * @description 成就id=15040的service
 * @date 2021/4/13 10:01
 **/
@Service
public class AchievementService15040 extends BaseAchievementService {
    /**
     * 获取当前成就id
     *
     * @return 当前成就id
     */
    @Override
    public int getMyAchievementId() {
        return 15040;
    }

    /**
     * 获取当前成就进度(用于展示给客户端)
     *
     * @param uid  玩家id
     * @param info 成就对象信息
     * @return 当前成就进度
     */
    @Override
    public int getMyProgress(long uid, UserAchievementInfo info) {
        if (isAccomplished(info)) {
            return getMyNeedValue();
        }
        return 0;
    }
}
