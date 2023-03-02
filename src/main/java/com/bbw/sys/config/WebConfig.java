package com.bbw.sys.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.bbw.App;
import com.bbw.god.security.CorsInterceptor;
import com.bbw.god.security.I18nInterceptor;
import com.bbw.god.security.PostDataDumperInterceptor;
import com.bbw.god.security.limiter.RequestLimiterInterceptor;
import com.bbw.god.security.param.SecurityParamInterceptor;
import com.bbw.god.security.token.TokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;

/**
 * WebMvc配置
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月27日 下午6:16:32
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Resource
    private TokenInterceptor tokenInterceptor;
    @Resource
    private CorsInterceptor corsInterceptor;
    @Resource
    private SecurityParamInterceptor securityParamInterceptor;
    @Resource
    private I18nInterceptor i18nInterceptor;
    @Resource
    private RequestLimiterInterceptor requestLimiterInterceptor;
    @Resource
    private PostDataDumperInterceptor postDataDumperInterceptor;
    @Resource
    private App app;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 跨域拦截器需放在最上面
        registry.addInterceptor(corsInterceptor).addPathPatterns("/**");
        registry.addInterceptor(i18nInterceptor).addPathPatterns("/**");
        // 身份 Token 校验
        registry.addInterceptor(tokenInterceptor).addPathPatterns("/**");
        if (!app.runAsDev()) {
            //频次检查
            registry.addInterceptor(requestLimiterInterceptor).addPathPatterns("/**");
            // 安全参数检查
            registry.addInterceptor(securityParamInterceptor).addPathPatterns("/**");
        }
        registry.addInterceptor(postDataDumperInterceptor).addPathPatterns("/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/statics/**").addResourceLocations("classpath:/statics/");
    }

    /**
     * 配置消息转换器（使用fastjson）
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter fastJsonConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        fastJsonConverter.setFastJsonConfig(fastJsonConfig);

        // MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new
        // MappingJackson2HttpMessageConverter();
        // ObjectMapper objectMapper = jackson2HttpMessageConverter.getObjectMapper();
        //
        // SimpleModule simpleModule = new SimpleModule();
        // objectMapper.registerModule(simpleModule);
        //
        // jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        converters.add(0, fastJsonConverter);
    }

    @Bean
    public CookieSerializer httpSessionIdResolver() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setSameSite(null);//通过将sameSite设为null,h5版本便可跨域传递cookie信息
        return cookieSerializer;
    }
}
