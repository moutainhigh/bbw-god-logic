package com.bbw.god.fight;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.WorldType;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.UserCard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OpponentCardsUtil {

	/**
	 * 获取对手卡牌
	 * 
	 * @param cardsStr
	 * @param type
	 * @return
	 */
	public static List<UserCard> getOpponentCardsForMonster(String cardsStr, int type,List<Integer> extraExcludes) {
		List<UserCard> opponentCards = new ArrayList<>();
		List<Integer> cardInts = new ArrayList<>(); // 仅用来避免重复卡牌
		if (ListUtil.isNotEmpty(extraExcludes)){
			cardInts.addAll(extraExcludes);
		}
		String[] cardsSrc = cardsStr.split(";");
		for (String cardSrc : cardsSrc) {
			CfgCardEntity card;
			int cardLevel = 0;
			String[] cardParts = cardSrc.split(",");
			if (cardParts.length == 2) {
				// 长度为2，根据city属性/卡牌ID生成卡牌
				int cardPart0 = Integer.valueOf(cardParts[0]);
				if (cardPart0 > 5) {
					card = CardTool.getCardById(cardPart0);
				} else {
					card = getRandomCardByTypeNStar(type, cardPart0, cardInts);
				}
				cardLevel = Integer.valueOf(cardParts[1]);
			} else {
				// 长度为3， 比如 木二0 表示 木属性二星0级卡牌
				card = getRandomCardByTypeNStar(PowerRandom.getRandomBySeed(5) * 10, Integer.valueOf(cardParts[1]), cardInts);
				cardLevel = Integer.valueOf(cardParts[2]);
			}
			cardLevel = Math.min(20, cardLevel);
			cardInts.add(card.getId());
			opponentCards.add(UserCard.instance(card.getId(), cardLevel, 0));
		}
		return opponentCards;
	}

	/**
	 * 获取对手卡牌（robot）
	 *
	 * @param gu
	 * @param cardsStr
	 * @param cardLevelToAdd
	 * @param hierarchy
	 * @return
	 */
	public static List<UserCard> getOpponentCards(GameUser gu, String cardsStr, int cardLevelToAdd, int hierarchy) {
		List<UserCard> opponentCards = new ArrayList<>();
		List<Integer> cardInts = new ArrayList<>(); // 仅用来避免重复卡牌
		String[] cardsSrc = cardsStr.split(";");
		// 提前记录本次必然生成的卡牌
		for (String cardSrc : cardsSrc) {
			String[] cardParts = cardSrc.split(",");
			if (cardParts.length == 2) {
				int cardId = Integer.valueOf(cardParts[0]);
				cardInts.add(cardId);
			}
		}
		// 生成卡牌数据
		for (String cardSrc : cardsSrc) {
			CfgCardEntity card;
			int cardLevel = 0;
			String[] cardParts = cardSrc.split(",");
			if (cardParts.length == 2) {
				// 长度为2，根据卡牌ID生成卡牌
				cardLevel = Integer.valueOf(cardParts[1]);
				int cardId = Integer.valueOf(cardParts[0]);
				card = CardTool.getCardById(cardId);

			} else {
				// 长度为3， 比如 木二0 表示 木属性二星0级卡牌
				int cardType = Integer.valueOf(cardParts[0]);
				int cardStar = Integer.valueOf(cardParts[1]);
				cardLevel = Integer.valueOf(cardParts[2]);
				card = getRandomCardByTypeNStar(cardType, cardStar, cardInts);
				cardInts.add(card.getId());
			}
			if (WorldType.NORMAL.getValue()==(gu.getStatus().getCurWordType())) {
				cardLevel = Math.min(20, cardLevel + cardLevelToAdd);
			} else {
				cardLevel += cardLevelToAdd;
			}
			opponentCards.add(UserCard.instance(card.getId(), cardLevel, hierarchy));
		}
		return opponentCards;
	}

	/**
	 * 获得随机 卡牌 和exclude 不重复
	 *
	 * @param type
	 * @param star
	 * @param exclude
	 * @return
	 */
	private static CfgCardEntity getRandomCardByTypeNStar(int type, int star, List<Integer> exclude) {
		List<CfgCardEntity> cards = CardTool.getAllCards(type, star).stream()
				.filter(card -> !exclude.contains(card.getId())).collect(Collectors.toList());
		// 如果属性星级都已选出，则随机一张星级卡牌
		if (ListUtil.isEmpty(cards)) {
			cards = CardTool.getAllCards(star).stream().filter(card -> !exclude.contains(card.getId()))
					.collect(Collectors.toList());
		}
		return PowerRandom.getRandomFromList(cards);
	}

}
