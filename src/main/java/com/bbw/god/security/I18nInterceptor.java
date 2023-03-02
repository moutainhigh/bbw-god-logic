package com.bbw.god.security;

import com.bbw.sys.session.RequestContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 多语言拦截器
 *
 * @author: suhq
 * @date: 2021/11/25 3:28 下午
 */
@Component
@Slf4j
public class I18nInterceptor extends AbstractInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    try {
      RequestContextHolder.setValue(request.getHeader("language"));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return true;
  }
}
