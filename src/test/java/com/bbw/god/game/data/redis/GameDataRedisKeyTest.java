package com.bbw.god.game.data.redis;

import org.junit.Test;

import com.bbw.god.game.data.GameDataType;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-03-14 16:31
 */
public class GameDataRedisKeyTest {

	@Test
	public void test() {
		System.out.println(GameRedisKey.getDataTypeKey(GameDataType.FLXRESULT));
		System.out.println(GameRedisKey.getDataTypeKey(GameDataType.FLXRESULT, "1"));
		System.out.println(GameRedisKey.getDataTypeKey(GameDataType.FLXRESULT, "1", "2"));
	}

}
