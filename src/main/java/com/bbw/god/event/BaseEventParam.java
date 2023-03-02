package com.bbw.god.event;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDCommon;

import lombok.Getter;
import lombok.Setter;

/**
 * 基础事件参数
 * 
 * @author suhq
 * @date 2019-10-15 10:06:41
 */
@Getter
@Setter
public class BaseEventParam {

	private Long guId;
	private WayEnum way;
	private RDCommon rd;

	public BaseEventParam() {

	}

	public BaseEventParam(Long guId, WayEnum way, RDCommon rd) {
		this.guId = guId;
		this.way = way;
		this.rd = rd;
	}

	public BaseEventParam(Long guId, WayEnum way) {
		this.guId = guId;
		this.way = way;
		this.rd = new RDCommon();
	}

	public BaseEventParam(WayEnum way) {
		this.way = way;
	}

	public BaseEventParam(long guId) {
		this.guId = guId;
		this.rd = new RDCommon();
	}

	public void setValues(BaseEventParam bep) {
		this.guId = bep.getGuId();
		this.way = bep.getWay();
		this.rd = bep.getRd();
	}
}
