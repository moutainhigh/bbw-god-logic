package com.bbw.god.login.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.stereotype.Service;

import com.bbw.common.StrUtil;

/**
 * 账号验证器
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-05 10:10
 */
@Service
public class UserNameValidator implements ConstraintValidator<CheckUserName, String> {

	@Override
	public boolean isValid(String userName, ConstraintValidatorContext context) {
		if (StrUtil.isNull(userName)) {
			return false;
		}
		return true;
	}

}
