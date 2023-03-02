package com.bbw.god.gameuser.bag;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author suchaobin
 * @description 玩家背包购买记录
 * @date 2020/11/27 14:53
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class UserBagBuyRecord extends UserSingleObj implements Serializable {
    private Integer buyTimes = 0;

    public static UserBagBuyRecord getInstance(long uid) {
        UserBagBuyRecord record = new UserBagBuyRecord();
        record.setGameUserId(uid);
        record.setId(ID.INSTANCE.nextId());
        return record;
    }

    public void addBuyTimes(int addValue) {
        this.buyTimes += addValue;
    }

    /**
     * 玩家资源类型
     *
     * @return
     */
    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_BAG_BUY_RECORD;
    }
}
