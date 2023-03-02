package com.bbw.god.gameuser.achievement;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author suchaobin
 * @description 玩家最近完成的成就
 * @date 2020/2/19 15:15
 */
@Data
@Deprecated
public class UserRecentAchievements extends UserSingleObj {
    /** 最近完成的成就id集合 */
    private List<Integer> recentAchievementIds = new LinkedList<>();

    public static UserRecentAchievements instance(long uid) {
        UserRecentAchievements recentAchievements = new UserRecentAchievements();
        recentAchievements.setId(ID.INSTANCE.nextId());
        recentAchievements.setGameUserId(uid);
        return recentAchievements;
    }

    public void addRecent(int achievementId) {
        int limit = AchievementTool.getCfgAchievement().getRecentNumToShow();
        if (recentAchievementIds.contains(achievementId)) {
            return;
        }
        if (recentAchievementIds.size() >= limit) {
            recentAchievementIds.remove(0);
        }
        recentAchievementIds.add(achievementId);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.ACHIEVEMENT_RECENT;
    }
}
