package com.bbw.god.gameuser.achievement.listener;

/**
 * @author suchaobin
 * @description 卡牌成就监听
 * @date 2020/2/21 9:08
 */
/*
@Component
public class CardAchievementListener {
	@Autowired
	private UserAchievementService userAchievementService;
	@Autowired
	private UserCardService userCardService;

	@EventListener
	@Order(1000)
	public void addCard(UserCardAddEvent event) {
		EPCardAdd ep = event.getEP();
		long guId = ep.getGuId();
		RDCommon rd = ep.getRd();
		List<EPCardAdd.CardAddInfo> newCards = ep.getAddCards().stream().filter(tmp -> tmp.isNew()).collect(Collectors.toList());
		if (ListUtil.isNotEmpty(newCards)) {
			// 去重
			List<Integer> newCardIds = newCards.stream().map(EPCardAdd.CardAddInfo::getCardId).distinct().collect(Collectors.toList());
			newCardIds.stream().forEach(tmp -> {
				CfgCardEntity card = CardTool.getCardById(tmp);
				//this.userAchievementService.achieve(guId, AchievementSerialEnum.COLLECT_CARD, 1, rd);
				this.userAchievementService.achieve(guId, AchievementSerialEnum.fromValue(2020 + card.getType()), 1,
						rd);
				// 收集到 杨戬、句芒、妲己、火灵圣母、闻仲
				if (Arrays.asList(102, 202, 301, 402, 501).contains(tmp)) {
					this.userAchievementService.achieve(guId, AchievementSerialEnum.COLLECT_FIVE_TYPE_CARD, 1, rd);
				}
			});
			// 集齐殷洪+殷洪四天君
			List<Integer> yhCardIds = CardTool.getYinHongCards();
			long yhCardsNum = newCardIds.stream().filter(yhCardIds::contains).count();
			if (yhCardsNum > 0) {
				this.userAchievementService.achieve(guId, AchievementSerialEnum.COLLECT_YHSTJ, (int) yhCardsNum, rd);
			}
		}
	}

	@EventListener
	@Order(1000)
	public void hierarchyUp(UserCardHierarchyUpEvent event) {
		EPCardHierarchyUp ep = event.getEP();
		int cardId = ep.getCardId();
		this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.FIRST_PROMOTE_CARD, 1, ep.getRd());
		this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.PROMOTE_CARD, 1, ep.getRd());
		*/
/*List<Integer> disciplesOfTianZun = Arrays.asList(106, 333, 432);// 道行天尊三弟子
		if (disciplesOfTianZun.contains(cardId)) {
			UserCard userCard = userCardService.getUserCards(ep.getGuId()).stream().filter(uc ->
					uc.getBaseId().equals(cardId)).findFirst().orElse(null);
			if (userCard != null && userCard.getHierarchy() == 3) {
					this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.PROMOTE_WH_HDL_XEH, 1, ep
					.getRd());
			}
		}*//*

	}

	@EventListener
	@Order(1000)
	public void handleEleConsumeEvent(EleDeductEvent event) {
		EPEleDeduct ep = event.getEP();
		WayEnum way = ep.getWay();
		// 只处理升级卡牌的元素消耗
		if (way != WayEnum.CARD_UPDATE) {
			return;
		}

		List<EVEle> evEles = ep.getDeductEles();
		// 处理单种元素消耗成就
		evEles.forEach(ele -> {
			int goldEleAchievement = AchievementSerialEnum.USE_GOLD_ELE_TO_UPDATE.getValue();
			AchievementSerialEnum serial = AchievementSerialEnum.fromValue(goldEleAchievement + ele.getType() - 10);
			this.userAchievementService.achieve(ep.getGuId(), serial, ele.getNum(), ep.getRd());
		});
		// 处理总元素消耗成就
		int totalUse = evEles.stream().mapToInt(EVEle::getNum).sum();
		this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.USE_ELE_TO_UPDATE, totalUse, ep.getRd());
	}

	@EventListener
	@Order(1000)
	public void cardDraw(DrawEndEvent event) {
		EventParam<EPDraw> ep = (EventParam<EPDraw>) event.getSource();
		EPDraw epDraw = ep.getValue();
		Integer drawTimes = epDraw.getDrawTimes();
		this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.DRAW_CARD, drawTimes, ep.getRd());
	}

	@EventListener
	@Order(1000)
	public void changeSkill(UserCardSkillChangeEvent event) {
		EPCardSkillChange ep = event.getEP();
		Integer cardId = ep.getCardId();
		Integer oldSkill = ep.getOldSkill();
		CfgCardEntity cfgCard = CardTool.getCardById(cardId);
		List<Integer> cfgCardSkill = Arrays.asList(cfgCard.getZeroSkill(), cfgCard.getFiveSkill(), cfgCard.getTenSkill());
		if (this.userAchievementService.isFinish(ep.getGuId(), 13360)) {
			return;
		}
		// 被替换的卡牌技能不是卡牌初始化自带的技能，说明该卡牌不是第一次炼技
		if (!cfgCardSkill.contains(oldSkill) && oldSkill != null) {
			this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.CHANGE_CARD_SKILL, 2, ep.getRd());
		} else {
			// 被替换的卡牌技能是卡牌初始化自带的技能，判断其它技能换过没
			UserCard userCard = userCardService.getUserCards(ep.getGuId()).stream().filter(uc ->
					uc.getBaseId().equals(cardId)).findFirst().orElse(null);
			if (userCard != null) {
				UserCard.UserCardStrengthenInfo info = userCard.getStrengthenInfo();
				List<Integer> newCardSkill = Arrays.asList(info.getSkill0(), info.getSkill5(), info.getSkill10());
				int changeTimes = (int) newCardSkill.stream().filter(Objects::nonNull).count();
				UserAchievement userAchievement = this.userAchievementService.getUserAchievement(ep.getGuId(), 13360);
				if (userAchievement == null) {
					this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.CHANGE_CARD_SKILL, changeTimes, ep.getRd());
				} else {
					this.userAchievementService.resetAchievement(ep.getGuId(), AchievementSerialEnum.CHANGE_CARD_SKILL, changeTimes, ep.getRd());
				}
			}
		}
	}

	@EventListener
	@Order(1000)
	public void cardLevelUp(UserCardLevelUpEvent event) {
		EPCardLevelUp ep = event.getEP();
		int cardId = ep.getCardId();
		CfgCardEntity card = CardTool.getCardById(cardId);
		if (ep.getNewLevel() >= 10 && ep.getOldLevel() < 10 && card.getStar() >= 4) {
			this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.YU_HUA_DENG_XIAN, 1, ep.getRd());
		}
	}
}
*/
