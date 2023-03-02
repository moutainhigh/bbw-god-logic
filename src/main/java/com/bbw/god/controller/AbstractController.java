package com.bbw.god.controller;

import com.alibaba.fastjson.JSON;
import com.bbw.coder.CoderNotify;
import com.bbw.common.Rst;
import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.login.LoginPlayer;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller公共组件
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月27日 下午5:42:52
 */
public abstract class AbstractController extends CoderNotify {
	@Autowired
	public HttpServletRequest request;
	@Autowired
	protected GameUserService gameUserService;

	protected GameUser getGameUser() {
		return gameUserService.getGameUser(this.getUserId());
	}

	/**
	 * 返回当前登录用户
	 *
	 * @return
	 */
	protected LoginPlayer getUser() {
		LoginPlayer player = (LoginPlayer) request.getAttribute(LoginPlayer.REQUEST_ATTR_KEY);
		return player;
	}

	/**
	 * 返回登录的区服ID
	 * 
	 * @return
	 */
	protected int getLoginServerId() {
		return getUser().getLoginSid();
	}

	/**
	 * 返回当前登录用户的账号名称
	 * 
	 * @return
	 */
	protected String getAccount() {
		return getUser().getAccount();
	}

	/**
	 * 返回当前登录的区服ID也是合服后的ID
	 * 
	 * @return
	 */
	protected int getServerId() {
		return getUser().getServerId();
	}

	/**
	 * 返回当前登录用户的区服ID
	 * 
	 * @return
	 */
	protected Long getUserId() {
		LoginPlayer user = getUser();
		if (null == user) {
			return 0L;
		}
		return user.getUid();
	}

	/**
	 * 返回当前登录用户的区服昵称
	 * 
	 * @return
	 */
	protected String getNickName() {
		return getUser().getNickName();
	}

	/**
	 * Object转成JSON数据
	 */
	protected String toJson(Object object) {
		if (object instanceof Integer || object instanceof Long || object instanceof Float || object instanceof Double || object instanceof Boolean || object instanceof String) {
			return String.valueOf(object);
		}
		return JSON.toJSONString(object);
	}

	/**
	 * 业务执行成功
	 *
	 * @return
	 */
	protected Rst OK() {
		return Rst.businessOK();
	}

	/**
	 * 控制器出现错误（非controller的错误统一在异常处处理）
	 * 
	 * @param message
	 * @return
	 */
	protected Rst FAIL(String message) {
		return Rst.businessFAIL(message);
	}

	/**
	 * 空字符串检验
	 * 
	 * @param str
	 */
	protected void checkStrNotBlank(String str) {
		if (StrUtil.isNull(str)) {
			throw new ExceptionForClientTip("request.param.not.valid");
		}
	}

	/**
	 * 获取当前的平台号
	 * @return
	 */
	protected int getGid(){
		return gameUserService.getActiveGid(getUserId());
	}
}
