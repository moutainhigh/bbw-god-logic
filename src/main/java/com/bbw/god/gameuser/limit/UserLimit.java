package com.bbw.god.gameuser.limit;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserData;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 单个玩家角色行为限制记录 type 10 限制登录 20 聊天限制
 *
 * @author suhq 2018年9月30日 上午10:31:44
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserLimit extends UserData implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer type;
    private String account;// 账号
    private Date limitBegin;// 限制开始时间
    private Date limitEnd;// 限制结束时间

    public static UserLimit Instance(long guId, String account, Date endDate) {
        UserLimit userLimit = new UserLimit();
        userLimit.setId(ID.INSTANCE.nextId());
        userLimit.setGameUserId(guId);
        userLimit.setType(UserLimitType.LOGIN_LIMIT.getValue());
        userLimit.setLimitBegin(DateUtil.now());
        userLimit.setLimitEnd(endDate);
        return userLimit;
    }

    public static UserLimit instanceTalkingLimit(long guId, Date endDate) {
        UserLimit userLimit = new UserLimit();
        userLimit.setId(ID.INSTANCE.nextId());
        userLimit.setGameUserId(guId);
        userLimit.setType(UserLimitType.TALK_LIMIT.getValue());
        userLimit.setLimitBegin(DateUtil.now());
        userLimit.setLimitEnd(endDate);
        return userLimit;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.LIMIT;
    }

}
