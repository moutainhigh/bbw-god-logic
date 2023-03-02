package com.bbw.db.redis.ds;

import com.alibaba.fastjson.parser.ParserConfig;
import com.bbw.db.redis.serializer.FastJsonRedisSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * redis多数据源
 *
 * @author suhq
 * @date 2020-06-19 11:33
 **/
@Slf4j
@Component
public class MultiRedis {
    public static final String COMMON_REDIS_DS = "common";
    private Map<String, RedisTemplate<String, Object>> redisTemplates = new ConcurrentHashMap<>();
    private Map<String, RedisConnectionFactory> redisConnectionFactories = new ConcurrentHashMap<>();
    @Autowired
    private RedisConfig redisConfig;
    @Autowired(required = false)
    IRedisMarkKeyMatch redisMarkKeyMatch;

    /**
     * 为了配合spring session的使用
     *
     * @return
     */
    @Primary
    @Bean(name = "lettuceConnectionFactory")
    public RedisConnectionFactory lettuceConnectionFactory() {
        return redisConnectionFactories.get(COMMON_REDIS_DS);
    }


    @PostConstruct
    private void init() {
        if (null == redisConfig.getMultiRedis() || redisConfig.getMultiRedis().isEmpty()) {
            throw new Error("未配置数据源：bbw-god.multi-redis");
        }
        Set<String> redisMarks = redisConfig.getMultiRedis().keySet();
        redisMarks.stream().forEach(tmp -> {
            RedisProperties redisProperties = redisConfig.getMultiRedis().get(tmp);
            LettuceConnectionFactory factory = buildFactory(redisProperties);
            RedisTemplate<String, Object> template = buildTemplate(factory);
            checkTemplate(template);
            redisTemplates.put(tmp, template);
            redisConnectionFactories.put(tmp, factory);
        });
    }

    /**
     * 根据key获取模板
     *
     * @param key
     * @return
     */
    public RedisTemplate<String, Object> getRedisTemplate(String key) {
        String redisMark = getRedisMark(key);
        RedisTemplate<String, Object> template = redisTemplates.get(redisMark);
        if (null == template) {
            throw new Error("未配置数据源：bbw-god.multi-redis:" + redisMark);
        }
        return template;
    }

    /**
     * 获得key所在的Redis服务器
     *
     * @param key
     * @return
     */
    public String getRedisMark(String key) {
        if (redisMarkKeyMatch == null) {
            throw new Error("未实现key->redis的映射接口IRedisMarkKeyMatch");
        }
        String redisMark = redisMarkKeyMatch.getRedisMark(key);
        if (redisMark == null) {
            throw new Error("没有找到" + key + "对应的Redis服务器");
        }
        return redisMark;
    }

    /**
     * 检查redisTemplate是否可用
     *
     * @param redisTemplate
     */
    private void checkTemplate(RedisTemplate<String, Object> redisTemplate) {
        try {
            redisTemplate.opsForValue().get("1");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    private LettuceConnectionFactory buildFactory(RedisProperties redisProperties) {
        /* ========= 基本配置 ========= */
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(redisProperties.getHost());
        configuration.setPort(redisProperties.getPort());
        configuration.setDatabase(redisProperties.getDatabase());
        if (!StringUtils.isEmpty(redisProperties.getPassword())) {
            RedisPassword redisPassword = RedisPassword.of(redisProperties.getPassword());
            configuration.setPassword(redisPassword);
        }
        /* ========= 连接池通用配置 ========= */
        RedisProperties.Pool pool = redisProperties.getLettuce().getPool();
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(pool.getMaxActive());
        genericObjectPoolConfig.setMinIdle(pool.getMinIdle());
        genericObjectPoolConfig.setMaxIdle(pool.getMaxIdle());
        genericObjectPoolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
        /* ========= lettuce pool ========= */
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        builder.poolConfig(genericObjectPoolConfig);
        builder.commandTimeout(redisProperties.getTimeout());
        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration, builder.build());
        //!!!多database,要关闭共享链接，并且不再注入RedisConnectionFactory，改为注入LettuceConnectionFactory
        factory.setShareNativeConnection(false);
        factory.afterPropertiesSet();
        return factory;
    }

    private RedisTemplate<String, Object> buildTemplate(LettuceConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);

        //设置为自定义的序列化
        FastJsonRedisSerializer<Object> serializer = new FastJsonRedisSerializer<Object>(Object.class);
        //自动类型支持，持久化时会添加类型信息
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        // 设置key、hashkey、value的序列化规则
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);
        redisTemplate.setValueSerializer(serializer);

        //redis   开启事务
        //redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


}
