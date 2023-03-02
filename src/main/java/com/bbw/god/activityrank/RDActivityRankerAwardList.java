package com.bbw.god.activityrank;

import com.bbw.god.db.entity.CfgActivityRankEntity;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 冲榜活动排名信息
 *
 * @author suhq
 * @date 2019年3月5日 下午6:18:25
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDActivityRankerAwardList extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer activityType = null;// 冲榜类型
	private Long remainTime = null;// 剩余时间
	private String activityTime = null;// 时间范围
	private List<RDActivityRankerAward> rankersAwards = null;// 奖励
	private Integer myRank = 0;// 我的排名
	private Integer myValue = 0;// 我的数值
	private Integer firstValue = 0;// 第一名的数值
	private Integer beforerValue = 0;// 前一名的数值
	private List<RDActivityRanker> rankers = new ArrayList<>();
	private Integer myLastRank = null;// 我上次的排名
	private Integer totalSize = 0;// 排行榜总人数

	/**
	 * 单个冲榜奖励的数据
	 * 
	 * @author suhq
	 * @date 2019年3月7日 下午5:18:30
	 */
	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RDActivityRankerAward implements Serializable {
		private static final long serialVersionUID = 1L;
		private Integer maxLevel = null;// 最大排名
		private Integer minLevel = null;// 最小排名
		private List<RDAward> rankerAwards = null;// 奖励

		public RDActivityRankerAward(CfgActivityRankEntity activity, List<RDAward> rankerAwards) {
			this.maxLevel = activity.getMaxRank();
			this.minLevel = activity.getMinRank();
			this.rankerAwards = rankerAwards;
		}
	}

	/**
	 * 单个冲榜玩家的数据
	 * 
	 * @author suhq
	 * @date 2019年3月7日 下午5:18:30
	 */
	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RDActivityRanker implements Serializable {
		private static final long serialVersionUID = 1L;
		private String nickname = null;
		private String server = null;
		private Integer head = null;
		private Integer iconId=null;
		private Integer level = null;
		private Integer rank = null;// 排名
		private Integer value = null;// 在榜单中的数值

		public RDActivityRanker(String nickname, Integer head, Integer level, Integer rank, Integer value,Integer icon) {
			this.nickname = nickname;
			this.head = head;
			this.level = level;
			this.rank = rank;
			this.value = value;
			this.iconId=icon;
		}

		public RDActivityRanker(String nickname, String server, Integer head, Integer level, Integer rank, Integer value,Integer icon) {
			this.nickname = nickname;
			this.server = server;
			this.head = head;
			this.level = level;
			this.rank = rank;
			this.value = value;
			this.iconId=icon;
		}
	}

}
