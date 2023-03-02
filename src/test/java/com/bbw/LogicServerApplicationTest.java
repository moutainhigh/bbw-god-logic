package com.bbw;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-20 10:39
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { LogicServerApplication.class }) // 指定启动类
public class LogicServerApplicationTest {

	@Test
	public void testOne() {
		System.out.println("test hello 1");
	}

}
