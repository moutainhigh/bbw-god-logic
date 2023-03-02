package com.bbw.god.game.chanjie;

import com.bbw.exception.CoderException;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
* @author lwb  
* @date 2019年6月14日  
* @version 1.0  
*/
@Getter
@AllArgsConstructor
public enum ChanjieType {
	OUT_STATE_FALSE(0,"参赛中"),
	OUT_STATE_TRUE(1,"已退出"),
	
	DATE_TYPE_NOMAL(10,"周一至周五"),
	DATE_TYPE_SAT(20,"周六"),
	DATE_TYPE_SUN(30,"周日"),
	
	Email_religious_victory(110,"胜不骄纵，赛季教派胜利"),
	Email_religious_defeat(120,"败不馁，赛季教派失败"),
	Email_invitation_letter(130,"乱斗封神邀请函"),
	Email_season_first(140,"荣登宝座,赛季第一名"),
	Email_thumbs_up(150,"教派奇人，点赞奖励"),
	Email_final_warad(160,"乱斗封神最终胜利"),
	
	Religious_CHAN(1010,"阐教"),
	Religious_JIE(1020,"截教"),
	
	Special_YRYY(2001,"游刃有余"),
	Special_RBKD(2001,"锐不可当"),
	Special_DDST(2001,"得道升天"),
	Special_TXZR(2001,"天选之人"),
	
	KEY_WIN_NUM(3001,"win"),
	KEY_FIRST_UID(3003,"first"),
	KEY_RANKING_ZSET(3004,"ranking"),
	KEY_FIGHT_LOG_ZSET(3005,"fightLogs"),
	KEY_SPECIAL_YRYY(3006,"yryy"),
	KEY_SPECIAL_TXZR(3007,"txzr"),
	KEY_SPECIAL_RBKD(3008,"rbkd"),
	KEY_SPECIAL_DDST(3009,"ddst"),
	KEY_RANKING_LOSER_ZSET(3010,"rankingLoser"),
	KEY_SUNDAY_GAME_STOP(3011,"gmstatus"),
	KEY_SUNDAY_GAME_people(3012,"gameNumber"),
	KEY_HASH_SETTELE(3013,"settle"),
	;
	private Integer value;
	private String memo;
	
	public static ChanjieType getType(Integer value) {
		for(ChanjieType t:values()) {
			if (t.getValue().equals(value)) {
				return t;
			}
		}
		throw CoderException.high("无效的阐截枚举类型");
	}
	
	public static String getSpecailName(ChanjieType key) {
		switch (key) {
			case KEY_SPECIAL_DDST :
				return Special_DDST.getMemo();
			case KEY_SPECIAL_RBKD:
				return Special_RBKD.getMemo();
			case KEY_SPECIAL_TXZR:
				return Special_TXZR.getMemo();
			case KEY_SPECIAL_YRYY:
				return Special_YRYY.getMemo();
			default :
				break;
		}
		throw CoderException.high("无效的阐截枚举类型");
	} 
}
