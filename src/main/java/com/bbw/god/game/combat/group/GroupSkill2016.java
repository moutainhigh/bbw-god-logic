package com.bbw.god.game.combat.group;

import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.PlayerId;
import com.bbw.god.game.combat.data.attack.BattleSkillEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.config.card.CardSkillTool;
import com.bbw.god.game.config.card.CfgCardSkill;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <pre>
 * <font color=red>闻仲四天君</font>
 * 两名以上在场，该回合为我方全体卡牌追加无相技能，三名以上全体卡牌追加金刚技能。
 * </pre>
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-07-01 01:39
 */
@Service
public class GroupSkill2016 extends GroupSkillService {
	private static final int GROUP_ID = 2016;//组合ID
	private static final int MINIMUM_CARDS = 2;//至少需要多少张才能形成组合

	@Override
	public boolean match(int groupId) {
		return GROUP_ID == groupId;
	}

	@Override
	protected int getMinimumCards() {
		return MINIMUM_CARDS;
	}

	@Override
	protected List<Effect> groupAttack(Combat combat, PlayerId playing, List<BattleCard> groupCards) {
		//闻仲四天君	两名以上在场，该回合为我方全体卡牌追加无相技能，三名以上全体卡牌追加金刚技能。
		//我方上阵卡牌，含云台
		List<BattleCard> playingCards = combat.getPlayingCards(playing, true);
		List<Effect> atks = new ArrayList<>(0);
		Optional<BattleSkill> skillOp = getSkillId(groupCards.size());
		if (!skillOp.isPresent()){
			return atks;
		}
		int seq = combat.getAnimationSeq();
		BattleSkill skill=skillOp.get();
		for (BattleCard card : playingCards) {
			BattleSkillEffect effect = BattleSkillEffect.getSkillEffect(GROUP_ID, card.getPos());
			effect.addSkill(skill.getId(), skill.getTimesLimit());
			effect.setSequence(seq);
			atks.add(effect);
		}
		return atks;
	}

	private Optional<BattleSkill> getSkillId(int size) {
		int skillId=0;
		if (size==2){
			skillId=CombatSkillEnum.WX.getValue();
		}else if (size>2){
			skillId=CombatSkillEnum.JG.getValue();
		}
		if (skillId==0){
			return Optional.empty();
		}
		Optional<CfgCardSkill> cardSkillOpById = CardSkillTool.getCardSkillOpById(skillId);
		if (!cardSkillOpById.isPresent()){
			return Optional.empty();
		}
		return Optional.of(BattleSkill.instanceSkill(GROUP_ID,cardSkillOpById.get()));
	}
}
