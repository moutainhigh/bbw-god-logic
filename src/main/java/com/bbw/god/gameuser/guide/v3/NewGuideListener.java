package com.bbw.god.gameuser.guide.v3;

//@Component
public class NewGuideListener {
	/*@Autowired
	private NewerGuideService newerGuideService;
	@Autowired
	private InsGuideDetailService insGuideDetailService;
	@Autowired
	private GameUserService gameUserService;

	@EventListener
	public void addCard(UserCardAddEvent event) {
		EPCardAdd ep = event.getEP();
		long uid = ep.getGuId();
		WayEnum way = ep.getWay();
		switch (way) {
			case KZ:
				newerGuideService.updateNewerGuide(uid, NewerGuideEnum.KZ_BUY, ep.getRd());
				break;
			case OPEN_JU_XIAN_CARD_POOL:
				newerGuideService.updateNewerGuide(uid, NewerGuideEnum.DRAW_CARD, ep.getRd());
				break;
			default:
				break;
		}
	}

	@EventListener
	public void grouping(UserCardGroupingEvent event) {
		EPCardGrouping ep = event.getEP();
		long uid = ep.getGuId();
		UserNewerGuide userNewerGuide = this.newerGuideService.getUserNewerGuide(uid);
		// 判断更新后的进度
		if (NewerGuideEnum.KZ_BUY.getStep().equals(userNewerGuide.getNewerGuide())) {
			newerGuideService.updateNewerGuide(ep.getGuId(), NewerGuideEnum.BIAN_ZHU_1, ep.getRd());
			return;
		}
		if (NewerGuideEnum.DRAW_CARD.getStep().equals(userNewerGuide.getNewerGuide())) {
			newerGuideService.updateNewerGuide(ep.getGuId(), NewerGuideEnum.BIAN_ZHU_2, ep.getRd());
		}
	}

	@EventListener
	public void fightWin(FightWinEvent event) {
		EVFightEnd ep = (EVFightEnd) event.getSource();
		long uid = ep.getGuId();
		UserNewerGuide userNewerGuide = this.newerGuideService.getUserNewerGuide(uid);
		if (FightTypeEnum.ATTACK == ep.getFightType() && NewerGuideEnum.BIAN_ZHU_1.getStep().equals(userNewerGuide.getNewerGuide())) {
			newerGuideService.updateNewerGuide(ep.getGuId(), NewerGuideEnum.ATTACK_1, ep.getRd());
			return;
		}
		if (FightTypeEnum.ATTACK == ep.getFightType() && NewerGuideEnum.YOU_SHANG_GUAN.getStep().equals(userNewerGuide.getNewerGuide())) {
			newerGuideService.updateNewerGuide(ep.getGuId(), NewerGuideEnum.ATTACK_2, ep.getRd());
			return;
		}
		if (FightTypeEnum.YG == ep.getFightType()) {
			newerGuideService.updateNewerGuide(ep.getGuId(), NewerGuideEnum.YE_GUAI, ep.getRd());
		}
	}

	@EventListener
	public void fightFail(FightFailEvent event) {
		EVFightEnd ep = (EVFightEnd) event.getSource();
		if (FightTypeEnum.YG == ep.getFightType()) {
			newerGuideService.updateNewerGuide(ep.getGuId(), NewerGuideEnum.YE_GUAI, ep.getRd());
		}
	}

	@EventListener
	public void addTreasure(TreasureAddEvent event) {
		EPTreasureAdd ep = event.getEP();
		WayEnum way = ep.getWay();
		if (way == WayEnum.JXZ_AWARD) {
			newerGuideService.updateNewerGuide(ep.getGuId(), NewerGuideEnum.JXZ, ep.getRd());
		}
	}

	@EventListener
	public void cardLevelUp(UserCardLevelUpEvent event) {
		EPCardLevelUp ep = event.getEP();
		if (ep.getWay() == WayEnum.CARD_UPDATE) {
			long uid = ep.getGuId();
			UserNewerGuide userNewerGuide = this.newerGuideService.getUserNewerGuide(uid);
			if (NewerGuideEnum.BIAN_ZHU_2.getStep().equals(userNewerGuide.getNewerGuide())) {
				newerGuideService.updateNewerGuide(ep.getGuId(), NewerGuideEnum.CARD_LEVEL_UP_1, ep.getRd());
				return;
			}
			if (NewerGuideEnum.KC.getStep().equals(userNewerGuide.getNewerGuide())) {
				newerGuideService.updateNewerGuide(ep.getGuId(), NewerGuideEnum.CARD_LEVEL_UP_2, ep.getRd());
				return;
			}
			if (NewerGuideEnum.CARD_LEVEL_UP_2.getStep().equals(userNewerGuide.getNewerGuide())) {
				newerGuideService.updateNewerGuide(ep.getGuId(), NewerGuideEnum.CARD_LEVEL_UP_3, ep.getRd());
			}
		}
	}

	@EventListener
	public void addGold(GoldAddEvent event) {
		EPGoldAdd ep = event.getEP();
		if (ep.getWay() == WayEnum.FD) {
			newerGuideService.updateNewerGuide(ep.getGuId(), NewerGuideEnum.FD, ep.getRd());
		}

	}

	@EventListener
	public void deductCopper(CopperDeductEvent event) {
		EPCopperDeduct ep = event.getEP();
		Long uid = ep.getGuId();
		if (ep.getWay() == WayEnum.YSG) {
			newerGuideService.updateNewerGuide(uid, NewerGuideEnum.YOU_SHANG_GUAN, ep.getRd());
		}
		if (ep.getWay() == WayEnum.TRADE) {
			newerGuideService.updateNewerGuide(uid, NewerGuideEnum.JIAOYI, ep.getRd());
		}
	}

	@EventListener
	public void addEle(EleAddEvent event) {
		EPEleAdd ep = event.getEP();
		if (WayEnum.KC_AWARD == ep.getWay()) {
			newerGuideService.updateNewerGuide(ep.getGuId(), NewerGuideEnum.KC, ep.getRd());
		}
	}

	@EventListener
	@Async
	@Order(1000)
	public void log(LogNewerGuideEvent event) {
		EPLogNewerGuide ep = event.getEP();
		long uid = ep.getGuId();
		Integer sid = gameUserService.getGameUser(uid).getServerId();
		Integer newerGuide = ep.getNewerGuide();
		String name = NewerGuideEnum.fromValue(newerGuide).getName();
		InsGuideDetail detail = InsGuideDetail.getInstance(ep.getGuId(), sid, newerGuide, name, "v3");
		insGuideDetailService.insert(detail);
	}*/
}
