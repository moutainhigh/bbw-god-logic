package com.bbw.sys.config;

import org.springframework.context.annotation.Configuration;

/**
 * Filter配置。注意registration.setOrder()方法。order 数值越小，优先级越高
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月27日 下午6:15:00
 */
@Configuration
public class FilterConfig {

    /**
     * xss攻击过滤
     *
     * @return
     */
    // @Bean
    // public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
    // FilterRegistrationBean<XssFilter> registration = new
    // FilterRegistrationBean<XssFilter>();
    // registration.setDispatcherTypes(DispatcherType.REQUEST);
    // registration.setFilter(new XssFilter());
    // registration.addUrlPatterns("/*");
    // registration.setName("xssFilter");
    // registration.setOrder(Integer.MAX_VALUE);
    // return registration;
    // }

}
