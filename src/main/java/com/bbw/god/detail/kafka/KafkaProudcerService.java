package com.bbw.god.detail.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * kafka生产者
 *
 * @author: suhq
 * @date: 2021/11/4 9:42 上午
 */
@Service
@RequiredArgsConstructor
public class KafkaProudcerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    /**
     * 向特定的主题发送数据
     *
     * @param topic
     * @param data
     */
    public void send(String topic, String data) {
        kafkaTemplate.send(topic, data);
    }
}
