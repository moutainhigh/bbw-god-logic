package com.bbw.god.validator;

import com.bbw.common.SpringContextUtil;
import com.bbw.exception.ExceptionForClientTip;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Set;

/**
 * hibernate-validator校验工具类
 * <p>
 * 参考文档：http://docs.jboss.org/hibernate/validator/6.0/reference/en-US/html_single/
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-02 15:56
 */
@Slf4j
public class GodValidator {
    private static Validator validator;
    private static String osName;

    static {
//        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class).configure().messageInterpolator(new ResourceBundleMessageInterpolator(new PlatformResourceBundleLocator("i18n/GodMessages"))).failFast(true).buildValidatorFactory();
        validator = (Validator) SpringContextUtil.getBean("validator");
        Properties props = System.getProperties();
        osName = props.getProperty("os.name");
        System.out.println("当前操作系统是:" + osName);
        System.out.println("当前系统编码:" + props.getProperty("file.encoding"));
        System.out.println("当前系统语言:" + props.getProperty("user.language"));
    }

    /**
     * 校验对象
     *
     * @param object 待校验对象
     * @param groups 待校验的组
     */
    public static void validateEntity(Object object, Class<?>... groups) {
//        Validator validator = (Validator) SpringContextUtil.getBean("validator");
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            ConstraintViolation<Object> constraint = constraintViolations.iterator().next();
            try {
                String key = new String(constraint.getMessage().getBytes("ISO-8859-1"), "utf-8");
                throw new ExceptionForClientTip(key);
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
