package com.bbw.god.gameuser.special;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author 作者 ：lwb
 * @version 口袋特产
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserPocket extends UserCfgObj {
    private Date lockTime;
    private Long userSpecialDataId;

    public UserPocket(Long dataId, long uid) {
        this.id = ID.INSTANCE.nextId();
        this.userSpecialDataId = dataId;
        this.lockTime = new Date();
        this.gameUserId = uid;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.POCKET;
    }
}
