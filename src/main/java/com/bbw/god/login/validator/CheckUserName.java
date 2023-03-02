package com.bbw.god.login.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 账号验证器
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-05 10:08
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
//指定验证器  
@Constraint(validatedBy = UserNameValidator.class)
@Documented
public @interface CheckUserName {
    //默认错误消息
    String message() default "login.account.invalid";

    //分组
    Class<?>[] groups() default {};

    //负载
    Class<? extends Payload>[] payload() default {};
}
