package com.bbw.god;

import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.impl.DefaultAwardService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 不能基于接口自动注入的bean定义
 *
 * @author fzj
 * @date 2021/11/1 15:27
 */
@Configuration
public class GodBeanConfig {
    @Bean
    public AwardService awardService(){
        return new DefaultAwardService();
    }
}
