package com.bbw.god.game.config.treasure;

import java.util.List;

import org.junit.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bbw.god.game.config.Cfg;

public class TreasureToolTest {

	@Test
	public void test() {
		List<CfgSkillScrollLimitEntity> skillScrollLimitEntities = Cfg.I.get(CfgSkillScrollLimitEntity.class);
		JSONArray jsonArray = new JSONArray();
		skillScrollLimitEntities.forEach(tmp -> {
			JSONObject jObject = new JSONObject(true);
			jObject.put("id", tmp.getId());
			jObject.put("name", tmp.getName());
			jObject.put("skillId", tmp.getSkillId());
			jObject.put("limitTypes", tmp.getLimitTypes());
			jObject.put("limitCards", tmp.getLimitCards());
			jObject.put("limitSkills", tmp.getLimitSkills());
			jObject.put("limitLevels", tmp.getLimitLevels());
			jsonArray.add(jObject);
		});
		System.out.println(jsonArray);
	}

}
