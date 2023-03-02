package com.bbw.sys.controller;

import com.bbw.common.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SysPageController {
	@Autowired
	private HttpServletRequest request;
	private static String logic1_ip = "172.18.128.5";
	private static String logic2_ip = "172.18.128.6";

	@RequestMapping("serverlogs.html")
	public String serverlogs() {
		String ip = IpUtil.getInet4Address();
		String logicName = "逻辑服务器1";
		if (ip.equals(logic2_ip)) {
			logicName = "逻辑服务器2";
		}
		request.setAttribute("logicName", logicName);
		return "serverlogs";
	}

	@RequestMapping("log.html")
	public String logs() {
		String ip = IpUtil.getInet4Address();
		String logicName = "逻辑服务器1";
		if (ip.equals(logic2_ip)) {
			logicName = "逻辑服务器2";
		}
		request.setAttribute("logicName", logicName);
		return "log";
	}

	@RequestMapping(value = { "/health.html" })
	public String health() {
		return "/health";
	}
}
