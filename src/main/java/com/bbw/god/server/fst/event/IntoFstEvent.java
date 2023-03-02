package com.bbw.god.server.fst.event;

import org.springframework.context.ApplicationEvent;

import com.bbw.god.event.EventParam;

public class IntoFstEvent extends ApplicationEvent {

	public IntoFstEvent(EventParam<Integer> source) {
		super(source);
	}

}
