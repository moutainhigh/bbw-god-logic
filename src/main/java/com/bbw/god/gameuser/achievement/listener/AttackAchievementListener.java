/*
package com.bbw.god.gameuser.achievement.listener;

import com.bbw.common.ListUtil;
import com.bbw.god.city.event.EPCityAdd;
import com.bbw.god.city.event.UserCityAddEvent;
import com.bbw.god.event.EventParam;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.combat.event.EPEliteYeGuaiFightWin;
import com.bbw.god.game.combat.event.EVFightEnd;
import com.bbw.god.game.combat.event.EliteYeGuaiFightWinEvent;
import com.bbw.god.game.combat.event.FightWinEvent;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.AchievementSerialEnum;
import com.bbw.god.gameuser.achievement.UserAchievement;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.maou.alonemaou.UserAloneMaouData;
import com.bbw.god.server.maou.alonemaou.event.AloneMaouKilledEvent;
import com.bbw.god.server.maou.alonemaou.event.EPAloneMaou;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import com.bbw.god.server.maou.bossmaou.attackinfo.BossMaouAttackSummary;
import com.bbw.god.server.maou.bossmaou.attackinfo.BossMaouAttackSummaryService;
import com.bbw.god.server.maou.bossmaou.event.BossMaouAwardSendEvent;
import com.bbw.god.server.maou.bossmaou.event.EPBossMaou;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

*/
/**
 * @author suchaobin
 * @description 征战成就监听
 * @date 2020/2/21 9:09
 * <p>
 * 攻城成就
 * @param event
 * <p>
 * 独战魔王击杀成就
 * @param event
 * <p>
 * 根据所击杀的独战魔王等级来设置玩家魔王成就进度
 * @param maouLevel
 * @param guId
 * @param rd
 *//*

@Component
public class AttackAchievementListener {
	@Autowired
	private GameUserService gameUserService;
	@Autowired
	private UserAchievementService userAchievementService;
	@Autowired
	private BossMaouAttackSummaryService bossMaouAttackSummaryService;

	*/
/**
 * 攻城成就
 *
 * @param event
 *//*

	@EventListener
	@Order(1000)
	public void addUserCity(UserCityAddEvent event) {
		EventParam<EPCityAdd> ep = (EventParam<EPCityAdd>) event.getSource();
		long guId = ep.getGuId();
		RDFightResult rd = (RDFightResult) ep.getRd();
		CfgCityEntity city = CityTool.getCityById(ep.getValue().getCityId());
		// 攻城成就
		this.userAchievementService.achieve(guId, AchievementSerialEnum.FIRST_CITY, 1, rd);
		this.userAchievementService.achieve(guId, AchievementSerialEnum.OWN_CITY, 1, rd);
		int cityLevelSerial = 4010 + city.getLevel() * 10;
		this.userAchievementService.achieve(guId, AchievementSerialEnum.fromValue(cityLevelSerial), 1, rd);
		int cityCountrySerial = 4060 + city.getCountry();
		this.userAchievementService.achieve(guId, AchievementSerialEnum.fromValue(cityCountrySerial), 1, rd);
	}


	@EventListener
	@Order(1000)
	public void fightWin(FightWinEvent event) {
		EVFightEnd ep = (EVFightEnd) event.getSource();
		long guId = ep.getGuId();
		RDCommon rd = ep.getRd();
		switch (ep.getFightType()) {
			case TRAINING:
				this.userAchievementService.achieve(guId, AchievementSerialEnum.TRAINING_WIN, 1, rd);
				break;
			case YG:
				FightSubmitParam param = ep.getFightSubmit();
				String resultStr = param.getResultStr();
				String[] resultPart = resultStr.split(";");
				String[] opp = resultPart[1].split(",");
				// 对手等级
				int oppLevel = Integer.valueOf(opp[0]);
				if (param.getNotLostCard() != null && oppLevel >= 15) {
					this.userAchievementService.achieve(guId, AchievementSerialEnum.YG_NOT_LOST_CARD, 1, rd);
				}
				if (param.getNotLostBlood() != null && oppLevel >= 15) {
					this.userAchievementService.achieve(guId, AchievementSerialEnum.YG_NOT_LOST_BLOOD, 1, rd);
				}
				this.userAchievementService.achieve(guId, AchievementSerialEnum.YG_WIN, 1, rd);
				break;
			case HELP_YG:
				this.userAchievementService.achieve(guId, AchievementSerialEnum.HELP_YG_WIN, 1, rd);
				break;
			default:
				break;
		}
	}

	*/
/**
 * 独战魔王击杀成就
 *
 * @param event
 * <p>
 * 根据所击杀的独战魔王等级来设置玩家魔王成就进度
 * @param maouLevel
 * @param guId
 * @param rd
 *//*

	@EventListener
	@Order(1000)
	public void killAloneMaou(AloneMaouKilledEvent event) {
		EPAloneMaou ep = event.getEP();
		Long guId = ep.getGuId();
		RDCommon rd = ep.getRd();
		Integer maouType = ep.getAloneMaou().getType();
		Integer maouLevel = ep.getMaouLevelInfo().getMaouLevel();
		setMaouAchievement(maouLevel, guId, rd);
		UserAchievement userAchievement = this.userAchievementService.getUserAchievement(guId, 840 + maouType);
		int uProcess = userAchievement == null ? 0 : userAchievement.getValue();
		if (uProcess < maouLevel) {
			AchievementSerialEnum achievementSerialEnum = AchievementSerialEnum.fromValue(4200 + maouType);
			if (userAchievement == null) {
				this.userAchievementService.achieve(guId, achievementSerialEnum, maouLevel, rd);
			} else {
				this.userAchievementService.resetAchievement(guId, achievementSerialEnum, maouLevel, rd);
			}
		}
	}

	*/
/**
 * 根据所击杀的独战魔王等级来设置玩家魔王成就进度
 *
 * @param maouLevel
 * @param guId
 * @param rd
 *//*

	private void setMaouAchievement(int maouLevel, long guId, RDCommon rd) {
		UserAloneMaouData userAloneMaouData = this.gameUserService.getSingleItem(guId, UserAloneMaouData.class);
		int killMaouNum = userAloneMaouData.getKillMaouNum(maouLevel);
		UserAchievement userAchievement;
		switch (maouLevel) {
			case 7:
				userAchievement = this.userAchievementService.getUserAchievement(guId, 830);
				if (userAchievement == null) {
					this.userAchievementService.achieve(guId, AchievementSerialEnum.MAOU_XYFM, killMaouNum, rd);
				} else {
					this.userAchievementService.resetAchievement(guId, AchievementSerialEnum.MAOU_XYFM, killMaouNum,
							rd);
				}
				break;
			case 10:
				userAchievement = this.userAchievementService.getUserAchievement(guId, 840);
				if (userAchievement == null) {
					this.userAchievementService.achieve(guId, AchievementSerialEnum.MAOU_NWHS, killMaouNum, rd);
				} else {
					this.userAchievementService.resetAchievement(guId, AchievementSerialEnum.MAOU_NWHS, killMaouNum,
							rd);
				}
				break;
			default:
				break;
		}
	}

	@EventListener
	@Order(1000)
	public void sendMaouAward(BossMaouAwardSendEvent event) {
		EPBossMaou ep = event.getEP();
		ServerBossMaou bossMaou = ep.getBossMaou();
		List<BossMaouAttackSummary> ranker = bossMaouAttackSummaryService.getAttackInfoSorted(bossMaou);
		if (ListUtil.isNotEmpty(ranker)) {
			BossMaouAttackSummary summary = ranker.get(0);
			Integer beatedBlood = summary.getBeatedBlood();
			Integer totalBlood = bossMaou.getTotalBlood();
			if (getPercent(beatedBlood, totalBlood) >= 0.7) {
				this.userAchievementService.achieve(summary.getGuId(), AchievementSerialEnum.MAOU_RANK, 1, ep.getRd());
			}
		}
	}

	private Float getPercent(int num, int total) {
		Float percent = (float) num / (float) total;
		return percent;
	}

	@EventListener
	@Order(1000)
	public void eliteYeGuaiFightWin(EliteYeGuaiFightWinEvent event) {
		EPEliteYeGuaiFightWin ep = event.getEP();
		Integer type = ep.getType();
		Integer cardLevel = ep.getCardLevel();
		Integer cardHierarchy = ep.getCardHierarchy();
		if (40 == cardLevel && 10 == cardHierarchy) {
			AchievementSerialEnum achievementSerialEnum = AchievementSerialEnum.fromValue(4250 + type);
			if (!this.userAchievementService.isFinish(ep.getGuId(), 14500 + type)) {
				this.userAchievementService.achieve(ep.getGuId(), achievementSerialEnum, 1, ep.getRd());
				this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.ALL_ELITE_YG, 1, ep.getRd());
			}
		}
	}
}
*/
