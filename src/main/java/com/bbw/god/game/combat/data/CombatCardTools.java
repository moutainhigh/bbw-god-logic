package com.bbw.god.game.combat.data;

import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardStatus;

import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2019年9月23日 上午10:21:55 类说明 卡牌工具类
 * 
 */
public class CombatCardTools {

	/**
	 * 卡牌各属性信息拼接的字符串。多个卡牌之间以‘N’分隔，代表“AND”,属性之间以‘P’分隔。
	 * 属性顺序依次为卡牌位置、图片ID、等级、阶级、回合初攻、回合初防、当前攻、当前防需要消耗的法力值、状态。卡牌位置详见 战斗动画设计文档。
	 * 状态可能存在多个值，以’S’分隔），没有特殊状态则值为-1。状态值为法术ID值。
	 * 例1：11P201P11P0P800P1323P800P1323P6P-1P0
	 * 表示：p2玩家云台位置为哪吒,11级，0阶，攻800，防1323，上阵法力值6，无特殊状态
	 */
	public static String getCardStrs(BattleCard[] cards, PlayerId mainPlayerId) {
		String str = "";
		for (BattleCard addcard : cards) {
			if (null == addcard) {
				continue;
			}
			str += getCardStr(addcard, str, mainPlayerId);
		}
		if (str.equals("")) {
			return str;
		}
		return "N" + str;
	}

	public static String getCardStrs(List<BattleCard> cards, PlayerId mainPlayerId) {
		String str = "";
		for (BattleCard addcard : cards) {
			if (null == addcard) {
				continue;
			}
			str += getCardStr(addcard, str, mainPlayerId);
		}
		if (str.equals("")) {
			return str;
		}
		return "N" + str;
	}
	public static String getCardStr(BattleCard card, String cardContent, int pos) {
		StringBuilder cardStr = new StringBuilder();
		if (!cardContent.equals("")) {
			cardStr.append('N');
		}
		cardStr.append(pos);
		cardStr.append('P');
		cardStr.append(card.getImgId());
		cardStr.append('P');
		cardStr.append(card.getLv());
		cardStr.append('P');
		cardStr.append(card.getHv());
		cardStr.append('P');
		cardStr.append(card.getInitAtk());
		cardStr.append('P');
		cardStr.append(card.getInitHp());
		cardStr.append('P');
		cardStr.append(card.getRoundAtk());
		cardStr.append('P');
		cardStr.append(card.getRoundHp());
		cardStr.append('P');
		cardStr.append(card.getMp());
		cardStr.append('P');
		String skillStatus = "";
		// 法术状态
		for (BattleCardStatus skillId : card.getStatus()) {
			skillStatus += 'S';
			skillStatus += skillId.getSkillID();
		}
		// 法术状态
		for (CardValueEffect effect : card.getLastingEffects()) {
			if (effect.isEffective()) {
				skillStatus += 'S';
				skillStatus += effect.getSourceID();
			}
		}
		// 飞行技能当作法术状态标识给客户端显示
		if (card.canFly()) {
			skillStatus += 'S';
			skillStatus += CombatSkillEnum.FX.getValue();
		}
		if (skillStatus.length() > 0) {
			cardStr.append(skillStatus.substring(1));
		} else {
			cardStr.append("-1");
		}
		cardStr.append("P");
		cardStr.append(card.getIsUseSkillScroll());
		cardStr.append("P");
		cardStr.append(card.getBuff());
		//性别
		cardStr.append("P");
		if (card.getSex()!=null){
			cardStr.append(card.getSex());
		} else {
			cardStr.append(0);
		}
		//属性
		cardStr.append("P");
		cardStr.append(card.getType().getValue());
		//星级
		cardStr.append("P");
		cardStr.append(card.getStars());
		//时装
		cardStr.append("P");
		if (card.getFashion() != null) {
			cardStr.append(card.getFashion());
		} else {
			cardStr.append(0);
		}
		return cardStr.toString();
	}

	public static String getCardStr(BattleCard card, String cardContent, PlayerId mainPlayerId) {
		int pos = card.getPos();
		if (PlayerId.P2 == mainPlayerId) {
			// p2为主视角 则坐标交换
			pos = card.getPos() > 1000 ? card.getPos() - 1000 : card.getPos() + 1000;
		}
		return getCardStr(card,cardContent,pos);
	}

	/**
	 * 卡牌坐标转换为敌对视角
	 * 
	 * @param strs
	 * @return
	 */
	public static String getOppCardStrs(String strs) {
		String[] strings = strs.split("N");
		String newStr = "";
		for (String s : strings) {
			if (s.equals("")) {
				continue;
			}
			int end = s.indexOf("P");
			int pos = Integer.parseInt(s.substring(0, end));
			pos = pos > 1000 ? pos - 1000 : pos + 1000;
			newStr += "N" + pos + s.substring(end, s.length());
		}
		if (newStr.equals("")) {
			return strs;
		}
		return newStr.substring(1);
	}
}
