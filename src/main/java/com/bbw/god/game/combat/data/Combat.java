package com.bbw.god.game.combat.data;

import com.alibaba.fastjson.annotation.JSONField;
import com.bbw.common.ID;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.wanxianzhen.WanXianSpecialType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 战斗数据定义。
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-06-18 09:55
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
public class Combat implements Serializable {
	private static final long serialVersionUID = -8749675285510329361L;
	//基本信息
	private Long id;// 战斗ID
	private FightTypeEnum fightType = FightTypeEnum.TRAINING;// 战斗类型
	private String awardDesc = "";//野怪任务描述
	private Player p1;//玩家1
	private Player p2;//玩家2
	//回合信息
	private Integer yeguaiType = null;//野怪类型
	private PlayerId playingId = PlayerId.P2;// 当前出手的玩家
	private int round = 1;// 当前回合
	private int winnerId = 0;//1 或 2，0为未分胜负
	private List<AnimationSequence> animationList = new ArrayList<>();//回合动画数据
	private RDFightResult result = null;// 结算信息
	private Integer wxType= 0;//1000为常规赛 其他对应的为特色赛
	private transient int animationSeq = 0;//动画序列号
	//PVE专用 是否点过自动战斗 和 是否使用了跳过功能
	private boolean auto=false;
	private boolean skip=false;
	private Integer newerGuide = null;// 是否是新手引导

	@JSONField(serialize=false)
	public int getAnimationSeq() {
		return animationSeq++;
	}

	@JSONField(serialize=false)
	public Player getFirstPlayer(){
		if (playingId.equals(PlayerId.P1)){
			return p1;
		}
		return p2;
	}
	@JSONField(serialize=false)
	public Player getSecondPlayer(){
		if (playingId.equals(PlayerId.P2)){
			return p1;
		}
		return p2;
	}

	/**
	 * 获取玩家数组
	 * @return
	 */
	public Player[] findPlayers(){
		return new Player[]{getFirstPlayer(), getSecondPlayer()};
	}
	public static Combat instance(Player p1, Player p2, CombatPVEParam param){
		Combat combat=new Combat();
		combat.setId(ID.INSTANCE.nextId());
		p1.setCombatId(combat.getId());
		p2.setCombatId(combat.getId());
		combat.setP1(p1);
		combat.setP2(p2);
		combat.setFightType(FightTypeEnum.fromValue(param.getFightType()));
		combat.setYeguaiType(param.getYgType());
		return combat;
	}
	public void addAnimation(AnimationSequence animation) {
		if (animation.getSeq() > 0) {
			Optional<AnimationSequence> has = animationList.stream()
					.filter(al -> (al.getSeq() == animation.getSeq()) && (al.getType() == animation.getType()))
					.findAny();
			if (has.isPresent()) {
				has.get().getList().addAll(animation.getList());
			} else {
				animationList.add(animation);
				if (this.animationSeq <= animation.getSeq()) {
					this.animationSeq = animation.getSeq() + 1;
				}
			}
		} else {
			animationList.add(animation);
			if (this.animationSeq <= animation.getSeq()) {
				this.animationSeq = animation.getSeq() + 1;
			}
		}
	}

	public void addAnimations(List<AnimationSequence> animations) {
		for (AnimationSequence as : animations) {
			addAnimation(as);
		}
	}

	public void setFirst(PlayerId first) {
		playingId=first;
	}

	/**
	 * 已经结束
	 *
	 * @return
	 */
	public boolean hadEnded() {
		// 已经设置了赢家;
		if (winnerId != 0) {
			return true;
		}
		int maxRound=gainMaxRounds();
		if (wxType== WanXianSpecialType.GONG_CHENG.getVal()){
			maxRound=10;
		}
		if (round <= maxRound){
			// 玩家召唤师HP=0
			if (p1.getHp() < 1) {
				winnerId = this.p2.getId().getValue();
				return true;
			}
			if (p2.getHp() < 1) {
				winnerId = this.p1.getId().getValue();
				return true;
			}
			// 玩家没有卡牌了
			if (!p1.hasActiveCards()){
				winnerId = p2.getId().getValue();
				return true;
			}
			if (!p2.hasActiveCards()){
				winnerId = p1.getId().getValue();
				return true;
			}
			return false;
		}
		if (fightType.getValue()==FightTypeEnum.SXDH.getValue() || fightType.getValue()==FightTypeEnum.CJDF.getValue() || fightType.getValue()==FightTypeEnum.WXZ.getValue()){
			//PVP 血量高的胜
			if (p1.getHp() < p2.getHp()) {
				winnerId = this.p2.getId().getValue();
				return true;
			}
			if (p1.getHp() > p2.getHp()) {
				winnerId = this.p1.getId().getValue();
				return true;
			}
			//血量一样则判断 卡牌数（战场+手牌+牌堆）,一样则第一回合后手赢
			int p1CardsCount=p1.countAliveCard();
			int p2CardsCount=p2.countAliveCard();
			if (p1CardsCount>p2CardsCount){
				winnerId = this.p1.getId().getValue();
				return true;
			}else {
				winnerId = this.p2.getId().getValue();
				return true;
			}
		}else{
			//pve超过最大回合默认AI赢
			winnerId=PlayerId.P2.getValue();
			return true;
		}
	}
	/**
	 * 获取战斗结束类型
	 * @return
	 */
	public CombatResultEnum getCombatResultType() {
		if (hadEnded()) {
			if (p1.getHp()<1 || p2.getHp()<1) {
				return CombatResultEnum.HP_EMPTY;
			}
			if (!p1.hasActiveCards() || !p2.hasActiveCards()) {
				return CombatResultEnum.CARD_EMPTY;
			}
			int maxRounds = gainMaxRounds();
			if (round > maxRounds) {
				return CombatResultEnum.ROUND_TIMEOUT;
			}
		}
		//未结束
		return CombatResultEnum.NO_END;
	}

	/**
	 *
	 * /** 根据id获取玩家
	 *
	 * @param ombat
	 * @param playerId
	 * @return
	 */
	@NonNull
	public Player getPlayer(PlayerId playerId) {
		if (p1.getId() == playerId) {
			return p1;
		}
		return p2;
	}

	@NonNull
	public Player getPlayerByUid(long uid) {
		if (p1.getUid() == uid) {
			return p1;
		}
		return p2;
	}

	/**
	 * 根据我的ID获取对手ID
	 *
	 * @param mine
	 * @return
	 */
	public static PlayerId getOppoId(PlayerId mine) {
		return mine == PlayerId.P1 ? PlayerId.P2 : PlayerId.P1;
	}

	/**
	 * 根据位置信息获取所属玩家
	 *
	 * @param combat
	 * @param pos
	 * @return
	 */
	@NonNull
	public Player getPlayer(int pos) {
		PlayerId playerId = PositionService.getPlayerIdByPos(pos);
		return getPlayer(playerId);
	}

	/**
	 * 获取对手玩家
	 *
	 * @param combat
	 * @param playerId
	 * @return
	 */
	@NonNull
	public Player getOppoPlayer(PlayerId playerId) {
		if (p1.getId() == playerId) {
			return p2;
		}
		return p1;
	}

	/**
	 * 通过当前玩家ID获取对手玩家
	 *
	 * @param combat
	 * @param playerId
	 * @return
	 */
	@NonNull
	public Player getOppoPlayerByUid(long playerUid) {
		Player player = getPlayerByUid(playerUid);
		if (p1.getId() == player.getId()) {
			return p2;
		}
		return p1;
	}

	/**
	 * 获取对手召唤师的位置标识
	 *
	 * @param mine:当前玩家的标识
	 * @return
	 */
	public int getOppoZhaoHuanShiPos(PlayerId mine) {
		int pos = PositionService.getZhaoHuanShiPos(getOppoId(mine));
		return pos;
	}

	/**
	 * 获取玩家当前上阵的卡牌
	 *
	 * @param combat
	 * @param playerId
	 * @param includeYunTai：是否包含云台
	 * @return
	 */
	@NonNull
	public List<BattleCard> getPlayingCards(PlayerId playerId, boolean includeYunTai) {
		BattleCard[] cards = getPlayer(playerId).getPlayingCards();
		List<BattleCard> list = new ArrayList<>();
		for (int i = 0; i < cards.length; i++) {
			BattleCard battleCard = cards[i];
			if (null == battleCard || battleCard.isKilled()) {
				continue;
			}
			// 不包含云台
			if (!includeYunTai && PositionService.isYunTaiPos(battleCard.getPos())) {
				continue;
			}
			list.add(battleCard);
		}
		return list;
	}

	/**
	 * 获取玩家当前的手牌
	 *
	 * @param combat
	 * @param playerId
	 * @return
	 */
	@NonNull
	public List<BattleCard> getHandCards(PlayerId playerId) {
		BattleCard[] cards = getPlayer(playerId).getHandCards();
		List<BattleCard> list = new ArrayList<>(CombatConfig.MAX_IN_HAND);
		for (BattleCard battleCard : cards) {
			if (null == battleCard) {
				continue;
			}
			list.add(battleCard);
		}
		return list;
	}

	/**
	 * 获取战斗位置上的卡牌
	 *
	 * @param combat
	 * @param pos
	 * @return
	 */
	@Nullable
	public BattleCard getBattleCard(int pos) {
		Player player = getPlayer(pos);
		int index = PositionService.getBattleCardIndex(pos);
		return player.getPlayingCards(index);
	}

	/**
	 * 通过坐标获取卡牌
	 *
	 * @param pos
	 * @return
	 */
	public BattleCard getBattleCardByPos(int pos) {
		Player player = getPlayer(pos);
		Optional<BattleCard> card = PositionService.getCard(player, pos);
		if (card.isPresent()) {
			return card.get();
		}
		return null;
	}

	/**
	 * 获取对位卡牌
	 *
	 * @param playerId
	 * @param cardIndex
	 * @return
	 */
	@Nullable
	public Optional<BattleCard> getFaceToFaceCard(PlayerId playerId, int cardIndex) {
		// 对手
		Player oppoPlayer = getOppoPlayer(playerId);
		// 对位卡牌
		return Optional.ofNullable(oppoPlayer.getPlayingCards()[cardIndex]);
	}

	/**
	 * 判断是否已经结算
	 * @return
	 */
	public boolean hasSubmitFightResult() {
		return result != null;
	}

	/**
	 * PVE默认玩家是1
	 * @return
	 */
	public boolean pveWinnerIsUser(){
		return getWinnerId()==1;
	}

	/**
	 * 获取战斗最大回合数
	 *
	 * @return
	 */
	private int gainMaxRounds(){
		int maxRound;
		switch (fightType){
			case ZXZ:
			case ZXZ_FOUR_SAINTS:
				maxRound = CombatConfig.ZXZ_MAX_ROUNDS;
				break;
			default:
				maxRound = CombatConfig.MAX_ROUNDS;
				break;

		}
		return maxRound;
	}
}