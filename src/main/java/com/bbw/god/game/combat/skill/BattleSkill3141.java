package com.bbw.god.game.combat.skill;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 巫医（3141）：
 *
 * （1）0~4阶：根据卡牌存活期间损失的永久防御，较多的卡牌被回复
 * 如：巫医卡牌在第二回合上场
 * 【回合一】
 * A卡损失永久防御300点，剩余700点
 * B卡：剩余1000点
 * ：
 * 【回合二】
 * A卡损失永久防御100点，但道法增加100点，剩余700点
 * B卡损失永久防御300点，剩余700点
 * ：
 * 此时，巫医发动
 * A卡=300+100=400：：：B卡=300
 * 所以，治疗A卡：400点永久防御，恢复到1100点
 * ：
 * （2）5~9阶：根据卡牌存活期间损失的永久防御+巫医发动时损失的临时防御，较多的卡牌被回复
 * 如：巫医卡牌在第二回合上场
 * 【回合一】
 * A卡损失永久防御300点，剩余700点
 * B卡剩余1000点
 * ：
 * 【回合二】
 * A卡被飞狙（永久）损失400点，此时剩余300点
 * B卡联攻增加300点，被流动（永久）损失100点，被业火（临时）损失800点，剩余400点
 * ：
 * 此时，巫医发动
 * A卡=300+400=700，B卡100+800=900点
 * 将治愈B卡牌，回复900点，恢复到1300点
 * （3）10阶：参考5~9阶，但需加入攻击的计算。
 * 
 * @author lwb
 * @date 2020年02月19日
 * @version 1.0
 */
@Service
public class BattleSkill3141 extends BattleSkillService {
	private static final int SKILL_ID = 3141;// 技能ID
	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action ar = new Action();
		BattleCard card = psp.getPerformCard();
		if (card == null) {
			return ar;
		}
		List<BattleCard> cards=psp.getMyPlayingCards(true);
		if (ListUtil.isEmpty(cards)){
			return ar;
		}
		//每回合随机回复1张卡牌受到的永久伤害（恢复到卡牌初始攻防*阵位加成），5阶回复受到的全部伤害（恢复到存活期间最高永久血量）。10阶回复所有攻防（恢复到存活期间最高的攻防）。
		int hv=card.getHv();
		if (hv<5){
			//每回合随机回复1张卡牌受到的永久伤害（恢复到卡牌初始攻防*阵位加成）
			attackHv(psp,cards,ar);
		}else if (hv<10){
			//5阶回复受到的全部伤害（恢复到存活期间最高永久血量）
			attack5Hv(psp,cards,ar);
		}else {
			//10阶回复所有攻防（恢复到存活期间最高的攻防）。
			attack10Hv(psp,cards,ar);
		}
		return ar;
	}

	/**
	 * 0~4阶段
	 * @param psp
	 * @param cards
	 * @param ar
	 */
	private void  attackHv(PerformSkillParam psp,List<BattleCard> cards,Action ar){
		//恢复到卡牌最高的血量上限
		BattleCard targetCard=cards.get(0);
		int addRoundHp = targetCard.getRoundMaxHp()-targetCard.getRoundHp();
		for (BattleCard card:cards){
			if (card==null || !card.isAlive()){
				continue;
			}
			if (addRoundHp < (card.getRoundMaxHp() - card.getRoundHp())) {
				targetCard = card;
				addRoundHp = targetCard.getRoundMaxHp() - targetCard.getRoundHp();

			}
		}
		int addHp = Math.max(0, addRoundHp);
		attackHv(targetCard, psp.getNextAnimationSeq(), ar);
	}

	public void attackHv(BattleCard targetCard, int seq, Action ar) {
		//恢复到卡牌最高的血量上限
		int addRoundHp = targetCard.getRoundMaxHp() - targetCard.getRoundHp();
		int addHp = Math.max(0, addRoundHp);
		CardValueEffect cardValueEffect = CardValueEffect.getSkillEffect(getMySkillId(), targetCard.getPos());
		cardValueEffect.setSequence(seq);
		cardValueEffect.setRoundHp(addHp);
		ar.addEffect(cardValueEffect);
	}

	/**
	 * 5~9阶段
	 * <p>
	 * 恢复永久上限+本回合扣除的最多伤害 最大的卡
	 *
	 * @param psp
	 * @param cards
	 * @param ar
	 */
	private void attack5Hv(PerformSkillParam psp, List<BattleCard> cards, Action ar) {
		BattleCard targetCard=cards.get(0);
		int addRoundHp = targetCard.getRoundMaxHp()-targetCard.getRoundHp();
		int tempHp= targetCard.getReduceRoundTempHp();
		for (BattleCard card:cards){
			if (card==null || !card.isAlive()) {
				continue;
			}
			if ((addRoundHp + tempHp) < (card.getRoundMaxHp() - card.getRoundHp() + card.getReduceRoundTempHp())) {
				targetCard = card;
				addRoundHp = targetCard.getRoundMaxHp() - targetCard.getRoundHp();
				tempHp = targetCard.getReduceRoundTempHp();

			}
		}
		attack5Hv(targetCard, psp.getNextAnimationSeq(), ar);
	}

	public void attack5Hv(BattleCard targetCard, int seq, Action ar) {
		int addRoundHp = targetCard.getRoundMaxHp() - targetCard.getRoundHp();
		int tempHp = targetCard.getReduceRoundTempHp();
		CardValueEffect cardValueEffect = CardValueEffect.getSkillEffect(SKILL_ID, targetCard.getPos());
		cardValueEffect.setSequence(seq);
		cardValueEffect.setRoundHp(Math.max(0, addRoundHp));
		cardValueEffect.setHp(Math.max(0, tempHp));
		ar.addEffect(cardValueEffect);
	}

	/**
	 * 10阶段
	 *
	 * @param psp
	 * @param cards
	 * @param ar
	 */
	private void attack10Hv(PerformSkillParam psp, List<BattleCard> cards, Action ar) {
		BattleCard targetCard = cards.get(0);
		int addRoundHp = targetCard.getRoundMaxHp()-targetCard.getRoundHp();
		int tempHp= targetCard.getReduceRoundTempHp();
		int addRoundAtk= targetCard.getRoundMaxAtk()-targetCard.getRoundAtk();
		int tempAtk= targetCard.getReduceRoundTempAtk();
		int sumAdd=addRoundHp+addRoundAtk+tempAtk+tempHp;

		for (BattleCard card:cards){
			if (card==null || !card.isAlive()){
				continue;
			}
			int iAddRoundHp = card.getRoundMaxHp()-card.getRoundHp();
			int iTempHp= card.getReduceRoundTempHp();
			int iAddRoundAtk= card.getRoundMaxAtk()-card.getRoundAtk();
			int iTempAtk= card.getReduceRoundTempAtk();
			int iSumAdd=iAddRoundHp+iAddRoundAtk + iTempAtk + iTempHp;
			if (sumAdd < iSumAdd) {
				targetCard = card;
				addRoundHp = targetCard.getRoundMaxHp() - targetCard.getRoundHp();
				tempHp = targetCard.getReduceRoundTempHp();
				addRoundAtk = targetCard.getRoundMaxAtk() - targetCard.getRoundAtk();
				tempAtk = targetCard.getReduceRoundTempAtk();
				sumAdd = addRoundHp + addRoundAtk + tempAtk + tempHp;
			}
		}
		attack10Hv(targetCard, psp.getNextAnimationSeq(), ar);
	}

	public void attack10Hv(BattleCard targetCard, int seq, Action ar) {
		int addRoundHp = targetCard.getRoundMaxHp() - targetCard.getRoundHp();
		int tempHp = targetCard.getReduceRoundTempHp();
		int addRoundAtk = targetCard.getRoundMaxAtk() - targetCard.getRoundAtk();
		int tempAtk = targetCard.getReduceRoundTempAtk();
		CardValueEffect cardValueEffect = CardValueEffect.getSkillEffect(getMySkillId(), targetCard.getPos());
		cardValueEffect.setSequence(seq);
		cardValueEffect.setRoundHp(Math.max(0, addRoundHp));
		cardValueEffect.setRoundAtk(Math.max(0, addRoundAtk));
		cardValueEffect.setHp(Math.max(0, tempHp));
		cardValueEffect.setAtk(Math.max(0,tempAtk));
		ar.addEffect(cardValueEffect);
	}

}
