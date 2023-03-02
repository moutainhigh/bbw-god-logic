package com.bbw.exception;

import com.bbw.common.LM;

import lombok.Getter;
import lombok.Setter;

/**
 * 程序员引起的错误
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-21 22:34
 */
@Getter
@Setter
public class CoderException extends GodException {
	private static final long serialVersionUID = 1L;
	private ErrorLevel errorLevel = ErrorLevel.NORMAL;

	/**
	 * 国际化
	 * @param errorLevel
	 * @param msgKey：国际化msgkey
	 * @return
	 */
	public static CoderException fromLocalMessage(ErrorLevel errorLevel, String msgKey) {
		String msg = LM.I.getMsg(msgKey);
		CoderException ce = new CoderException(msg, errorLevel);
		return ce;
	}

	/**
	 * 国际化
	 * @param errorLevel
	 * @param msgKey：国际化msgkey
	 * @param args
	 * @return
	 */
	public static CoderException fromLocalMessage(ErrorLevel errorLevel, String msgKey, Object... args) {
		String msgTpl = LM.I.getMsg(msgKey);
		String msg = String.format(msgTpl, args);
		CoderException ce = new CoderException(msg, errorLevel);
		return ce;
	}

	public static CoderException normal(String msg) {
		CoderException ce = new CoderException(msg);
		return ce;
	}

	public static CoderException normal(String msg, Throwable e) {
		CoderException ce = new CoderException(msg, DEFAULT_ERROR, ErrorLevel.NORMAL, e);
		return ce;
	}

	public static CoderException high(String msg) {
		CoderException ce = new CoderException(msg, ErrorLevel.HIGH);
		return ce;
	}

	public static CoderException high(String msg, Throwable e) {
		CoderException ce = new CoderException(msg, DEFAULT_ERROR, ErrorLevel.HIGH, e);
		return ce;
	}

	public static CoderException fatal(String msg) {
		CoderException ce = new CoderException(msg, ErrorLevel.FATAL);
		return ce;
	}

	public static CoderException fatal(String msg, Throwable e) {
		CoderException ce = new CoderException(msg, DEFAULT_ERROR, ErrorLevel.FATAL, e);
		return ce;
	}

	public CoderException(String msg) {
		super(msg);
	}

	public CoderException(String msg, ErrorLevel errorLevel) {
		super(msg);
		this.errorLevel = errorLevel;
	}

	public CoderException(String msg, int code, ErrorLevel errorLevel) {
		super(msg, code);
		this.errorLevel = errorLevel;
	}

	public CoderException(String msg, int code, ErrorLevel errorLevel, Throwable e) {
		super(msg, code, e);
		this.errorLevel = errorLevel;
	}
}
