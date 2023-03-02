package com.bbw.god.game.data.redis;

import org.springframework.lang.NonNull;

import com.bbw.exception.CoderException;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataType;

/**
 * 全服统一的redis键值
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-01-14 21:33
 */
public class GameRedisKey extends RedisKeyConst {
	public static final String PREFIX = "game";

	/**
	 * 程序员运行时临时变量
	 * 
	 * @param businessKey
	 * @return
	 */
	@NonNull
	public static String getRunTimeVarKey(String businessKey) {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX);
		sb.append(SPLIT);
		sb.append("var");
		sb.append(SPLIT);
		sb.append(businessKey);
		return sb.toString();//PREFIX + SPLIT + "var" + SPLIT + businessKey;
	}

	/**
	 * 获取的redis的基础key。格式：“game:数据类型:具体业务”
	 * 
	 * @param dataType
	 * @param business
	 * @return
	 */
	public static String getDataTypeKey(GameDataType dataType, String... business) {
		StringBuilder sb = getDataTypeKey(dataType);
//		String key = PREFIX + SPLIT + dataType.getRedisKey();
		if (0 == business.length) {
			return sb.toString();
		}
		for (String s : business) {
			sb.append(SPLIT);
			sb.append(s);
//			key += SPLIT + s;
		}
		return sb.toString();
	}
	/**
	 * 获取的redis的基础key。格式：“game:数据类型:平台:具体业务”
	 *
	 * @param dataType
	 * @param business
	 * @return
	 */
	public static String getDataTypeKey(GameDataType dataType, int gid,String business) {
		StringBuilder sb = getDataTypeKey(dataType);
		sb.append(SPLIT);
		sb.append(gid);
		sb.append(SPLIT);
		sb.append(business);
//		String key = PREFIX + SPLIT + dataType.getRedisKey()+gid+business;
		return sb.toString();
	}

	/**
	 * 获取的redis的基础key。格式：“game:平台:具体业务”
	 * @param gid
	 * @param businessKey
	 * @return
	 */
	public static String getDataTypeKey(int gid,String businessKey) {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX);
		sb.append(SPLIT);
		sb.append(gid);
		sb.append(SPLIT);
		sb.append(businessKey);
		return sb.toString();//PREFIX + SPLIT + gid+ SPLIT + businessKey;
	}

	/**
	 * 格式：“game:数据类型”
	 * 
	 * @param dataType
	 * @return
	 */
	private static StringBuilder getDataTypeKey(GameDataType dataType) {
		StringBuilder sb = new StringBuilder();
		sb.append(PREFIX);
		sb.append(SPLIT);
		sb.append(dataType.getRedisKey());
//		String key = PREFIX + SPLIT + data.gainDataType().getRedisKey();
		return sb;
	}

	/**
	 * 获取GameData的redis的key。格式：“game:数据类型:资源ID”
	 * 
	 * @param data
	 * @return
	 */
	public static String getGameDataKey(GameData data) {
		if (null == data.gainDataType()) {
			String msg = data.getClass().getSimpleName() + "对象未设置区服数据类型！";
			CoderException.high(msg);
		}
		return getGameDataKey(data.gainDataType(), data.getId());
//		StringBuilder sb = getDataTypeKey(data.gainDataType());
//		sb.append(SPLIT);
//		sb.append(data.getId());
//		String key = getDataTypeKey(data) + SPLIT + data.getId();
//		return sb.toString();
	}

	/**
	 * 获取GameData的redis的key。格式：“game:数据类型:资源ID”
	 * 
	 * @param dataType
	 * @param dataId
	 * @return
	 */
	public static String getGameDataKey(GameDataType dataType, Long dataId) {
		StringBuilder sb = getDataTypeKey(dataType);
		sb.append(SPLIT);
		sb.append(dataId);
//		String key = PREFIX + SPLIT + dataType.getRedisKey() + SPLIT + dataId;
		return sb.toString();
	}

}
