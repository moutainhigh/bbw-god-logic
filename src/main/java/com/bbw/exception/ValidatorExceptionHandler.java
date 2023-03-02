package com.bbw.exception;

import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bbw.common.Rst;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-05 11:28
 */
@ControllerAdvice
@ResponseBody
public class ValidatorExceptionHandler {
	@ExceptionHandler(value = BindException.class)
	public Rst bindExceptionHandler(BindException e) {
		//参数校验会抛出多个异常，这里取第一个。
		return Rst.businessFAIL(e.getAllErrors().get(0).getDefaultMessage());
	}
}
