package com.bbw.god.game.combat;

import com.bbw.common.ListUtil;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.data.*;
import com.bbw.god.game.combat.data.attack.CardValueEffect;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import com.bbw.god.game.combat.data.card.BattleCardStatus;
import com.bbw.god.game.combat.data.param.PerformSkillParam;
import com.bbw.god.game.combat.data.skill.BattleSkill;
import com.bbw.god.game.combat.data.skill.SkillSection;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.combat.weapon.WeaponLogic;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.guide.v3.NewerGuideEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 回合服务
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-06-30 20:16
 */
@Service
public class RoundService {
	@Autowired
	private GroupSkillRoundService groupSkillRoundService;
	@Autowired
	private SkillRoundService skillRoundService;
	@Autowired
	private NormalSkillRoundService normalAttackService;
	@Autowired
	private WeaponLogic weaponLogic;
	@Autowired
	private AttackServiceFactory attackServiceFactory;
	@Autowired
	private AcceptEffectService acceptEffectService;
	@Autowired
	private PlayerService playerService;
	@Autowired
	private CombatRunesPerformService runesPerformService;
	@Autowired
	private BattleCardService battleCardService;
	@Autowired
	private NewerGuideService newerGuideService;

	public void deployPVE(Combat combat,int autoDeploy,String moveToPlaying){
		Player player = combat.getPlayer(PlayerId.P1);
		if (autoDeploy == 1) {
			playerService.autoDeployCards(combat, player, moveToPlaying);
		} else {
			playerService.deployCards(combat, player, moveToPlaying);
		}
		Player robot = combat.getPlayer(PlayerId.P2);
		// 新手引导
		if (null != combat.getNewerGuide()) {
			Long uid = player.getUid();
			int newerGuide = newerGuideService.getNewerGuide(uid);
			if (2 == combat.getRound() && NewerGuideEnum.BIAN_ZHU_1.getStep() == newerGuide) {
				playerService.autoDeployCards(combat, robot, "100T12");
				return;
			}
			if (3 == combat.getRound() && NewerGuideEnum.BIAN_ZHU_1.getStep() == newerGuide) {
				playerService.autoDeployCards(combat, robot, "100T12");
				playerService.autoDeployCards(combat, robot, "101T13");
				return;
			}
			if (3 == combat.getRound() && NewerGuideEnum.YOU_SHANG_GUAN.getStep() == newerGuide) {
				playerService.autoDeployCards(combat, robot, "100T14");
				return;
			}
		}
		playerService.autoDeployCards(combat, robot);
	}
	/**
	 * 回合开始
	 *
	 * @param combat
	 */
	public void after(Combat combat) {
		// 回合结束，恢复卡牌数据
		combat.setRound(combat.getRound() + 1);
		runesPerformService.runRoundEndRunes(combat);
		if (combat.hadEnded()) {
			return;
		}
		doRoundEndForRune(combat);
		dealZxzPlayerMp(combat.getFirstPlayer(), combat.getFightType());
		dealZxzPlayerMp(combat.getSecondPlayer(), combat.getFightType());
		recoverPlayer(combat.getFirstPlayer(), combat.getSecondPlayer());
		recoverPlayer(combat.getSecondPlayer(), combat.getFirstPlayer());
		runesPerformService.runInitRoundRunes(combat.getFirstPlayer(), combat.getSecondPlayer(), combat.getId());
		doRoundEndSkill(combat.getFirstPlayer(), combat);
		doRoundEndSkill(combat.getSecondPlayer(), combat);
	}

	public void run(Combat combat) {
		if (combat.hadEnded()) {
			return;
		}
		// 布阵结束阶段
		skillRoundService.roundDeployEnd(combat);
		if (combat.hadEnded()) {
			return;
		}
		// -----------------组合技能阶段-------------------
		groupSkillRoundService.round(combat);
		if (combat.hadEnded()) {
			return;
		}
		// -----------------法术阶段-------------------
		skillRoundService.roundSkill(combat);
		if (combat.hadEnded()) {
			return;
		}
		//物理攻击之前的符文效果
		runesPerformService.runBeforeNormalAttackRunes(combat);
		// -----------------物理攻击阶段-------------------
		normalAttackService.round(combat);
	}

	/**
	 * 回合结束后的buff数据处理
	 *
	 * @param combat
	 */
	private void doRoundEndForRune(Combat combat) {
		Player[] players = new Player[]{combat.getFirstPlayer(), combat.getSecondPlayer()};
		for (Player player : players) {
			if (ListUtil.isEmpty(player.getRunes())) {
				continue;
			}
			for (CombatBuff rune : player.getRunes()) {
				rune.deductRound();
			}
		}
	}

	/**
	 * 处理诛仙阵相关战斗 玩家法力值
	 *
	 * @param player
	 * @param fightTypeEnum
	 */
	private void dealZxzPlayerMp(Player player, FightTypeEnum fightTypeEnum) {

		if (FightTypeEnum.ZXZ == fightTypeEnum || FightTypeEnum.ZXZ_FOUR_SAINTS == fightTypeEnum) {
			// 双方召唤师的法力值无法被降低至等级法力值以下。
			int playerMp = player.getStatistics().getMp();
			int playerCurMp = playerMp + player.getMaxMp();
			if (playerCurMp < player.getLvMp()) {
				playerMp = 0;
				player.setMaxMp(player.getLvMp());
				player.setMp(player.getLvMp());
			}
			// 双方召唤师每回合增加2点法力值上限
			player.getStatistics().setMp(playerMp);
			player.setMaxMp(player.getMaxMp() + 1);
		}
	}

	private void recoverPlayer(Player player, Player opPlayer) {
		// 恢复召唤师法力
		int maxMp = player.getMaxMp() + 1;
		player.setMaxMp(maxMp);
		player.setMp(player.getMaxMp() + player.getStatistics().getMp());
		player.getStatistics().setMp(0);
		//处理云台封禁回合数
		if (null != player.getBanYunTai() && !player.getBanYunTai().isForbid()) {
			player.getBanYunTai().lostTimes();
		}
		// 恢复卡牌
		BattleCard[] playingCards = player.getPlayingCards();
		// 上场技能
		SkillSection shangChangSkill = SkillSection.getDeploySection();
		BattleCard[] opPlayingCards = opPlayer.getPlayingCards();
		for (int i = 0; i < playingCards.length; i++) {
			BattleCard card = playingCards[i];
			if (null == card) {
				continue;
			}
			card.setBehit(false);
			card.setLeftAtk(0);
			card.setReduceRoundTempAtk(0);
			card.setReduceRoundTempHp(0);
			card.setBanDieSkills(new ArrayList<Integer>());
			// 恢复hp,atk 到回合攻击数（扣除永久伤害）
			if (card.isHit() && null != opPlayingCards[i] && !opPlayingCards[i].isKilled()) {
				card.setRoundAtk(card.getRoundAtk() + 30+20*card.getHv());
			}
			card.setAtk(card.getRoundAtk());
			card.setHp(card.getRoundHp());
			// 战场上的卡牌该回合没出手 比如拉人技能上场的，需要将上场技能去除
			// 以及重置技能的回合可用性
			List<BattleSkill> skills = card.getSkills();
			if (!skills.isEmpty()) {
				List<BattleSkill> effectSkills = new ArrayList<>();
				for (BattleSkill skill : skills) {
					if (shangChangSkill.contains(skill.getId())
							|| skill.getId() == CombatSkillEnum.JINS.getValue()
							|| skill.getId() == CombatSkillEnum.XIAO_TIAN.getValue()
							|| skill.getId() == CombatSkillEnum.JI_BIAN.getValue()) {
						skill.getTimesLimit().lostTimes();
					}
					skill.roundReset();
					if (skill.isEffective() || skill.isBornSkill()) {
						effectSkills.add(skill);
					}

				}
				card.setSkills(effectSkills);
			}
			card.getNormalAttackSkill().roundReset();
			card.getNormalDefenseSkill().roundReset();
			Optional<BattleSkill> nomalOp=card.getSkill(CombatSkillEnum.NORMAL_ATTACK.getValue());
			if (nomalOp.isPresent()) {
				//回合完重置攻击目标
				nomalOp.get().setTargetPos(-1);
			}
			card.resetRoundCardStatus();
			card.clearTransientVal();
			// 筛选有效的持续技能效果
			List<CardValueEffect> valueEffects = card.getLastingEffects();
			if (valueEffects.isEmpty()) {
				continue;
			}
			List<CardValueEffect> effectValueEffects = valueEffects.stream().filter(ve -> ve.isEffective()).collect(Collectors.toList());
			card.setLastingEffects(effectValueEffects);
		}
		// 去除单回合生效的 临时技能
		BattleCard[] handCards = player.getHandCards();
		for (BattleCard card : handCards) {
			if (card == null) {
				continue;
			}
			for (BattleSkill skill:card.getSkills()){
				if (!skill.isBornSkill()) {
					skill.getTimesLimit().roundReset();
				}
				// 重置卡牌技能（手牌中的卡牌）本回合可使用次数为1
				if (skill.getTimesLimit().getCurrentRoundTimes() ==0
						&& skill.getTimesLimit().getCurrentTotalTimes() == 0){
					skill.getTimesLimit().setCurrentTotalTimes(1);
					skill.getTimesLimit().setCurrentRoundTimes(1);
				}
			}
			List<BattleSkill> skillList = card.getSkills().stream().filter(p -> p.isBornSkill() || p.getTimesLimit().hasPerformTimes()).collect(Collectors.toList());
			card.getSkills().clear();
			card.setSkills(skillList);
		}
		// 重置所有卡牌位置
		player.resetPos();
		battleCardService.moveDrawCardsToHand(player);
		player.getStatistics().setHandCardUpLimit(5);
	}

	private void doRoundEndSkill(Player player, Combat combat) {
		BattleCard[] cards = player.getPlayingCards();
		List<Integer> handledCardIds = new ArrayList<>();
		for (int i = 0; i < CombatConfig.MAX_BATTLE_CARD; i++) {
			BattleCard card = cards[i];
			if (card == null) {
				continue;
			}
			handledCardIds.add(card.getImgId());
			List<BattleCardStatus> cardStatus = card.getRoundEndCardStatus();
			if (cardStatus.isEmpty()) {
				continue;
			}
			for (BattleCardStatus status : cardStatus) {
				BattleCard souceCard = status.getSouceCard();
				PerformSkillParam param = null;
				if (null != souceCard) {
					param = new PerformSkillParam(combat, souceCard, card);
				} else {
					param = new PerformSkillParam(combat, card);
				}
				BattleSkillService service = attackServiceFactory.getSkillAttackService(status.getSkillID());
				List<Effect> effects = service.attakRoundLasting(param);
				acceptEffectService.acceptRoundEndSkillEffect(combat, effects);
			}
			if (card.getRoundHp() == 0 || card.getHp() == 0) {
				int beginPos = PositionService.getDiscardBeginPos(player.getId());
				int toPos = battleCardService.getEmptyPos(player.getDiscard(), beginPos);
				card.setPos(toPos);
				card.reset(true);
				player.getDiscard().add(card);
				cards[i] = null;
			} else {
				card.resetRoundEndCardStatus();
			}
		}
		//处理已死亡的卡牌
		List<BattleCard> cachedCardsToDoRoundEnd = BattleCardLocalCacheService.getCachedCardsToDoRoundEnd(combat, player);
		if (ListUtil.isEmpty(cachedCardsToDoRoundEnd)) {
			return;
		}
		List<BattleCard> cardsToDoRoundEnd = cachedCardsToDoRoundEnd.stream().filter(tmp -> !handledCardIds.contains(tmp.getImgId())).collect(Collectors.toList());
		for (BattleCard card : cardsToDoRoundEnd) {
			List<BattleCardStatus> cardStatus = card.getRoundEndCardStatus();
			if (cardStatus.isEmpty()) {
				continue;
			}
			for (BattleCardStatus status : cardStatus) {
				BattleCard souceCard = status.getSouceCard();
				PerformSkillParam param = null;
				if (null != souceCard) {
					param = new PerformSkillParam(combat, souceCard, card);
				} else {
					param = new PerformSkillParam(combat, card);
				}
				BattleSkillService service = attackServiceFactory.getSkillAttackService(status.getSkillID());
				List<Effect> effects = service.attakRoundLasting(param);
				acceptEffectService.acceptRoundEndSkillEffect(combat, effects);
			}
		}

	}

}