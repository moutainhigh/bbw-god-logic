package com.bbw.god.server.fst;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.bbw.common.MathTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 封神台排行榜用户信息
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2018-12-21 17:38
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDFstRankerList extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer myRank = null;// 我的封神台排名
	private Integer myPoint = null;// 我的积分
	private Integer myAblePoint = null;// 我可得的积分
	private Integer pvpTimes = null;// 封神台次数
	private Integer incrementState=null;//封神台增值积分状态
	private Integer addedPoint=null;//封神台领取的增值积分
	private List<RDFstRanker> ranks = new ArrayList<RDFstRanker>();// 封神台排行
	private Integer currentPoint=null;//可领取封神台增值积分

	public void setCurrentPoint(int incrementPoint){
		currentPoint=incrementPoint;
		if (currentPoint==0){
			// 增值积分为0 不可领取
			incrementState=-1;
		}else if (currentPoint>=30000){
			// 增值积分达到上限
			incrementState=1;
		}else {
			// 正常状态 可领取，且未达到上限
			incrementState=0;
		}
	}
	@Data
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class RDFstRanker implements Serializable {
		private static final long serialVersionUID = 1L;
		private Long id = null;
		private String nickname = "";// 昵称
		private Integer head = null;// 头像
		private Integer iconId = TreasureEnum.HEAD_ICON_Normal.getValue();// 头像框
		private Integer level = null;// 等级
		private Integer ablePoints = null;
		private Integer fightAble = null;// 是否可以攻击。
		private Integer pvpRanking = null;
		private Integer pvpWin = 0;
		private Integer pvpTotalTimes = 0;
		private Double winRate = null;

		public Double getWinRate() {
			if (0 == pvpTotalTimes) {
				return 0.0;
			}
			if (null == this.winRate) {
				this.winRate = MathTool.divHalfUp2(pvpWin, pvpTotalTimes).doubleValue();
			}
			return this.winRate;
		}
	}

}
