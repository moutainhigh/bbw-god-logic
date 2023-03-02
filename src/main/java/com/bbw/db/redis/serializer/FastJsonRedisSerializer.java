package com.bbw.db.redis.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

/**
 * redis fastjson序列化
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-10-25 23:25
 */
public class FastJsonRedisSerializer<T> implements RedisSerializer<T> {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private Class<T> clazz;

    public FastJsonRedisSerializer(Class<T> clazz) {
        super();
        this.clazz = clazz;
    }

    public byte[] serialize(T object) throws SerializationException {
        if (object == null) {
            return new byte[0];
        }
        return JSON.toJSONString(object, SerializerFeature.WriteClassName, SerializerFeature.WriteDateUseDateFormat).getBytes(DEFAULT_CHARSET);
    }

    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        String str = new String(bytes, DEFAULT_CHARSET);
        T object = JSON.parseObject(str, clazz);
        return object;
    }
}