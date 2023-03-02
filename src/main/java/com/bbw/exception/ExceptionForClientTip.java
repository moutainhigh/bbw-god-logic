package com.bbw.exception;

import com.bbw.common.LM;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 给客户端到提示信息，不创建堆栈
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-23 13:16
 */
@Slf4j
@Getter
public class ExceptionForClientTip extends RuntimeException {

	private static final long serialVersionUID = 1L;
	@Setter
	private String message;
	@Setter
	private String tip;

	public static ExceptionForClientTip fromMsg(String msg) {
		ExceptionForClientTip e = new ExceptionForClientTip();
		e.setTip(msg);
		e.setMessage(msg);
		//log.info("\n客户端提示：{}", msg);
		return e;
	}

	private ExceptionForClientTip() {
		super();
	}

	public static ExceptionForClientTip fromi18nKey(String tipKey) {
		ExceptionForClientTip e = new ExceptionForClientTip();
		e.setTip(LM.I.getMsg(tipKey));
		e.setMessage(e.getTip());
		return e;
	}

	public ExceptionForClientTip(String tipCode) {
		this.tip = LM.I.getMsg(tipCode);
		this.message = this.tip;
		//log.info("\n客户端提示：{}:{}", tipCode, this.tip);
	}

	public ExceptionForClientTip(String tipKey, Object... args) {
		String msgTpl = LM.I.getMsg(tipKey);
		this.tip = String.format(msgTpl, args);
		this.message = this.tip;
		//log.info("\n客户端提示：{}:{}", tipCode, this.tip);
	}

	@Override
	public String toString() {
		return tip;
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}

}
