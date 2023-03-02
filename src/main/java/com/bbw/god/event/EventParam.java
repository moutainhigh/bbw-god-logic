package com.bbw.god.event;

import com.bbw.god.game.config.WayEnum;
import com.bbw.god.rd.RDCommon;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EventParam<T> {

	private Long guId;
	private T value;
	private WayEnum way;
	private String broadcastWayInfo = "";// 触发广播的条件信息
	private RDCommon rd;

	public EventParam(Long guId, T value, WayEnum way, RDCommon rd) {
		this.guId = guId;
		this.value = value;
		this.way = way;
		this.rd = rd;
	}

	public EventParam(Long guId, T value, WayEnum way) {
		this.guId = guId;
		this.value = value;
		this.way = way;
	}

	public EventParam(Long guId, T value, RDCommon rd) {
		this.guId = guId;
		this.value = value;
		this.rd = rd;
	}

	public EventParam(Long guId, RDCommon rd) {
		this.guId = guId;
		this.rd = rd;
	}

	public EventParam(BaseEventParam bp) {
		this.guId = bp.getGuId();
		this.way = bp.getWay();
		this.rd = bp.getRd();
	}

	public EventParam(BaseEventParam bp, T value) {
		this.guId = bp.getGuId();
		this.way = bp.getWay();
		this.rd = bp.getRd();
		this.value = value;
	}

	public EventParam(long uid, T value) {
		this.guId = uid;
		this.value = value;
	}
}
