package com.bbw.god.gameuser.buddy;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

/**
 * 好友对象数据
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-02 20:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FriendBuddy extends UserSingleObj {
    private Set<Long> friendUids = new HashSet<>();

    @Override
    public UserDataType gainResType() {
        return UserDataType.FriendBuddy;
    }

    public FriendBuddy() {
        this.id = ID.INSTANCE.nextId();
    }
}
