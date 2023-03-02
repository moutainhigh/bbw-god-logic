package com.bbw.god.uac;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.bbw.god.uac.entity.BasePlatEntity;
import com.bbw.god.uac.entity.PacksEntity;

import lombok.Synchronized;

/**
 * 配置缓存，这里只能缓存系统配置，
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018年5月30日 下午3:20:01
 */
public class GodUACCache {
	//目前渠道总条目50+条，指定列表容量100。省得产生扩容动作。
	public static ArrayList<BasePlatEntity> allBasePlatEntity = new ArrayList<BasePlatEntity>();
	//礼包
	public static ArrayList<PacksEntity> packsCache = new ArrayList<PacksEntity>();
	//设备id
	public static ConcurrentHashMap<String, String> deviceIds = new ConcurrentHashMap<String, String>();
	//微信每周礼包领取状态(key,缓存终止日期)
	public static ConcurrentHashMap<String, String> wechatCode = new ConcurrentHashMap<String, String>();
	//区服账号和uid映射，key=sid#account
	public static ConcurrentHashMap<String, String> roleIds = new ConcurrentHashMap<String, String>();

	@Synchronized
	public static void clearCache() {
		allBasePlatEntity.clear();
		packsCache.clear();
		deviceIds.clear();
		wechatCode.clear();
	}

	@Synchronized
	public static <T> void clear(List<T> list) {
		list.clear();
	}

	@Synchronized
	public static <T> void clear(Map<String, String> map) {
		map.clear();
	}

	@Synchronized
	public static <T> boolean add(List<T> list, T entity) {
		return list.add(entity);
	}

	@Synchronized
	public static <T> boolean addAll(List<T> list, List<T> entityList) {
		return list.addAll(entityList);
	}

	@Synchronized
	public static void put(Map<String, String> map, String key, String value) {
		map.put(key, value);
	}

	@Synchronized
	public static void remove(Map<String, String> map, String key, String value) {
		map.remove(key);
	}
}
