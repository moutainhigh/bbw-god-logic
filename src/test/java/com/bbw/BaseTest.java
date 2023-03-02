package com.bbw;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-25 23:34
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = LogicServerApplication.class)
@WebAppConfiguration
public class BaseTest {
    protected static Integer SERVER = 99;
    protected static Long UID = 220920008500005L;
    protected static Long WB_UID=210421009600016L;

}