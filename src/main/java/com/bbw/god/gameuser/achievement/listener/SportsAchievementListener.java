package com.bbw.god.gameuser.achievement.listener;

/**
 * @author suchaobin
 * @description 竞技成就监听
 * @date 2020/2/21 9:09
 */
/*
@Component
public class SportsAchievementListener {
	@Autowired
	private UserAchievementService userAchievementService;

	@EventListener
	@Order(1000)
	public void fightWin(FightWinEvent event) {
		EVFightEnd ep = (EVFightEnd) event.getSource();
		long guId = ep.getGuId();
		RDCommon rd = ep.getRd();
		switch (ep.getFightType()) {
			case FST:
				this.userAchievementService.achieve(guId, AchievementSerialEnum.FST_WIN, 1, rd);
				break;
			case SXDH:
				this.userAchievementService.achieve(guId, AchievementSerialEnum.SXDH_SXPM, 1, rd);
				this.userAchievementService.achieve(guId, AchievementSerialEnum.SXDH_FIGHT_WIN, 1, rd);
				this.userAchievementService.todayFirstAchieve(guId, AchievementSerialEnum.SXDH_DAY_BY_DAY, 1, rd);
				break;
			default:
				break;
		}
	}

	@EventListener
	@Order(1000)
	public void fightFail(FightFailEvent event) {
		EVFightEnd ep = (EVFightEnd) event.getSource();
		long guId = ep.getGuId();
		RDCommon rd = ep.getRd();
		switch (ep.getFightType()) {
			case SXDH:
				// 失败则重置
				this.userAchievementService.resetAchievement(guId, AchievementSerialEnum.SXDH_SXPM, 0, rd);
				this.userAchievementService.todayFirstAchieve(guId, AchievementSerialEnum.SXDH_DAY_BY_DAY, 1, rd);
				break;
			default:
				break;
		}
	}

	@EventListener
	@Order(1000)
	public void specialHonor(ChanjieSpecailHonorEvent event) {
		EPChanjieSpecailHonor val = event.getEP();
		if (val.getDdst() > 0) {
			userAchievementService.achieve(val.getDdst(), AchievementSerialEnum.CHANJIE_TMZZ, 1, new RDCommon());
		}
		if (val.getRbkd() > 0) {
			userAchievementService.achieve(val.getRbkd(), AchievementSerialEnum.CHANJIE_TMZZ, 1, new RDCommon());
		}
		if (val.getTxzr() > 0) {
			userAchievementService.achieve(val.getTxzr(), AchievementSerialEnum.CHANJIE_TMZZ, 1, new RDCommon());
		}
		if (val.getYryy() > 0) {
			userAchievementService.achieve(val.getYryy(), AchievementSerialEnum.CHANJIE_TMZZ, 1, new RDCommon());
		}
	}

	@EventListener
	@Order(1000)
	public void fight(ChanjieFightEvent event) {
		EPChanjieFight fight = event.getEP();
		//失败方
		int headlv = fight.getHeadlv();
		int rid = fight.getRid();
		long uid = fight.getGuId();
		RDCommon rd = new RDCommon();
		if (ChanjieType.Religious_CHAN.getValue().intValue() == rid) {
			// 击败阐教门徒
			userAchievementService.achieve(uid, AchievementSerialEnum.CHANJIE_JIE_JY, 1, rd);
		} else {
			// 击败截教门徒
			userAchievementService.achieve(uid, AchievementSerialEnum.CHANJIE_CHAN_JY, 1, rd);
		}

		if (ChanjieTools.FIGHT_XIAN_REN_LV <= headlv) {
			// 击杀仙人
			userAchievementService.achieve(uid, AchievementSerialEnum.CHANJIE_BWQD, 1, rd);
		}
		if (ChanjieTools.FIGHT_LEADER_LV == headlv) {
			// 击败掌教师尊
			userAchievementService.achieve(uid, AchievementSerialEnum.CHANJIE_PFYN, 1, rd);
		}
	}

	@EventListener
	@Order(1000)
	public void religionSelect(ChanjieReligionSelectEvent event) {
		RDCommon rd = new RDCommon();
		int nowRid = event.getEP().getNowRid();
		long uid = event.getEP().getGuId();
		if (nowRid == ChanjieType.Religious_CHAN.getValue().intValue()) {
			//选择阐教   则阐教进度+1   截教清0
			userAchievementService.achieve(uid, AchievementSerialEnum.CHANJIE_CHAN_SELECT, 1, rd);
			userAchievementService.resetAchievement(uid, AchievementSerialEnum.CHANJIE_JIE_SELECT, 0, rd);
		} else {
			//选择截教   则截教进度+1   阐教清0
			userAchievementService.achieve(uid, AchievementSerialEnum.CHANJIE_JIE_SELECT, 1, rd);
			userAchievementService.resetAchievement(uid, AchievementSerialEnum.CHANJIE_CHAN_SELECT, 0, rd);
		}
		//战无不胜  连续6天获得护教法王（同赛季）
		userAchievementService.resetAchievement(uid, AchievementSerialEnum.CHANJIE_ZWBS, 0, rd);
	}

	@EventListener
	@Order(1000)
	public void gainDaliyHeadlv(ChanjieGainHeadEvent event) {
		RDCommon rd = new RDCommon();
		int headlv = event.getEP().getHeadlv();
		List<Long> uidsList = event.getEP().getUids();
		//内门弟子2 真传弟子3 渡劫地仙4 大乘天仙5 大罗金仙6 护教法王7 掌教8
		// 本赛季连续6天获得护教法王头衔   获得则进度+1 否则清0
		if (DateUtil.isWeekDay(1)) {
			uidsList.forEach(p -> {
				userAchievementService.resetAchievement(p, AchievementSerialEnum.CHANJIE_ZWBS, 0, rd);
			});
		}
		if (7 == headlv) {
			uidsList.forEach(p -> {
				userAchievementService.achieve(p, AchievementSerialEnum.CHANJIE_ZWBS, 1, rd);
			});
		} else {
			uidsList.forEach(p -> {
				userAchievementService.resetAchievement(p, AchievementSerialEnum.CHANJIE_ZWBS, 0, rd);
			});
		}
		if (DateUtil.isWeekDay(6)) {
			gainHeadlv(headlv, uidsList);
		}
	}

	// 赛季结束时 获得的头衔
	public void gainHeadlv(int headlv, List<Long> uidsList) {
		// 外门弟子1 内门弟子2 真传弟子3 渡劫地仙4 大乘天仙5 大罗金仙6 护教法王7 掌教8
		AchievementSerialEnum type;
		switch (headlv) {
			case 2:
				type = AchievementSerialEnum.CHANJIE_CLZD;
				break;
			case 3:
				type = AchievementSerialEnum.CHANJIE_JRJJ;
				break;
			case 4:
				type = AchievementSerialEnum.CHANJIE_CRTD;
				break;
			case 5:
				type = AchievementSerialEnum.CHANJIE_MSXH;
				break;
			case 6:
				type = AchievementSerialEnum.CHANJIE_JZQZ;
				break;
			case 7:
				type = AchievementSerialEnum.CHANJIE_YRZX;
				break;
			case 8:
				type = AchievementSerialEnum.CHANJIE_WRZS;
				break;
			default:
				return;
		}
		RDCommon rd = new RDCommon();
		uidsList.forEach(p -> {
			userAchievementService.achieve(p, type, 1, rd);
		});
	}

	@EventListener
	@Order(1000)
	public void sxdhTitleChange(TreasureAddEvent event) {
		EPTreasureAdd ep = event.getEP();
		List<EVTreasure> addTreasures = ep.getAddTreasures();
		for (EVTreasure ev : addTreasures) {
			Integer treasureId = ev.getId();
			TreasureEnum treasureEnum = TreasureEnum.fromValue(treasureId);
			if (null == treasureEnum) {
				continue;
			}
			switch (treasureEnum) {
				case HEAD_ICON_SXDH_SX:
					userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.SXDH_DDCX, 1, ep.getRd());
					break;
				case HEAD_ICON_SXDH_FX:
					userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.SXDH_TYJW, 1, ep.getRd());
					break;
				case HEAD_ICON_SXDH_ZL:
					userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.SXDH_ZLDZ, 1, ep.getRd());
					break;
				case HEAD_ICON_SXDH_XJ:
					userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.SXDH_TJDD, 1, ep.getRd());
					break;
				case HEAD_ICON_SXDH_YS:
					userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.SXDH_LQHS, 1, ep.getRd());
					break;
				case HEAD_ICON_SXDH_JZ:
					userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.SXDH_WXZZ, 1, ep.getRd());
					break;
				case HEAD_ICON_SXDH_TZ:
					userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.SXDH_TZZZ, 1, ep.getRd());
					break;
				default:
					break;
			}
		}
	}
}
*/
