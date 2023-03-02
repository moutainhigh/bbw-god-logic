package com.bbw.god.gameuser.res.dice;

import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.io.Serializable;

/**
 * @author：lwb
 * @date: 2021/1/7 14:31
 * @version: 1.0
 */
@Data
public class UserDiceCapacity extends UserSingleObj implements Serializable {
    private Integer dice=0;//当前存储的量
    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_DICE_CAPACITY;
    }
}
