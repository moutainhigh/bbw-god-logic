package com.bbw.god.activityrank;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 冲榜活动类别
 *
 * @author suhq
 * @date 2019年3月2日 下午6:45:56
 */
@Getter
@AllArgsConstructor
public enum ActivityRankEnum {

	RECHARGE_RANK("充值榜", "recharge", 10000, false, false),
	FUHAO_RANK("富豪榜", "copper", 10010, false, false),
	ELE_CONSUME_RANK("元素消耗榜", "eleconsume", 10050, false, false),
	WIN_BOX_RANK("胜利宝箱", "winbox", 10060, false, false),
	CHENGCHI_RANK("攻城榜", "owncity", 10070, false, false),
	GU_LEVEL_RANK("等级榜", "gulevel", 10080, false, false),
	EXPEDITION_RANK("远征榜", "expedition", 10090, false, false),
	FST_RANK("封神台冲榜", "fst", 10100, false, false),
	FIGHT_RANK("战斗荣耀榜", "fight", 10110, false, false),
	CONSUME_RANK("材料消耗榜", "consume", 10120, false, false),
	TRAVEL_RANK("商周游历榜", "businessTravel", 10130, false, false),
	ATTACK_RANK("王者之路榜", "attack", 10140, false, false),
	XIAN_YUAN_RANK("仙缘周总榜", "xianyuan", 11010, false, false),
	XIAN_YUAN_DAY_RANK("仙缘每日榜", "xianyuanDay", 11020, true, false),
	GOLD_CONSUME_RANK("元宝消耗总榜", "gold", 11030, false, true),
	GOLD_CONSUME_DAY_RANK("元宝消耗每日榜", "goldDay", 11040, true, false),
	KILL_DEMON_RANK("除魔卫道榜", "killDemon", 11050, false, true),
	KILL_DEMON_DAY_RANK("除魔卫道每日榜", "killDemonDay", 11060, true, false),
	WINTER_RANK("凛冬跨服总榜", "winter", 11070, false, true),
	WINTER_DAY_RANK("凛冬跨服每日榜", "winterDay", 11080, true, false),
	MONEY_DRAWING_RANK("招财进宝跨服总榜", "moneyDrawing", 11090, false, true),
	MONEY_DRAWING_DAY_RANK("招财进宝跨服每日榜", "moneyDrawingDay", 11100, true, false),
	CELEBRATION_RANK("庆典跨服榜", "celebration", 11110, false, true),
	CELEBRATION_DAY_RANK("庆典跨服每日榜", "celebrationDay", 11120, true, false),
	LOVE_VALUE_RANK("情缘值榜", "charisma", 11130, false, true),
	LOVE_VALUE_DAY_RANK("情缘值每日榜", "charismaDay", 11140, true, false),
	CHARM_RANK("魅力值榜", "harvestFlowers", 11150, false, true),
	COMBINED_SERVICE_GOLD_CONSUME_RANK("合服-元宝消耗总榜", "CombinedServiceGold", 11160, false, true),
	GUESSING_COMPETITION_DAY_RANK("竞猜跨服每日榜", "guessingCompetitionDay", 11170, true, false),
	GUESSING_COMPETITION_RANK("竞猜跨服总榜", "guessingCompetition", 11180, false, true),
	CAREFREE_DAY_RANK("逍遥跨服每日榜", "carefreeDay", 11190, true, false),
	CAREFREE_RANK("逍遥跨服总榜", "carefree", 11200, false, true),

	;

	private final String name;
	private final String redisKey;
	private final int value;
	private final boolean isDayRank;
	private final boolean isShowAfterEnd;

	public static ActivityRankEnum fromValue(int value) {
		for (ActivityRankEnum item : values()) {
			if (item.getValue() == value) {
				return item;
			}
		}
		return null;
	}
}