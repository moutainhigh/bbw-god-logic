package com.bbw.god.server.guild.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.bbw.god.city.chengc.in.event.BuildingLevelUpEvent;
import com.bbw.god.city.chengc.in.event.EPBuildingLevelUp;
import com.bbw.god.city.event.CityArriveEvent;
import com.bbw.god.event.EventParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.BuildingEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.CityTypeEnum;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.card.event.UserCardAddEvent;
import com.bbw.god.gameuser.res.copper.CopperDeductEvent;
import com.bbw.god.gameuser.res.copper.EPCopperDeduct;
import com.bbw.god.gameuser.res.dice.DiceDeductEvent;
import com.bbw.god.gameuser.res.dice.EPDiceDeduct;
import com.bbw.god.gameuser.res.ele.EPEleDeduct;
import com.bbw.god.gameuser.res.ele.EleDeductEvent;
import com.bbw.god.gameuser.res.gold.EPGoldDeduct;
import com.bbw.god.gameuser.res.gold.GoldDeductEvent;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialDeductEvent;
import com.bbw.god.gameuser.treasure.event.EPTreasureAdd;
import com.bbw.god.gameuser.treasure.event.EPTreasureDeduct;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureAddEvent;
import com.bbw.god.gameuser.treasure.event.TreasureDeductEvent;
import com.bbw.god.server.guild.UserGuild;
import com.bbw.god.server.guild.event.EPAddGuildExp;
import com.bbw.god.server.guild.event.EPGuildTaskFinished;
import com.bbw.god.server.guild.event.GuildAddExpEvent;
import com.bbw.god.server.guild.event.GuildTaskFinishedEvent;

/**
 * @author lwb
 * @date 2019???5???17???
 * @version 1.0
 */
@Component
public class GuildTaskProgressListener {

	@Autowired
	GuildEightDiagramsTaskService guildEDTaskService;
	@Autowired
	private GuildInfoService guildInfoService;
	@Autowired
	private GuildUserService guildUserService;
	@Autowired
	private GameUserService gameUserService;

	// ????????????
	@Async
	@EventListener
	public void sellSpecials(SpecialDeductEvent event) {
		EPSpecialDeduct ep = event.getEP();
		List<EPSpecialDeduct.SpecialInfo> specialInfoList = ep.getSpecialInfoList();
		if (WayEnum.TRADE == ep.getWay()) {
			guildEDTaskService.updateTaskProgress(ep.getGuId(), 1001, specialInfoList.size());
		}
	}

	/**
	 * ????????????
	 * 
	 * @param event
	 */
	@Async
	@EventListener
	public void fightWin(CombatFightWinEvent event) {
		EPFightEnd ep = (EPFightEnd) event.getSource();
		if (FightTypeEnum.ATTACK == ep.getFightType() || FightTypeEnum.TRAINING == ep.getFightType()) {
			guildEDTaskService.updateTaskProgress(ep.getGuId(), 1002, 1);
		} else if (FightTypeEnum.YG == ep.getFightType()) {
			guildEDTaskService.updateTaskProgress(ep.getGuId(), 1003, 1);
		}
	}

	/**
	 * ????????????
	 * 
	 * @param event
	 */
	@Async
	@EventListener
	public void diceDeduct(DiceDeductEvent event) {
		EPDiceDeduct ep = event.getEP();
		guildEDTaskService.updateTaskProgress(ep.getGuId(), 1004, ep.getDeductDice());
	}

	/**
	 * ????????????????????????????????????????????????
	 * 
	 * @param event
	 */
	@Async
	@EventListener
	public void addCard(UserCardAddEvent event) {
		EPCardAdd ep = event.getEP();
		if (ep.getWay() == WayEnum.KZ || ep.getWay() == WayEnum.JXZ_AWARD) {
			guildEDTaskService.updateTaskProgress(ep.getGuId(), 1008, ep.getAddCards().size());
		}

	}

	// ????????????????????????
	@Async
	@EventListener
	public void ysDeduct(TreasureDeductEvent event) {
		EPTreasureDeduct ep = event.getEP();
		EVTreasure ev = ep.getDeductTreasure();
		CfgTreasureEntity treasure = TreasureTool.getTreasureById(ev.getId());
		if (treasure.getType() == 10) {
			guildEDTaskService.updateTaskProgress(ep.getGuId(), 1006, ev.getNum());
		}

	}

	// ??????????????????????????????
	@Async
	@EventListener
	public void ysDeduct(GoldDeductEvent event) {
		EPGoldDeduct ep = event.getEP();
		if (ep.getWay() == WayEnum.FLX_SG) {
			guildEDTaskService.updateTaskProgress(ep.getGuId(), 1007, 1);
		}
	}

	// ??????????????????????????????
	@Async
	@EventListener
	public void ysDeduct(CopperDeductEvent event) {
		EPCopperDeduct ep = event.getEP();
		if (ep.getWay() == WayEnum.FLX_SG) {
			guildEDTaskService.updateTaskProgress(ep.getGuId(), 1007, 1);
		}
	}

	// ???????????????????????? ?????? ??????????????????
	@Async
	@EventListener
	public void RandomEvent(CityArriveEvent event) {
		EventParam<Integer> ep = (EventParam<Integer>) event.getSource();
		CfgRoadEntity road = RoadTool.getRoadById(ep.getValue());
		CfgCityEntity city = road.getCity();
		int taskId = 0;
		switch (CityTypeEnum.fromValue(city.getType())) {
		case YD:
			taskId = 1005;
			break;
		case CZ:// ?????????????????? ?????????????????????
		case XRD:
			taskId = 1017;
			break;
		case CC1:// ?????????????????????????????????
		case CC2:
		case CC3:
		case CC4:
		case CC5:
			taskId = 1016;
			break;
		default:
			return;
		}
		guildEDTaskService.updateTaskProgress(ep.getGuId(), taskId, 1);
	}

	// ??????????????????
	@Async
	@EventListener
	public void hsBuyTreasure(TreasureAddEvent event) {
		EPTreasureAdd ep = event.getEP();
		if (ep.getWay() == WayEnum.HEIS) {
			guildEDTaskService.updateTaskProgress(ep.getGuId(), 1009, ep.getAddTreasures().size());
		}

	}

	// ????????????
	@Async
	@EventListener
	public void deductEle(BuildingLevelUpEvent event) {
		EventParam<EPBuildingLevelUp> ep = (EventParam<EPBuildingLevelUp>) event.getSource();
		EPBuildingLevelUp up = ep.getValue();
		up.getLevelUpBuildings().forEach(p -> {
//			System.out.println("???????????????" + BuildingEnum.fromValue(p).getName());
			switch (BuildingEnum.fromValue(p)) {
			case FY:
			case KC:
			case QZ:
			case TCP:
			case JXZ:
			case LBL:
			case DC:
			case LDF:
				guildEDTaskService.updateTaskProgress(ep.getGuId(), 1010, 1);
				break;
			default:
				break;
			}
		});

	}

	// ????????????
	@Async
	@EventListener
	public void deductEle(EleDeductEvent event) {
		EPEleDeduct ep = event.getEP();
		ep.getDeductEles().stream().forEach(p -> {
			guildEDTaskService.updateTaskProgress(ep.getGuId(), 1010 + p.getType() / 10, p.getNum());
		});
	}

	@EventListener
	public void guildAddExp(GuildAddExpEvent event) {
		EPAddGuildExp ep = event.getEP();
		guildInfoService.addGuildExp(ep.getGuId(), ep.getExp());
	}

	@EventListener
	public void guildFinished(GuildTaskFinishedEvent event) {
		EPGuildTaskFinished ep = event.getEP();
		guildEDTaskService.updateEDNumber(ep.getGuId(), ep.getEdNumber());
	}

	// ??????????????????
	@Async
	@EventListener
	public void addGXTreasure(TreasureAddEvent event) {
		EPTreasureAdd ep = event.getEP();
		for (EVTreasure et : ep.getAddTreasures()) {
			if (et.getId() != TreasureEnum.GUILD_CONTRIBUTE.getValue()) {
				continue;
			}
			if (ep.getWay() == WayEnum.Guild_Box || ep.getWay() == WayEnum.Guild_TASK) {
				Optional<UserGuild> userGuildOp = guildUserService.getUserGuildOp(ep.getGuId());
				if (userGuildOp.isPresent()) {
					UserGuild userGuild = userGuildOp.get();
					userGuild.setWeekContrbution(userGuild.getWeekContrbution() + et.getNum());
					gameUserService.updateItem(userGuild);
				}
			}
		}

	}
}
