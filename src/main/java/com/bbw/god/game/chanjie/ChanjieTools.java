package com.bbw.god.game.chanjie;

import com.bbw.common.DateUtil;
import com.bbw.god.game.config.server.ServerTool;

import java.util.Date;

/**
 * @author lwb
 * @date 2019年6月14日
 * @version 1.0
 */
public class ChanjieTools {

	public static final Integer BUY_BLOOD_GOLD = 50;// 购买血量所需元宝

	public static final Integer BUY_BLOOD_LIMIT = 2;// 每日血量限购次数

	public static final Integer BLOOD_VOLUME = 3;// 每日血量初始值

	public static final Integer LIKE_ADD_COPPER = 10000;// 点赞铜钱奖励

	public static final Integer JOIN_CHECK_NUM = 300;// 当人数大于该值时需要检测玩家加入的教派人数均衡问题

	public static final Integer FIGHT_HONOR_LV = 1;// 荣誉榜记录战斗击败最小等级 目前为渡劫地仙
	
	public static final Integer LOSER_HONER=1;//失败荣誉点加成
	
	// 外门弟子1 内门弟子2 真传弟子3 渡劫地仙4 大乘天仙5 大罗金仙6 护教法王7 掌教8
	public static final Integer FIGHT_LEADER_LV = 8;// 掌教
	public static final Integer FIGHT_XIAN_REN_LV = 2;// 仙人等级 内门弟子
	public static final String REDIS_RANKING_KEY = "chanjiezset";

	public static final String GAME_STAT_PLAYER = "game:chanjiePlayer:statistics";

	public static String getServerNamePrefix(int sid) {
		String serverName = ServerTool.getServer(sid).getName();
		return "s" + serverName.replaceAll("[^0-9]", "");
	}

	/**
	 * 获得分数 格式：原分数+1+6位时间参数 加一防止 分数为0时格式改变 <br>
	 * <font color="red">时间参数：当前时间距离周日23：59：5秒的时差</font>
	 * 
	 * @param point
	 * @return
	 */
	public static long getScore(long point) {
		String interval = DateUtil.getSecondsBetween(new Date(), DateUtil.getWeekEndDateTime(new Date())) + "";
		while (interval.length() < 6) {
			interval = "0" + interval;
		}
		return Long.parseLong(point + "1" + interval);
	}

	/**
	 * 获取真实分数
	 * 
	 * @param score
	 * @return
	 */
	public static Long getValueByScore(Long score) {
		String str = score + "";
		if (str.length() < 7) {
			return Long.parseLong(str);
		}
		str = str.substring(0, str.length() - 7);
		return Long.parseLong(str);
	}

	/**
	 * 获得本赛季的key
	 * 
	 * @param religious
	 * @param key
	 * @param gid
	 * @return
	 */
	public static String getZsetKey(ChanjieType religious, ChanjieType key, int gid) {
		return getKey(religious, gid) + ":" + key.getMemo();
	}

	/**
	 * 获得下一个新赛季的key
	 * 
	 * @param religious
	 * @param key
	 * @param gid
	 * @return
	 */
	public static String getNewSeasonZsetKey(ChanjieType religious, ChanjieType key, int gid) {
		String rid = "chan";
		if (religious == null) {
			rid = "sunday";
		} else if (religious.equals(ChanjieType.Religious_CHAN)) {
			rid = "chan";
		} else if (religious.equals(ChanjieType.Religious_JIE)) {
			rid = "jie";
		}
		Date d = DateUtil.addDays(DateUtil.getWeekEndDateTime(new Date()), 1);
		String dateStr = DateUtil.toDayString(d);
		return "game:" + gid + ":chanjie:" + dateStr + ":" + rid + ":" + key.getMemo();
	}

	/**
	 * 荣誉榜 存储位置：game:chanjie:周一日期:教派:今日:fightlog
	 * 
	 * @param religious
	 * @return
	 */
	public static String getDailyKey(ChanjieType religious, Date date, ChanjieType key, int gid) {
		return getKey(religious, gid) + ":" + DateUtil.toDateInt(date) + ":" + key.getMemo();
	}

	public static String getSpecailMapKey(Date date, ChanjieType key, boolean isValue) {
		if (isValue) {
			return key.getMemo() + DateUtil.toDayString(date) + "val";
		}
		return key.getMemo() + DateUtil.toDayString(date);
	}

	public static String getKey(ChanjieType religious, int gid) {
		String rid = "chan";
		if (religious == null) {
			rid = "sunday";
		} else if (religious.equals(ChanjieType.Religious_CHAN)) {
			rid = "chan";
		} else if (religious.equals(ChanjieType.Religious_JIE)) {
			rid = "jie";
		}
		Date d = DateUtil.getWeekBeginDateTime(new Date());
		String dateStr = DateUtil.toDayString(d);
		return "game:" + gid + ":chanjie:" + dateStr + ":" + rid;
	}

	public static String getNewSeasonKey(ChanjieType religious, int gid) {
		String rid = "chan";
		if (religious == null) {
			rid = "sunday";
		} else if (religious.equals(ChanjieType.Religious_CHAN)) {
			rid = "chan";
		} else if (religious.equals(ChanjieType.Religious_JIE)) {
			rid = "jie";
		}
		Date d = DateUtil.addDays(DateUtil.getWeekEndDateTime(new Date()), 1);
		String dateStr = DateUtil.toDayString(d);
		return "game:" + gid + ":chanjie:" + dateStr + ":" + rid;
	}

	/**
	 * 获取当前赛季ID
	 * 
	 * @return
	 */
	public static Integer getNowSeason() {
		return DateUtil.toDateInt(DateUtil.getWeekBeginDateTime(new Date()));
	}

	/**
	 * 获取下赛季ID
	 * 
	 * @return
	 */
	public static Integer getNextSeason() {
		Date nextMonday = DateUtil.addDays(DateUtil.getThisWeekEndDateTime(), 1);
		return DateUtil.toDateInt(nextMonday);
	}

	/**
	 * 获取上赛季ID
	 * 
	 * @return
	 */
	public static Integer getPreSeason() {
		Date nextMonday = DateUtil.addDays(DateUtil.getThisWeekBeginDateTime(), -7);
		return DateUtil.toDateInt(nextMonday);
	}
}
