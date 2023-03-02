package com.bbw.sys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;

/**
 * 校验器配置类
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-10 09:35
 */
@Configuration
public class ValidatorConfig {
//    @Bean("validator")
//    public Validator validator() {
//        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
//                .configure()
//                .addProperty("hibernate.validator.fail_fast", "true")
//                .buildValidatorFactory();
//        Validator validator = validatorFactory.getValidator();
//
//        return validator;
//        Properties props = System.getProperties();
//        String osName = props.getProperty("os.name");
//        System.out.println("当前操作系统是:" + osName);
//        System.out.println("当前系统编码:" + props.getProperty("file.encoding"));
//        System.out.println("当前系统语言:" + props.getProperty("user.language"));
//        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
//                .configure()
//                .messageInterpolator(new ResourceBundleMessageInterpolator(new PlatformResourceBundleLocator("i18n/GodMessages_zh_TW")))
//                .failFast(true)
//                .buildValidatorFactory();
//        return validatorFactory.getValidator();
//    }

    @Bean
    public LocalValidatorFactoryBean validatorFactoryBean() {
        LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean();
//        validatorFactory.setProviderClass(HibernateValidator.class);
//        validatorFactory.setValidationMessageSource(messageSource());
        validatorFactory.getValidationPropertyMap().put("hibernate.validator.fail_fast", "true");
        return validatorFactory;
    }

    @Bean("validator")
    public Validator validator() {
        return validatorFactoryBean().getValidator();
    }
}
