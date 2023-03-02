package com.bbw.god.city.yeg;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.processor.FightProcessorFactory;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 野怪相关接口
 *
 * @author suhq
 * @date 2018年11月2日 下午5:53:56
 */
@RestController
public class YeGController extends AbstractController {
	@Autowired
	private FightProcessorFactory fightProcessorFactory;
	@Autowired
	private YeGProcessor yGProcessor;
	@Autowired
	private NewerGuideService newerGuideService;

	/**
	 * 提交攻击野怪结果
	 *
	 * @return
	 */
	@GetMapping(value = CR.YG.SUBMIT_ATTACK)
	public RDFightResult submitAttackYG(FightSubmitParam param) {
		return fightProcessorFactory.makeFightProcessor(FightTypeEnum.YG).submitFightResult(getUserId(), param);
	}

	/**
	 * 野怪开箱子
	 *
	 * @return
	 */
	@GetMapping(CR.YG.OPEN_BOX)
	public RDCommon openBox() {
		return yGProcessor.openBox(getUserId());
	}
}
