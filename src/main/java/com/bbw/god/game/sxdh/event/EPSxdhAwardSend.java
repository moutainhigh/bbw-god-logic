package com.bbw.god.game.sxdh.event;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.sxdh.config.SxdhRankType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EPSxdhAwardSend extends BaseEventParam {
	private Integer season;
	private SxdhRankType rankType;
	private Integer rank;

	public EPSxdhAwardSend(Integer season, SxdhRankType rankType, Integer rank, BaseEventParam bep) {
		this.season = season;
		this.rankType = rankType;
		this.rank = rank;
		setValues(bep);
	}
}
