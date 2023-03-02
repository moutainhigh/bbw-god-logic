package com.bbw.mc.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import lombok.extern.slf4j.Slf4j;

/**
 * 短信推送
 * 
 * @author Administrator
 *
 */
@Slf4j
@Service
public class SmsService {
	/**
	 * 短信签名
	 */
	@Value("${spring.sms.signature}")
	private String signature;
	/**
	 * 短信模板id
	 */
	@Value("${spring.sms.template}")
	private String template;
	/**
	 * 短信服务accessKeyId
	 */
	@Value("${spring.sms.accessKeyId}")
	private String accessKeyId;
	/**
	 * 短信服务accessKeySecret
	 */
	@Value("${spring.sms.accessKeySecret}")
	private String accessKeySecret;

	// 产品名称:云通信短信API产品,开发者无需替换
	private final String product = "Dysmsapi";
	// 产品域名,开发者无需替换
	private final String domain = "dysmsapi.aliyuncs.com";

	public boolean sendSms(String mobile, String content) throws ClientException {
		boolean ret = false;
		if (null == content || null == mobile || mobile.length() == 0) {
			log.error("短信-> {} 无法继续执行，因为缺少基本的参数：内容，手机号", content);
			throw new RuntimeException("短信无法继续发送，因为缺少必要的参数！");
		}
		// 可自助调整超时时间
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");

		// 初始化acsClient,暂不支持region化
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
		DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		IAcsClient acsClient = new DefaultAcsClient(profile);

		// 组装请求对象-具体描述见控制台-文档部分内容
		SendSmsRequest request = new SendSmsRequest();
		// 必填:待发送手机号
		request.setPhoneNumbers(mobile);
		// 必填:短信签名-可在短信控制台中找到
		request.setSignName(signature);
		// 必填:短信模板-可在短信控制台中找到
		request.setTemplateCode(template);
		request.setTemplateParam(content);
		// hint 此处可能会抛出异常，注意catch
		SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
		String code = sendSmsResponse.getCode();
		if ("OK".equals(code)) {
			ret = true;
		}else {
			log.info(sendSmsResponse.getMessage());
		}

		return ret;
	}

}
