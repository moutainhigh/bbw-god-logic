package com.bbw.god.game.combat.pve.fst;

import com.bbw.common.ID;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatInfo;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.leadercard.beast.UserLeaderBeastService;
import com.bbw.god.gameuser.leadercard.equipment.UserLeaderEquimentService;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-07-06
 */
@Service
public class FstCombatInitService extends CombatInitService {
	@Autowired
	private BattleCardService battleCardService;
	@Autowired
	private CombatRedisService combatRedisService;
	@Autowired
	private CombatRunesPerformService runesPerformService;
	
	public Combat init(CPlayerInitParam p1,CPlayerInitParam p2,boolean p1IsFirst){
		runesPerformService.runInitCombatParamRunes(p1, p2, null);
		Combat combat=new Combat();
		combat.setId(ID.INSTANCE.nextId());
		combat.setFightType(FightTypeEnum.FST);
		Player player1=initPlayer(PlayerId.P1,p1);
		setHpByMaxOfPlayerHpOrCardsSumHp(player1);
		Player player2=initPlayer(PlayerId.P2,p2);
		setHpByMaxOfPlayerHpOrCardsSumHp(player2);
		combat.setP1(player1);
		combat.setP2(player2);
		combat.setFirst(p1IsFirst?PlayerId.P1:PlayerId.P2);

		CombatInfo combatInfo = CombatInfo.instance(combat);
		combatRedisService.saveCombatInfo(combatInfo);

		//执行初始化符文效果
		runesPerformService.runInitCombatRunes(combat.getFirstPlayer(), combat.getSecondPlayer(), combat.getId());
		battleCardService.firstMoveDrawCardsToHand(combat.getFirstPlayer());
		battleCardService.firstMoveDrawCardsToHand(combat.getSecondPlayer());
		runesPerformService.runInitRoundRunes(combat.getFirstPlayer(), combat.getSecondPlayer(), combat.getId());
		return combat;
	}
	
	/**
	 * 初始化玩家
	 *
	 * @return
	 */
	protected Player initPlayer(PlayerId playerId, CPlayerInitParam cpp) {
		Player player = Player.instance(cpp, getPlayerInitHp(cpp.getLv()), getPlayerInitMp(cpp.getLv()));
		player.initBuffs(cpp.getBuffs());
		player.setId(playerId);
		int beginPos = PositionService.getDrawCardsBeginPos(player.getId());
		// 初始化牌堆
		int id = playerId.getValue() * 1000;
		int minHv = 10;
		String specialCards = "";
		for (CCardParam bcd : cpp.getCards()) {
			if (bcd.ifSpecial()) {
				specialCards += bcd.getId() + "," + bcd.buildSkillAndSymbolStr();
			}
			BattleCard card = initBattleCard(bcd, id++);
			card.setPos(beginPos++);
			card.setStars(bcd.getStar());
			card.setType(TypeEnum.fromValue(bcd.getType()));
			player.getDrawCards().add(card);
			if (card.getHv() < minHv) {
				minHv = card.getHv();
			}
		}
		player.setSpecialCards(specialCards);
		player.setMinCardHv(minHv);
		shuffleDrawCards(player.getDrawCards());
		return player;
	}
}
