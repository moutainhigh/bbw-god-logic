package com.bbw.god.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 跨域请求拦截器
 * <br/> 这个拦截器要排在最前面
 */
@Component
@Slf4j
public class CorsInterceptor extends AbstractInterceptor {

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin")); //  这里最好明确的写允许的域名
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
    response.setHeader("Access-Control-Max-Age", "3600");
    response.setHeader("XDomainRequestAllowed", "1");
    response.setHeader("Access-Control-Allow-Headers", "Content-Type,Content-Length,Accept-Encoding,Cookie,token,language");
    // 如果是OPTIONS则结束请求
    if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
      response.setStatus(HttpStatus.NO_CONTENT.value());
      return false;
    }
    return true;
  }
}
