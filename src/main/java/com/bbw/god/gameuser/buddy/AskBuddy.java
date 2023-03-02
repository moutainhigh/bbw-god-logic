package com.bbw.god.gameuser.buddy;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashSet;
import java.util.Set;

/**
 * 好友添加请求
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-02 20:17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AskBuddy extends UserSingleObj {
    private Set<Long> askUids = new HashSet<>();

    public AskBuddy() {
        this.id = ID.INSTANCE.nextId();
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.AskBuddy;
    }
}
