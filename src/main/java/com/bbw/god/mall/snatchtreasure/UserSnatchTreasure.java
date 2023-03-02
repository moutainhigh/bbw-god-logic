package com.bbw.god.mall.snatchtreasure;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author suchaobin
 * @description 玩家夺宝
 * @date 2020/6/29 16:34
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class UserSnatchTreasure extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 2093850999242159832L;

    private Integer wishValue = 0;
    private Integer needWish = 600;
    // 上一轮抽中夺宝符时的许愿值
    private Integer lastWishValue = 0;

    public static UserSnatchTreasure getInstance(long uid) {
        UserSnatchTreasure snatchTreasure = new UserSnatchTreasure();
        snatchTreasure.setId(ID.INSTANCE.nextId());
        snatchTreasure.setGameUserId(uid);
        return snatchTreasure;
    }


    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_SNATCH_TREASURE;
    }
}
