package com.bbw.god.fight.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.fight.FightTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class FightProcessorFactory {

	@Autowired
	@Lazy
	private List<AbstractFightProcessor> fightProcessors;
	/**
	 * 获得战斗处理器
	 * 
	 * @param fightTypeEnum
	 * @return
	 */
	public AbstractFightProcessor makeFightProcessor(FightTypeEnum fightTypeEnum) {
		for (AbstractFightProcessor fightProcessor : fightProcessors) {
			if (fightProcessor.isMatch(fightTypeEnum)) {
				return fightProcessor;
			}
		}
		throw new ExceptionForClientTip("fight.type.not.exists");
	}
}
