package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.game.config.treasure.TreasureType;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.biyoupalace.cfg.BYPalaceTool;
import com.bbw.god.gameuser.biyoupalace.cfg.CfgBYPalaceSkillEntity;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 传奇卷轴宝箱处理器
 * @date 2020/3/10 16:31
 */
@Service
public class ChuanQJZBoxProcessor extends TreasureUseProcessor {
	private static final ChuanQJZBoxAward BOX_AWARD = ChuanQJZBoxAward.getInstance();

	public ChuanQJZBoxProcessor() {
		this.treasureEnum = TreasureEnum.LEGEND_SKILL_SCROLL_BOX;
		this.isAutoBuy = false;
	}

	/**
	 * 是否宝箱类
	 *
	 * @return
	 */
	@Override
	public boolean isChestType() {
		return true;
	}

	@Override
	public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
		int skillLevel = getSkillLevelByProbability();
		List<Integer> awardIds = new ArrayList<>();
		switch (skillLevel) {
			case 3:
				awardIds = BOX_AWARD.getAwardIds_3();
				break;
			case 4:
				awardIds = BOX_AWARD.getAwardIds_4();
				break;
			case 5:
				awardIds = BOX_AWARD.getAwardIds_5();
				break;
			default:
				break;
		}
		Integer treasureId = PowerRandom.getRandomFromList(awardIds);
		TreasureEventPublisher.pubTAddEvent(gu.getId(), treasureId, 1, WayEnum.TREASURE_USE, new RDCommon());
		rd.addTreasure(new RDCommon.RDTreasureInfo(treasureId, 1,
				TreasureType.SKILL_SCROLL.getValue()));
	}

	/**
	 * 通过概率获取技能等级
	 *
	 * @return
	 */
	private int getSkillLevelByProbability() {
		int random = PowerRandom.getRandomBySeed(100);
		if (random <= 50) {
			return 3;
		}
		if (random <= 80) {
			return 4;
		}
		return 5;
	}

	@Data
	public static class ChuanQJZBoxAward {
		private List<Integer> awardIds_3;
		private List<Integer> awardIds_4;
		private List<Integer> awardIds_5;

		protected static ChuanQJZBoxAward getInstance() {
			ChuanQJZBoxAward boxAward = new ChuanQJZBoxAward();
			boxAward.setAwardIds_3(getAwardIds(3));
			boxAward.setAwardIds_4(getAwardIds(4));
			boxAward.setAwardIds_5(getAwardIds(5));
			return boxAward;
		}
	}

	public static List<Integer> getAwardIds(int level) {
		List<Integer> awardIds = new ArrayList<>();
		List<CfgBYPalaceSkillEntity> skillEntities = BYPalaceTool.getBYPSkillEntityList().stream()
				.filter(b -> b.getChapter() == level).collect(Collectors.toList());
		for (CfgBYPalaceSkillEntity skillEntity : skillEntities) {
			List<String> skills = skillEntity.getSkills();
			for (String skill : skills) {
				CfgTreasureEntity treasure = TreasureTool.getTreasureByName(skill);
				awardIds.add(treasure.getId());
			}
		}
		awardIds = awardIds.stream().distinct().collect(Collectors.toList());
		// 不包括 火-混元
		List<Integer> byPalaceExclusiveSkills = BYPalaceTool.getBYPalaceExclusiveSkills();
		awardIds.removeIf(byPalaceExclusiveSkills::contains);
		return awardIds;
	}
}
