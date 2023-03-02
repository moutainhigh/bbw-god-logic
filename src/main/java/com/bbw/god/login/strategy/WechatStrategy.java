package com.bbw.god.login.strategy;

import com.alibaba.fastjson.JSON;
import com.bbw.coder.CoderNotify;
import com.bbw.common.HttpClientUtil;
import com.bbw.common.LM;
import com.bbw.god.db.entity.CfgChannelEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 微信登录。对应 UAC的GameServerController.wechatLogin服务。
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-01 12:05
 */
@Slf4j
@Service
public class WechatStrategy extends CoderNotify implements LoginCheckStrategy {
    //	@Autowired
    //	private AccountService accountService;

    @Override
    public LoginResult check(HttpServletRequest request, CfgChannelEntity channel) {
        String unionId = request.getParameter("email");
        String channelCode = request.getParameter("plat");
        String wxName = request.getParameter("wxUserInfoNameString");
        wxName = null == wxName ? "未获取" : wxName;

        LoginResult result = new LoginResult();
        if (!unionId.startsWith("opdkDw")) {
            result.setFail(LM.I.getMsg("Login.fail1"));
            log.error("微信登录。账号[{}]不符合规范！", unionId);
            return result;
        }
        try {
            //public Rst LogicServerController.wechatLogin(String unionId, @RequestParam(required = false) String wxName, String channelCode)
            String check_url = getUacBaseUrl() + "account!serverWechatLogin?unionId=%s&channelCode=%s&wxName=%s";
            String url = String.format(check_url, unionId, channelCode, wxName);
            String json = HttpClientUtil.doGet(url);
            result = JSON.parseObject(json, LoginResult.class);
            return result;
        } catch (Exception e) {
            result.setFail(LM.I.getMsg("Login.fail1"));
            log.error(e.getMessage(), e);
            notifyCoderNormal(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public boolean support(int loginType) {
        return 20 == loginType;
    }

}
