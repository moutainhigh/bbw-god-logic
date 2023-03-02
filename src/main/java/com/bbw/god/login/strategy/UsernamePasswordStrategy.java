package com.bbw.god.login.strategy;

import com.alibaba.fastjson.JSON;
import com.bbw.coder.CoderNotify;
import com.bbw.common.HttpClientUtil;
import com.bbw.common.LM;
import com.bbw.god.db.entity.CfgChannelEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;

/**
 * 账号密码登录。对应 UAC的GameServerController.usernamePwd服务。
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-01 12:05
 */
@Slf4j
@Service
public class UsernamePasswordStrategy extends CoderNotify implements LoginCheckStrategy {

    @Override
    public LoginResult check(HttpServletRequest request, CfgChannelEntity channel) {
        String userName = request.getParameter("email");
        String password = request.getParameter("password");
        LoginResult result = new LoginResult();
        try {
            //public Rst usernamePwdLogin(String accountName, String pwd)
            String check_url = getUacBaseUrl() + "account!serverUsernamePwdLogin?accountName=%s&pwd=%s&channelCode=%s";
            String url = String.format(check_url, URLEncoder.encode(userName, "utf-8"), password, channel.getPlatCode());
            String json = HttpClientUtil.doGet(url);
            result = JSON.parseObject(json, LoginResult.class);
            return result;
        } catch (Exception e) {
            result.setFail(LM.I.getMsg("Login.fail0"));
            log.error(e.getMessage(), e);
            notifyCoderNormal(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public boolean support(int loginType) {
        return 10 == loginType;
    }
}
