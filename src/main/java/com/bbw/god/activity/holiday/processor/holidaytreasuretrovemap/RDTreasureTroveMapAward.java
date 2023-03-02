package com.bbw.god.activity.holiday.processor.holidaytreasuretrovemap;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.RDAward;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 藏宝图奖励
 *
 * @author: huanghb
 * @date: 2022/2/9 11:19
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDTreasureTroveMapAward extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 藏宝图奖励 */
	private List<RDAward> awards;
	/** 藏宝图奖励状态 */
	private boolean status = false;

	public static RDTreasureTroveMapAward getInstance(List<Award> awards, boolean status) {
		RDTreasureTroveMapAward rdTreasureTroveMapAward = new RDTreasureTroveMapAward();
		rdTreasureTroveMapAward.setAwards(RDAward.getInstances(awards));
		rdTreasureTroveMapAward.setStatus(status);
		return rdTreasureTroveMapAward;
	}
}
