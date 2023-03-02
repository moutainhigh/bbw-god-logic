package com.bbw.god.statistics;

import java.io.Serializable;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.GameUser;

import lombok.Data;

/**
 * 记录区服的统计数据
 * 
 * @author suhq
 * @date 2019-07-10 10:50:56
 */
@Data
public class ServerStatistic implements Serializable {
	private static final long serialVersionUID = 1L;
	protected Integer sid;// 区服ID
	private String key;
	private Long uid;// 玩家ID
	private Integer level;// 玩家达成时的等级
	private String pos;// 位置
	private Integer date;// 日期

	public static ServerStatistic instance(StatisticKeyEnum key, GameUser gu) {
		ServerStatistic ss = new ServerStatistic();
		ss.setSid(gu.getServerId());
		ss.setKey(key.getKey());
		ss.setUid(gu.getId());
		ss.setLevel(gu.getLevel());
		ss.setPos(gu.gainCurCity().getName());
		ss.setDate(DateUtil.getTodayInt());
		return ss;
	}
}
