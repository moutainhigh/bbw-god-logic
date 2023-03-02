package com.bbw.god.road;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author suhq
 * @description: 玩家经过路口的记录
 * @date 2019-12-10 15:13
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Deprecated
public class UserCrossingRecord extends UserCfgObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer dir;

    public static UserCrossingRecord instance(long uid, int roadId, int dir) {
        UserCrossingRecord ucr = new UserCrossingRecord();
        ucr.setId(ID.INSTANCE.nextId());
        ucr.setGameUserId(uid);
        ucr.setBaseId(roadId);
        ucr.setDir(dir);
        return ucr;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.CROSSING_RECORD;
    }
}
