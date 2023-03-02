package com.bbw.god.game.combat.pve;

import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.FighterInfo;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.combat.BattleCardService;
import com.bbw.god.game.combat.CombatRedisService;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.RDCombat;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.data.weapon.Weapon;
import com.bbw.god.game.combat.runes.CombatRunesPerformService;
import com.bbw.god.game.combat.video.service.CombatVideoService;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.CardSkillPosEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTool;
import com.bbw.god.gameuser.task.timelimit.*;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lwb
 * @date 2020/4/15 16:01
 */
@Service
@Slf4j
public class PVEFightTaskLogic {
    /** 村庄疑云活动类型1 */
    private static final List<TaskGroupEnum> CUNZ_YI_YUN_NORMAL_MODEL = Arrays.asList(TaskGroupEnum.WAN_SHENG_JIE_TASK,
            TaskGroupEnum.THANKS_GIVING_TASK, TaskGroupEnum.NEW_YEAR_AND_CHRISTMAS_TASK);
    /** 村庄疑云活动类型2 */
    private static final List<TaskGroupEnum> CUNZ_YI_YUN_SPECIAL_FIGHT_MODEL = Arrays.asList(
            TaskGroupEnum.SPRING_FESTIVAL_TASK,
            TaskGroupEnum.QING_MING_TASK,
            TaskGroupEnum.DRAGON_BOAT_FESTIVAL_TASK);


    @Qualifier("combatPVEInitService")
    @Autowired
    private CombatPVEInitService combatInitService;

    @Autowired
    private CombatRedisService combatService;

    @Autowired
    private CombatVideoService videoService;

    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private BattleCardService battleCardService;
    @Autowired
    private CombatRunesPerformService runesPerformService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private UserTimeLimitTaskService userTimeLimitTaskService;
    @Autowired
    private CunZYiYunFightService cunZYiYunFightService;
    @Autowired
    private TimeLimitFightTaskService timeLimitFightTaskService;

    /**
     * 初始化战斗
     *
     * @param fightType
     * @param myUid
     * @param fightTaskId
     * @return
     */
    public RDCombat initFightData(int fightType, long myUid, Long fightTaskId) {
        FightTypeEnum type = FightTypeEnum.fromValue(fightType);
        UserTimeLimitTask ut = gameUserService.getUserData(myUid, fightTaskId, UserTimeLimitTask.class).get();
        CPlayerInitParam myParam = getMyFightsInfo(ut, type);
        CombatPVEParam pveParam = getInitPVEParam(ut, type);
        Combat combat = combatInitService.initCombatPVE(myParam, pveParam.getAiPlayer(), pveParam);
        //执行初始化符文效果
        runesPerformService.runInitCombatRunes(combat.getFirstPlayer(), combat.getSecondPlayer(), combat.getId());
        battleCardService.firstMoveDrawCardsToHand(combat.getFirstPlayer());
        battleCardService.firstMoveDrawCardsToHand(combat.getSecondPlayer());
        runesPerformService.runInitRoundRunes(combat.getFirstPlayer(), combat.getSecondPlayer(), combat.getId());
        combatService.save(combat);
        RDCombat rdc = RDCombat.fromCombat(combat);
        videoService.addRoundData(combat, 0);
        //清除战斗结果缓存
        TimeLimitCacheUtil.setFightResultCache(myUid, null);
        return rdc;
    }

    /**
     * 获取自己的战斗信息
     *
     * @param ut
     * @param fightType
     * @return
     */
    public CPlayerInitParam getMyFightsInfo(UserTimeLimitTask ut, FightTypeEnum fightType) {
        long uid = ut.getGameUserId();
        TaskGroupEnum taskGroup = TaskGroupEnum.fromValue(ut.getGroup());
        List<CCardParam> cardParams = getCardParams(uid, taskGroup, ut.getBaseId());
        List<Weapon> weapons = new ArrayList<>();
        List<UserTreasure> userTreasures = userTreasureService.getFightTreasures(uid);
        for (UserTreasure t : userTreasures) {
            weapons.add(new Weapon(t.getBaseId(), t.gainTotalNum()));
        }
        GameUser gu = gameUserService.getGameUser(uid);
        CPlayerInitParam playerInfo = CPlayerInitParam.initParam(gu, cardParams, new ArrayList<>(), weapons);
        changeUserParam(ut, playerInfo);
        return playerInfo;
    }

    /**
     * 获取卡牌参数
     *
     * @param uid
     * @param taskGroup
     * @return
     */
    private List<CCardParam> getCardParams(long uid, TaskGroupEnum taskGroup, int taskId) {
        List<CCardParam> cardParams = new ArrayList<>();
        if (taskGroup == TaskGroupEnum.CUN_ZHUANG_TASK) {
            List<CfgCardEntity> cards = getMyFightCards();
            List<UserCard> userCards = userCardService.getUserCards(uid);
            for (CfgCardEntity card : cards) {
                int cardId = card.getId();
                int deifyCardId = CardTool.getDeifyCardId(card.getId());
                UserCard userCard = userCards.stream().filter(tmp -> tmp.getBaseId() == cardId || tmp.getBaseId() == deifyCardId).findFirst().orElse(null);
                if (null != userCard) {
                    cardParams.add(CCardParam.init(userCard.getBaseId(), userCard.getLevel(), userCard.getHierarchy(), userCard.getStrengthenInfo()));
                } else {
                    cardParams.add(CCardParam.init(cardId, 0, 0, null));
                }
            }
            return cardParams;
        }
        if (CUNZ_YI_YUN_NORMAL_MODEL.contains(taskGroup)) {
            List<UserCard> fightingCards = userCardService.getFightingCards(uid);
            for (UserCard card : fightingCards) {
                cardParams.add(CCardParam.init(card.getBaseId(), card.getLevel(), card.getHierarchy(), card.getStrengthenInfo()));
            }
            return cardParams;
        }
        if (CUNZ_YI_YUN_SPECIAL_FIGHT_MODEL.contains(taskGroup)) {
            cardParams.addAll(getUserCardGroups(uid, taskGroup, taskId));
            return cardParams;
        }
        return cardParams;
    }

    /**
     * 获取PVE战斗初始化信息
     *
     * @param ut
     * @param fightType
     * @return
     */
    public CombatPVEParam getInitPVEParam(UserTimeLimitTask ut, FightTypeEnum fightType) {
        long uid = ut.getGameUserId();
        long fightTaskId = ut.getId();
        userTimeLimitTaskService.checkIsAble(uid, fightTaskId);
        List<CCardParam> cardParams = new ArrayList<>();
        RDFightsInfo fighterInfo = getOppFighterInfo(ut);
        for (RDFightsInfo.RDFightCard card : fighterInfo.getCards()) {
            cardParams.add(CCardParam.init(card.getBaseId(), card.getLevel(), card.getHierarchy(), card.getStrengthenInfo()));
        }

        CPlayerInitParam ai = new CPlayerInitParam();
        ai.setHeadImg(fighterInfo.getHead());
        if (StrUtil.isNotBlank(fighterInfo.getNickname())) {
            ai.setNickname(fighterInfo.getNickname());
        } else {
            ai.setNickname(CardTool.getCardById(cardParams.get(0).getId()).getName());
        }
        ai.setLv(fighterInfo.getLevel());
        ai.setCards(cardParams);
        CombatPVEParam pveParam = new CombatPVEParam();
        pveParam.setAiPlayer(ai);
        pveParam.setFightTaskId(fightTaskId);
        pveParam.setFightType(fightType.getType());
        return pveParam;
    }

    /**
     * 生成参战卡牌
     *
     * @return
     */
    public List<CfgCardEntity> getMyFightCards() {
        List<CfgCardEntity> cards = new ArrayList<>();
        cards.addAll(CardTool.getRandomCard(5, 5));
        cards.addAll(CardTool.getRandomCard(4, 5));
        List<Integer> excludes = cards.stream().map(CfgCardEntity::getId).collect(Collectors.toList());
        cards.addAll(CardTool.getRandomCards(10, excludes));
        return cards;
    }

    /**
     * 获取对手数据
     *
     * @param ut
     */
    private RDFightsInfo getOppFighterInfo(UserTimeLimitTask ut) {
        TaskGroupEnum taskGroup = TaskGroupEnum.fromValue(ut.getGroup());
        CfgTaskEntity taskEntity = TaskTool.getTaskEntity(taskGroup, ut.getBaseId());
        return getOppInfo(ut.getGameUserId(), taskEntity.getId(), taskGroup);
    }

    /**
     * 获得对手信息
     *
     * @param taskId
     * @param taskGroup
     * @return
     */
    private RDFightsInfo getOppInfo(long uid, int taskId, TaskGroupEnum taskGroup) {
        if (CUNZ_YI_YUN_NORMAL_MODEL.contains(taskGroup)) {
            return cunZYiYunFightService.getOrCreatOpponentFightsInfo(uid, taskId, taskGroup);
        }
        if (CUNZ_YI_YUN_SPECIAL_FIGHT_MODEL.contains(taskGroup)) {
            RDFightsInfo info = cunZYiYunFightService.getOrCreatOpponentFightsInfo(taskId, taskGroup);
            //为卡牌获取技能
            cunZYiYunFightService.cardInstallSkills(info);
            return info;
        }
        return cunZYiYunFightService.generateOpponentCards(taskId, taskGroup);
    }


    /**
     * 获得玩家战斗卡组
     *
     * @param uid
     * @return
     */
    private List<CCardParam> getUserCardGroups(long uid, TaskGroupEnum taskGroup, int taskId) {
        List<CCardParam> ownCardGroup = cunZYiYunFightService.getOwnCardGroup(uid, taskGroup, taskId);
        if (ownCardGroup.isEmpty()) {
            throw new ExceptionForClientTip("card.grouping.not.blank");
        }
        List<CCardParam> cardParams = new ArrayList<>();
        for (CCardParam cardParam : ownCardGroup) {
            UserCard.UserCardStrengthenInfo strengthenInfo = new UserCard.UserCardStrengthenInfo();
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_0, cardParam.getSkills().get(0));
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_5, cardParam.getSkills().get(1));
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_10, cardParam.getSkills().get(2));
            cardParam.setStrengthenInfo(strengthenInfo);
            CCardParam cCardParam = CCardParam.init(cardParam.getId(), cardParam.getLv(), cardParam.getHv(), cardParam.getStrengthenInfo());
            cardParams.add(cCardParam);
        }
        return cardParams;
    }

    /**
     * 根据规则更改玩家参数
     *
     * @param ut
     * @param playerInfo
     */
    private void changeUserParam(UserTimeLimitTask ut, CPlayerInitParam playerInfo) {
        TaskGroupEnum taskGroup = TaskGroupEnum.fromValue(ut.getGroup());
        if (!CUNZ_YI_YUN_SPECIAL_FIGHT_MODEL.contains(taskGroup)) {
            return;
        }
        FighterInfo attackerInfo = TimeLimitTaskTool.getAttackerCfgInfo(taskGroup, ut.getBaseId());
        playerInfo.setLv(attackerInfo.getLv());
    }

}
