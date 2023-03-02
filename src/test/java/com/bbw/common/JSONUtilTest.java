package com.bbw.common;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JSONUtilTest {

    @Test
    public void fromJson() {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            map.put("" + i, i);
        }
        long begin = System.currentTimeMillis();
        Map<String, Integer> redisMap = new HashMap<>();
        for (String s : map.keySet()) {
            redisMap.put(s, Integer.class.cast(map.get(s)));
        }
//        String mapJson = JSONUtil.toJson(map);
//        Map<String, Integer> redisMap = JSONUtil.fromJson(mapJson, Map.class);
        System.out.println(System.currentTimeMillis() - begin);
    }

    @Test
    public void toJsonString() {
    }
}