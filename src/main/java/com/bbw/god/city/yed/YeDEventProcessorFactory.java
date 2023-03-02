package com.bbw.god.city.yed;

import com.bbw.exception.CoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author suchaobin
 * @description 野地事件工厂类
 * @date 2020/6/1 11:40
 **/
@Service
public class YeDEventProcessorFactory {
	@Autowired
	@Lazy
	private List<BaseYeDEventProcessor> yeDProcessors;
	@Autowired
	@Lazy
	private List<ExtraYeDEventProcessor> extraYeDEventProcessors;

	public BaseYeDEventProcessor getBaseProcessorById(int id) {
		for (BaseYeDEventProcessor processor : yeDProcessors) {
			if (processor.getMyId() == id) {
				return processor;
			}
		}
		throw CoderException.high(String.format("程序员没有编写id=%s的野地事件处理器", id));
	}

	public ExtraYeDEventProcessor getExtraProcessorById(int id) {
		for (ExtraYeDEventProcessor processor : extraYeDEventProcessors) {
			if (processor.getMyId() == id) {
				return processor;
			}
		}
		throw CoderException.high(String.format("程序员没有编写id=%s的野地事件拓展处理器", id));
	}
}
