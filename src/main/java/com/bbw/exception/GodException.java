package com.bbw.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 富甲基本异常类
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-19 14:44
 */
public class GodException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/**
	 * 默认错误编码
	 */
	public static final int DEFAULT_ERROR = 1;
	private String msg;
	private int code = DEFAULT_ERROR;

	public GodException(String msg) {
		super(msg);
		this.msg = msg;
	}

	public GodException(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
	}

	public GodException(String msg, int code) {
		super(msg);
		this.msg = msg;
		this.code = code;
	}

	public GodException(String msg, int code, Throwable e) {
		super(msg, e);
		this.msg = msg;
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * 堆栈信息
	 * @return
	 */
	public String getStackMessage() {
		StringWriter sw = new StringWriter();
		this.printStackTrace(new PrintWriter(sw, true));
		String strs = sw.toString();
		return strs;
	}

	public static String getStackMessage(Exception e) {
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw, true));
		String strs = sw.toString();
		return strs;
	}

}
