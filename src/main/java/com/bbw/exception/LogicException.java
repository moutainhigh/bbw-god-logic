package com.bbw.exception;

import com.bbw.common.LM;

/**
 * 逻辑错误
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-29 13:06
 */
public class LogicException extends GodException {

	private static final long serialVersionUID = 4995635070950366079L;

	public static LogicException get(int code) {
		LogicException pe = new LogicException(code);
		return pe;
	}

	private LogicException(int code) {
		super("逻辑异常", code);
	}

	private LogicException(String msg) {
		super(msg);
	}

	public LogicException(String msg, Throwable e) {
		super(msg, e);
	}

	public LogicException(String msg, int code) {
		super(msg, code);
	}

	public LogicException(String msg, int code, Throwable e) {
		super(msg, code, e);
	}

	public static LogicException get(String msg) {
		LogicException pe = new LogicException(msg);
		return pe;
	}

	/**
	 * 国际化
	 * @param msgKey：国际化的msgKey
	 * @return
	 */
	public static GodException fromLocalMessage(String msgKey) {
		String msg = LM.I.getMsg(msgKey);
		GodException ce = new GodException(msg);
		return ce;
	}

	/**
	 * 国际化
	 * @param msgKey：国际化的msgKey
	 * @param args
	 * @return
	 */
	public static GodException fromLocalMessage(String msgKey, Object... args) {
		String msgTpl = LM.I.getMsg(msgKey);
		String msg = String.format(msgTpl, args);
		GodException ce = new GodException(msg);
		return ce;
	}
}
