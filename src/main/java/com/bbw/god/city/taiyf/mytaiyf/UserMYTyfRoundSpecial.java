package com.bbw.god.city.taiyf.mytaiyf;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.util.List;

/**
 * 梦魇太一府本轮特产
 *
 * @author lzc
 * @date 2021-03-19 09:01:58
 */
@Data
public class UserMYTyfRoundSpecial extends UserData {
    public List<Integer> specialIds;// 特产IDs

    public static UserMYTyfRoundSpecial instance(long guId, List<Integer> specialIds) {
        UserMYTyfRoundSpecial record = new UserMYTyfRoundSpecial();
        record.setId(ID.INSTANCE.nextId());
        record.setGameUserId(guId);
        record.setSpecialIds(specialIds);
        return record;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.MYTYF_ROUND_SPECIAL;
    }

}
