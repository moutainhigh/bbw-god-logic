package com.bbw.god.detail.async;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.db.entity.UserSpecialCardEntity;
import com.bbw.god.db.service.UserSpecialCardService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.CardSkillPosEnum;
import com.bbw.god.gameuser.card.UserCard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 玩家卡牌打技能存储
 *
 * @author: suhq
 * @date: 2021/12/16 1:52 下午
 */
@Slf4j
@Async
@Component
public class UserSpecialCardAsyncHandler {

	/**
	 * 记录明细
	 *
	 * @param userCard
	 * @param gid
	 * @param lv
	 * @param sid
	 * @param roleInfo
	 */
	public void log(UserCard userCard, int gid, int lv, int sid, GameUser.RoleInfo roleInfo) {
		try {
			UserSpecialCardService specialCardService = SpringContextUtil.getBean(UserSpecialCardService.class);
			UserCard.UserCardStrengthenInfo strengthenInfo = userCard.getStrengthenInfo();
			EntityWrapper<UserSpecialCardEntity> ew = new EntityWrapper<>();
			ew.eq("uid", userCard.getGameUserId()).eq("card_id", userCard.getBaseId());
			if (strengthenInfo == null || strengthenInfo.ifUseSkillScroll()) {
				//全部技能都重置了
				specialCardService.delete(ew);
				return;
			}
			UserSpecialCardEntity specialCardEntity = specialCardService.selectOne(ew);
			if (specialCardEntity == null) {
				specialCardEntity = UserSpecialCardEntity.instance(userCard, gid, sid);
				specialCardEntity.setLv(lv);
				specialCardEntity.updateRoleInfo(roleInfo);
				specialCardService.insert(specialCardEntity);
				return;
			}
			specialCardEntity.setLv(lv);
			specialCardEntity.updateRoleInfo(roleInfo);
			specialCardEntity.updateSkill(strengthenInfo);
			specialCardService.updateById(specialCardEntity);
		} catch (Exception e) {
			log.error("卡牌炼技 信息更新失败！");
			log.error(e.getMessage(), e);
		}
	}

}
