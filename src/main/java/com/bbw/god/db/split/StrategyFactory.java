package com.bbw.god.db.split;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.bbw.exception.CoderException;

//可以理解为拦截规则的容器，即使一条拦截策略也要写这个类
@Service
public class StrategyFactory {
	@Autowired
	@Lazy
	private List<Strategy> strategies;

	public Strategy getStrategy(String key) {
		for (Strategy s : strategies) {
			if (s.support(key)) {
				return s;
			}
		}
		throw CoderException.fatal("没有此分表策略。key=[" + key + "]");
	}

}
