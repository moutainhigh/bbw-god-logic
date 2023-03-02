package com.bbw.god.gameuser.achievement;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.util.List;

/**
 * 玩家成就领取记录
 *
 * @author suhq
 * @date 2019-06-14 13:48:49
 */
@Data
@Deprecated
public class UserAwardedAchievements extends UserSingleObj {

    private List<Integer> awardeds;// 已领取的记录

    public static UserAwardedAchievements instance(long uid, List<Integer> awardeds) {
        UserAwardedAchievements awardedAchievements = new UserAwardedAchievements();
        awardedAchievements.setId(ID.INSTANCE.nextId());
        awardedAchievements.setGameUserId(uid);
        awardedAchievements.setAwardeds(awardeds);
        return awardedAchievements;
    }

    public void addAwarded(int achievementId) {
        awardeds.add(achievementId);
    }

    /**
     * 奖励是否已领取
     *
     * @param achievementId
     * @return
     */
    public boolean ifAwarded(int achievementId) {
        if (awardeds.contains(achievementId)) {
            return true;
        }
        return false;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.ACHIEVEMENT_AWARDED;
    }

}
