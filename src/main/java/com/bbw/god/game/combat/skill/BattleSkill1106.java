package com.bbw.god.game.combat.skill;

import com.bbw.god.game.combat.BattleSkillService;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.attack.Action;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardFactory;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.group.GroupSkill2019;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 麒麟 1106：每回合，优先于上场技能发动，召唤1张麒麟至随机阵位，最多同时召唤1张，一场战斗触发一次。
 * （1）该技能召唤的麒麟逻辑与组合技-四圣显灵基本相同。
 * 1）召唤时，将会根据玩家持有的四圣兽等级、阶级、攻防进行数值计算；玩家未持有的四圣兽则按照0阶10级的数据进行计算。
 *
 * @author: suhq
 * @date: 2022/8/26 3:58 下午
 */
@Service
public class BattleSkill1106 extends BattleSkillService {
	private static final int SKILL_ID = CombatSkillEnum.QI_LIN.getValue();
	private static final List<Integer> SHENG_SHOU_IDS = Arrays.asList(142, 236, 347, 401, 10142, 10236, 10347, 10401);
	@Autowired
	private GroupSkill2019 groupSkill2019;
	@Autowired
	private UserCardService userCardService;

	@Override
	public int getMySkillId() {
		return SKILL_ID;
	}

	@Override
	protected Action attack(PerformSkillParam psp) {
		Action action = new Action();
		int qiLinEffectTimes = psp.getPerformPlayer().getStatistics().gainSkillEffectTime(CombatSkillEnum.QI_LIN);
		if (qiLinEffectTimes >= 1) {
			return action;
		}
		Combat combat = psp.getCombat();
		Player performPlayer = psp.getPerformPlayer();
		List<UserCard> userCards = userCardService.getUserCards(performPlayer.getUid(), SHENG_SHOU_IDS);
		List<BattleCard> battleCards = userCards.stream().map(tmp -> BattleCardFactory.buildCard(tmp)).collect(Collectors.toList());
		if (!battleCards.stream().anyMatch(tmp -> tmp.getImgId() % 10000 == 142)) {
			battleCards.add(BattleCardFactory.buildCard(142, 10, 0));
		}
		if (!battleCards.stream().anyMatch(tmp -> tmp.getImgId() % 10000 == 236)) {
			battleCards.add(BattleCardFactory.buildCard(236, 10, 0));
		}
		if (!battleCards.stream().anyMatch(tmp -> tmp.getImgId() % 10000 == 347)) {
			battleCards.add(BattleCardFactory.buildCard(347, 10, 0));
		}
		if (!battleCards.stream().anyMatch(tmp -> tmp.getImgId() % 10000 == 401)) {
			battleCards.add(BattleCardFactory.buildCard(401, 10, 0));
		}
		BattleCard card = groupSkill2019.summonCard(performPlayer, battleCards, combat);
		if (null == card) {
			return action;
		}
		// 召唤墨麒麟
		/*CfgCardEntity hideCard = CardTool.getHideCard(CardEnum.MO_QI_LIN.getCardId());
		card.setImgId(hideCard.getId());
		card.setStars(hideCard.getStar());
		card.setName(hideCard.getName());
		card.setType(TypeEnum.fromValue(hideCard.getType()));
		performPlayer.getStatistics().addSkillEffectTime(CombatSkillEnum.QI_LIN);*/
		//添加卡牌的动画
		AnimationSequence as = new AnimationSequence(combat.getAnimationSeq(), Effect.EffectResultType.CARD_ADD);
		AnimationSequence.Animation animation = new AnimationSequence.Animation();
		animation.setPos1(-1);
		animation.setPos2(card.getPos());
		animation.setSkill(SKILL_ID);
		animation.setCards(CombatCardTools.getCardStr(card, "", card.getPos()));
		as.add(animation);
		combat.addAnimation(as);
		return action;
	}
}