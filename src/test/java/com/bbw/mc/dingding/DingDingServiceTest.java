package com.bbw.mc.dingding;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DingDingServiceTest {
	
	@Autowired
	private DingdingService service;
	
	@Test
	public void testSendMsg() {
		String subject = "这是标题";
		String content = "这是内容";
		String[] toWho = new String[] {"b124156645b14ba51a92461c9ff595784c98d2b084a64325218d978dd1669877"};
		service.sendSimpleTextMail(content, toWho);
	}
	
}
