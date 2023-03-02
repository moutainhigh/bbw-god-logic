package com.bbw.god.validator;

import com.bbw.common.BbwSensitiveWordUtil;
import com.bbw.common.StrUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 文字内容判断
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-15 14:03
 */
public class ContentValidator implements ConstraintValidator<CheckContent, String> {

    /*
     * (non-Javadoc)
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StrUtil.isNull(value)) {
            return false;
        }
        return !BbwSensitiveWordUtil.contains(value.trim());
//            return !LimitWords.isLimit(value);
    }

}
