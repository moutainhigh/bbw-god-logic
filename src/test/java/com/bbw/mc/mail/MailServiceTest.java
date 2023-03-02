package com.bbw.mc.mail;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MailServiceTest {
	@Autowired
	private SysMailService mailService;

	@Value("${spring.mail.username}")
	private String userName;

	//请把如下的信息写成你自己的邮箱信息
	private String mailQQ = "lsj@bamboowind.com";

	private String content = "简单的文本内容";

	@Test
	/**
	 * 测试发送文本邮件的接口
	 */
	public void sendSimpleTextMail() throws InterruptedException {

		//为我的Outlook邮箱发送一封邮件，抄送我的139邮箱，密送QQ邮箱，带着三个附件
		mailService.sendSimpleTextMail("测试带附件简单文本文件", content, new String[] { mailQQ });
	}
}
