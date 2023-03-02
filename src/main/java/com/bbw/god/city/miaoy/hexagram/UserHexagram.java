package com.bbw.god.city.miaoy.hexagram;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 玩家文王64卦
 *
 * @author lzc
 * @date 2021-04-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserHexagram extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 9005217330075919061L;
    /** 已点亮的卦象id */
    private List<Integer> hexagramIds = new ArrayList<>();

    public static UserHexagram instance(long uid,int hexagramId) {
        UserHexagram instance = new UserHexagram();
        instance.setId(ID.INSTANCE.nextId());
        instance.setGameUserId(uid);
        instance.addHexagram(hexagramId);
        return instance;
    }

    public boolean addHexagram(int hexagramId) {
        if(!this.hexagramIds.contains(hexagramId)){
            this.hexagramIds.add(hexagramId);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_HEXAGRAM;
    }

}
