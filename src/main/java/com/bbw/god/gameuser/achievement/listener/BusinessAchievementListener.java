package com.bbw.god.gameuser.achievement.listener;

/**
 * @author suchaobin
 * @description 商途成就监听
 * @date 2020/2/21 9:09
 */
/*
@Component
public class BusinessAchievementListener {
	@Autowired
	private UserAchievementService userAchievementService;

	@EventListener
	@Order(1000)
	public void finishCocTask(CocTaskFinishedEvent event) {
		EPTaskFinished ep = event.getEP();
		this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.COC_TASK, 1, ep.getRd());
	}

	@EventListener
	@Order(1000)
	public void useCocTreasure(TreasureDeductEvent event) {
		EPTreasureDeduct ep = event.getEP();
		EVTreasure deductTreasure = ep.getDeductTreasure();
		Integer treasureId = deductTreasure.getId();
		switch (treasureId) {
			// 商会令牌
			case 10050:
				this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.USE_COC_TOKEN, 1, ep.getRd());
				break;
			// 特许交易券
			case 10060:
				this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.USE_SPECIAL_TRADE_TOKEN, 1, ep.getRd());
				break;
			default:
				break;
		}
	}


	@EventListener
	@Order(1000)
	public void addCopper(CopperAddEvent event) {
		EPCopperAdd ep = event.getEP();
		WayEnum way = ep.getWay();
		// 富豪榜
		int fhbCopper = ep.getWeekCopper();
		// 满足添加条件
		if (shouldAdd(way, fhbCopper)) {
			this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.COPPER, fhbCopper, ep.getRd());
		}
	}

	private boolean shouldAdd(WayEnum way, int fhbCopper) {
		if (fhbCopper <= 0) {
			return false;
		}
		switch (way) {
			case SALARY_COPPER:// 俸禄不算入富豪榜
			case EXCHANGE_FST:// 封神台兑换收入不算入富豪榜
			case Mail:// 邮件不纳入富豪榜
				return false;
			default:
				return true;
		}
	}


	@EventListener
	@Order(1000)
	public void addSpecial(SpecialAddEvent event) {
		EPSpecialAdd ep = event.getEP();
		WayEnum way = ep.getWay();
		if (way != WayEnum.TRADE) {
			List<EVSpecialAdd> addSpecials = ep.getAddSpecials();
			this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.GET_SPECIAL_EXCEPT_TRADE,
			addSpecials.size(), ep.getRd());
		}
	}
}
*/
