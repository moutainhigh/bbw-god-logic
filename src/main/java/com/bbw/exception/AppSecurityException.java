package com.bbw.exception;

import lombok.Getter;
import lombok.Setter;

/**<pre>
 * 安全异常行为
 * 安全级别：SecurityLevel
 * </pre>
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-12 16:15
 */
@Getter
@Setter
public class AppSecurityException extends GodException {
	private static final long serialVersionUID = 1L;
	private SecurityLevel securityLevel = SecurityLevel.NORMAL;
	private static final int DEFAULT_ERROR_CODE = 119;

	public AppSecurityException(String msg, SecurityLevel level) {
		super(msg, DEFAULT_ERROR_CODE);
		this.securityLevel = level;
	}

	public AppSecurityException(String msg, SecurityLevel level, Throwable e) {
		super(msg, DEFAULT_ERROR_CODE, e);
		this.securityLevel = level;
	}

	public AppSecurityException(String msg, int code, SecurityLevel level) {
		super(msg, DEFAULT_ERROR_CODE);
		this.securityLevel = level;
	}

	public AppSecurityException(String msg, int code, SecurityLevel level, Throwable e) {
		super(msg, DEFAULT_ERROR_CODE, e);
		this.securityLevel = level;
	}
}
