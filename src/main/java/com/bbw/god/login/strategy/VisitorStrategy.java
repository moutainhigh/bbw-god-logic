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
 * 游客登录。对应 UAC的GameServerController.visitorlLogin服务。
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-01 14:49
 */
@Slf4j
@Service
public class VisitorStrategy extends CoderNotify implements LoginCheckStrategy {

    @Override
    public LoginResult check(HttpServletRequest request, CfgChannelEntity channel) {
        String accountName = request.getParameter("email");
        LoginResult result = new LoginResult();
        try {
            // public Rst visitorlLogin(String accountName, String channelCode)
            String check_url = getUacBaseUrl() + "account!serverVisitorlLogin?accountName=%s&channelCode=%s";
            String url = String.format(check_url, URLEncoder.encode(accountName, "utf-8"), channel.getPlatCode());
            String json = HttpClientUtil.doGet(url);
            result = JSON.parseObject(json, LoginResult.class);
            return result;
        } catch (Exception e) {
            result.setFail(LM.I.getMsg("Login.fail3"));
            log.error(e.getMessage(), e);
            notifyCoderNormal(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public boolean support(int loginType) {
        return 30 == loginType;
    }
}
