package com.bbw.god.gameuser.achievement.listener;

/**
 * @author suchaobin
 * @description 个人成就监听
 * @date 2020/2/21 9:08
 */
/*@Component
public class PersonAchievementListener {
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private UserAchievementService userAchievementService;

	private List<Integer> attackSymbolList = Arrays.asList(20110, 20120, 20130, 20140, 20150, 20160);
	private List<Integer> defendSymbolList = Arrays.asList(20210, 20220, 20230, 20240, 20250, 20260);


	@EventListener
	@Order(1000)
	public void firstLogin(FirstLoginPerDayEvent event) {
		EventParam<EPFirstLoginPerDay> ep = (EventParam<EPFirstLoginPerDay>) event.getSource();
		this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.LOGIN, 1, ep.getRd());
	}

	@EventListener
	@Order(1000)
	public void levelUp(GuLevelUpEvent event) {
		EPGuLevelUp ep = event.getEP();
		RDCommon rd = ep.getRd();
		int oldLevel = ep.getOldLevel();
		int newLevel = ep.getNewLevel();
		// 等级成就
		int addNumForAchievement = newLevel - oldLevel;
		if (oldLevel == 1) {
			// 初始等级（1）也要算进去
			addNumForAchievement += 1;
		}
		this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.GU_LEVEL, addNumForAchievement, rd);
	}

	@EventListener
	@Order(1000)
	public void openBox(OpenBoxEvent event) {
		EPOpenBox ep = event.getEP();
		int groupId = ep.getBoxId() / 1000 * 1000;
		CfgTaskConfig cfgTaskConfig = TaskTool.getTaskConfig(groupId);
		CfgTaskConfig.CfgBox cfgBox = cfgTaskConfig.getBoxs().stream().max(Comparator.comparingInt(
				CfgTaskConfig.CfgBox::getScore)).orElse(null);
		if (cfgBox != null && ep.getScore() == cfgBox.getScore()) {
			this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.DAILY_TASK_BOX, 1, ep.getRd());
		}
	}

	@EventListener
	@Order(1000)
	public void makeFriend(BuddyAcceptEvent event) {
		EventParam<Long> ep = (EventParam<Long>) event.getSource();
		Long guId = ep.getGuId();
		Long buddyId = ep.getValue();
		this.userAchievementService.achieve(guId, AchievementSerialEnum.MAKE_FRIEND, 1, ep.getRd());
		this.userAchievementService.achieve(buddyId, AchievementSerialEnum.MAKE_FRIEND, 1, ep.getRd());
	}

	@EventListener
	@Order(1000)
	public void gainSkillScroll(TreasureAddEvent event) {
		EPTreasureAdd ep = event.getEP();
		Long guId = ep.getGuId();
		List<EVTreasure> addTreasures = ep.getAddTreasures();
		// 技能卷轴
		List<EVTreasure> treasureList = addTreasures.stream().filter(at -> TreasureTool.getTreasureById(at.getId())
				.getType().equals(TreasureType.SKILL_SCROLL.getValue())).collect(Collectors.toList());
		if (ListUtil.isNotEmpty(treasureList)) {
			this.userAchievementService.achieve(guId, AchievementSerialEnum.BY_PLACE_SCROLL, treasureList.size(), ep.getRd());
		}
		// 秘传卷轴
		List<EVTreasure> secretTreasureList = treasureList.stream().filter(t ->
				TreasureTool.getTreasureById(t.getId()).getName().startsWith("全")).collect(Collectors.toList());
		if (ListUtil.isNotEmpty(secretTreasureList)) {
			this.userAchievementService.achieve(guId, AchievementSerialEnum.BY_PLACE_SECRET_SCROLL, secretTreasureList.size(), ep.getRd());
		}
	}

	@EventListener
	@Order(1000)
	public void updateSymbol(TreasureAddEvent event) {
		EPTreasureAdd ep = event.getEP();
		WayEnum way = ep.getWay();
		if (way == WayEnum.UPDATE_SYMBOL) {
			EVTreasure evTreasure = ep.getAddTreasures().get(0);
			if (evTreasure.getId() == 20160) {
				// 天力符
				this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.TIAN_LI_SYMBOL, 1, ep.getRd());
			} else if (evTreasure.getId() == 20260) {
				// 元阳符
				this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.YUAN_YANG_SYMBOL, 1, ep.getRd());
			}
		}
	}

	@EventListener
	@Order(1000)
	public void getSymbol(TreasureAddEvent event) {
		EPTreasureAdd ep = event.getEP();
		UserSymbolInfo userSymbolInfo = gameUserService.getSingleItem(ep.getGuId(), UserSymbolInfo.class);
		if (userSymbolInfo == null) {
			userSymbolInfo = new UserSymbolInfo(ep.getGuId());
			this.gameUserService.addItem(ep.getGuId(), userSymbolInfo);
		}
		List<Integer> awardedSymbolList = userSymbolInfo.getAwardedSymbolList();
		List<EVTreasure> addTreasures = ep.getAddTreasures();
		int addedAttack = 0;
		int addedDefend = 0;
		for (EVTreasure addTreasure : addTreasures) {
			// 筛选出新增的符箓
			if (TreasureTool.getTreasureById(addTreasure.getId()).getType().equals(TreasureType.SYMBOL.getValue())) {
				if (isNewAttackSymbol(addTreasure.getId(), userSymbolInfo)) {
					// 获得未得到过的攻击符箓
					addedAttack++;
				} else if (isNewDefendSymbol(addTreasure.getId(), userSymbolInfo)) {
					// 获得未得到过的防御符箓
					addedDefend++;
				}
				awardedSymbolList.add(addTreasure.getId());
			}
		}
		this.gameUserService.updateItem(userSymbolInfo);
		this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.BY_PLACE_ATTACK_SYMBOL, addedAttack, ep.getRd());
		this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.BY_PLACE_DEFEND_SYMBOL, addedDefend, ep.getRd());
	}

	private boolean isNewAttackSymbol(int treasureId, UserSymbolInfo userSymbolInfo) {
		if (!userSymbolInfo.getAwardedSymbolList().contains(treasureId) && attackSymbolList.contains(treasureId)) {
			return true;
		}
		return false;
	}

	private boolean isNewDefendSymbol(int treasureId, UserSymbolInfo userSymbolInfo) {
		if (!userSymbolInfo.getAwardedSymbolList().contains(treasureId) && defendSymbolList.contains(treasureId)) {
			return true;
		}
		return false;
	}

	/**
 * 邮件标题在ExchangeCodeServiceImpl中定义
 *
 * @param event
 * @see com.bbw.god.uac.service.impl.ExchangeCodeServiceImpl
 *//*
	@EventListener
	@Order(1000)
	public void payAttentionWeChatPublic(ExchangeCodeEvent event) {
		EPExchangeCode ep = event.getEP();
		this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.GONG_ZHONG_HAO, 1, new RDCommon());
	}
}*/
