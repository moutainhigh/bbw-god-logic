package com.bbw.god.login.strategy;

import lombok.Data;

/**
 * 登录结果。参考uac的Rst对象和GameServerController的私有方法getReturnInfo
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月29日 下午2:58:26
 */
@Data
public class LoginResult {
    private int code = 0;
    private String msg = "";
    private int accountId = 0;
    private String accountName = "";
    private String inviCode = "";
    private int regDate = 0;

    public static LoginResult sucess() {
        LoginResult lr = new LoginResult();
        lr.setCode(0);
        return lr;
    }

    public static LoginResult fail(String msg) {
        LoginResult lr = new LoginResult();
        lr.setCode(-1);
        lr.setMsg(msg);
        return lr;
    }

    /**
     * 登录通过
     *
     * @return
     */
    public boolean pass() {
        return 0 == this.code;
    }

    public void setFail(String msg) {
        this.code = 1;
        this.msg = msg;
    }
}
