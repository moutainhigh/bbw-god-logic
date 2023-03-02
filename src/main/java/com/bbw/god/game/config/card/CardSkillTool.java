package com.bbw.god.game.config.card;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.config.Cfg;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-06-19 15:52
 */
@Slf4j
public class CardSkillTool {

	/**
	 * 根据ID获取卡牌技能
	 * @param id
	 * @return
	 */
	public static Optional<CfgCardSkill> getCardSkillOpById(int id) {
		List<CfgCardSkill> allCardSkills = getAllCardSkills();
		for (CfgCardSkill cardSkill:allCardSkills){
			if (cardSkill.getId()==id){
				return Optional.of(cardSkill);
			}
		}
		log.error("无效的技能ID:"+id+"---请检查【卡牌技能.yml】是否有配置该技能！");
		return Optional.empty();
	}

	/**
	 * 根据技能ID获取技能名字
	 * @param skillId
	 * @return
	 */
	public static String getSkillNameBySkillId(int skillId) {
		Optional<CfgCardSkill> op = getCardSkillOpById(skillId);
		if (op.isPresent()){
			return op.get().getName();
		}
		throw new ExceptionForClientTip("card.skill.id.not.exist",skillId);
	}
	/**
	 * 根据名字获取技能
	 * @param name
	 * @return
	 */
	public static Optional<CfgCardSkill> getCardSkillOpByName(String name) {
		List<CfgCardSkill> allCardSkills = getAllCardSkills();
		for (CfgCardSkill cardSkill:allCardSkills){
			if (cardSkill.getName().equals(name)){
				return Optional.of(cardSkill);
			}
		}
		return Optional.empty();
	}

	/**
	 * 根据名字获取技能  不存在 抛出异常提示
	 * @param name
	 * @return
	 */
	public static CfgCardSkill getCardSkillByName(String name) {
		Optional<CfgCardSkill> op = getCardSkillOpByName(name);
		if (op.isPresent()){
			return op.get();
		}
		throw new ExceptionForClientTip("card.skill.name.not.exist",name);
	}

	/**
	 * 存在则返回技能ID  不存在则返回0
	 * @param name
	 * @return
	 */
	public static int getSkillIdByName(String name) {
		Optional<CfgCardSkill> op = getCardSkillOpByName(name);
		if (op.isPresent()){
			return op.get().getId();
		}
		return 0;
	}

	/**
	 * 获取所有技能
	 * @return
	 */
	public static List<CfgCardSkill> getAllCardSkills(){
		return Cfg.I.get(CfgCardSkill.class);
	}

	/**
	 * 获取法外分身技能分类
	 * @return
	 */
	public static Map<LeaderCardSkillGroupEnum,List<Integer>> getLeaderCardSkillGroups(){
		List<CfgCardSkill> allCardSkills = getAllCardSkills();
		Map<LeaderCardSkillGroupEnum,List<Integer>> map=new HashMap<>();
		for (LeaderCardSkillGroupEnum type : LeaderCardSkillGroupEnum.values()) {
			List<Integer> collect = allCardSkills.stream().filter(p -> p.getLeaderCardSkillGroup().intValue() == type.getType()).map(CfgCardSkill::getId).collect(Collectors.toList());
			map.put(type,collect);
		}
		return map;
	}
}
