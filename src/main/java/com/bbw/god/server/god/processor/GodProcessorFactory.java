package com.bbw.god.server.god.processor;

import com.bbw.god.gameuser.guide.NewerGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GodProcessorFactory {
	@Autowired
	@Lazy
	private List<AbstractGodProcessor> processors;
	@Autowired
	private NewerGuideService newerGuideService;

	public AbstractGodProcessor create(long uid, int godId) {
		//boolean passNewerGuide = newerGuideService.isPassNewerGuide(uid);
		return processors.stream().filter(cp -> cp.isMatch(godId)).findFirst().orElse(null);
	}

}
