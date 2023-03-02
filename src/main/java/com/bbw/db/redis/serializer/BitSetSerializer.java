package com.bbw.db.redis.serializer;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author suchaobin
 * @description 自定义bitset的序列化器
 * @date 2020/5/13 18:11
 **/
public class BitSetSerializer implements ObjectSerializer {

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.out;
        if (null == object) {
            out.writeNull();
            return;
        }
        out.writeString(object.toString());
    }
}
