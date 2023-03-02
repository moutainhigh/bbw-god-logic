package com.bbw.exception;

/**
 * 渠道登录错误
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-19 14:44
 */
public class ChannelLoginException extends GodException {
	private static final long serialVersionUID = 2968653210485768731L;

	public ChannelLoginException(String msg) {
		super(msg);
	}

	public ChannelLoginException(String msg, Throwable e) {
		super(msg, e);
	}

	public ChannelLoginException(String msg, int code) {
		super(msg, code);
	}

	public ChannelLoginException(String msg, int code, Throwable e) {
		super(msg, code, e);
	}
}
