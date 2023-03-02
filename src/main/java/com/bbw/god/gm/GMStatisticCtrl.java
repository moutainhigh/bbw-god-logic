package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.god.statistics.CardSkillStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gm")
public class GMStatisticCtrl {
	@Autowired
	private CardSkillStatisticService cardSkillStatisticService;

	/**
	 * 卡牌强化统计
	 *
	 * @param sinceDate
	 * @return
	 */
	@RequestMapping("statistic!cardSkill")
	public Rst cardSkillStatistic(int sinceDate) {
		cardSkillStatisticService.doStatistic(sinceDate);
		return Rst.businessOK();
	}

}
