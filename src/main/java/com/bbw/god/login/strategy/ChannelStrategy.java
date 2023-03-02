package com.bbw.god.login.strategy;

import com.bbw.coder.CoderNotify;
import com.bbw.common.StrUtil;
import com.bbw.common.encrypt.JWTUtil;
import com.bbw.god.channel.LoginTokenService;
import com.bbw.god.db.entity.CfgChannelEntity;
import com.bbw.god.uac.entity.AccountEntity;
import com.bbw.god.uac.service.AccountService;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * 渠道登录。对应 UAC的GameServerController.channelLogin服务。
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-01 14:30
 */
@Slf4j
@Service
public class ChannelStrategy extends CoderNotify implements LoginCheckStrategy {
    @Autowired
    private LoginTokenService loginTokenService;
    @Autowired
    private AccountService accountService;

    private LoginResult getLoginResult(AccountEntity entity) {
        LoginResult result = LoginResult.sucess();
        result.setAccountId(entity.getId());
        result.setAccountName(entity.getEmail());
        result.setInviCode(entity.getInvitationCode());
        result.setRegDate(entity.getRegDate());
        return result;
    }

    @Override
    public LoginResult check(HttpServletRequest request, CfgChannelEntity channel) {
        String channelUserKey = request.getParameter("email");
        String loginId = request.getParameter("loginId");
        String loginToken = request.getParameter("loginToken");
        if (StrUtil.isNotNull(loginId)) {
            Claims claims = JWTUtil.getClaims(loginToken);
            boolean b = JWTUtil.checkToken(claims);//loginTokenService.validToken(loginId, loginToken);
            if (b) {
//                return JWTUtil.getExtraInfo(claims, LoginResult.class);
                AccountEntity entity = accountService.selectById(Integer.parseInt(loginId));
                LoginResult result = getLoginResult(entity);
                return result;
            } else {
                log.error("无效凭证。channelUserKey={},loginId={},loginToken={}", channelUserKey, loginId, loginToken);
                LoginResult result = LoginResult.fail("无效的凭证！");
                return result;
            }
        }
        AccountEntity entity = accountService.findByAccount(channelUserKey);
        if (null == entity) {
            LoginResult result = LoginResult.fail("无效的渠道凭证！");
            return result;
        } else {
            LoginResult result = getLoginResult(entity);
            return result;
        }
    }

    @Override
    public boolean support(int loginType) {
        return 40 == loginType;
    }
}
