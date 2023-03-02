package com.bbw.god.game.combat.group;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.SetUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.event.CombatEventPublisher;
import com.bbw.god.game.combat.event.EPCombatAchievement;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.*;
import com.bbw.god.game.wanxianzhen.WanXianSpecialType;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.biyoupalace.cfg.BYPalaceTool;
import com.bbw.god.gameuser.biyoupalace.cfg.CfgBYPalaceSkillEntity;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * （2018）组合技：四圣显灵
 * 成员：孟章青龙，金睛白虎，金銮火凤，重甲玄武
 * 四兽同时在场时，若场上有空位则召唤麒麟
 * 将四圣兽的攻击按照从大到小排列为ABCD，麒麟的攻击力范围为【(A+B+C)/2,(B+C+D)】
 * 防御力同上。
 * <p>
 * 技能：
 * ①　当麒麟位于飞行位置时，麒麟自带飞行技能于0级技能
 * ②　其余技能将随机生成，生成范围为“碧游宫全系4级以上技能+秘传+金刚+斥退+道法+弘法”
 * ③　麒麟不会获得复活技能
 * ④　麒麟不会获得重复技能
 */
@Service
@Slf4j
public class GroupSkill2019 extends GroupSkillService {
	private static final int GROUP_ID = 2019;//组合ID
	private static final int MINIMUM_CARDS = 4;//至少需要多少张才能形成组合
	/** 召唤卡牌随机技能池不包含的技能 **/
	private static List<String> EXCLUDE_SKILLS = Arrays.asList("飞行", "修仙", "回光", "祛毒", "治愈", "得道", "升仙", "复活");
	/** 召唤卡牌随机技能池包含的其他技能 **/
	private static List<String> EXTRA_SKILLS = Arrays.asList("斥退", "解封", "鬼雄", "金身", "神照", "落羽", "蚀月", "道法", "冷刀", "化血", "弘法", "烈焰", "芒刺", "嘲讽");
	/** 召唤卡牌随机技能池里的合成卷轴技能 **/
	private static List<String> SYNTHESIS_SKILLS = Arrays.asList("解毒", "毒矢", "毒镖", "神医", "太极");
	private static Set<String> skillNames = null;
	@Autowired
	private BattleCardService battleCardService;
	@Autowired
	private CombatRunesPerformService runesPerformService;
	@Autowired
	private UserCardService userCardService;

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
		List<Effect> effects = new ArrayList<>();
		Player performPlayer = combat.getPlayer(playing);
		BattleCard card = summonCard(performPlayer, groupCards, combat);
		if (null == card) {
			return effects;
		}
		//添加组合技释放动画
		AnimationSequence asPerform = new AnimationSequence(combat.getAnimationSeq(), Effect.EffectResultType.PLAY_ANIMATION);
		//客户端动画
		for (BattleCard c : groupCards) {
			AnimationSequence.Animation action = new AnimationSequence.Animation();
			action.setSkill(GROUP_ID);
			action.setPos1(c.getPos());
			asPerform.add(action);
		}
		combat.addAnimation(asPerform);
		//添加卡牌的动画
		AnimationSequence as = new AnimationSequence(combat.getAnimationSeq(), Effect.EffectResultType.CARD_ADD);
		AnimationSequence.Animation action = new AnimationSequence.Animation();
		action.setPos1(-1);
		action.setPos2(card.getPos());
		action.setSkill(GROUP_ID);
		action.setCards(CombatCardTools.getCardStr(card, "", card.getPos()));
		as.add(action);
		combat.addAnimation(as);

		//去除原先卡的组合技
		for (BattleCard c : groupCards) {
			c.setGroupId(0);
		}
		if (performPlayer.getUid() > 0) {
			EPCombatAchievement ep = EPCombatAchievement.instance(new BaseEventParam(performPlayer.getUid()), 14900);
			CombatEventPublisher.pubCombatAchievement(ep);
		}
		// 成就盛世获麟-封神四圣兽并召唤1次麒麟
		List<Integer> groupCardIds = groupCards.stream().map(BattleCard::getImgId).collect(Collectors.toList());
		//完成盛世获麟成就
		if (isCompleteAchievement17420(performPlayer.getUid(), groupCardIds)) {
			EPCombatAchievement ep = EPCombatAchievement.instance(new BaseEventParam(performPlayer.getUid()), 17420);
			CombatEventPublisher.pubCombatAchievement(ep);
		}
		return effects;
	}

	/**
	 * 是否完成成就17420 盛世获麟
	 *
	 * @param uid
	 * @param groupCardIds 组合卡牌
	 * @return
	 */
	private boolean isCompleteAchievement17420(long uid, List<Integer> groupCardIds) {
		if (uid <= 0) {
			return false;
		}
		//场上是否拥有封神四圣兽
		List<Integer> deityFourHolyBeastIds = CardTool.getDeityFourHolyBeast();
		if (!groupCardIds.containsAll(deityFourHolyBeastIds)) {
			return false;
		}
		//玩家否拥有封神四圣兽
		List<UserCard> userCards = userCardService.getUserCards(uid, deityFourHolyBeastIds);
		if (ListUtil.isEmpty(userCards)) {
			return false;
		}
		//玩家否拥有所有封神四圣兽
		List<Integer> userCardIds = userCards.stream().map(UserCfgObj::getBaseId).collect(Collectors.toList());
		if (!userCardIds.containsAll(deityFourHolyBeastIds)) {
			return false;
		}
		return true;

	}

	/**
	 * 召唤卡牌
	 *
	 * @param performPlayer
	 * @param groupCards
	 * @param combat
	 * @return
	 */
	public BattleCard summonCard(Player performPlayer, List<BattleCard> groupCards, Combat combat) {
		int[] emptyPos = performPlayer.getEmptyBattlePos(true);
		if (emptyPos.length == 0) {
			return null;
		}
		int index = PowerRandom.getRandomBySeed(emptyPos.length) - 1;
		int toPos = emptyPos[index];
		BattleCard card = buildQiLing(PositionService.isYunTaiPos(toPos), groupCards);
		runesPerformService.runInitCardRunes(performPlayer, card);
		if (combat.getWxType() != null && combat.getWxType() == WanXianSpecialType.BEI_SHUI.getVal()) {
			card.getSkills().removeIf(p -> p.getId() == 1201 || p.getId() == 3105 || p.getId() == 3104);
		}
		card.setPos(toPos);
		battleCardService.replaceCard(performPlayer, card);
		return card;
	}


	/**
	 * 将四圣兽的攻击按照从大到小排列为ABCD，麒麟的攻击力范围为【（A+B+c）/2,B+C+D】
	 * 麒麟的等级，与在场的四圣兽中等级最高的相同，麒麟的阶数，与在场的四圣兽阶数最高的相同
	 * 防御力同上。
	 *
	 * @param yunTai
	 * @param groupCards
	 * @return
	 */
	private BattleCard buildQiLing(boolean yunTai, List<BattleCard> groupCards) {
		groupCards = groupCards.stream().sorted(Comparator.comparing(BattleCard::getAtk).reversed()).collect(Collectors.toList());
		int atk1 = (groupCards.get(0).getAtk() + groupCards.get(1).getAtk() + groupCards.get(2).getAtk()) / 2;
		int atk2 = groupCards.get(1).getAtk() + groupCards.get(2).getAtk() + groupCards.get(3).getAtk();
		int atkMin = Math.min(atk1, atk2);
		int atkMax = Math.max(atk1, atk2);
		int initAtk = PowerRandom.getRandomBetween(atkMin, atkMax);

		groupCards = groupCards.stream().sorted(Comparator.comparing(BattleCard::getHp).reversed()).collect(Collectors.toList());
		int hp1 = (groupCards.get(0).getHp() + groupCards.get(1).getHp() + groupCards.get(2).getHp()) / 2;
		int hp2 = groupCards.get(1).getHp() + groupCards.get(2).getHp() + groupCards.get(3).getHp();
		int hpMin = Math.min(hp1, hp2);
		int hpMax = Math.max(hp1, hp2);
		int initHp = PowerRandom.getRandomBetween(hpMin, hpMax);

		// 麒麟等级计算方式，四圣兽的平均等级;阶级计算方式，四圣兽的平均阶级。
		int hv = (int) groupCards.stream().mapToInt(BattleCard::getHv).average().orElse(0);
		int lv = (int) groupCards.stream().mapToInt(BattleCard::getLv).average().orElse(0);
		CfgCardEntity hideCard = CardTool.getHideCard(CardEnum.QI_LIN.getCardId());
		BattleCard hero = new BattleCard();
		hero.setId(-1000);
		hero.setImgId(hideCard.getId());
		hero.setStars(hideCard.getStar());
		hero.setName(hideCard.getName());
		hero.setType(TypeEnum.fromValue(hideCard.getType()));
		hero.setHv(hv);
		hero.setLv(lv);
		hero.setInitAtk(initAtk);
		hero.setInitHp(initHp);
		hero.setRoundAtk(initAtk);
		hero.setRoundHp(initHp);
		hero.setAtk(initAtk);
		hero.setHp(initHp);
		hero.setSkills(getRandomSkill(yunTai));
		return hero;
	}

	/**
	 * 碧游宫全系4级以上技能+秘传+金刚+斥退+道法+弘法”
	 * 麒麟不会获得复活技能
	 * 麒麟不会获得重复技能
	 */
	private List<BattleSkill> getRandomSkill(boolean needFeiXing) {
		int needSkillNum = needFeiXing ? 2 : 3;
		if (SetUtil.isEmpty(skillNames)) {
			List<CfgBYPalaceSkillEntity> allBYPSkillEntities = BYPalaceTool.getBYPSkillEntityList();
			skillNames = new HashSet<>();
			for (CfgBYPalaceSkillEntity entity : allBYPSkillEntities) {
				if (entity.getChapter() < 3) {
					continue;
				}
				for (String skillScrollNames : entity.getSkills()) {
					String[] skillScrollNamesArray = skillScrollNames.split("-");
					skillNames.add(skillScrollNamesArray[skillScrollNamesArray.length - 1]);
				}
			}
			skillNames.removeAll(EXCLUDE_SKILLS);
			skillNames.addAll(EXTRA_SKILLS);
			skillNames.addAll(SYNTHESIS_SKILLS);

		}
		Set<String> randomsFromSet = PowerRandom.getRandomsFromSet(skillNames, needSkillNum);
		if (needFeiXing){
			randomsFromSet.add("飞行");
		}
		List<BattleSkill> skills=new ArrayList<>();
		for (String skillName:randomsFromSet){
			Optional<CfgCardSkill> skillOpByName = CardSkillTool.getCardSkillOpByName(skillName);
			if (!skillOpByName.isPresent()){
				continue;
			}
			BattleSkill skill =BattleSkill.instanceBornSkill(skillOpByName.get());
			skills.add(skill);
		}
		return skills;
	}
}