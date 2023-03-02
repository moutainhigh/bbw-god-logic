package com.bbw.god.game.combat.data;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.StrUtil;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.combat.data.AnimationSequence.Animation;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.weapon.Weapon;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 每回合返回给客户端的数据
 * 
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-06-23 15:48
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDCombat extends RDSuccess implements Serializable {
	private static final long serialVersionUID = -2296536224030233211L;
	private Long combatId;// 战斗ID
	private String awardDesc = "";// 额外奖励
	private int round = 0;// 当前回合
	private Long playingId = null;// 当前出手的玩家
	private int isFirst = 0;// 是否先手
	private RDPlayer p1;// 屏幕上方玩家
	private RDPlayer p2;// 屏幕下方玩家（当前玩家、播放视角玩家）
	private List<AnimationSequence> animation = null;
	private RDFightResult result = null;
	private String message=null;
	private Integer yeGuaiType = null;
	@Data
	public static class RDPlayer {
		private int imgId = 10;// 图像
		private int iconId = TreasureEnum.HEAD_ICON_Normal.getValue();
		private String name;// 昵称
		private int lv = 1;// 等级
		private int maxHp = 0;// 当前血量
		private int hp = 0;// 当前血量
		private int maxMp = 0;// 当前魔法值
		private int mp = 0;// 当前魔法值、
		private int discard = 0;// 坟场卡牌数
		private int godId = 0;// 神仙ID
		private int bloodBarNum;
		private Long uid = -1l;
		private Long cardFromUid = -1l;
		private List<RDCombatBuf> buffs;
		/**
		 * 法宝ID及数量拼接的字符串。多个法宝之间以‘N’分隔，代表“AND”;法宝ID与数量之间以‘H’分隔，代表HAVE
		 * 例：300H2N310H1，表示id为300的有2个，id为310的法宝有1个。
		 */
		private String weapons = "";
		/**
		 * 卡牌各属性信息拼接的字符串。多个卡牌之间以‘N’分隔，代表“AND”,属性之间以‘P’分隔。
		 * 属性顺序依次为卡牌位置、图片ID、等级、阶级、回合初攻、回合初防、当前攻、当前防需要消耗的法力值、状态。卡牌位置详见 战斗动画设计文档。
		 * 状态可能存在多个值，以’S’分隔），没有特殊状态则值为-1。状态值为法术ID值。
		 * 例1：11P201P11P0P800P1323P800P1323P6P-1
		 * 表示：p2玩家云台位置为哪吒,11级，0阶，攻800，防1323，上阵法力值6，无特殊状态
		 * 例2：1012P101P9P1P700P1100P700P1100P5P530
		 * 表示：p1玩家先锋位置为姜子牙,9级，1阶，攻700，防1100，上阵法力值5，处于封咒状态
		 * 例3：1012P101P9P1P700P1100P700P1100P5P530S560
		 * 表示：p1玩家先锋位置为姜子牙,9级，1阶，攻700，防1100，上阵法力值5，处于封咒和入痘状态
		 * 末尾追加R符文ID  标识该卡有此符文加成
		 */
		private String cards = "";
		private String specialCars = "";


		public boolean hasBuff() {
			return ListUtil.isNotEmpty(buffs);
		}
	}

	public static RDCombat fromCombat(Combat combat) {
		Combat cbt = CloneUtil.clone(combat);
		RDCombat rdc = new RDCombat();
		rdc.setCombatId(cbt.getId());
		rdc.setAwardDesc(cbt.getAwardDesc());
		rdc.setPlayingId(cbt.getPlayer(PlayerId.P1).getUid());
		rdc.setRound(cbt.getRound());
		rdc.setP1(fromPlayer(cbt.getPlayer(PlayerId.P1), PlayerId.P1,combat.getRound()));
		rdc.setP2(fromPlayer(cbt.getPlayer(PlayerId.P2), PlayerId.P1,combat.getRound()));
		rdc.setYeGuaiType(cbt.getYeguaiType());
		List<AnimationSequence> animationList = cbt.getAnimationList();
		animationList = animationList.stream().filter(am -> !am.getList().isEmpty()).collect(Collectors.toList());
		rdc.setAnimation(animationList);
		if (combat.getResult()!=null){
			rdc.setResult(combat.getResult());
		}
		return rdc;
	}

	public static RDCombat getP2fromCombat(Combat combat) {
		Combat cbt = CloneUtil.clone(combat);
		RDCombat rdc = new RDCombat();
		rdc.setCombatId(cbt.getId());
		rdc.setPlayingId(cbt.getPlayer(PlayerId.P2).getUid());
		rdc.setRound(cbt.getRound());
		rdc.setP1(fromPlayer(cbt.getPlayer(PlayerId.P2), PlayerId.P2,combat.getRound()));
		rdc.setP2(fromPlayer(cbt.getPlayer(PlayerId.P1), PlayerId.P2,combat.getRound()));
		List<AnimationSequence> animationList = cbt.getAnimationList();
		animationList = animationList.stream().filter(am -> !am.getList().isEmpty()).collect(Collectors.toList());
		for (AnimationSequence as : animationList) {
			List<Animation> anims = as.getList();
			for (Animation an : anims) {
				if (an.getPos() != null && an.getPos() != -1) {
					int pos = an.getPos() >= 1000 ? an.getPos() - 1000 : an.getPos() + 1000;
					an.setPos(pos);
				}

				if (an.getPos1() != null && an.getPos1() != -1) {
					int pos = an.getPos1() >= 1000 ? an.getPos1() - 1000 : an.getPos1() + 1000;
					an.setPos1(pos);
				}

				if (an.getPos2() != null && an.getPos2() != -1) {
					int pos = an.getPos2() >= 1000 ? an.getPos2() - 1000 : an.getPos2() + 1000;
					an.setPos2(pos);
				}
				if (StrUtil.isNotBlank(an.getCards())){
					String[] cardStrs=an.getCards().split("N");
					StringBuilder sb=new StringBuilder(64);
					for (String card:cardStrs){
						String[] info=card.split("P");
						int pos=Integer.parseInt(info[0]);
						info[0]= pos >= 1000?String.valueOf(pos-1000):String.valueOf(pos+1000);
						StringBuilder newSB=new StringBuilder(32);
						for (String s:info){
							newSB.append(s);
							newSB.append("P");
						}
						sb.append(newSB);
						sb.append("N");
					}
					an.setCards(sb.substring(0,sb.length()-1));
				}
			}
		}
		rdc.setAnimation(animationList);
		return rdc;
	}

	private static RDPlayer fromPlayer(Player player, PlayerId mainPlayerId,int round) {
		RDPlayer rdp = new RDPlayer();
		rdp.setIconId(player.getIconId());
		rdp.setGodId(player.getGodId());
		rdp.setImgId(player.getImgId());
		rdp.setName(player.getName());
		rdp.setLv(player.getLv());
		rdp.setMaxHp(player.getMaxHp());
		rdp.setHp(player.getHp());
		rdp.setMaxMp(player.getMaxMp());
		rdp.setMp(player.getMp());
		rdp.setDiscard(player.getDiscard().size());
		rdp.setUid(player.getUid());
		rdp.setBloodBarNum(player.getBloodBarNum());
		rdp.setCardFromUid(player.getCardFromUid());
		rdp.setBuffs(RDCombatBuf.getInstances(player.getRunes()));
		if (null != player.getWeapons() && !player.getWeapons().isEmpty()) {
			// 法宝ID及数量拼接的字符串。多个法宝之间以‘N’分隔，代表“AND”;法宝ID与数量之间以‘H’分隔，代表HAVE
			StringBuilder weaponStr = new StringBuilder();
			for (Weapon weapon : player.getWeapons()) {
				weaponStr.append('N');
				weaponStr.append(weapon.getId());
				weaponStr.append('H');
				weaponStr.append(weapon.getNum());
			}
			rdp.setWeapons(weaponStr.substring(1));
		}
		StringBuilder cardStr = new StringBuilder();

		// 战场阵位卡牌
		cardStr.append(CombatCardTools.getCardStrs(player.getPlayingCards(), mainPlayerId));
		// 手牌
		cardStr.append(CombatCardTools.getCardStrs(player.getHandCards(), mainPlayerId));
		// 牌堆
		cardStr.append(CombatCardTools.getCardStrs(player.getDrawCards().toArray(new BattleCard[0]), mainPlayerId));
		// 坟场
		cardStr.append(CombatCardTools.getCardStrs(player.getDiscard().toArray(new BattleCard[0]), mainPlayerId));
		// 援军
		cardStr.append(CombatCardTools.getCardStrs(player.getReinforceCards().toArray(new BattleCard[0]), mainPlayerId));
		// 异次元
		cardStr.append(CombatCardTools.getCardStrs(player.getDegenerator().toArray(new BattleCard[0]), mainPlayerId));
		if (cardStr.length() > 0) {
			rdp.setCards(cardStr.substring(1));
		}
		if (round == 1) {
			rdp.setSpecialCars(player.getSpecialCards());
		}
		return rdp;
	}


	/**
	 * 战斗buf
	 */
	@Data
	@NoArgsConstructor
	public static class RDCombatBuf implements Serializable {
		private static final long serialVersionUID = 7906359125031008125L;
		private int runeId;
		private int level;
		/** 是否发动 */
		private boolean isToPerform;

		public RDCombatBuf(CombatBuff buff) {
			this.runeId = buff.getRuneId();
			this.level = buff.getLevel();
			this.isToPerform = buff.isToPerform();
		}

		public static List<RDCombatBuf> getInstances(List<CombatBuff> buffs) {
			if (ListUtil.isEmpty(buffs)) {
				return new ArrayList<>();
			}
			return buffs.stream().map(tmp -> new RDCombatBuf(tmp)).collect(Collectors.toList());
		}
	}

}
