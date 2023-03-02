package com.bbw.god.game.combat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import com.bbw.common.JSONUtil;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleSolution;
import com.bbw.god.game.combat.data.card.BattleSolutionSelector;
import com.bbw.god.game.combat.data.param.CardMovement;
import com.bbw.god.game.combat.deploy.DeployCardsStrategy;

import lombok.extern.slf4j.Slf4j;

/**
 * 手牌上阵方案
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-07 17:00
 */
@Slf4j
@Service
public class DeployCardsSolutionService {

	@NonNull
	private List<int[]> getCardsToBattleSolutions(BattleCard[] cards, BattleSolutionSelector selector) {
		//参数校验
		int handCardsCount = 0;//手牌数量
		for (BattleCard card : cards) {
			if (null != card) {
				handCardsCount++;
			}
		}
		//存储有效的解决方案
		List<int[]> solutions = new ArrayList<>();
		if (0 == handCardsCount) {
			return solutions;
		}
		//上阵卡牌<=n的所有可能的上阵组合。
		for (int solutionCardNum = selector.getMaxCardNum(); solutionCardNum > 0; solutionCardNum--) {
			//从 handCardsCount 张手牌中选出 solutionCardNum 张卡的方案集合
			int[][] mnSolution = SolutionTable.getSolutions(handCardsCount, solutionCardNum);
			//System.out.println("m=" + m + ",n=" + count + " 解决方案数量：" + mnSolution.length);
			for (int i = 0; i < mnSolution.length; i++) {
				//根据业务规则判定方案是否有效
				BattleSolution battleSolution = new BattleSolution(selector, mnSolution[i]);
				if (validSolution(cards, battleSolution)) {
					solutions.add(mnSolution[i]);
				}
			}
		}
		return solutions;
	}

	private boolean validSolution(final BattleCard[] handCards, BattleSolution battleSolution) {
		//System.out.print("方案明细:" + JSONUtil.toJson(battleSolution));
		//验证卡牌位置，可能手动上牌已经移动了此解决方案中的某张卡牌，那么这个解决方案就无效了。
		//这个判定要排在一定要放在 第1位判定，否则后续的校验需要判定 手牌是否为空
		if (!validCardExists(handCards, battleSolution)) {
			return false;
		}
		//验证法力
		if (!validMp(handCards, battleSolution)) {
			//log.debug("法力验证失败！");
			return false;
		}
		//验证云台
		if (!validYunTai(handCards, battleSolution)) {
			//log.debug("云台验证失败！");
			return false;
		}

		return true;
	}

	private boolean validCardExists(final BattleCard[] handCards, final BattleSolution battleSolution) {
		int[] solution = battleSolution.getSolution();
		for (int index = 0; index < solution.length; index++) {
			if (null == handCards[solution[index]]) {
				return false;
			}
		}
		return true;
	}

	private boolean validMp(final BattleCard[] handCards, final BattleSolution battleSolution) {
		int costMp = 0;
		int[] solution = battleSolution.getSolution();
		for (int index = 0; index < solution.length; index++) {
			BattleCard card = handCards[solution[index]];
			//手动上牌后再自动战斗可能导致手牌为空
			if (null == card) {
				continue;
			}
			costMp += card.getMp();
		}
		//log.debug("玩家目前拥有Mp：" + battleSolution.getSelector().getMpLimit() + ",此上阵方案总数Mp:" + costMp);
		return battleSolution.getSelector().getMpLimit() >= costMp;
	}

	private boolean validYunTai(final BattleCard[] handCards, final BattleSolution battleSolution) {
		//不需要云台卡
		if (battleSolution.getSelector().getEmptyYunTaiPosNum() < 1) {
			return true;
		}
		//此方案产生的允许阵位的空位数，空位抵消云台卡需求
		int freeCount = battleSolution.getSelector().getEmptyBattlePosNum() - battleSolution.getSolution().length;
		//允许的空位数量 > 如果云台卡牌要求的数量 ，则表示此方案不需要考虑云台卡牌
		if (freeCount >= battleSolution.getSelector().getEmptyYunTaiPosNum()) {
			return true;
		}
		//统计云台卡数量
		int need = battleSolution.getSelector().getEmptyYunTaiPosNum() - freeCount;
		int count = 0;
		int[] solution = battleSolution.getSolution();
		for (int index = 0; index < solution.length; index++) {
			BattleCard card = handCards[solution[index]];
			//手动上牌后再自动战斗可能导致手牌为空
			if (null == card) {
				continue;
			}
			if (card.canFly()) {
				count++;
			}
		}
		return count >= need;
	}

	/**
	 * 上牌
	 * @param combat
	 * @param playing
	 * @param randomCards: 可选择的卡牌
	 * @param maxCardNum: 最多上多少张牌。
	 * @return
	 */
	@NonNull
	public List<CardMovement> randomSolutionCardToBattle(Player player, List<BattleCard> randomCards, int maxCardNum) {

		if (null == randomCards || randomCards.isEmpty()) {
			return new ArrayList<>(0);
		}

		//战场空位
		//空位
		int[] emptyBattlePos = player.getEmptyBattlePos(true);
		if (0 == emptyBattlePos.length) {
			return new ArrayList<>(0);
		}
		int maxLimitCardNum = Math.min(maxCardNum, emptyBattlePos.length);

		//空的云台位置数量
		int emptyYunTaiPosNum = PositionService.getYunTaiPosCount(emptyBattlePos);

		//上阵限制，法力值无限，
		BattleSolutionSelector selector = BattleSolutionSelector.getNoMpLimitSelector(emptyBattlePos.length, emptyYunTaiPosNum, maxLimitCardNum);
		//所有符合条件的上阵解决方案
		BattleCard[] cards = randomCards.toArray(new BattleCard[0]);
		List<int[]> solutions = getCardsToBattleSolutions(cards, selector);
		if (solutions.isEmpty()) {
			return new ArrayList<>(0);
		}
		//选择牌最多的那个方案
		Collections.shuffle(solutions);
		int index = 0;
		for (int i = 0; i < solutions.size(); i++) {
			if (solutions.get(i).length > solutions.get(index).length) {
				index = i;
			}
		}
		//选中的上阵方案
		int[] solution = solutions.get(index);

		List<CardMovement> atks = upCards(player.getId(), cards, emptyBattlePos, solution);
		return atks;
	}

	/**
	 * 根据方案形成上牌行动
	 * @param player
	 * @param emptyBattlePos
	 * @param solution
	 * @return
	 */
	private List<CardMovement> upCards(PlayerId playerId, BattleCard[] cards, int[] emptyBattlePos, int[] solution) {
		List<CardMovement> moves = new ArrayList<>(solution.length);

		List<Integer> emptyPos = new ArrayList<>();
		for (int index : emptyBattlePos) {
			emptyPos.add(index);
		}

		List<Integer> solutionIndex = new ArrayList<>();
		for (int index : solution) {
			solutionIndex.add(index);
		}

		//先处理云台位置
		int ytIndex = -1;
		final int ytPos = PositionService.getYunTaiPos(playerId);
		Optional<Integer> ytEmptyPos = emptyPos.stream().filter(yt -> yt == ytPos).findAny();
		if (ytEmptyPos.isPresent()) {//有云台空位
			for (int i = 0; i < solutionIndex.size(); i++) {
				BattleCard card = cards[solutionIndex.get(i)];
				if (card.canFly()) {
					ytIndex = i;
					CardMovement movement = new CardMovement(card.getPos(), ytPos);
					moves.add(movement);
					break;
				}
			}
		}

		//已经放置了云台卡
		if (moves.size() > 0) {
			solutionIndex.remove(ytIndex);
		}
		//无论是有已经有卡牌部署到云台位
		emptyPos = emptyPos.stream().filter(pos -> pos.intValue() != ytPos).collect(Collectors.toList());

		//TODO:这里可以有智能算法
		Collections.shuffle(emptyPos);
		for (int i = 0; i < solutionIndex.size(); i++) {
			BattleCard card = cards[solutionIndex.get(i)];
			int upToPos = emptyPos.get(i);
			CardMovement movement = new CardMovement(card.getPos(), upToPos);
			moves.add(movement);
		}

		return moves;
	}

	/**
	 * 按照业务规则自动上牌。受Mp，阵位限制
	 * @param combat
	 * @param player
	 * @param strategy
	 * @return
	 */
	public List<CardMovement> autoDeployCardsWithRuleLimit(Combat combat, Player player, DeployCardsStrategy strategy) {
		int mpLimit = player.getMp();//mp值限制
		List<Integer> freeBattleIndex = player.getUnlockBattleIndex(combat.getRound(), combat.getFightType());//空阵位索引
		int[] emptyBattlePos = PositionService.getBattleCardPos(player.getId(), freeBattleIndex);
		int emptyYunTaiPosNum = PositionService.getYunTaiPosCount(emptyBattlePos);//可上阵的阵位中，有多少个云台位。云台位置需要飞行技能，需要特殊判定

		//player.printHandCards();

		int handCardsCount = player.getHandCardsCount();//手牌数量
		int emptyBattlePosCount = freeBattleIndex.size();//战场空的阵位数量
		int maxCardNum = Math.min(handCardsCount, emptyBattlePosCount);//最多可上阵的卡牌数量
		BattleSolutionSelector selector = BattleSolutionSelector.getMpLimitSelector(emptyBattlePosCount, emptyYunTaiPosNum, maxCardNum, mpLimit);
		log.debug("" + selector);
		List<int[]> solutions = getCardsToBattleSolutions(player.getHandCards(), selector);
		if (null == solutions || solutions.isEmpty()) {
			return new ArrayList<CardMovement>(0);
		}
		log.debug("\n有效方案数量：" + solutions.size() + "。明细：\n" + JSONUtil.toJson(solutions));

		int[] solution = strategy.getSolution(combat, player, solutions, emptyBattlePos);
		log.debug("\n策略选中的方案：" + solutions.size() + "。明细：" + JSONUtil.toJson(solution));

		List<CardMovement> movements = upCards(player.getId(), player.getHandCards(), emptyBattlePos, solution);
		return movements;
	}
}