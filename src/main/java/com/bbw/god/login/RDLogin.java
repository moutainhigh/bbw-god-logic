package com.bbw.god.login;

import java.io.Serializable;

import com.bbw.god.rd.RDSuccess;
import com.bbw.god.rd.ResCode;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 登录数据
 * 
 * @author suhq
 * @date 2019年3月17日 下午2:40:24
 */
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDLogin extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;

	private String username = null;

	/**
	 * 返回让玩家创建角色的信息
	 * 
	 * @param username
	 * @return
	 */
	public static RDLogin toCreateRole(String username) {
		RDLogin rdLogin = new RDLogin();
		rdLogin.setUsername(username);
		rdLogin.setRes(ResCode.TO_CREATE_ROLE);
		return rdLogin;
	}

	public static RDLogin toHotLoad() {
		RDLogin rdLogin = new RDLogin();
		rdLogin.setRes(ResCode.TO_HOT_LOAD);
		return rdLogin;
	}

}
