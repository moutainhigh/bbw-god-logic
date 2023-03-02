package com.bbw.god.gameuser.treasure.processor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class TreasureProcessorFactory {
	@Autowired
	@Lazy
	private List<TreasureUseProcessor> treasureUseProcessors;

	public TreasureUseProcessor getTreasureUseProcessor(int treasureId) {
		return treasureUseProcessors.stream().filter(tup -> tup.isMatch(treasureId)).findFirst().orElse(null);
	}

}
