package com.bbw.god.login.strategy;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月29日 下午3:26:14
 */
@Service
public class LoginCheckStrategyFactory {
	@Lazy
	@Autowired
	private List<LoginCheckStrategy> services;

	/**
	 * 根据渠道code获取校验服务实现对象
	 * @param channelCode
	 * @return
	 */
	public LoginCheckStrategy getLoginCheckStrategy(int loginType) {
		LoginCheckStrategy service = null;

		for (LoginCheckStrategy tmpService : services) {
			if (tmpService.support(loginType)) {
				service = tmpService;
				break;
			}
		}
		return service;
	}
}
