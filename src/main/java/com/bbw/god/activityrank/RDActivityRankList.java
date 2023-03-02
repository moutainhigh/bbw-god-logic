package com.bbw.god.activityrank;

import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 冲榜活动列表信息
 * 
 * @author suhq
 * @date 2019年3月5日 下午6:18:25
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@EqualsAndHashCode(callSuper = true)
public class RDActivityRankList extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 7556617508773729662L;
	private List<RDActivityRank> rankActivities = null;

	/**
	 * 单个冲榜活动的数据
	 *
	 * @author suhq
	 * @date 2019年3月7日 下午5:18:30
	 */
	@Data
	public static class RDActivityRank implements Serializable {
		private static final long serialVersionUID = 1L;
		private Integer type = null;// 冲榜类型
		private Long remainTime = null;// 剩余时间
		private String activityTime = null;// 时间期间
		private Integer myRank = null;// 我的排行
		private Boolean isShowAfterEnd = false;// 结束之后是否展示
	}

}
