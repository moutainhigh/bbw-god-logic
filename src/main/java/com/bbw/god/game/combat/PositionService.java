package com.bbw.god.game.combat;

import com.bbw.exception.CoderException;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;

import java.util.List;
import java.util.Optional;

/**
 * 与客户端通讯的战场阵位协议。
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 00:36
 */
public class PositionService {
	/**
	 * P2玩家的位置标识偏移量
	 */
	private static final int P2_POSITION_OFFSET = 1000;
	/**
	 * p1召唤师位置
	 */
	private static final int P1_ZHAO_HUAN_SHI = 10;
	/**
	 * 阵位（战场），位置标识，依次为：云台、先锋、前军、中军、后军、军师
	 */
	private static final int P1_BATTLE_POSITION[] = { 11, 12, 13, 14, 15, 16 };
	/**
	 * 第1回合解锁的位置
	 */
	private static final int P1_ROUND1_BATTLE_POSITION[] = { 11, 13 };
	/**
	 * 第1回合解锁的位置（诛仙阵）
	 */
	private static final int P1_ROUND1_BATTLE_POSITION_ZXZ[] = { 11, 12, 13, 14 };
	/**
	 * 第2回合解锁的位置
	 */
	private static final int P1_ROUND2_BATTLE_POSITION[] = { 11, 12, 13, 14 };
	/**
	 * 第2回合解锁的位置（诛仙阵）
	 */
	private static final int P1_ROUND2_BATTLE_POSITION_ZXZ[] = { 11, 12, 13, 14, 15, 16 };
	/**
	 * 手牌位置信息
	 */
	private static final int P1_HAND_POSITION[] = { 100, 101, 102, 103, 104, 105 };

	/**
	 * 返回召唤师的位置
	 *
	 * @param playerId
	 * @return
	 */
	public static int getZhaoHuanShiPos(PlayerId playerId) {
		int pos = PlayerId.P1 == playerId ? P1_ZHAO_HUAN_SHI : P1_ZHAO_HUAN_SHI + P2_POSITION_OFFSET;
		return pos;
	}

	/**
	 * 阵位解锁是否解锁
	 *
	 * @param round：回合数
	 * @param playerId:玩家标识
	 * @param toPos:阵位
	 * @return
	 */
	public static boolean positionUnlock(final int round, PlayerId playerId, final int toPos, FightTypeEnum fightTypeEnum) {
		if (round < 1) {
			throw CoderException.high("回合数要从1开始！");
		}
		if (PlayerId.P1 == playerId && toPos > P2_POSITION_OFFSET) {
			throw CoderException.high(playerId + "玩家不能将卡牌放置到" + toPos + "阵位！");
		}
		if (PlayerId.P2 == playerId && toPos < P2_POSITION_OFFSET) {
			throw CoderException.high(playerId + "玩家不能将卡牌放置到" + toPos + "阵位！");
		}
		int pos = toP1Pos(toPos);
		int[] roundBattlePositions;
		switch (round) {
			case 1:
				roundBattlePositions = P1_ROUND1_BATTLE_POSITION;
				if (FightTypeEnum.ZXZ == fightTypeEnum || FightTypeEnum.ZXZ_FOUR_SAINTS == fightTypeEnum) {
					roundBattlePositions = P1_ROUND1_BATTLE_POSITION_ZXZ;
				}
				for (int i = 0; i < roundBattlePositions.length; i++) {
					if (pos == roundBattlePositions[i]) {
						return true;
					}
				}
				return false;
			case 2:
				roundBattlePositions = P1_ROUND2_BATTLE_POSITION;
				if (FightTypeEnum.ZXZ == fightTypeEnum || FightTypeEnum.ZXZ_FOUR_SAINTS == fightTypeEnum) {
					roundBattlePositions = P1_ROUND2_BATTLE_POSITION_ZXZ;
				}
				for (int i = 0; i < roundBattlePositions.length; i++) {
					if (pos == roundBattlePositions[i]) {
						return true;
					}
				}
			return false;
		default:
			return true;
		}
	}

	/**
	 * 根据位置标识，获取
	 *
	 * @param pos
	 * @return
	 */
	public static PlayerId getPlayerIdByPos(final int pos) {
		PlayerId playerId = pos > P2_POSITION_OFFSET ? PlayerId.P2 : PlayerId.P1;
		return playerId;
	}

	/**
	 * 根据玩家<font color=red>手牌索引</font>获取手牌位置标识
	 *
	 * @param playerId:玩家标识
	 * @param index:        Combat.Player.playingCards 数组下标索引
	 * @return
	 */
	public static int getHandCardPos(PlayerId playerId, int index) {
		if (index < 0 || index >= P1_HAND_POSITION.length) {
			throw CoderException.high("无效的" + playerId + "玩家手牌索引。index=[" + index + "]");
		}
		if (playerId == PlayerId.P1) {
			return P1_HAND_POSITION[index];
		}
		return P2_POSITION_OFFSET + P1_HAND_POSITION[index];
	}

	/**
	 * 根据玩家<font color=red>手牌卡牌索引</font>获取手牌位置标识
	 *
	 * @param index: Combat.Player.playingCards 数组下标索引
	 * @return
	 */
	public static int getHandCardIndex(final int pos) {
		int p1Pos = toP1Pos(pos);
		for (int i = 0; i < P1_HAND_POSITION.length; i++) {
			if (p1Pos == P1_HAND_POSITION[i]) {
				return i;
			}
		}
		throw CoderException.high("无效的手牌位置信息。pos=[" + pos + "]");
	}

	/**
	 * 根据玩家<font color=red>上阵卡牌索引</font>获取上阵位置标识
	 *
	 * @param playerId:玩家标识
	 * @param index:        Combat.Player.playingCards 数组下标索引
	 * @return
	 */
	public static int getBattleCardPos(PlayerId playerId, int index) {
		if (index < 0 || index >= P1_BATTLE_POSITION.length) {
			throw CoderException.high("无效的" + playerId + "玩家上阵卡牌索引。index=[" + index + "]");
		}
		if (playerId == PlayerId.P1) {
			return P1_BATTLE_POSITION[index];
		}
		return P2_POSITION_OFFSET + P1_BATTLE_POSITION[index];
	}

	/**
	 * 根据玩家<font color=red>上阵卡牌索引</font>获取上阵位置标识
	 *
	 * @param playerId:玩家标识
	 * @param index:        Combat.Player.playingCards 数组下标索引
	 * @return
	 */
	public static int[] getBattleCardPos(PlayerId playerId, List<Integer> index) {
		int[] pos = new int[index.size()];
		for (int i = 0; i < index.size(); i++) {
			pos[i] = getBattleCardPos(playerId, index.get(i));
		}
		return pos;
	}

	/**
	 * 根据玩家的<font color=red>上阵位置标识</font>获取上阵卡牌下标
	 *
	 * @param pos: 与客户端协议的位置标识
	 * @return
	 */
	public static int getBattleCardIndex(final int pos) {
		int p1Pos = toP1Pos(pos);
		for (int i = 0; i < P1_BATTLE_POSITION.length; i++) {
			if (p1Pos == P1_BATTLE_POSITION[i]) {
				return i;
			}
		}
		throw CoderException.high("无效的阵位信息。pos=[" + pos + "]");
	}

	/**
	 * 是否是召唤师的位置
	 *
	 * @param pos
	 * @return
	 */
	public static boolean isZhaoHuanShiPos(final int pos) {
		return P1_ZHAO_HUAN_SHI == pos || (P1_ZHAO_HUAN_SHI + P2_POSITION_OFFSET) == pos;
	}

	/**
	 * 是否是战场位置
	 *
	 * @param pos
	 * @return
	 */
	public static boolean isPlayingPos(final int pos) {
		for (int p : P1_BATTLE_POSITION) {
			if (p == pos || p + P2_POSITION_OFFSET == pos) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否是云台阵位
	 *
	 * @param pos
	 * @return
	 */
	public static boolean isYunTaiPos(final int pos) {
		return P1_BATTLE_POSITION[0] == toP1Pos(pos);
	}

	/**
	 * 是否是前锋位置
	 *
	 * @param pos
	 * @return
	 */
	public static boolean isXianFengPos(final int pos) {
		return P1_BATTLE_POSITION[1] == toP1Pos(pos);
	}

	/**
	 * 是否是中军位置
	 *
	 * @param pos
	 * @return
	 */
	public static boolean isZhongJunPos(final int pos) {
		return P1_BATTLE_POSITION[3] == toP1Pos(pos);
	}

	/**
	 * 是否是军师位置
	 *
	 * @param pos
	 * @return
	 */
	public static boolean isJunShiPos(final int pos) {
		return P1_BATTLE_POSITION[5] == toP1Pos(pos);
	}

	/**
	 * 获取云台位置
	 *
	 * @param playerId
	 * @return
	 */
	public static int getYunTaiPos(final PlayerId playerId) {
		if (PlayerId.P1 == playerId) {
			return P1_BATTLE_POSITION[0];
		}
		return P1_BATTLE_POSITION[0] + P2_POSITION_OFFSET;
	}

	/**
	 * 云台位置数量
	 *
	 * @param battlePos
	 * @return
	 */
	public static int getYunTaiPosCount(final int[] battlePos) {
		int count = 0;
		for (int pos : battlePos) {
			if (isYunTaiPos(pos)) {
				count++;
			}
		}
		return count;
	}

	/** 转化成P1位置信息进行计算 */
	public static int toP1Pos(final int pos) {
		int adjustPos = pos > P2_POSITION_OFFSET ? pos - P2_POSITION_OFFSET : pos;
		// 转化后的位置不会大于 P2_POSITION_OFFSET
		if (adjustPos > P2_POSITION_OFFSET) {
			throw CoderException.fatal("无效的位置标识！pos=[" + pos + "]");
		}
		return adjustPos;
	}

	/**
	 * 获得牌堆下标起始位
	 */
	public static int getDrawCardsBeginPos(PlayerId playerId) {
		return playerId == PlayerId.P1 ? 200 : 200 + P2_POSITION_OFFSET;
	}

	/**
	 * 获得坟场下标起始位
	 */
	public static int getDiscardBeginPos(PlayerId playerId) {
		return playerId == PlayerId.P1 ? 300 : 300 + P2_POSITION_OFFSET;
	}

	/**
	 * 获得援军下标起始位
	 */
	public static int getReinforceCardsBeginPos(PlayerId playerId) {
		return playerId == PlayerId.P1 ? 400 : 400 + P2_POSITION_OFFSET;
	}

	/**
	 * 获取异次元下标起始位
	 *
	 * @param playerId
	 * @return
	 */
	public static int getDegeneratorBeginPos(PlayerId playerId) {
		return playerId == PlayerId.P1 ? 500 : 500 + P2_POSITION_OFFSET;
	}

	/**
	 * 根据卡牌位置获取牌堆类型
	 *
	 * @param cardPos
	 * @return
	 */
	public static PositionType getPositionType(int cardPos) {
		int p1Pos = toP1Pos(cardPos);
		if (p1Pos < 20) {
			return PositionType.BATTLE;
		}
		if (p1Pos < 200) {
			return PositionType.HAND;
		}
		if (p1Pos < 300) {
			return PositionType.DRAWCARD;
		}
		if (p1Pos < 400) {
			return PositionType.DISCARD;
		}
		if (p1Pos < 500) {
			return PositionType.REINFORCEMENTS;
		}
		return PositionType.DEGENERATOR;
	}

	public static Optional<BattleCard> getCard(Player player, int pos) {
		PositionType type = PositionService.getPositionType(pos);
		switch (type) {
		case BATTLE:
			int battleCardIndex = PositionService.getBattleCardIndex(pos);
			return Optional.ofNullable(player.getPlayingCards(battleCardIndex));
		case HAND:
			int handCardIndex = PositionService.getHandCardIndex(pos);
			return Optional.ofNullable(player.getHandCards(handCardIndex));
		case DRAWCARD:
			int drawcardCardIndex = pos - PositionService.getDrawCardsBeginPos(player.getId());

			return getCardByIndex(player.getDrawCards(), drawcardCardIndex);
		case DISCARD:
			int discardIndex = pos - PositionService.getDiscardBeginPos(player.getId());
			return getCardByIndex(player.getDiscard(), discardIndex);
		case REINFORCEMENTS:
			int reIndex = pos - PositionService.getReinforceCardsBeginPos(player.getId());
			return getCardByIndex(player.getReinforceCards(), reIndex);
		case DEGENERATOR:
			int deIndex = pos - PositionService.getDegeneratorBeginPos(player.getId());
			return getCardByIndex(player.getDegenerator(), deIndex);
		default:
			return Optional.empty();
		}
	}

	private static Optional<BattleCard> getCardByIndex(List<BattleCard> cards, int drawcardCardIndex) {
		if (cards.isEmpty()) {
			return Optional.empty();
		}
		if (cards.size() > drawcardCardIndex) {
			return Optional.ofNullable(cards.get(drawcardCardIndex));
		} else {
			return Optional.ofNullable(cards.get(0));
		}
	}

	/**
	 * 是否是敌方位置
	 *
	 * @param pos
	 * @return
	 */
	public boolean isEnemyPos(int playerId, int targetpos) {
		// 获取召唤师ID 得到阵位的位置区间 目标位置在区间内则为 友方位置
		int minPos = getZhaoHuanShiPos(PlayerId.fromValue(playerId)) + 1;
		int maxPos = getZhaoHuanShiPos(PlayerId.fromValue(playerId)) + 6;
		if (minPos <= targetpos && targetpos <= maxPos) {
			return false;
		}
		return true;
	}

	/**
	 * 判断 战场或者 手牌位置的卡是否有王者技
	 *
	 * @param combat
	 * @param pos
	 * @return
	 */
	public static boolean posCardHasKingSkill(Combat combat, int pos) {
		PositionType type = PositionService.getPositionType(pos);
		boolean isCheck = type == PositionType.BATTLE || type == PositionType.HAND;
		if (!PositionService.isZhaoHuanShiPos(pos) && isCheck) {
			BattleCard card = combat.getBattleCardByPos(pos);
			if (card.hasKingSkill()) {
				// 王者卡不接受法宝效果
				return true;
			}
		}
		return false;
	}

	public static boolean isDiscardPos(int pos) {
		return getPositionType(pos).getValue() == PositionType.DISCARD.getValue();
	}
}