package com.bbw.mc.sms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.exceptions.ClientException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SmsServiceTest {
	
	
	@Autowired
	private SmsService service;

	@Test
	public void testSendMsg() throws ClientException {
		String mobile = "15060452176";
		JSONObject json = new JSONObject();
		json.put("sys", "九州战");
		json.put("time", "2019-05-13 00:00:00");
		service.sendSms(mobile, json.toString());
	}
}
