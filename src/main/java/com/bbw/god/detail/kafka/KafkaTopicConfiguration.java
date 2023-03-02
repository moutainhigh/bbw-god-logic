package com.bbw.god.detail.kafka;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * kafka初始化配置
 *
 * @author: suhq
 * @date: 2021/11/1 4:41 下午
 */
@Profile({"dev-suhq", "prod"})
@Configuration
public class KafkaTopicConfiguration {
    public static String PVE_DETAIL_TOPIC = "pveDetail";
}
