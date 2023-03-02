package com.bbw.god.login.repairdata;

import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.biyoupalace.BYPalaceService;
import com.bbw.god.gameuser.biyoupalace.UserBYPalaceLockSkill;
import com.bbw.god.gameuser.biyoupalace.cfg.BYPalaceTool;
import com.bbw.god.gameuser.biyoupalace.cfg.CfgBYPalaceSkillEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.bbw.god.login.repairdata.RepairDataConst.SKILL_CHANGE_07V2;
import static com.bbw.god.login.repairdata.RepairDataConst.SKILL_CHANGE_DATE;

/**
 * @author suchaobin
 * @description 修复碧游宫数据
 * @date 2020/7/7 15:10
 **/
@Service
public class RepairByPalaceService implements BaseRepairDataService {
	@Autowired
	private BYPalaceService byPalaceService;
	@Autowired
	private GameUserService gameUserService;

	@Override
	public void repair(GameUser gu, Date lastLoginDate) {
		// 碧游宫的全-龙息改为全-封咒，已达成的跳过
		if (lastLoginDate.before(SKILL_CHANGE_DATE)) {
			byPalaceService.changeUserSkillScroll(gu.getId(), 21167, 21162);
			Date regTime = gu.getRoleInfo().getRegTime();
			// 老玩家碧游宫解锁以前所有技能
			if (regTime.before(SKILL_CHANGE_DATE)) {
				UserBYPalaceLockSkill lockSkill = gameUserService.getSingleItem(gu.getId(),
						UserBYPalaceLockSkill.class);
				if (lockSkill == null) {
					lockSkill = UserBYPalaceLockSkill.instance(gu.getId());
					gameUserService.addItem(gu.getId(), lockSkill);
				}
				List<CfgBYPalaceSkillEntity> skillList = BYPalaceTool.getBYPSkillEntityList();
				for (CfgBYPalaceSkillEntity cfgBYPalaceSkillEntity : skillList) {
					List<String> skills = cfgBYPalaceSkillEntity.getSkills();
					for (String skill : skills) {
						Integer treasureId = TreasureTool.getTreasureByName(skill).getId();
						if (treasureId > 21168) {
							continue;
						}
						lockSkill.addSkill(treasureId, cfgBYPalaceSkillEntity.getChapter());
					}
				}
				gameUserService.updateItem(lockSkill);
			}
		}
		if (lastLoginDate.before(SKILL_CHANGE_07V2)) {
			//密传 双狙=》超度
			byPalaceService.changeUserSkillScroll(gu.getId(), 21182, 21159);
		}
	}
}
