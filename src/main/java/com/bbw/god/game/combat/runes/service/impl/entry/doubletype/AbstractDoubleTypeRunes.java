package com.bbw.god.game.combat.runes.service.impl.entry.doubletype;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.service.IRoundStageRunes;
import com.bbw.god.game.config.TypeEnum;

import java.util.List;

/**
 * 金水、水木、木火、火土、土金的共同父类
 *
 * @author: suhq
 * @date: 2022/9/23 11:10 上午
 */
public abstract class AbstractDoubleTypeRunes implements IRoundStageRunes {

	@Override
	public Action doRoundRunes(CombatRunesParam param) {
		Action action = new Action();
		Player performPlayer = param.getPerformPlayer();
		List<BattleCard> playingCards = performPlayer.getPlayingCards(true);
		if (ListUtil.isEmpty(playingCards)) {
			return action;
		}
		List<BattleCard> targetCards = playingCards;
		int targetCardNum = getTargetCardNum();
		if (playingCards.size() > targetCardNum) {
			targetCards = PowerRandom.getRandomsFromList(playingCards, targetCardNum);
		}
		//获取初始属性卡牌数
		Player.Statistics statistics = param.getOppoPlayer().getStatistics();
		int typeNum = 0;
		for (TypeEnum typeEnum : getTypeParam()) {
			typeNum += statistics.gainInitialTypeCardNum(typeEnum.getValue());
		}

		//减少比例
		CombatBuff combatBuff = performPlayer.gainBuff(getRunesId());
		//获取初始数值
		double initNum = getInitNum();
		//添加等级
		int addLevel = combatBuff.getLevel() - 1;
		//每添加多少等级添加多少比例
		double addRate = 0.001 * addLevel;
		//最后比例
		double rate = typeNum * (initNum + addRate);
		//生成效果
		for (BattleCard targetCard : targetCards) {
			CardValueEffect effect = makeEffect(targetCard, rate);
			action.addEffect(effect);
		}
		return action;
	}

	/**
	 * 获取目标卡牌的数量
	 *
	 * @return
	 */
	protected abstract int getTargetCardNum();

	/**
	 * 获取初始数值
	 * @return
	 */
	protected abstract double getInitNum();

	/**
	 * 获取属性参数
	 *
	 * @return
	 */
	protected abstract List<TypeEnum> getTypeParam();

	/**
	 * 给目标卡牌生成效果
	 *
	 * @param targetCard
	 * @param rate
	 * @return
	 */
	protected abstract CardValueEffect makeEffect(BattleCard targetCard, double rate);
}
