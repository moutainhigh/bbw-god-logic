package com.bbw.god.login;

import com.bbw.god.login.validator.CheckChannelCode;
import com.bbw.god.login.validator.CheckServerId;
import com.bbw.god.login.validator.CheckUserName;
import lombok.Data;

/**
 * 登录的参数值对象
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月29日 下午2:58:26
 */
@Data
public
class LoginVO {
    @CheckUserName
    private String email = ""; // （账号）
    @CheckChannelCode
    private String plat = "10";// （平台标识）
    @CheckServerId
    private Integer serverId = 99;// 服务器ID
    private Integer userType = 40;// （用户类型 10账号 20微信 30游客 40渠道)

    private String password = "";// （密码）

    private String inviCode = "邀请码";// （版本号）
    private int versionCode = 216;// （版本号）
    private String token = "";// （推送码）
    private String deviceId = "";// （ios这边没用这个，安卓好像是设备码）
    private String oaid = "";// （ios这边没用这个，安卓好像是设备码）

    private String openId = "";// （微信独有）
    private String wxName = "";// （微信名独有）

    private String pushToken = "";// 推送token，设备唯一标识
    private String clientVersion = "";// 客户端版本号

    /**
     * 原始区服
     *
     * @return
     */
    public int getOriginSid() {
        return this.serverId;
    }
}
