package com.bbw.god.detail.disruptor;

import com.bbw.god.detail.DetailData;
import com.lmax.disruptor.EventFactory;

import lombok.Data;

/**
 * 明细事件（基于disruptor的异步事件机制）
 * 
 * @author suhq
 * @date 2019年3月13日 下午3:05:41
 */
@Data
public class DetailEvent {
	private DetailData detailData;

	public final static EventFactory<DetailEvent> EVENT_FACTORY = (EventFactory<DetailEvent>) () -> new DetailEvent();
}
