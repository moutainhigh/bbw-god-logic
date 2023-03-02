package com.bbw.god.game.combat.data;

import java.io.Serializable;
import java.util.List;

import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 临时返回结果
* @author lwb  
* @date 2019年8月8日  
* @version 1.0  
*/

@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RDTempResult extends RDSuccess implements Serializable {
	private static final long serialVersionUID =1L;
	@Getter
	@Setter
	private Integer wid = null;
	@Setter
	public Integer treasureId = null;
	public String atk=null;//攻击力加成
	@Getter
	@Setter
	private Long playingId = null;// 当前出手的玩家
	public String hp=null;//防御力加成
	@Setter
	public Integer mp=null;//法术值加成
	@Setter
	@Getter
	public String cards=null;//添加卡牌
	@Setter
	@Getter
	public String card=null;//添加卡牌
	//---------上面为法宝使用结果---下面为速战或自动战斗返回信息------
	
	//回合战斗信息
	@Setter
	public List<RDCombat> autoAttakRes=null;
	//战斗结果
	@Setter
	public RDFightResult result=null;
	
	
	//cards: 例1：11P201P11P0P800P1323P6P-1 表示：p2玩家云台位置为哪吒,11级，0阶，攻800，防1323，上阵法力值6，无特殊状态
	
	public void setAtk(int val) {
		this.atk=String.valueOf(val);
	}
	public void setAtk(float val) {
		this.atk=String.valueOf(val);
	}
	public void setHp(int val) {
		this.hp=String.valueOf(val);
	}
	public void setHp(float val) {
		this.hp=String.valueOf(val);
	}

	public void addCards(PlayerId playerId,BattleCard[] cardList) {
		if (null==this.cards) {
			this.cards="";
		}
		String str=this.cards;
		for(BattleCard addcard:cardList) {
			if (null==addcard) {
				continue;
			}
			str += CombatCardTools.getCardStr(addcard, str, playerId);
		}
		this.cards=str;
	}
	public void addCard(PlayerId playerId,BattleCard addcard) {
		this.card = CombatCardTools.getCardStr(addcard, "", playerId);
	}
	
}
