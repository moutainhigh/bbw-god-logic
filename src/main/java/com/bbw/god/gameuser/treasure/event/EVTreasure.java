package com.bbw.god.gameuser.treasure.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EVTreasure {
	private Integer id;
	private Integer num;
	/** 源道具id */
	private Integer sourceId = id;

	public EVTreasure(int id, int num) {
		this.id = id;
		this.num = num;
	}

	public EVTreasure(int id, int num, int sourceId) {
		this.id = id;
		this.num = num;
		this.sourceId = sourceId;
	}
}
