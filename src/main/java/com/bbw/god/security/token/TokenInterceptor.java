package com.bbw.god.security.token;

import com.bbw.common.LM;
import com.bbw.common.StrUtil;
import com.bbw.common.encrypt.JWTUtil;
import com.bbw.god.game.online.GameOnlineAsyncHandler;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.security.AbstractInterceptor;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限校验
 *
 * @author: suhq
 * @date: 2021/11/25 2:56 下午
 */
@Slf4j
@Component
public class TokenInterceptor extends AbstractInterceptor {
  @Resource
  private SingleTokenService singleTokenService;
  @Autowired
  private GameOnlineAsyncHandler gameOnlineAsyncHandler;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    //不需要过滤
    if (!(handler instanceof HandlerMethod)) {
      return true;
    }
    if (isNotNeedToken(request.getRequestURI())) {
      return true;
    }
    String token = request.getHeader(JWTUtil.REQ_HEADER);
    //权限token不能为null或者""
    if (StrUtil.isBlank(token)) {
      relogin(response, LM.I.getMsg("login.to.relogin.by.arg.error"));
      return false;
    }
    //认证信息是否有效
    Claims claims = JWTUtil.getClaims(token);
    if (!singleTokenService.isTokenValid(token, claims)) {
      relogin(response, LM.I.getMsg("login.to.relogin"));
      return false;
    }
    //认证信息的有效性
    if (!JWTUtil.checkToken(claims)) {
      relogin(response, LM.I.getMsg("login.to.relogin.by.leave"));
      return false;
    }
    LoginPlayer loginPlayer = JWTUtil.getExtraInfo(claims, LoginPlayer.class);
    request.setAttribute(LoginPlayer.REQUEST_ATTR_KEY, loginPlayer);
    gameOnlineAsyncHandler.addToOnline(loginPlayer);
    return true;
  }

}
