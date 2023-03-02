package com.bbw.god.game.combat.data.param;

import com.bbw.common.SpringContextUtil;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.yuxg.*;
import com.bbw.god.gameuser.yuxg.cfg.CfgFuTuEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 卡牌升级参数
 *
 * @author suhq
 * @date 2018年11月6日 下午2:08:07
 */
@Data
@NoArgsConstructor
public class CPCardGroup {
	private static UserYuXGService userYuXGService = SpringContextUtil.getBean(UserYuXGService.class);
	private static YuXGService yuXGService = SpringContextUtil.getBean(YuXGService.class);
	private Long uid = 0L;
	/** 角色血量 */
	private Integer hp = 0;
	/** 战斗buff **/
	private List<CombatBuff> buffs = new ArrayList<>();
	/** 战斗卡牌 */
	private List<CCardParam> cards = new ArrayList<>();

	/**
	 * 设置卡牌参数
	 *
	 * @return
	 */
	public static CPCardGroup getInstance(long uid, int fuCeId, List<CCardParam> cardParams) {
		CPCardGroup cpCardGroup = new CPCardGroup();
		cpCardGroup.setUid(uid);
		cpCardGroup.setCards(cardParams);
		cpCardGroup.setFutus(fuCeId);
		return cpCardGroup;
	}

	/**
	 * 设置卡牌参数
	 *
	 * @return
	 */
	public static CPCardGroup getInstance(long uid, List<CCardParam> cardParams) {
		CPCardGroup cpCardGroup = new CPCardGroup();
		cpCardGroup.setUid(uid);
		cpCardGroup.setCards(cardParams);
		return cpCardGroup;
	}

	/**
	 * 设置卡牌参数
	 *
	 * @return
	 */
	public static CPCardGroup getInstanceByUserCards(long uid, int fuCeId, List<UserCard> ucs) {
		CPCardGroup cpCardGroup = new CPCardGroup();
		for (UserCard card : ucs) {
			cpCardGroup.getCards().add(CCardParam.init(card.getBaseId(), card.getLevel(), card.getHierarchy(), card.getStrengthenInfo()));
		}
		cpCardGroup.setUid(uid);
		cpCardGroup.setFutus(fuCeId);
		return cpCardGroup;
	}

	/**
	 * 设置卡牌参数
	 *
	 * @return
	 */
	public static CPCardGroup getInstanceByUserCards(long uid, List<UserCard> ucs) {
		CPCardGroup cpCardGroup = new CPCardGroup();
		for (UserCard card : ucs) {
			cpCardGroup.getCards().add(CCardParam.init(card.getBaseId(), card.getLevel(), card.getHierarchy(), card.getStrengthenInfo()));
		}
		cpCardGroup.setUid(uid);
		return cpCardGroup;
	}

	/**
	 * 战斗buff
	 *
	 * @return
	 */
	private void setFutus(int fuCeId) {
		List<CombatBuff> combatBuffs = new ArrayList<>();
		if (0 == fuCeId) {
			return;
		}
		List<UserFuCe.FuTu> fuTus = userYuXGService.getFuCe(uid, fuCeId).getFuTus()
				.stream().filter(f -> f.getDataId() != 0).collect(Collectors.toList());
		//相同技能符图进行去重
		List<Integer> fuTuIds = YuXGTool.getAllFuTuInfos().stream().filter(f -> f.getType() == 40)
				.map(CfgFuTuEntity::getFuTuId).collect(Collectors.toList());
		for (UserFuCe.FuTu fuTu : fuTus) {
			UserFuTu userFuTu = userYuXGService.getUserFuTu(uid, fuTu.getDataId());
			long sameSkillFuTuNum = combatBuffs.stream().filter(c -> fuTuIds.contains(c.getRuneId()) &&
					c.getRuneId() == userFuTu.getBaseId()).count();
			if (sameSkillFuTuNum >= 1) {
				continue;
			}
			//获得符图槽加成
			int fuTuSlotExtraRate = yuXGService.getFuTuSlotRate(uid, fuTu.getPos());
			combatBuffs.add(new CombatBuff(userFuTu.getBaseId(), userFuTu.getLv(), fuTuSlotExtraRate));
		}
		buffs = combatBuffs;
	}

}
