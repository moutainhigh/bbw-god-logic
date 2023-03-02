package com.bbw.god.game.combat;

import com.bbw.exception.CoderException;
import com.bbw.god.game.combat.group.GroupSkillService;
import com.bbw.god.game.combat.skill.service.ISkillDefenseService;
import com.bbw.god.game.combat.skill.service.ISkillNormalBuffDefenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 战斗服务工厂
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 13:50
 */
@Service
public class AttackServiceFactory {
	@Lazy
	@Autowired
	private List<GroupSkillService> groupServices;
	@Lazy
	@Autowired
	private List<BattleSkillService> skillServices;
	@Lazy
	@Autowired
	private List<ISkillDefenseService> battleSkillDefenseServices;
	@Lazy
	@Autowired
	private List<ISkillNormalBuffDefenseService> normalBuffDefenseServices;

	public GroupSkillService getGroupAttackService(int groupId) {
		for (GroupSkillService groupAttackService : groupServices) {
			if (groupAttackService.match(groupId)) {
				return groupAttackService;
			}
		}
		throw CoderException.high("程序员没有编写组合ID=" + groupId + "的服务程序！");
	}

	public BattleSkillService getSkillAttackService(int skillId) {
		for (BattleSkillService service : skillServices) {
			if (service.match(skillId)) {
				return service;
			}
		}
		throw CoderException.high("程序员没有编写技能ID=" + skillId + "的服务程序！");
	}

	/**
	 * 获取防御的实现类
	 * @param skillId
	 * @return
	 */
	public ISkillDefenseService getBattleSkillDefenseService(int skillId) {
		for (ISkillDefenseService service : battleSkillDefenseServices) {
			if (service.match(skillId)) {
				return service;
			}
		}
		throw CoderException.high("程序员没有编写技能ID=" + skillId + "的服务程序！");
	}

	/**
	 * 获取物理BUFF防御的实现类
	 * @param skillId
	 * @return
	 */
	public ISkillNormalBuffDefenseService getNormalBuffDefenseService(int skillId) {
		for (ISkillNormalBuffDefenseService service : normalBuffDefenseServices) {
			if (service.match(skillId)) {
				return service;
			}
		}
		throw CoderException.high("程序员没有编写技能ID=" + skillId + "的服务程序！");
	}
}
