package com.bbw.mc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;
import com.bbw.mc.dingding.DingdingService;
import com.bbw.mc.mail.SysMailService;
import com.bbw.mc.sms.SmsService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "mc")
@Slf4j
public class McController {
	@Autowired
	private SysMailService mailService;

	@Autowired
	private DingdingService dingService;

	@Autowired
	private SmsService smsService;
	/**
	 * http://localhost:8088/godLogic/mc/sendMail?subject=标题&content=内容&toWho=875424655@qq.com
	 * @param subject
	 * @param content
	 * @param toWho
	 * @return
	 */
	@RequestMapping(value = "sendMail")
	public String sendMail(String subject, String content, String toWho) {
		// TODO 签名校验
		String[] arr = toWho.split(",");
		mailService.sendSimpleTextMail(subject, content, arr);
		JSONObject json = new JSONObject();
		json.put("ret", 0);
		return json.toString();
	}
	/**
	 * 	http://localhost:8088/godLogic/mc/sendSms?content=%7b%22sys%22%3a%22%E4%B9%9D%E5%B7%9E%E6%88%98%22%2c%22time%22%3a%222019-05-10+00%3a00%3a00%22%7d&mobile=15060452176
	 * @param mobile
	 * @param content {"sys":"九州战","time":"2019-05-10 00:00:00"}
	 * @return
	 */
	@RequestMapping(value = "sendSms")
	public String sendSms(String mobile, String content) {
		// TODO 签名校验
		boolean b = false;
		try {
			b = smsService.sendSms(mobile, content);
		} catch (ClientException e) {
			log.error(e.getMessage(), e);
		}
		JSONObject json = new JSONObject();
		if (b) {
			json.put("ret", 0);
		} else {
			json.put("ret", 1);
		}
		return json.toString();
	}
	/**
	 * http://localhost:8088/godLogic/mc/sendDingDing?content=内容&toWho=b124156645b14ba51a92461c9ff595784c98d2b084a64325218d978dd1669877
	 * @param content
	 * @param toWho
	 * @return
	 */
	@RequestMapping(value = "sendDingDing")
	public String sendDingDing(String content, String toWho) {
		// TODO 签名校验
		boolean b = dingService.sendSimpleTextMail(content, toWho);
		JSONObject json = new JSONObject();
		if (b) {
			json.put("ret", 0);
		} else {
			json.put("ret", 1);
		}
		return json.toString();
	}

}
