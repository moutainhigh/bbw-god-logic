package com.bbw.god.game.combat.video;

import com.bbw.common.DateUtil;
import com.bbw.god.game.combat.data.AnimationSequence;
import com.bbw.god.game.combat.data.AnimationSequence.Animation;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.RDCombat;
import com.bbw.god.game.combat.data.RDTempResult;
import com.bbw.god.game.combat.data.attack.Effect.EffectResultType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** 
* @author 作者 ：lwb
* @version 创建时间：2019年9月16日 上午9:40:02 
* 类说明  战斗录像
*/
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
public class CombatVideo implements Serializable{
	private static final long serialVersionUID = 8095026493886308895L;
	private Long id;// 数据Id
	private String saveUrl=null;//指定存储名称
	private List<RoundData> datas = new ArrayList<>();// 回合数据
	private String datetime=DateUtil.nowToString();
	//回合数据
	@Data
	public static class RoundData implements Serializable{
		private static final long serialVersionUID = 1L;
		private RDCombat rdCombat;// 回合数据
		private RoundTrData trResult;// 法宝数据

	}
	
	public void addRoundData(RDCombat rd,int round) {
		RoundData data = getRoundData(round);
		data.setRdCombat(rd);
	}
	public void addSurrender(int round,String name) {
		RoundData data = getRoundData(round);
		if (data.getRdCombat()==null) {
			data.setRdCombat(new RDCombat());
		}
		String message="["+name+"]不敌，选择了投降！";
		data.getRdCombat().setMessage(message);
		
	} 
	public void addRoundData(RDTempResult rd,int wid,PlayerId playerId,int round) {
		if (wid!=460 && wid!=280) {
			//此处只需记录如意乾坤袋460和招魂幡280
			return;
		}
		int pos=PlayerId.P1.equals(playerId)?10:1010;
		RoundData data = getRoundData(round);
		if (data.getTrResult()==null) {
			data.setTrResult(new RoundTrData());
		}
		String cards=rd.getCards();
		if (cards==null) {
			cards=rd.getCard();
		}
		AnimationSequence as=new AnimationSequence();
		as.setType(EffectResultType.PLAY_ANIMATION.getValue());
		Animation animation=new Animation();
		animation.setSkill(wid);
		animation.setCards(cards);
		animation.setPos1(pos);
		as.add(animation);
		data.getTrResult().addAnimation(as);
	}
	
	private RoundData getRoundData(int round) {
		if (round==0 || datas.size()<(round+1)) {
			datas.add(new RoundData());
		}
		return datas.get(datas.size()-1);
	}
	
	@Data
	private static class RoundTrData implements Serializable{
		private static final long serialVersionUID = 1L;
		private List<AnimationSequence> animation = null;
		
		public void addAnimation(AnimationSequence as) {
			if (animation==null) {
				animation=new ArrayList<AnimationSequence>();
			}
			animation.add(as);
		}
	}
}
