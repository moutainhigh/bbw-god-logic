package com.bbw.god.gameuser.treasure.xianjiabox;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.io.Serializable;

/**
 * 说明：
 * 仙家宝库 开启记录
 * @author lwb
 * date 2021-06-01
 */
@Data
public class UserXianJiaBox extends UserData implements Serializable{
    private Long treasureDataId;
    private Integer lastOpenDate = 0;
    private Integer openTimes=1;

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_XIAN_JIA_BOX;
    }

    public static UserXianJiaBox instance(long uid, long treasureDataId) {
        UserXianJiaBox box = new UserXianJiaBox();
        box.setId(ID.INSTANCE.nextId());
        box.setGameUserId(uid);
        box.setTreasureDataId(treasureDataId);
        return box;
    }
}
