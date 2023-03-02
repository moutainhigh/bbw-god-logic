package com.bbw.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-11-06 11:37
 */
public class JSONUtil {
	/**
	 * Object转成JSON数据
	 */
	public static String toJson(Object object) {
		if (object instanceof Integer || object instanceof Long || object instanceof Float || object instanceof Double || object instanceof Boolean || object instanceof String) {
			return String.valueOf(object);
		}
		return JSON.toJSONString(object, SerializerFeature.WriteDateUseDateFormat);
	}

	/**
	 * JSON数据，转成Object
	 */
	public static <T> T fromJson(String json, Class<T> clazz) {
		return JSON.parseObject(json, clazz);
	}

	/**
	 * json数据，转List
	 * 
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> fromJsonArray(String json, Class<T> clazz) {
		return JSON.parseArray(json, clazz);
	}

	public static int[] fromJsonArray(String json){
		JSONArray jsonArray = JSONArray.parseArray(json);
		int[] res = new int[jsonArray.size()];
		for (int i = 0; i < jsonArray.size(); i++) {
			res[i]= (int) jsonArray.get(i);
		}
		return res;
	}
}
