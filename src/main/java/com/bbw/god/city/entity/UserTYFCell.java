package com.bbw.god.city.entity;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 太一府 - 填充特产
 *
 * @author suhq
 * @date 2018年10月25日 上午10:38:27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Deprecated
public class UserTYFCell extends UserCfgObj {
    private Long tyfTurnId;
    private Date fillTime;

    public static UserTYFCell instance(long guId, long turnId, int specialId) {
        UserTYFCell userTYFCell = new UserTYFCell();
        userTYFCell.setId(ID.INSTANCE.nextId());
        userTYFCell.setBaseId(specialId);
        userTYFCell.setGameUserId(guId);
        userTYFCell.setFillTime(DateUtil.now());
        userTYFCell.setTyfTurnId(turnId);
        return userTYFCell;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.TYF_CELL;
    }

}
