package com.bbw.god.city.entity;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 太一府 - 填充特产轮次
 *
 * @author suhq
 * @date 2018年10月26日 下午4:59:40
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Deprecated
public class UserTYFTurn extends UserData {
    private Integer award1;
    private Integer award2;
    private Integer award3;
    private Integer award4;
    private Integer award5;
    private Date beginTime;

    public static UserTYFTurn instance(long guId) {
        UserTYFTurn turn = new UserTYFTurn();
        turn.setId(ID.INSTANCE.nextId());
        turn.setGameUserId(guId);
        turn.setBeginTime(DateUtil.now());
        return turn;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.TYF_TURN;
    }

}
