package com.bbw.god.game.combat.runes.service.impl.entry;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.CombatBuff;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.runes.CombatRunesParam;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.combat.runes.service.IRoundEndStageRunes;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 陨星 每回合结束时，破除我方场上1张卡牌[10]%永久防御值，若目标阵亡则对相邻的1张卡牌造成溢出百分比的伤害，直到伤害为0。
 * <p>
 * 10阶陨星 -100%永久防
 * <p>
 * 对方场上从左至右，依次存在3张卡牌，防御值上限都为10，当前防御值依次为5、3、2
 * <p>
 * 触发陨星，选取当前防御值为5的卡牌，计算该卡牌当前防御值占防御上限的比例，得50%。减少该卡牌50%永久攻防，并从陨星的100%永久攻防中减少50%，剩50%。
 * 由于目标卡牌死亡，则对相邻的1张卡牌造成伤害，相邻卡牌的当前防御值为3，计算该卡牌的防御比例，得30%。减少该卡牌30%永久攻防，并从陨星的50%永久攻防中减少30%，剩20%。
 *
 * @author: suhq
 * @date: 2022/9/23 2:25 下午
 */
@Service
public class Runes331407 implements IRoundEndStageRunes {
	@Override
	public int getRunesId() {
		return RunesEnum.YUN_XING_ENTRY.getRunesId();
	}

	@Override
	public Action doRoundEndRunes(CombatRunesParam param) {
		Action action = new Action();
		Player performer = param.getPerformPlayer();
		List<BattleCard> playingCards = performer.getPlayingCards(false);
		if (ListUtil.isEmpty(playingCards)) {
			return action;
		}
		CombatBuff combatBuff = performer.gainBuff(getRunesId());
		int harmRate = 10 * combatBuff.getLevel();
		List<Effect> effects = new ArrayList<>();
		//处理首张卡
		BattleCard playingCard = playingCards.get(0);
		int deductHp = harmRate * playingCard.getInitHp() / 100;
		CardValueEffect effect = CardValueEffect.getSkillEffect(getRunesId(), playingCard.getPos());
		effect.setRoundHp(-deductHp);
		effect.setSequence(param.getNextSeq());
		effects.add(effect);
		//计算剩余溢出
		int maxCardRate = (int) (playingCard.getHp() * 1.0 / playingCard.getInitHp() * 100);
		int remainRate = harmRate - maxCardRate;
		while (remainRate > 0) {
			int pos = playingCard.getPos();
			int battleCardIndex = PositionService.getBattleCardIndex(pos);
			//已是最后一张卡
			if (battleCardIndex >= performer.getPlayingCards().length - 1) {
				break;
			}
			//相邻卡效果
			int nextPosIndex = battleCardIndex + 1;
			playingCard = performer.getPlayingCards(nextPosIndex);
			//没有相邻卡，则退出
			if (null == playingCard) {
				break;
			}
			deductHp = remainRate * playingCard.getInitHp() / 100;
			CardValueEffect sputteringEffect = CardValueEffect.getSkillEffect(getRunesId(), playingCard.getPos());
			sputteringEffect.setRoundHp(-deductHp);
			sputteringEffect.setSequence(param.getNextSeq());
			effects.add(sputteringEffect);

			//计算剩余溢出
			maxCardRate = (int) (playingCard.getHp() * 1.0 / playingCard.getInitHp() * 100);
			remainRate = remainRate - maxCardRate;
		}
		action.addEffects(effects);
		action.setTakeEffect(true);
		return action;
	}
}
