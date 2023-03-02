package com.bbw.god.db.entity;

import com.baomidou.mybatisplus.annotations.TableName;
import com.bbw.common.DateUtil;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatInfo;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.config.WorldType;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.server.ServerTool;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年9月30日 下午2:05:09
 * 类说明  PVE战斗结果明细
 */
@Data
@TableName("attack_city_strategy")
public class AttackCityStrategyEntity extends AbstractAttackStrategyEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer nightmare = 0;
	private Integer huweijun = 0;
	private Integer cityId;// 战斗地点Id
	private String city;// 战斗地点
	private Integer cityLv;//几级城


	public static AttackCityStrategyEntity getInstance(CombatInfo combatInfo, Combat combat, int gid, int sid, Integer seq) {
		Player user = combat.getP1();
		CfgCityEntity city = CityTool.getCityById(combatInfo.getCityId());
		AttackCityStrategyEntity strategyEntity = new AttackCityStrategyEntity();
		strategyEntity.setNightmare(combatInfo.getWorldType() != WorldType.NORMAL.getValue() ? 1 : 0);
		strategyEntity.setId(combat.getId());
		//结算后是反的
		strategyEntity.setServerPrefix(ServerTool.getServerShortName(sid));
		strategyEntity.setGid(gid);
		strategyEntity.setCity(city.getName());
		strategyEntity.setCityId(city.getId());
		strategyEntity.setCityLv(city.getLevel());
		strategyEntity.setUid(user.getUid());
		strategyEntity.setNickname(user.getName());
		strategyEntity.setHead(user.getImgId());
		strategyEntity.setIcon(user.getIconId());
		if (seq==null || seq==0){
			seq=1;
		}
		strategyEntity.setSeq(seq);
		strategyEntity.setLv(user.getLv());
		List<BattleCard> cards=combatInfo.getP1().getDrawCards();
		cards.addAll(combatInfo.getP1().getHandCardList());
		int specialCards=0;
		for (BattleCard card : cards) {
			if (card!=null && card.getIsUseSkillScroll()==1){
				specialCards++;
			}
		}
		strategyEntity.setCards(cards.size());
		strategyEntity.setSpecialCards(specialCards);
		strategyEntity.setUseWeapons(user.getWeaponsInUse().size());
		Player ai=combat.getP2();
		strategyEntity.setAiLv(ai.getLv());
		strategyEntity.setAiHead(ai.getImgId());
		strategyEntity.setAiNickname(ai.getName());
		strategyEntity.setHuweijun(ai.getName().equals("禁卫军")?0:1);
		strategyEntity.setRound(combat.getRound());
		strategyEntity.setResultType(combat.getCombatResultType().getVal());
		strategyEntity.setRecordedTime(DateUtil.now());
		strategyEntity.setRecordedDate(DateUtil.toDateInt(strategyEntity.getRecordedTime()));
		return strategyEntity;
	}
}
