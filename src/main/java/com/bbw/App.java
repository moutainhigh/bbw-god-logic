package com.bbw;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 当前运行程序的信息
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-21 11:02
 */
@Service
public class App {
    @Value("${spring.profiles.active}")
    @Getter
    private String active;
    @Value("${bbw-god.run-schedule:false}")
    @Getter
    private boolean runSchedule;
    @Value("${bbw-god.is-push-to-kafka:false}")
    @Getter
    public Boolean isPushToKafka;

    private static final List<String> DEV_LIST = Arrays.asList("dev", "dev-lsj", "dev-suhq", "dev-fzj", "dev-lzc", "dev-huanghb","dev-hzf","dev-lwh");

    /**
     * 开发者模式
     *
     * @return
     */
    public boolean runAsDev() {
        return DEV_LIST.contains(active);
    }

    public boolean runAsDevFZJ() {
        return "dev-fzj".equals(active);
    }

    public boolean runAsDevHHB() {
        return "dev-huanghb".equals(active);
    }

    /**
     * 测试模式
     *
     * @return
     */
    public boolean runAsTest() {
        return "test".equals(active);
    }

    /**
     * 生产模式
     *
     * @return
     */
    public boolean runAsProd() {
        return "prod".equals(active);
    }
}