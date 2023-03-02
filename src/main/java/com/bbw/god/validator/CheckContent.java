package com.bbw.god.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-15 14:06
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
//指定验证器  
@Constraint(validatedBy = ContentValidator.class)
@Documented
public @interface CheckContent {
    //默认错误消息
    String message() default "content.contain.unvalid";

    //分组
    Class<?>[] groups() default {};

    //负载
    Class<? extends Payload>[] payload() default {};
}
