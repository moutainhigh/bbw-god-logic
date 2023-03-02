package com.bbw.god.gameuser.biyoupalace;

import com.bbw.BaseTest;
import com.bbw.common.PowerRandom;
import com.bbw.god.gameuser.biyoupalace.cfg.BYPalaceTool;
import com.bbw.god.gameuser.biyoupalace.cfg.CfgBYPalaceSkillEntity;
import com.bbw.god.gameuser.biyoupalace.cfg.CfgBYPalaceWeightEntity;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BYPalaceLogicTest extends BaseTest {


	@Test
	public void test() {
		Map<String,Integer> map=new HashMap<>();
		CfgBYPalaceSkillEntity byPalaceSkillEntity = BYPalaceTool.getBYPSkillEntity(60, 98);
		List<CfgBYPalaceWeightEntity> weightEntities= BYPalaceTool.getWeightEntity(60);
		List<CfgBYPalaceWeightEntity> weightValid=weightEntities.stream().filter(p->byPalaceSkillEntity.getSkills().contains(p.getName())).collect(Collectors.toList());
		List<Integer> weight=weightValid.stream().map(CfgBYPalaceWeightEntity::getWeight).collect(Collectors.toList());
		int sumSeed=weight.stream().collect(Collectors.summingInt(Integer::intValue));
		for (int i=0;i<1000;i++){
			int indexByWeight= PowerRandom.getIndexByProbs(weight, sumSeed);
			String awardName = weightValid.get(indexByWeight).getName();
			if (map.get(awardName)==null){
				map.put(awardName,1);
			}else {
				map.put(awardName,map.get(awardName)+1);
			}
		}

		for (CfgBYPalaceWeightEntity cb:weightEntities){
			int bf=0;
			if (map.get(cb.getName())!=null){
				bf=map.get(cb.getName());
			}
			System.err.println(cb.getName()+":权重"+cb.getWeight()+"---比例："+(cb.getWeight()*100.0/sumSeed)+"---实际："+bf);
		}

	}

}
