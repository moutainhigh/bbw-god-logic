package com.bbw.god.city.taiyf;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 太一府捐献记录
 *
 * @author suhq
 * @date 2019-06-13 09:01:58
 */
@Data
public class UserTyfFillRecord extends UserData {
    public List<Integer> specialIds;// 捐献的特产ID
    public Boolean isFillAll = false;// 是否捐献了所有特产

    public static UserTyfFillRecord instance(long guId, int specialId) {
        List<Integer> sIds = new ArrayList<Integer>();
        sIds.add(specialId);
        return instance(guId, sIds);
    }

    public static UserTyfFillRecord instance(long guId, List<Integer> specialIds) {
        UserTyfFillRecord record = new UserTyfFillRecord();
        record.setId(ID.INSTANCE.nextId());
        record.setGameUserId(guId);
        record.setSpecialIds(specialIds);
        return record;
    }

    public void addFillSpecial(int specialId) {
        specialIds.add(specialId);
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.TYF_FILL_RECORD;
    }

}
