package com.bbw.common;

import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * 基于RestTemplate的Http工具类
 * 
 * @author suhq
 * @date 2019-10-18 10:09:46
 */
public class HttpClientNewUtil {
	private static RestTemplate restTemplate = SpringContextUtil.getBean("restTemplate", RestTemplate.class);

	public static String doGet(String url, Map<String, String> param) {
		ResponseEntity<byte[]> byteDatas = restTemplate.getForEntity(url, byte[].class, param);
		String data = new String(byteDatas.getBody());
		return data;
	}

	public static String doGet(String url) {
		ResponseEntity<byte[]> byteDatas = restTemplate.getForEntity(url, byte[].class);
		String data = new String(byteDatas.getBody());
		return data;
	}

	/**
	 * 入参数据
	 * 
	 * @param request
	 * @return a=***&b=***
	 */
	public static String getRequestParams(HttpServletRequest request) {
		String reqParam = "";
		Enumeration<String> paramter = request.getParameterNames();
		while (paramter.hasMoreElements()) {
			String name = (String) paramter.nextElement();
			reqParam += name + "=" + request.getParameter(name) + "&";
		}
		if (!StrUtil.isBlank(reqParam) && reqParam.length() > 1) {
			reqParam = reqParam.substring(0, reqParam.length() - 1);
		}
		return reqParam;
	}
}
