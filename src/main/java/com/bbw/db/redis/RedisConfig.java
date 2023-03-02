package com.bbw.db.redis;

/**
 * Redis配置
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年9月27日 下午6:14:37
 */
//@Configuration
public class RedisConfig {
//	@Autowired
//	private RedisConnectionFactory factory;
//
//	@Bean
//	public RedisTemplate<String, Object> redisTemplate() {
//		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//		//redis   开启事务
//		//redisTemplate.setEnableTransactionSupport(true);
//
//		//设置为自定义的序列化
//		FastJsonRedisSerializer<Object> fastJson = new FastJsonRedisSerializer<Object>(Object.class);
//		ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
//		// 设置value的序列化规则和 key的序列化规则
//		redisTemplate.setKeySerializer(new StringRedisSerializer());
//		redisTemplate.setHashKeySerializer(fastJson);
//		redisTemplate.setHashValueSerializer(fastJson);
//		redisTemplate.setValueSerializer(fastJson);
//
//		//	redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
//		redisTemplate.setConnectionFactory(factory);
//		return redisTemplate;
//	}
//
//	@Bean
//	public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
//		return redisTemplate.opsForHash();
//	}
//
//	@Bean
//	public ValueOperations<String, Object> valueOperations(RedisTemplate<String, Object> redisTemplate) {
//		return redisTemplate.opsForValue();
//	}
//
//	@Bean
//	public ListOperations<String, Object> listOperations(RedisTemplate<String, Object> redisTemplate) {
//		return redisTemplate.opsForList();
//	}
//
//	@Bean
//	public SetOperations<String, Object> setOperations(RedisTemplate<String, Object> redisTemplate) {
//		return redisTemplate.opsForSet();
//	}
//
//	@Bean
//	public ZSetOperations<String, Object> zSetOperations(RedisTemplate<String, Object> redisTemplate) {
//		return redisTemplate.opsForZSet();
//	}
}
