package com.bbw.db.redis.serializer;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.bbw.common.StrUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * @author suchaobin
 * @description 自定义bitset的反序列化器
 * @date 2020/5/13 18:11
 **/
public class BitSetDeserializer implements ObjectDeserializer {

    @Override
    @SuppressWarnings("unchecked")
    public BitSet deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        String str = (String) parser.parse(fieldName);
        str = str.replace("{", "").replace("}", "").replaceAll(" ", "");
        String[] split = str.split(",");
        List<Integer> list = new ArrayList<>();
        for (String s : split) {
            if (StrUtil.isBlank(s)) {
                continue;
            }
            list.add(Integer.parseInt(s));
        }
        BitSet bitSet = new BitSet();
        list.forEach(bitSet::set);
        return bitSet;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
