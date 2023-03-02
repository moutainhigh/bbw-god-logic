package com.bbw.god.gameuser.leadercard.fashion;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

/**
 * 穿戴的装备
 *
 * @author suhq
 * @date 2021-03-26 15:57
 **/
@Data
public class UserLeaderFashion extends UserData {
    /** 装备ID */
    private Integer fashionId;
    /** 等级 */
    private Integer level = 0;

    public static UserLeaderFashion getInstance(long uid, int fashionId) {
        UserLeaderFashion instance = new UserLeaderFashion();
        instance.setId(ID.INSTANCE.nextId());
        instance.setGameUserId(uid);
        instance.setFashionId(fashionId);
        return instance;
    }


    /**
     * 升级装备
     *
     * @param addLevel
     */
    public void addLevel(int addLevel) {
        level += addLevel;
    }


    @Override

    public UserDataType gainResType() {
        return UserDataType.USER_LEADER_FASHION;
    }
}
