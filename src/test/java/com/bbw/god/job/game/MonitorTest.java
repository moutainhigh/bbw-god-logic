package com.bbw.god.job.game;

import com.bbw.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author suchaobin
 * @description 监控测试
 * @date 2020/11/5 11:08
 **/
public class MonitorTest extends BaseTest {
    @Autowired
    private MonitorUserResourceJob resourceJob;

    @Test
    public void test() {
        resourceJob.job();
    }
}