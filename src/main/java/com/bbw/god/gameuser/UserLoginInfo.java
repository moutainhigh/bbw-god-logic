package com.bbw.god.gameuser;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户登录信息，可用于跟登录相关的活动
 *
 * @author suhq 2018年9月30日 上午10:08:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserLoginInfo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = -5220591807849281599L;
    private String deviceId;// 设备ID
    private String phone;// 手机号码
    private String token;// 推送token
    private Date enrollTime;// 注册时间
    private Date lastLoginTime;// 最近登录时间
    private String lastLoginIp;// 最近登录ip

    public static UserLoginInfo instance(long guId) {
        Date now = DateUtil.now();
        UserLoginInfo userLoginInfo = new UserLoginInfo();
        userLoginInfo.setId(ID.INSTANCE.nextId());
        userLoginInfo.setGameUserId(guId);
        userLoginInfo.setLastLoginTime(now);
        userLoginInfo.setEnrollTime(now);
        return userLoginInfo;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.LOGIN_INFO;
    }

}
