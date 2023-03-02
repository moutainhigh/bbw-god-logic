package com.bbw.god.gameuser.card;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * 卡牌编组用途
 * 
 * @author suhq
 * @date 2019-09-06 10:22:55
 */
@Getter
@AllArgsConstructor
public enum CardGroupWay {

	Normal_Fight("常规战斗", 1),
	FIERCE_FIGHTING_ATTACK("激战进攻卡组",10),
	FIERCE_FIGHTING_DEFENSE("激战防御卡组", 11),
	DFDJ_FIGHT("巅峰对决", 12),
	// Maou("魔王", 2);
	GAME_FST_ATTACK1("激战进攻卡组1", 21),
	GAME_FST_ATTACK2("激战进攻卡组2", 22),
	GAME_FST_ATTACK3("激战进攻卡组3", 23),

	GAME_FST_DEFENSE1("激战防御卡组1", 31),
	GAME_FST_DEFENSE2("激战防御卡组2", 32),
	GAME_FST_DEFENSE3("激战防御卡组3", 33),

	NIGHTMARE_JIN_WEI("梦魇攻打禁卫军卡组", 41),
	NIGHTMARE_HU_WEI("梦魇攻打护卫军卡组", 42),
	YAO_ZU_MIRRORING("妖族来犯镜像卡组", 43),
	YAO_ZU_ONTOLOGY("妖族来犯镜本体卡组", 44),
	TRANSMIGRATION("轮回世界卡组", 45),
	;
	private String name;
	private int value;

	public static CardGroupWay fromValue(int value) {
		for (CardGroupWay item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}

	/**
	 * 是否属于激战卡组
	 * @param way
	 * @return
	 */
	public static boolean inFierceFighting(CardGroupWay way){
		switch (way){
			case FIERCE_FIGHTING_ATTACK:
			case FIERCE_FIGHTING_DEFENSE:
			case GAME_FST_ATTACK1:
			case GAME_FST_ATTACK2:
			case GAME_FST_ATTACK3:
			case GAME_FST_DEFENSE1:
			case GAME_FST_DEFENSE2:
			case GAME_FST_DEFENSE3:
				return true;
			default:return false;
		}
	}
	
	public static List<CardGroupWay> getGameFstWays(){
		List<CardGroupWay> ways=new ArrayList<>();
		ways.add(GAME_FST_ATTACK1);
		ways.add(GAME_FST_ATTACK2);
		ways.add(GAME_FST_ATTACK3);
		ways.add(GAME_FST_DEFENSE1);
		ways.add(GAME_FST_DEFENSE2);
		ways.add(GAME_FST_DEFENSE3);
		return ways;
	}
}
