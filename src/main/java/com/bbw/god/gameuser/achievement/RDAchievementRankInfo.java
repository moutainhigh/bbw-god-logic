package com.bbw.god.gameuser.achievement;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author suchaobin
 * @description 成就排行榜
 * @date 2020/2/20 15:50
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDAchievementRankInfo extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<RDUserRankInfo> rankInfoList;
	private RDUserRankInfo myRankInfo;
	/**排行榜总人数*/
	private Integer rankSize;

	public static RDAchievementRankInfo getInstance(List<RDUserRankInfo> rankInfoList, RDUserRankInfo myRankInfo, int rankSize) {
		RDAchievementRankInfo rdAchievementRankInfo = new RDAchievementRankInfo();
		rdAchievementRankInfo.setRankInfoList(rankInfoList);
		rdAchievementRankInfo.setMyRankInfo(myRankInfo);
		rdAchievementRankInfo.setRankSize(rankSize);
		return rdAchievementRankInfo;
	}

	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@AllArgsConstructor
	public static class RDUserRankInfo implements Serializable {
		private static final long serialVersionUID = 1L;
		private Integer rank;
		private String server;
		private String nickname;
		private Integer finishNum;
		private Integer totalScore;

		public RDUserRankInfo(Integer rank, String nickname, Integer finishNum, Integer totalScore) {
			this.rank = rank;
			this.nickname = nickname;
			this.finishNum = finishNum;
			this.totalScore = totalScore;
		}
	}
}
