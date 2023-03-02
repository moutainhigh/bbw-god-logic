package com.bbw.god.game.chanjie;

import com.bbw.god.rd.RDCommon;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author lwb
 * @date 2019年6月14日
 * @version 1.0
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class ChanjieRd extends RDCommon {
	private static final long serialVersionUID = 1L;
	private Long chanMembersNum = null;
	private Long jieMembersNum = null;
	private Integer religionId = null;
	private Integer chanId = null;
	private Integer jieId = null;
	private Integer addedBlood = null;
	private List<RankingUserInfo> rankingList = null;
	private RankingUserInfo firstRankingInfo = null;// 掌教的排名信息
	private RankingUserInfo userRankingInfo = null;// 自己的排名信息
	private List<String> honorRankingList = null;
	private Long uid = null;
	private Long playingId = null;
	private String headName = null;
	private List<ChanjieRd> awards = null;
	private Long chanVictory = null;// 阐教胜场
	private Long jieVictory = null;// 截教胜场
	private Integer firstInto = null;// 周六首次进入判断 0 为非首次 1 为首次
	private Integer remind = null;// 成就红点提示
	private Integer bloodVolume = null;
	private UserInfo userInfo = null;
	private Long surplusTime = null;// 倒计时
	private Long beginTimes = null;// 开赛倒计时
	private Integer datetype = null;// 10。普通。 20。周六无限生命。 30周日乱斗
	private Long likeNum = null;
	private List<SpecailHonor> specailHonors = null;// 教派奇人
	// 乱斗封神部分
	private Integer haveJoin = null;// 是否入围比赛
	private List<RankingSunInfo> sunRankingList = null;
	private Long peopleNum = null;// 剩余人数
	private String ranking = null;// 目前排名
	private Integer killNum = null;// 战胜人数
	private Integer stop = null;// 是否结束比赛
	private RankingSunInfo rankingInfo = null;// 个人排行信息
	private Integer addedHonor=null;

	@Data
	public static class UserInfo {
		private Integer bloodVolume;// 玩家血量 初始3
		private Integer bought;// 血量已购次数
		private Integer victory;// 胜场
		private Integer fightNum;// 场数
		private Integer honor;// 荣誉点
		private String rateOfWinning;
		private String headName;
		private String ranking;
	}

	@Data
	public static class RankingUserInfo {
		private Integer honor;// 荣誉点
		private String headName;
		private String ranking;
		private String nickname;
	}

	@Data
	public static class RankingSunInfo {
		private String victory;// 胜场
		private String nickname;
		private Integer outState;
		private String rank = null;
	}

	@Data
	public static class SpecailHonor {
		private Integer id;
		private String memo;// 描述
		private String content;
		private String nickname;
		private Long like;// 点赞数
		private Integer status;// 是否已点赞
	}

	public void setHaveJoin(int join) {
		haveJoin = join;
	}
}
