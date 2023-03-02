package com.bbw.cache;

import java.util.List;

import org.junit.Test;

import com.bbw.god.db.entity.InsUserEntity;
import com.bbw.god.game.config.card.CfgCardEntity;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-04 17:23
 */
public class LocalCacheTest {

	@Test
	public void test() {
		LocalCache cache = LocalCache.getInstance();
		InsUserEntity user = new InsUserEntity();
		user.setUid(1000L);
		cache.put("user", user.getUid().toString(), user);
		InsUserEntity cacheUser = cache.get("user", user.getUid().toString());
		System.out.println(cacheUser);
		CfgCardEntity card1 = new CfgCardEntity();
		card1.setId(111);
		CfgCardEntity card2 = new CfgCardEntity();
		card2.setId(222);
		cache.put("card", card1.getId().toString(), card1);
		cache.put("card", card2.getId().toString(), card2);

		CfgCardEntity cacheCard = cache.get("card", card1.getId().toString());
		System.out.println(cacheCard);
		CfgCardEntity card3 = new CfgCardEntity();
		card3.setId(333);
		CfgCardEntity card4 = new CfgCardEntity();
		card4.setId(444);
		cache.put(card3.getId().toString(), card3);
		cache.put(card4.getId().toString(), card4);
		CfgCardEntity cacheCard3 = cache.get(card3.getId().toString());
		System.out.println(cacheCard3);
		CfgCardEntity cacheCard4 = cache.get(card4.getId().toString());
		System.out.println(cacheCard4);
		int count = 10000 * 10;
		for (int i = 0; i < count; i++) {
			CfgCardEntity card = new CfgCardEntity();
			card.setId(i);
			cache.put("card", card.getId().toString(), card);
		}
		long start = System.currentTimeMillis();
		List<CfgCardEntity> cards = cache.getTypeList("card");
		System.out.println("" + (System.currentTimeMillis() - start));
		cache.removeTimeOutCache();
	}

}
