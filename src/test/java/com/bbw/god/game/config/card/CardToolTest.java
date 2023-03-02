package com.bbw.god.game.config.card;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bbw.BaseTest;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.TypeEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class CardToolTest extends BaseTest {

	@Test
	public void test() {
		List<CfgCardEntity> cards = Cfg.I.get(CfgCardEntity.class);
		JSONArray jsonArray = new JSONArray();
		cards.forEach(tmp -> {
			JSONObject jObject = new JSONObject(true);
			jObject.put("id", tmp.getId());
			jObject.put("name", tmp.getName());
			jObject.put("type", tmp.getType());
			jObject.put("star", tmp.getStar());
			jObject.put("group", tmp.getGroup() == null ? "" : tmp.getGroup());
			jObject.put("attack", tmp.getAttack());
			jObject.put("hp", tmp.getHp());
			jObject.put("zero_skill", tmp.getZeroSkill());
			jObject.put("five_skill", tmp.getFiveSkill());
			jObject.put("ten_skill", tmp.getTenSkill());
			jObject.put("comment", tmp.getComment());
			jsonArray.add(jObject);
		});
		System.out.println(jsonArray);
	}

	@Before
	public void beforTest() {
		Cfg.I.loadAllConfig();
	}

	@After
	public void afterTest() {
		System.out.println("test over");
	}

	@Test
	public void getRandomCardByTypeStar() {
		for (int i = 0; i < 10000; i++) {
			CfgCardEntity randomCardByTypeStar = CardTool.getRandomCardByTypeStar(TypeEnum.Wood, 1, Arrays.asList(257));
			if (randomCardByTypeStar.getId() == 257) {
				System.out.println(randomCardByTypeStar);
			}
		}
	}
}
