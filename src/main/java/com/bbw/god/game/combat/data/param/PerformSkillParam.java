package com.bbw.god.game.combat.data.param;

import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.ClientAnimationService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.attack.Effect.EffectSourceType;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.BattleSkillLog;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 释放技能参数
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-04 18:40
 */
@Slf4j
@Data
@NoArgsConstructor
public class PerformSkillParam {
	private Combat combat; // 战斗数据对象
	private Effect receiveEffect; // 释放技能的卡牌所接收到的攻击效果。可能为NULL
	private BattleCard performCard;// 释放技能的卡牌
	private int performCardIndex;// 释放技能的卡牌下标索引
	private PlayerId performPlayerId;// 释放技能的玩家标识
	private Player performPlayer;// 释放技能的玩家标识
	private Effect attackEffect; // 卡牌释放的技能效果。可能为NULL
	private boolean seriousInjury = false;// 是否是永久性伤害
	private List<Effect> receiveEffects;
	@Setter
	private Player oppoPlayer = null;
	@Setter
	private BattleCard faceToFaceCard = null;

	public PerformSkillParam(Combat combat, Effect effect) {
		PlayerId playerId = PositionService.getPlayerIdByPos(effect.getTargetPos());
		Player targetPlayer = combat.getPlayer(playerId);
		int cardIndex = PositionService.getBattleCardIndex(effect.getTargetPos());
		BattleCard targetCard = targetPlayer.getPlayingCards(cardIndex);
		this.combat = combat;
		this.receiveEffect = effect;
		this.performPlayer = targetPlayer;
		this.performPlayerId = playerId;
		this.performCard = targetCard;
		this.performCardIndex = cardIndex;
	}

	public PerformSkillParam(Combat combat, Player performPlayer, int cardIndex) {
		this.combat = combat;
		this.performPlayer = performPlayer;
		this.performPlayerId = performPlayer.getId();
		this.performCardIndex = cardIndex;
		this.performCard = performPlayer.getPlayingCards(this.performCardIndex);
	}

	public PerformSkillParam(Combat combat, BattleCard performCard) {
		this.combat = combat;
		this.performPlayer = combat.getPlayer(performCard.getPos());
		this.performPlayerId = performPlayer.getId();
		this.performCardIndex = PositionService.getBattleCardIndex(performCard.getPos());
		this.performCard = performCard;
	}

	public static PerformSkillParam getRunesPsP(Combat combat,Player performPlayer){
		PerformSkillParam psp=new PerformSkillParam();
		psp.setPerformPlayerId(performPlayer.getId());
		psp.setPerformPlayer(performPlayer);
		psp.setCombat(combat);
		return psp;
	}

	public void updatePerformCardByIndex(){
		this.performCard = performPlayer.getPlayingCards(this.performCardIndex);
	}
	/**
	 * 通过卡牌EFFect 重构出 产生此Effect时的Psp对象，此方法一般在溅射构造使用
	 * @param combat
	 * @param effect
	 * @return
	 */
	public static PerformSkillParam getPspByAttakEffect(Combat combat, Effect effect){
		PlayerId playerId = PositionService.getPlayerIdByPos(effect.getSourcePos());
		Player targetPlayer = combat.getPlayer(playerId);
		int cardIndex = PositionService.getBattleCardIndex(effect.getSourcePos());
		BattleCard targetCard = targetPlayer.getPlayingCards(cardIndex);
		PerformSkillParam psp=new PerformSkillParam();
		psp.combat = combat;
		psp.receiveEffect = effect;
		psp.performPlayer = targetPlayer;
		psp.performPlayerId = playerId;
		psp.performCard = targetCard;
		psp.performCardIndex = cardIndex;
		psp.attackEffect=effect;
		return psp;
	}
	/**
	 * 通过指定
	 *
	 * @param combat
	 * @param performCard
	 */
	public PerformSkillParam(Combat combat, BattleCard performCard, BattleCard opBattleCard) {
		this.combat = combat;
		this.performPlayer = combat.getPlayer(performCard.getPos());
		this.performPlayerId = performPlayer.getId();
		this.performCardIndex = PositionService.getBattleCardIndex(performCard.getPos());
		this.performCard = performCard;

		this.oppoPlayer = combat.getPlayer(opBattleCard.getPos());
		this.faceToFaceCard = opBattleCard;
	}

	/**
	 * 获取对手玩家
	 */

	public Player getOppoPlayer() {
		if (oppoPlayer != null) {
			return oppoPlayer;
		}
		return combat.getOppoPlayer(performPlayerId);
	}

	/**
	 * 对手的上阵卡牌
	 *
	 * @param includeYunTai:是否包含云台
	 * @return
	 */
	@NonNull
	public List<BattleCard> getOppoPlayingCards(boolean includeYunTai) {
		PlayerId oppoPlayerId = getOppoPlayer().getId();
		return combat.getPlayingCards(oppoPlayerId, includeYunTai);// 对手上阵卡牌
	}

	/**
	 * 我的上阵卡牌
	 */
	@NonNull
	public List<BattleCard> getMyPlayingCards(boolean includeYunTai) {
		return combat.getPlayingCards(performPlayerId, includeYunTai);// 我的上阵卡牌
	}

	/**
	 * 我的手牌
	 */
	@NonNull
	public List<BattleCard> getMyHandCards() {
		return combat.getHandCards(performPlayerId);// 我的上阵卡牌
	}

	/**
	 * 随机一张对手上阵卡牌
	 *
	 * @param includePerformCard
	 *            是否包含释放卡
	 * @return
	 */
	@NonNull
	public Optional<BattleCard> randomMyPlayingCard(boolean includePerformCard) {
		List<BattleCard> myPlayingCards = getMyPlayingCards(true);
		// 过滤掉死亡卡牌
		myPlayingCards = myPlayingCards.stream().filter(card -> !card.isKilled() && (includePerformCard || card.getPos()!=performCard.getPos())).collect(Collectors.toList());
		if (myPlayingCards.isEmpty()) {
			return Optional.empty();
		}
		BattleCard randomCard = PowerRandom.getRandomFromList(myPlayingCards);
		return Optional.of(randomCard);
	}

	/**
	 * 随机一张对手上阵卡牌
	 *
	 * @param includeYunTai
	 *            是否包含云台
	 * @return
	 */
	@NonNull
	public Optional<BattleCard> randomOppoPlayingCard(boolean includeYunTai) {
		List<BattleCard> oppoPlayingCards = getOppoPlayingCards(includeYunTai);
		// 过滤掉死亡卡牌
		oppoPlayingCards = oppoPlayingCards.stream().filter(card -> !card.isKilled()).collect(Collectors.toList());
		if (oppoPlayingCards.isEmpty()) {
			return Optional.empty();
		}
		BattleCard randomCard = PowerRandom.getRandomFromList(oppoPlayingCards);
		return Optional.of(randomCard);
	}

	/**
	 * 随机 n张 对手上阵卡牌
	 *
	 * @param num:数量
	 * @param includeYunTai:是否包含云台
	 * @return
	 */
	@NonNull
	public List<BattleCard> randomOppoPlayingCards(int num, boolean includeYunTai) {
		List<BattleCard> oppoPlayingCards = getOppoPlayingCards(includeYunTai);
		// 过滤掉死亡卡牌
		oppoPlayingCards = oppoPlayingCards.stream().filter(card -> !card.isKilled()).collect(Collectors.toList());
		// 需要对数量比在场的多
		if (num >= oppoPlayingCards.size()) {
			return oppoPlayingCards;
		}
		// 洗牌
		Collections.shuffle(oppoPlayingCards);
		return oppoPlayingCards.subList(0, num);
	}

	/**
	 * 获取对手持有某个生效的技能的所有卡牌
	 *
	 * @param skillId
	 * @param includeYunTai
	 * @return
	 */
	@NonNull
	public List<BattleCard> getOppoPlayingCards(int skillId, boolean includeYunTai) {
		List<BattleCard> oppoPlayingCards = getOppoPlayingCards(includeYunTai);
		// 过滤掉卡牌
		oppoPlayingCards = oppoPlayingCards.stream().filter(card -> !card.isKilled() && card.effectiveSkill(skillId)).collect(Collectors.toList());
		return oppoPlayingCards;
	}

	/**
	 * 对位卡牌
	 */
	public Optional<BattleCard> getFaceToFaceCard() {
		// 对手
		if (faceToFaceCard != null) {
			return Optional.ofNullable(faceToFaceCard);
		}
		Player oppoPlayer = getOppoPlayer();
		// 对位卡牌
		return Optional.ofNullable(oppoPlayer.getPlayingCards(performCardIndex));
	}

	/**
	 * 获取伤害来源卡牌
	 */
	@Nullable
	public Optional<BattleCard> getEffectSourceCard() {
		// 没有受到伤害
		if (null == receiveEffect || receiveEffect.getSourcePos()<0 ||PositionService.isZhaoHuanShiPos(receiveEffect.getSourcePos())) {
			return Optional.empty();
		}
		int cardIndex = PositionService.getBattleCardIndex(receiveEffect.getSourcePos());
		PlayerId sourcePlayerId = PositionService.getPlayerIdByPos(receiveEffect.getSourcePos());
		Player player = combat.getPlayer(sourcePlayerId);
		return Optional.ofNullable(player.getPlayingCards()[cardIndex]);
	}

	/**
	 * 收到技能伤害
	 *
	 * @return
	 */
	public boolean receiveSkillEffect() {
		// TODO:改名
		return null != receiveEffect && receiveEffect.getSourceType() == EffectSourceType.SKILL;
	}

	/**
	 * 获取收到的技能ID
	 *
	 * @return
	 */
	public Integer getReceiveEffectSkillId() {
		if (receiveEffect == null) {
			return null;
		}
		if (receiveEffect.getPerformSkillID()>0){
			return receiveEffect.getPerformSkillID();
		}
		return receiveEffect.getSourceID();
	}

	/**
	 * 获取所有接收到的技能ID，如接受到的普攻 但是有来自地劫效果，则 返回 普攻 + 地劫的ID
	 *
	 * @return
	 */
	public List<Integer> getAllReceiveEffectSkillId() {
		List<Integer> ids = new ArrayList<Integer>();
		if (receiveEffect!=null) {
			ids.add(receiveEffect.getSourceID());
			if (receiveEffect.getExtraSkillEffect() != null) {
				ids.addAll(receiveEffect.getExtraSkillEffect());
			}
		}
		return ids;
	}

	public AnimationSequence getSkillAction(int skillId, int... toPos) {
		return ClientAnimationService.getSkillAction(combat.getAnimationSeq(), skillId, performCard.getPos(), toPos);
	}

	/**
	 * 生产一个对位物理攻击(包含武器加成)
	 *
	 * @return
	 */
	public CardValueEffect getNormalAttackEffect() {

		Optional<BattleCard> targetCard = this.getFaceToFaceCard();
		int targetPos = -1;
		// 确认攻击目标
		if (targetCard.isPresent()) {
			targetPos = targetCard.get().getPos();
		} else {
			targetPos = PositionService.getZhaoHuanShiPos(this.getOppoPlayer().getId());
		}
		Optional<BattleSkill> skill = this.performCard.getSkill(CombatSkillEnum.NORMAL_ATTACK.getValue());
		if (skill.isPresent() && skill.get().targetChanged()) {
			targetPos = skill.get().getTargetPos();
		}
		// 开始进行伤害计算
		CardValueEffect addtionEffeck = CardValueEffect.getNormalAttackEffect(skill.get().getId(), targetPos);
		addtionEffeck.setSourcePos(this.getPerformCard().getPos());
		// buff加成
		int addtionAtk = getPerformCard().getRoundDelayEffects().stream().collect(Collectors.summingInt(CardValueEffect::getAtk));
		if (addtionAtk > 0) {
			log.debug("物理攻击buff加成{}", addtionAtk);

		}
		int totalAtk = this.getPerformCard().getAtk() + addtionAtk;

		// 开始执行攻击
		if (isForeverHarm()) {
			addtionEffeck.setRoundHp(-totalAtk);
		} else {
			addtionEffeck.setHp(-totalAtk);
		}
		return addtionEffeck;
	}
	/**
	 * 是否携带销魂buff=》攻击造成的伤害 为永久伤害
	 *
	 * @return
	 */
	public boolean isForeverHarm() {
		for (CardValueEffect effect : getPerformCard().getRoundDelayEffects()) {
			if (CombatSkillEnum.XH.getValue() ==effect.getSourceID()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * 对方召唤师的位置
	 */
	public int getOppoZhsPos() {
		int oppoZhsPos = PositionService.getZhaoHuanShiPos(getOppoPlayer().getId());
		return oppoZhsPos;
	}

	/**
	 * 我方召唤师的位置
	 */
	public int getMyZhsPos() {
		int oppoZhsPos = PositionService.getZhaoHuanShiPos(performPlayer.getId());
		return oppoZhsPos;
	}

	/**
	 * 默认防守的结果为无法处理
	 *
	 * @return
	 */
	public Action getDefenseAction() {
		Action ar = new Action();
		if(receiveEffect!=null && receiveEffect.getPerformSkillID()<=0){
			receiveEffect.setPerformSkillID(receiveEffect.getSourceID());
		}
		ar.addEffect(receiveEffect);
		return ar;
	}

	/**
	 * 获取客户端动画序列
	 *
	 * @return
	 */
	public int getNextAnimationSeq() {
		return this.combat.getAnimationSeq();
	}

	/**
	 * 更新永久性伤害的判断
	 */
	public void updateSeriousInjuryStatus() {
		seriousInjury = getPerformCard().getRoundHp() <= 0;
	}

	/**
	 * 根据原来的卡牌位置 重新更新卡牌
	 */
	public void updatePerformCard(){
		if (this.performCard==null){
			return;
		}
		this.performCard=combat.getBattleCardByPos(this.getPerformCard().getPos());
	}

	public boolean checkPerformCardIndex(){
		BattleCard card = performPlayer.getPlayingCards(performCardIndex);
		if (card==null){
			return false;
		}
		if (card.getImgId()!=getPerformCard().getImgId()){
			return false;
		}
		return true;
	}
}