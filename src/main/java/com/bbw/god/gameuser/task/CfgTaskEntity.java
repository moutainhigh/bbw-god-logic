package com.bbw.god.gameuser.task;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CfgTaskEntity implements CfgEntityInterface, Serializable {
	private static final long serialVersionUID = 1L;
	private Integer id; //
	private String name; //
	/** 任务系列号 */
	private Integer seq = 0;//
	/** 任务系列组号 */
	private Integer seqGroup;
	/** 任务难度 */
	private Integer difficulty;
	private Integer type; //
	private Integer value; //
	private List<Award> awards; //
	private Boolean isValid;
	private Integer days;
	/** 任务次数 0代表无次数限制 */
	private Integer timesLimit = 1;
	/** 任务生成概率 */
	private Integer generateProb = 100;

	@Override
	public int getSortId() {
		return this.getId();
	}

}
