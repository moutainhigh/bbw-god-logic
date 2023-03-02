package com.bbw.god.detail.kafka;

import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * kafka初始化配置
 *
 * @author: suhq
 * @date: 2021/11/1 4:41 下午
 */
@Profile({"dev-suhq", "prod"})
@Configuration
public class KafkaProducerConfiguration {
    @Value("${spring.kafka.bootstrap-servers: }")
    private String bootstrapServers;
    @Value("${spring.kafka.producer.key-serializer:org.apache.kafka.common.serialization.StringSerializer}")
    private String keySerializer;
    @Value("${spring.kafka.producer.value-serializer:org.apache.kafka.common.serialization.StringSerializer}")
    private String valueSerializer;
    @Value("${spring.kafka.producer.batch-size:16384}")
    private int batchSize;
    @Value("${spring.kafka.producer.buffer-memory:33554432}")
    private int bufferMemory;
    @Value("${spring.kafka.producer.retries:0}")
    private int retries;
    @Value("${spring.kafka.producer.acks:0}")
    private String acks;
    @Value("${spring.kafka.producer.properties.sasl.mechanism:PLAIN}")
    private String saslMechanism;
    @Value("${spring.kafka.producer.security.protocol:SASL_PLAINTEXT}")
    private String securityProtocol;
    @Value("${spring.kafka.producer.properties.sasl.jaas.config: }")
    private String saslJaasConfig;

    @Bean("kafkaTemplate")
    public KafkaTemplate<String, String> kafkaTemplate() {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory());
        return kafkaTemplate;
    }

    private ProducerFactory<String, String> producerFactory() {
        Map<String, Object> properties = new HashMap<>(9);
        if (StringUtils.isNotEmpty(saslJaasConfig)) {
            // 设置sasl认证的两种方式
//            System.setProperty("java.security.auth.login.config", "classpath:/application.properties:/kafka_client_jaas.conf");
            properties.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
        }
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        properties.put(ProducerConfig.ACKS_CONFIG, acks);
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        properties.put(ProducerConfig.RETRIES_CONFIG, retries);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
        if (StringUtils.isNotEmpty(saslMechanism) && StringUtils.isNotEmpty(securityProtocol)) {
            properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
            properties.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        }
        return new DefaultKafkaProducerFactory<>(properties);
    }
}
