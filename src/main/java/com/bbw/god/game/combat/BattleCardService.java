package com.bbw.god.game.combat;

import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.cache.CombatCache;
import com.bbw.god.game.combat.cache.CombatCacheUtil;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.attack.CardPositionEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.PositionType;
import com.bbw.god.game.combat.data.param.CardMovement;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.CardEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 卡牌服务
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-05 17:28
 */
@Slf4j
@Service
public class BattleCardService {

	private static final int[] MP_INIT_VALUE = { 1, 1, 2, 2, 3 };// 手牌下标位置对应需要的魔法初始值

	/**
	 * 抓牌,从牌堆里补充到手牌
	 *
	 * @param player
	 */
	public void moveDrawCardsToHand(@NonNull Player player) {
		int num1=player.getStatistics().getHandCardUpLimit();
		int num2=player.getHandCardsCount();
		int num=num1-num2;
		if (num<0){
			//需要把手牌洗回牌堆
			moveHandCardsToDraw(player,-num);
		}else {
			//需要补充手牌
			moveDrawCardsToHand(player,num);
		}
	}

	/**
	 * 首次初始化手牌：影随技能优先
	 *
	 * @param player
	 */
	public void firstMoveDrawCardsToHand(@NonNull Player player) {
		List<BattleCard> moveCards=new ArrayList<>();
		int need=Math.max(0,player.getStatistics().getHandCardUpLimit()-player.getHandCardsCount());
		for (BattleCard drawCard : player.getDrawCards()) {
			if (player.handCardsIsFull()||need==0){
				break;
			}
			if (drawCard.hasSkill(CombatSkillEnum.YING_SUI.getValue())){
				moveCards.add(drawCard);
				player.addHandCard(drawCard);
				need--;
			}
		}
		if (!moveCards.isEmpty()){
			player.setDrawCards(player.getDrawCards().stream().filter(p->!moveCards.contains(p)).collect(Collectors.toList()));
			updateHandCardsMp(player);
		}
		if (need>0){
			moveDrawCardsToHand(player,need);
		}
	}


	/**
	 * 将多余的卡牌挪回牌堆
	 * @param player
	 * @param num
	 */
	public void moveHandCardsToDraw(Player player,int num){
		List<BattleCard> randomList = PowerRandom.getRandomsFromList(num, player.getHandCardList());
		int beginPos = PositionService.getDrawCardsBeginPos(player.getId());
		for (BattleCard card:randomList){
			int toPos = getEmptyPos(player.getDrawCards(), beginPos);
			this.moveTo(player,card.getPos(), toPos);
			card.setPos(toPos);
		}
		updateHandCardsMp(player);
	}

	/**
	 * 从牌堆补充卡牌到手牌
	 * @param player
	 * @param num
	 */
	public void moveDrawCardsToHand(@NonNull Player player,int num) {
		if (!player.handCardsIsFull() || num<=0) {
			player.sortHandCards();
		}
		// 牌堆里有牌，并且手牌未满，则补充手牌
		for (int need=0;need<num;need++){
			if (player.getDrawCards().isEmpty() || player.handCardsIsFull()) {
				break;
			}
			// 移动牌堆第一张牌到手牌
			BattleCard card = player.getDrawCards().get(0);
			player.addHandCard(card);
			player.getDrawCards().remove(0);
		}
		updateHandCardsMp(player);
	}

	/**
	 * 更新手牌Mp
	 * @param player
	 */
	private void updateHandCardsMp(Player player){
		//先整理手牌 左移
		player.sortHandCards();

		//回合初始时 卡牌的MP加成值
		int extra = player.getStatistics().getHandCardRoundMpAddtion()+player.getStatistics().getInitCardMp();
		int nextRoundHandCardRoundMp = player.getStatistics().getNextRoundHandCardRoundMp();
		player.getStatistics().setHandCardRoundMpAddtion(0);
		player.getStatistics().resetNextRoundHp();
		for (int index = 0; index < player.getHandCards().length; index++) {
			BattleCard card = player.getHandCards(index);
			if (null == card) {
				continue;
			}
			// 位置标识
			card.setPos(PositionService.getHandCardPos(player.getId(), index));
			//处理法力值
			if (nextRoundHandCardRoundMp >= 0) {
				card.setMp(nextRoundHandCardRoundMp);
				continue;
			}
			int baseMp = BattleCard.MIN_MP;
			if (!card.hasMinMpSkill()) {
				// 如果不存在疾驰技能则获取上阵法术值
				baseMp = getCostMp(card, index);// 位置+星级
			}
			int costMp = baseMp + extra;
			costMp = Math.max(BattleCard.MIN_MP, costMp);
			card.setMp(costMp);
		}
	}
	/**
	 * 根据坐标删除卡牌
	 *
	 * @param player
	 * @param formPos
	 * @param toPos
	 */
	public void removeCardFromPos(Player player, int formPos, int toPos) {
		// 手牌 和战场牌是通过数组的下标的形式存储 只需原来的坐标即可
		// 其他位置的是集合对象 需要 用最新的坐标去匹配
		PositionType type = PositionService.getPositionType(formPos);
		switch (type) {
		case BATTLE:
			int battleCardIndex = PositionService.getBattleCardIndex(formPos);
			player.getPlayingCards()[battleCardIndex] = null;
			break;
		case HAND:
			int handCardIndex = PositionService.getHandCardIndex(formPos);
			player.getHandCards()[handCardIndex] = null;
			break;
		case DRAWCARD:
			player.getDrawCards().removeIf(c -> c.getPos() == toPos);
			break;
		case DISCARD:
			player.getDiscard().removeIf(c -> c.getPos() == toPos);
			break;
		case REINFORCEMENTS:
			player.getReinforceCards().removeIf(c -> c.getPos() == toPos);
			break;
		case DEGENERATOR:
			player.getDegenerator().removeIf(c -> c.getPos() == toPos);
			break;
		default:
			throw CoderException.high("无效的牌堆！位置pos=[" + formPos + "]");
		}
	}
	public void removeHanCardOrPlayingCardByPos(Player player, int pos) {
		// 手牌 和战场牌是通过数组的下标的形式存储 只需原来的坐标即可
		// 其他位置的是集合对象 需要 用最新的坐标去匹配
		PositionType type = PositionService.getPositionType(pos);
		switch (type) {
			case BATTLE:
				int battleCardIndex = PositionService.getBattleCardIndex(pos);
				player.getPlayingCards()[battleCardIndex] = null;
				break;
			case HAND:
				int handCardIndex = PositionService.getHandCardIndex(pos);
				player.getHandCards()[handCardIndex] = null;
				break;
			case DRAWCARD:
				player.getDrawCards().removeIf(c -> c.getPos() == pos);
		}
	}
	/**
	 * 根据坐标获取卡牌
	 *
	 * @param player
	 * @param pos
	 * @return
	 */
	public Optional<BattleCard> getCard(Player player, int pos) {
		PositionType type = PositionService.getPositionType(pos);
		switch (type) {
		case BATTLE:
			int battleCardIndex = PositionService.getBattleCardIndex(pos);
			return Optional.ofNullable(player.getPlayingCards(battleCardIndex));
		case HAND:
			int handCardIndex = PositionService.getHandCardIndex(pos);
			return Optional.ofNullable(player.getHandCards(handCardIndex));
		case DRAWCARD:
			return player.getDrawCards().stream().filter(card -> card.getPos() == pos).findAny();
		case DISCARD:
			return player.getDiscard().stream().filter(card -> card.getPos() == pos).findAny();
		case REINFORCEMENTS:
			return player.getReinforceCards().stream().filter(card -> card.getPos() == pos).findAny();
		case DEGENERATOR:
			return player.getDegenerator().stream().filter(card -> card.getPos() == pos).findAny();
		default:
			throw CoderException.high("无效的牌堆！位置pos=[" + pos + "]");
		}
	}

	/***
	 * 卡牌移动
	 *
	 * @param player
	 * @param fromPos
	 * @param toPos
	 */
	public void moveTo(Player player, int fromPos, int toPos) {
		// 原始卡牌
		Optional<BattleCard> cardObj = getCard(player, fromPos);
		BattleCard card = cardObj.get();
		card.setPos(toPos);
		PositionType toPositionType = PositionService.getPositionType(toPos);
		// 移动到新位置
		switch (toPositionType) {
		case BATTLE:
			int battleIndex = PositionService.getBattleCardIndex(toPos);
			player.getPlayingCards()[battleIndex] = card;
			// 移动到战场 则需要阵位加成
			posGain(card, battleIndex);
			if (card.getImgId()== CardEnum.XIAO_TIAN.getCardId() || card.getImgId()==CardEnum.GOD_YANG_JIAN.getCardId()){
				CombatCache cache = CombatCacheUtil.getCombatCache(player.getUid(), player.getCombatId());
				cache.toPlaying(card.getImgId());
				CombatCacheUtil.setCombatCache(cache);
			}
			//单回合场上同时有羽翼仙和其他三张飞行卡牌，并获得胜利
			int fly=0;
			boolean yuyixian=false;
			for (BattleCard playingCard : player.getPlayingCards()) {
				if (playingCard==null){
					continue;
				}
				if (playingCard.getImgId()== CardEnum.YU_YI_XIAN.getCardId()){
					yuyixian=true;
				}else if (playingCard.canFly()) {
					fly++;
				}
			}
			if (fly>=3 && yuyixian){
				CombatCache cache = CombatCacheUtil.getCombatCache(player.getUid(), player.getCombatId());
				cache.setYuYiXianAndFourFlyCards(true);
				CombatCacheUtil.setCombatCache(cache);
			}
			//单回合场上有四张疾驰卡牌，并获得胜利
			int jichi=0;
			for (BattleCard playingCard : player.getPlayingCards()) {
				if (playingCard==null){
					continue;
				}
				if (playingCard.hasSkill(CombatSkillEnum.JC.getValue())) {
					jichi++;
				}
			}
			if (jichi>=4){
				CombatCache cache = CombatCacheUtil.getCombatCache(player.getUid(), player.getCombatId());
				cache.setFourJiChiCards(true);
				CombatCacheUtil.setCombatCache(cache);
			}

			break;
		case HAND:
			int handCardIndex = PositionService.getHandCardIndex(toPos);
			player.getHandCards()[handCardIndex] = card;
			break;
		case DRAWCARD:
			player.getDrawCards().add(card);
			break;
		case DISCARD:
			player.getDiscard().add(card);
			card.setFuHuoSkillEffectTimes(0);
			break;
		case REINFORCEMENTS:
			player.getReinforceCards().add(card);
			break;
		case DEGENERATOR:
			player.getDegenerator().add(card);
			break;
		default:
			throw CoderException.high("无效的牌堆！位置pos=[" + toPos + "]");
		}
		// 从旧位置中删除
		removeCardFromPos(player, fromPos, toPos);
	}

	/**
	 * 通过坐标替换指定位置卡牌 <fonte>如果被替换的位置存在卡牌，则该卡牌将会被删除</fonte>
	 *
	 * @param player
	 */
	public void replaceCard(Player player, BattleCard card) {
		// 先清空 将要被替换的卡
		removeCardFromPos(player, card.getPos(), card.getPos());
		int toPos = card.getPos();
		PositionType toPositionType = PositionService.getPositionType(toPos);
		// 移动到新位置
		switch (toPositionType) {
		case BATTLE:
			int battleIndex = PositionService.getBattleCardIndex(toPos);
			player.getPlayingCards()[battleIndex] = card;
			// 移动到战场 则需要阵位加成
			posGain(card, battleIndex);
			break;
		case HAND:
			int handCardIndex = PositionService.getHandCardIndex(toPos);
			player.getHandCards()[handCardIndex] = card;
			break;
		case DRAWCARD:
			player.getDrawCards().add(card);
			break;
		case DISCARD:
			player.getDiscard().add(card);
			break;
		case REINFORCEMENTS:
			player.getReinforceCards().add(card);
			break;
		case DEGENERATOR:
			player.getDegenerator().add(card);
			break;
		default:
			throw CoderException.high("无效的牌堆！位置pos=[" + toPos + "]");
		}

	}

	/**
	 * 阵位加成
	 *
	 * @param card
	 * @param toPos
	 */
	private void posGain(BattleCard card, int toPos) {
		if (card==null){
			return;
		}
		switch (toPos) {
		case 1:// 前锋攻击加成10%
			BattleCard qianFengCard = card;
			Double addtionRoundAtk = qianFengCard.getAtk() * 0.1;
			qianFengCard.incRoundAtk(addtionRoundAtk.intValue());
			qianFengCard.incAtk(addtionRoundAtk.intValue());
			break;
		case 3:// 中军防御加成15%
			// 中军
			BattleCard zhongJunCard = card;
			Double addtionRoundHp = zhongJunCard.getHp() * 0.15;
			zhongJunCard.incRoundHp(addtionRoundHp.intValue());
			zhongJunCard.incHp(addtionRoundHp.intValue());
		}
	}
	public void addtionPos(Combat combat, Player player, List<CardMovement> movements) {
		for (CardMovement cm : movements) {
			if (PositionType.BATTLE != PositionService.getPositionType(cm.getToPos())) {
				continue;
			}
			int toIndex = PositionService.getBattleCardIndex(cm.getToPos());
			BattleCard card = player.getPlayingCards(toIndex);
			posGain(card,toIndex);
		}
	}

	/**
	 * 获取阵位攻击加成数值
	 * @param card
	 * @return
	 */
	public static int posGainAtk(BattleCard card) {
		if (PositionService.isXianFengPos(card.getPos())) {
			Double val = card.getInitAtk() * 0.1;
			return val.intValue();
		}
		return 0;
	}

	/**
	 * 获取阵位防御加成
	 *
	 * @param card
	 * @return
	 */
	public static int posGainHp(BattleCard card) {
		if (PositionService.isZhongJunPos(card.getPos())) {
			Double val = card.getInitHp() * 0.15;
			return val.intValue();
		}
		return 0;
	}

	/**
	 * 出牌: 上阵
	 *
	 * @param movements
	 */
	private void moveCardsToBattle(boolean mpLimit, Player player, List<CardMovement> movements) {
		if (null == movements || movements.isEmpty()) {
			return;
		}
		List<CardMovement> errorMovements=new ArrayList<CardMovement>();
		for (CardMovement cm : movements) {
			// 手牌
			Optional<BattleCard> fromCard = getCard(player, cm.getFromPos());
			if (!fromCard.isPresent()) {
				//没有该手牌
				errorMovements.add(cm);
				continue;
			}
			if (getCard(player,cm.getToPos()).isPresent()) {
				//目标位置已经有卡了
				errorMovements.add(cm);
				continue;
			}
			BattleCard card = fromCard.get();
			// 上云台必须要有飞行技能特殊判定
			if (PositionService.isYunTaiPos(cm.getToPos())) {
				if (!card.canFly()) {
					//不能上云台
					errorMovements.add(cm);
					continue;
				}
			}
			// 法力值判定
			if (player.getMp() >= card.getMp()) {
				// 战场阵位
				int toIndex = PositionService.getBattleCardIndex(cm.getToPos());
				// 可能是封神、复活等技能的卡，要清除历史状态信息
				card.reset(true);
				// 手牌移动到阵位，更新位置信息
				player.getPlayingCards()[toIndex] = card;
				player.getPlayingCards()[toIndex].setPos(cm.getToPos());
				// 删除手牌
				removeCardFromPos(player, cm.getFromPos(), card.getPos());
				if (mpLimit) {
					// 消耗法力值
					player.incMp(-card.getMp());
				}
				if (player.getUid()>0 && (card.getImgId()== CardEnum.XIAO_TIAN.getCardId() || card.getImgId()==CardEnum.GOD_YANG_JIAN.getCardId())){
					CombatCache cache = CombatCacheUtil.getCombatCache(player.getUid(), player.getCombatId());
					cache.toPlaying(card.getImgId());
					CombatCacheUtil.setCombatCache(cache);
				}

				//单回合场上同时有羽翼仙和其他三张飞行卡牌，并获得胜利
				int fly=0;
				boolean yuyixian=false;
				for (BattleCard playingCard : player.getPlayingCards()) {
					if (playingCard==null){
						continue;
					}
					if (playingCard.getImgId()== CardEnum.YU_YI_XIAN.getCardId()){
						yuyixian=true;
					}else if (playingCard.canFly()) {
						fly++;
					}
				}
				if (fly>=3 && yuyixian){
					CombatCache cache = CombatCacheUtil.getCombatCache(player.getUid(), player.getCombatId());
					cache.setYuYiXianAndFourFlyCards(true);
					CombatCacheUtil.setCombatCache(cache);
				}
				//单回合场上有四张疾驰卡牌，并获得胜利
				int jichi=0;
				for (BattleCard playingCard : player.getPlayingCards()) {
					if (playingCard==null){
						continue;
					}
					if (playingCard.hasSkill(CombatSkillEnum.JC.getValue())) {
						jichi++;
					}
				}
				if (jichi>=4){
					CombatCache cache = CombatCacheUtil.getCombatCache(player.getUid(), player.getCombatId());
					cache.setFourJiChiCards(true);
					CombatCacheUtil.setCombatCache(cache);
				}

			} else {
				errorMovements.add(cm);
			}
		}
		movements.removeAll(errorMovements);
	}

	/**
	 * 检查出牌是否合规：MP 阵位解锁  阵位是否已经有卡
	 * @param movements
	 */
	public void moveHandCardsToBattleMpPosCheck(int round, Player player, List<CardMovement> movements, FightTypeEnum fightTypeEnum) {
		if (null == movements || movements.isEmpty()) {
			return;
		}
		List<CardMovement> errorMovements=new ArrayList<>();
		if (!player.isUnlockAllPosBuff()){
			for (CardMovement cm : movements) {
				// 阵位是否解锁判定
				if (!PositionService.positionUnlock(round, player.getId(), cm.getToPos(), fightTypeEnum)) {
					errorMovements.add(cm);
				}
			}
		}
		movements.removeAll(errorMovements);
		moveCardsToBattle(true, player, movements);
		player.setDeployCardFlag(round);
	}

	/**
	 * 移动到手牌
	 *
	 * @param player
	 * @param effect
	 * @return
	 */
	public int moveToHandCards(Player player, CardPositionEffect effect) {
		int toPos = -1;
		BattleCard[] handCards = player.getHandCards();
		for (int i = handCards.length - 1; i > 0; i--) {
			if (null == handCards[i]) {
				toPos = PositionService.getHandCardPos(player.getId(), i);
				break;
			}
		}
		if (toPos == -1) {
			int drawCardsBeginPos = PositionService.getDrawCardsBeginPos(player.getId());
			toPos = getEmptyPos(player.getDrawCards(), drawCardsBeginPos);
		}
		effect.setToPos(toPos);
		this.moveTo(player, effect.getFromPos(), toPos);
		return toPos;
	}

	/**
	 * 移动到坟场
	 *
	 * @param player
	 * @param effect
	 * @return
	 */
	public int moveToDiscard(Player player, CardPositionEffect effect) {
		int beginPos = PositionService.getDiscardBeginPos(player.getId());
		int toPos = this.getEmptyPos(player.getDiscard(), beginPos);
		effect.setToPos(toPos);
		this.moveTo(player, effect.getFromPos(), toPos);
		return toPos;
	}
	/**
	 * 移动到援军
	 *
	 * @param player
	 * @param effect
	 * @return
	 */
	public int moveToReinforcements(Player player, CardPositionEffect effect) {
		int beginPos = PositionService.getReinforceCardsBeginPos(player.getId());
		int toPos = this.getEmptyPos(player.getReinforceCards(), beginPos);
		effect.setToPos(toPos);
		this.moveTo(player, effect.getFromPos(), toPos);
		return toPos;
	}

	/**
	 * 移动到异次元
	 *
	 * @param player
	 * @param effect
	 * @return
	 */
	public int moveToDegenerator(Player player, CardPositionEffect effect) {
		int beginPos = PositionService.getDegeneratorBeginPos(player.getId());
		int toPos = this.getEmptyPos(player.getDegenerator(), beginPos);
		effect.setToPos(toPos);
		this.moveTo(player, effect.getFromPos(), toPos);
		return toPos;
	}

	/**
	 * 移动到牌堆
	 *
	 * @param player
	 * @param effect
	 * @return
	 */
	public int moveToDrawcards(Player player, CardPositionEffect effect) {
		int beginPos = PositionService.getDrawCardsBeginPos(player.getId());
		int toPos = this.getEmptyPos(player.getDrawCards(), beginPos);
		effect.setToPos(toPos);
		this.moveTo(player, effect.getFromPos(), toPos);
		return toPos;
	}

	/**
	 * 手牌上阵需要的法力值。目前手牌最多5张
	 *
	 * @param card:     战斗卡牌实例
	 * @param position: 手牌下标[0,Combat.MAX_IN_HAND-1]
	 * @return
	 */
	public int getCostMp(BattleCard card, int position) {
		if (0 <= position && position < CombatConfig.MAX_IN_HAND) {
			return MP_INIT_VALUE[position] + card.getStars();
		}
		throw CoderException.high("手牌位置参数错误。position=" + position + ",有效值范围[0," + (CombatConfig.MAX_IN_HAND - 1) + "]");
	}

	public int getEmptyPos(List<BattleCard> cards, int beginPos) {
		if (null == cards || cards.isEmpty()) {
			return beginPos;
		}
		int maxPos = beginPos + 100 - 1;
		for (int pos = beginPos; pos <= maxPos; pos++) {
			if (!existsPos(cards, pos)) {
				return pos;
			}
		}
		log.error("对应位置类型的位置已满，正常不应该执行到这里。beginPos=" + beginPos);
		return maxPos;
	}

	private boolean existsPos(List<BattleCard> cards, final int beginPos) {
		return cards.stream().filter(card -> card.getPos() == beginPos).findAny().isPresent();
	}

	/**
	 * 根据类型获取一个空位 暂不支持阵位和手牌中获取空位置 有需要时再加
	 * @param player
	 * @param type
	 * @return
	 */
	public int getEmptyPos(Player player, PositionType type) {
		PlayerId playerId = player.getId();
		int beginPos = 0;
		List<BattleCard> cards = null;
		switch (type) {
		case DISCARD:
			beginPos = PositionService.getDiscardBeginPos(playerId);
			cards = player.getDiscard();
			break;
		case DRAWCARD:
			beginPos = PositionService.getDrawCardsBeginPos(playerId);
			cards = player.getDrawCards();
			break;
		case REINFORCEMENTS:
			beginPos = PositionService.getReinforceCardsBeginPos(playerId);
			cards = player.getReinforceCards();
			break;
		case DEGENERATOR:
			beginPos = PositionService.getDegeneratorBeginPos(playerId);
			cards = player.getDegenerator();
			break;
		default:
			return 0;
		}
		int toPos = getEmptyPos(cards, beginPos);
		return toPos;
	}

	/**
	 * 卡牌位置互换
	 * @param player
	 * @param fromPos
	 * @param toPos
	 */
	public void exchangeCard(Player player,int fromPos,int toPos){
		Optional<BattleCard> fromCardOp=getCard(player,fromPos);
		Optional<BattleCard> toCardOp=getCard(player,toPos);
		if (!fromCardOp.isPresent() || !toCardOp.isPresent()){
			throw CoderException.high("不能交换不存在的卡牌");
		}
		removeHanCardOrPlayingCardByPos(player,fromPos);
		removeHanCardOrPlayingCardByPos(player,toPos);
		moveToHandOrPlayingOrDrawing(player,fromCardOp.get(),toPos);
		moveToHandOrPlayingOrDrawing(player,toCardOp.get(),fromPos);
	}

	/**
	 * 移动卡牌到指定的手牌或战场位置或牌堆
	 * @param player
	 * @param card
	 * @param toPos
	 */
	private void moveToHandOrPlayingOrDrawing(Player player,BattleCard card,int toPos) {
		PositionType toPositionType = PositionService.getPositionType(toPos);
		// 移动到新位置
		card.setPos(toPos);
		switch (toPositionType) {
			case BATTLE:
				int battleIndex = PositionService.getBattleCardIndex(toPos);
				player.getPlayingCards()[battleIndex] = card;
				// 移动到战场 则需要阵位加成
				posGain(card, battleIndex);
				if (player.getUid()>0 && (card.getImgId()== CardEnum.XIAO_TIAN.getCardId() || card.getImgId()==CardEnum.GOD_YANG_JIAN.getCardId())){
					CombatCache cache = CombatCacheUtil.getCombatCache(player.getUid(), player.getCombatId());
					cache.toPlaying(card.getImgId());
					CombatCacheUtil.setCombatCache(cache);
				}
				break;
			case HAND:
				int handCardIndex = PositionService.getHandCardIndex(toPos);
				player.getHandCards()[handCardIndex] = card;
				break;
			case DRAWCARD:
				player.getDrawCards().add(card);
				break;

		}
	}

	/**
	 * 检查神·崇侯虎与至少4张水系卡牌同时在场，且对方场上6张卡牌处于封禁状态
	 * @param player
	 * @param oppPlayer
	 */
	public static void godChongHouHuSkill5(Player player,Player oppPlayer){
		if (oppPlayer.getPlayingCards().length<6) {
			return;
		}
		for (BattleCard playingCard : oppPlayer.getPlayingCards()) {
			if (!playingCard.existSkillStatus(SkillSection.getFenJinSkills())){
				return;
			}
		}
		int water=0;
		boolean godChongHouHu=false;
		for (BattleCard playingCard : player.getPlayingCards()) {
			if (playingCard.getImgId()== CardEnum.GOD_CHONG_HOU_HU.getCardId()){
				godChongHouHu=true;
			}else if (playingCard.getType().equals(TypeEnum.Water)) {
				water++;
			}
		}
		if (water>=3 && godChongHouHu){
			CombatCache cache = CombatCacheUtil.getCombatCache(player.getUid(), player.getCombatId());
			cache.setYuYiXianAndFourFlyCards(true);
			CombatCacheUtil.setCombatCache(cache);
		}
	}
}