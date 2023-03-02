package com.bbw.god.game.combat.pvp;

import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.attack.Effect.EffectResultType;
import com.bbw.god.rd.RDSuccess;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lwb
 * @date 2019年8月25日
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class RDPVPcombat extends RDSuccess implements Serializable {
	private static final long serialVersionUID = 1L;
	private RDCombat firstCombatResult = null;
	private RDCombat secondCombatResult = null;
	private Long winnerId = null;// 1 为结算 0为未结算
	private Integer roundPlayingTime = null;
	private Integer ZCtimes = null;// 招财次数
	private Integer winnerAttakHp=0;

	private List<RDTempResult> rdtList = null;// 法宝使用

	public RDPVPcombat(Combat combat) {
		winnerId = 0l;
		if (combat.hadEnded()) {
			winnerId = combat.getPlayer(PlayerId.fromValue(combat.getWinnerId())).getUid();
		}
		RDCombat p1 = null;
		RDCombat p2 = null;
		if (combat.getPlayingId() == PlayerId.P2) {
			p1 = RDCombat.getP2fromCombat(combat);
			p2 = RDCombat.fromCombat(combat);
		} else {
			p1 = RDCombat.fromCombat(combat);
			p2 = RDCombat.getP2fromCombat(combat);
		}
		p1.setIsFirst(1);
		firstCombatResult = p1.getPlayingId() < 0 ? null : p1;
		secondCombatResult = p2.getPlayingId() < 0 ? null : p2;
		List<AnimationSequence> anims = combat.getAnimationList();
		Map<Integer, Long> typesMap = anims.stream().collect(Collectors.groupingBy(AnimationSequence::getType, Collectors.counting()));
		float times = 0;
		for (Map.Entry<Integer, Long> mp : typesMap.entrySet()) {
			switch (EffectResultType.fromValue(mp.getKey())) {
			case PLAY_ANIMATION:
				times += 1.5 * mp.getValue();
				break;
			case CARD_VALUE_CHANGE:
				times += 1.0 * mp.getValue();
				break;
			case CARD_POSITION_CHANGE:
				times += 1.1 * mp.getValue();
				break;
			default:
			}
		}
		roundPlayingTime = (int) Math.ceil(times / 2.5);
	}

	/**
	 * 卡牌添加
	 * 
	 * @param res
	 * @return
	 */
	public static RDPVPcombat fromWeaponToAddCard(RDTempResult res, long oppuid) {
		List<RDTempResult> results = new ArrayList<RDTempResult>();
		results.add(res);
		// 生成 对方视角
		RDTempResult rdT = new RDTempResult();
		if (res.getCard() != null) {
			rdT.setCard(CombatCardTools.getOppCardStrs(res.getCard()));
		}
		if (res.getCards() != null) {
			rdT.setCards(CombatCardTools.getOppCardStrs(res.getCards()));
		}
		rdT.setPlayingId(oppuid);
		rdT.setWid(res.getWid());
		results.add(rdT);

		RDPVPcombat rd = new RDPVPcombat();
		rd.setRdtList(results);
		return rd;
	}

}
