package com.bbw.god.gameuser.treasure.xianrenbox;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 玩家仙人宝箱设定
 *
 * @author lwb
 * @date 2020/8/12 16:24
 */
@Data
public class UserXianRenBox extends UserData implements Serializable {
    private static final long serialVersionUID = 6201673975424882617L;
    private Long treasureDataId;
    private Integer lastOpenDate = 0;
    private List<Integer> awardsList;

    public static UserXianRenBox instance(long uid, long treasureDataId) {
        UserXianRenBox box = new UserXianRenBox();
        box.setId(ID.INSTANCE.nextId());
        box.setGameUserId(uid);
        box.setTreasureDataId(treasureDataId);
        return box;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_XIANREN_BOX;
    }
}
