package com.bbw.god.fight.processor;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.yeg.UserYeGEliteService;
import com.bbw.god.city.yeg.YeGuaiEnum;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.ResWayType;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.unique.UserMonster;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.monster.MonsterLogic;
import com.bbw.god.server.monster.MonsterService;
import com.bbw.god.server.monster.ServerMonster;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 野怪出没战斗
 *
 * @author suhq
 * @date 2019年2月18日 下午4:42:59
 */
@Slf4j
@Service
public class MonsterFightProcessor extends AbstractFightProcessor {

	@Autowired
	private ServerService serverService;
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private MonsterService monsterService;
	@Autowired
	private UserYeGEliteService userYeGEliteService;
	@Autowired
	private MonsterLogic monsterLogic;


	@Override
	public FightTypeEnum getFightType() {
		return FightTypeEnum.HELP_YG;
	}

	@Override
	public WayEnum getWay() {
		return  WayEnum.FIGHT_HELP_YG;
	}

	@Override
	public CombatPVEParam getOpponentInfo(Long uid, Long oppId, boolean fightAgain) {
		RDFightsInfo attackMonster = monsterLogic.attackMonster(uid, oppId);
		CombatPVEParam pveParam = toCombatPVEParam(attackMonster);
		pveParam.setAwardkey(-1);
		pveParam.setOpponentId(oppId);
		return pveParam;
	}

	@Override
	public void settleBefore(GameUser gu, FightSubmitParam param) {
		super.settleBefore(gu, param);
	}

	@Override
	public void failure(GameUser gu, RDFightResult rd, FightSubmitParam param) {
		ServerMonster serverMonster = serverService.getServerData(gu.getServerId(), param.getMonsterId(), ServerMonster.class);
		// 更新怪物血量和参与者
		if (serverMonster == null) {
			throw new ExceptionForClientTip("monster.not.exist");
		}
		int oppLostBlood = param.getOppLostBlood(); // 对手损失血量
		int blood = serverMonster.getBlood() - oppLostBlood;
		if (!serverMonster.getJoiners().contains(gu.getId() + "")) {
			serverMonster.updateJoiners(gu.getId());
		}
		if (blood <= 0) {
			//多人抢怪且造成多人打的血量超过野怪的血量时，且参与的人都没有打赢，则全部发参与奖
			YeGuaiEnum yeGuaiEnum = YeGuaiEnum.YG_FRIEND;
			if (YeGuaiEnum.isYeGuaiFriend(rd.getYeGType())) {
				yeGuaiEnum = rd.getYeGType();
			}
			// 处理野怪
			serverMonster.setBeDefeated(true);
			serverMonster.setBlood(0);
			serverService.updateServerData(serverMonster);
			// 给发现者发放奖励
			monsterService.sendDiscoverAward(yeGuaiEnum, serverMonster);
			// 给参与者发放邮件奖励
			monsterService.sendJoinerAward(serverMonster);
			serverService.updateServerData(serverMonster);
			return;
		}
		serverMonster.deductBlood(oppLostBlood);
		serverService.updateServerData(serverMonster);
		this.userTreasureEffectService.effectAsLBJQ(gu, rd);
	}
	@Override
	public void handleAward(GameUser gu, RDFightResult rd,FightSubmitParam param) {
		ServerMonster serverMonster = serverService.getServerData(gu.getServerId(), param.getMonsterId(), ServerMonster.class);
		if (serverMonster == null) {
			throw new ExceptionForClientTip("monster.not.exist");
		} else {
			if (serverMonster.getBeDefeated()) {
				UserMonster umHelp = gameUserService.getSingleItem(gu.getId(), UserMonster.class);
				umHelp.setNextBeatTime(DateUtil.now());
				gameUserService.updateItem(umHelp);
				throw new ExceptionForClientTip("monster.is.defeated");
			}
		}
		// 发放奖励
		int gainExp = getExp(gu, param.getOppLostBlood(), param);
		gainExp /= 5;
		int baseCopper = gainExp;
		// 铜钱加成 10V1版本友怪去除财神卡加成
		double copperAddRate = getCopperAddRate(0, 0);
		int extraCopper = (int) (baseCopper * copperAddRate);
		// 处理战斗铜钱
		EPCopperAdd copperAdd = new EPCopperAdd(new BaseEventParam(gu.getId(), getWay(), rd), baseCopper, baseCopper);
		copperAdd.addCopper(ResWayType.Extra, extraCopper);
		ResEventPublisher.pubCopperAddEvent(copperAdd);
		YeGuaiEnum yeGuaiEnum = YeGuaiEnum.YG_FRIEND;
		if (YeGuaiEnum.isYeGuaiFriend(rd.getYeGType())) {
			yeGuaiEnum = rd.getYeGType();
		}
		// 处理战斗经验
		gainJinYanDan(gu.getId(), param, gainExp, rd);
		ResEventPublisher.pubExpAddEvent(gu.getId(), gainExp, getWay(), rd);
		// 处理卡牌
		GameUser discover = gameUserService.getGameUser(serverMonster.getGuId());
		CfgCardEntity card = monsterService.getCardAward(gu, yeGuaiEnum, discover.getLevel());
		CardEventPublisher.pubCardAddEvent(gu.getId(), card.getId(), getWay(), "帮好友打怪获得", rd);

		// 处理野怪
		serverMonster.setBeDefeated(true);
		if (!serverMonster.getJoiners().contains(gu.getId() + "")) {
			serverMonster.updateJoiners(gu.getId());
		}
		serverMonster.setBlood(0);
		serverService.updateServerData(serverMonster);
		// 给发现者发放奖励
		monsterService.sendDiscoverAward(gu, yeGuaiEnum, serverMonster);
		// 给参与者发放邮件奖励
		monsterService.sendJoinerAward(gu, serverMonster);

		if (serverMonster.getProperty() != null && yeGuaiEnum != null && yeGuaiEnum.equals(YeGuaiEnum.YG_ELITE_FRIEND)) {
			userYeGEliteService.updateYeGLevel(serverMonster.getProperty(), serverMonster.getGuId());
		}
		rd.setWinDes("恭喜您帮好友打败了野怪！");
	}
}
