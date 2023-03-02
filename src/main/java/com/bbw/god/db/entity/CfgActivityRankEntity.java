package com.bbw.god.db.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.bbw.god.game.config.CfgEntityInterface;

import lombok.Data;

/**
 * 冲榜活动
 * 
 * @author suhq
 * @date 2019年3月4日 下午3:03:44
 */
@Data
@TableName("cfg_activity_rank")
public class CfgActivityRankEntity implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 1L;
	@TableId
	private Integer id; //
	private Integer scope;// 范围 全服或者区服
	private Integer type; // 活动类型
	private Integer serial; // 排序
	private String name; //
	private Integer minRank;// 排行上限
	private Integer maxRank;// 排行下限
	private Integer minValue;// 进入排行的最小值
	private String awards;// 如果某活动时玩家指定奖励的，值为"-"
	private Boolean status; //

	@Override
	public int getSortId() {
		return this.getId();
	}
}
