package com.bbw.god.gameuser.task.timelimit;

import com.bbw.common.CloneUtil;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.cache.GameDataTimeLimitCacheUtil;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.fight.FighterInfo;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.FightCardGenerateRule;
import com.bbw.god.game.transmigration.entity.TransmigrationCard;
import com.bbw.god.gameuser.card.CardSkillPosEnum;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 村庄疑云战斗服务
 *
 * @author fzj
 * @date 2022/1/6 17:08
 */
@Service
public class CunZYiYunFightService {

    @Autowired
    TimeLimitFightTaskService timeLimitFightTaskService;

    /**
     * 生成战斗者的数据
     *
     * @param taskId
     * @param taskGroup
     */
    public RDFightsInfo generateOpponentCards(int taskId, TaskGroupEnum taskGroup) {
        CfgTimeLimitTaskRules rules = TimeLimitTaskTool.getRules(taskGroup);
        FighterInfo fighterInfo = rules.getFightersInfo().get(taskId);
        //获取守卫卡牌
        List<CCardParam> cardParams = getFightBaseData(fighterInfo);
        RDFightsInfo rdFightInfo = new RDFightsInfo();
        List<RDFightsInfo.RDFightCard> cards = new ArrayList<>();
        for (CCardParam card : cardParams) {
            cards.add(RDFightsInfo.RDFightCard.instance(card));
        }
        rdFightInfo.setHead(fighterInfo.getHead());
        rdFightInfo.setNickname(fighterInfo.getNickname());
        rdFightInfo.setLevel(fighterInfo.getLv());
        rdFightInfo.setCards(cards);
        return rdFightInfo;
    }

    /**
     * 获取战斗基础数据
     *
     * @param fighterInfo
     * @return
     */
    public List<CCardParam> getFightBaseData(FighterInfo fighterInfo) {
        List<Integer> cardIds = new ArrayList<>();
        for (FightCardGenerateRule cardRule : fighterInfo.getCards()) {
            if (ListUtil.isNotEmpty(cardRule.getCardIds())) {
                cardIds.addAll(cardRule.getCardIds());
                continue;
            }
            for (int i = 0; i < cardRule.getNum(); i++) {
                int star = PowerRandom.getRandomFromList(cardRule.getStars());
                int cardId = CardTool.getRandomCard(star, 1, cardIds).get(0).getId();
                cardIds.add(cardId);
            }
        }
        List<CCardParam> cardParams = new ArrayList<>();
        for (Integer cardId : cardIds) {
            TransmigrationCard tCard = new TransmigrationCard();
            tCard.setId(cardId);
            int cardHv = PowerRandom.getRandomBetween(fighterInfo.getCardHvInterval()[0], fighterInfo.getCardHvInterval()[1]);
            int cardLv = PowerRandom.getRandomBetween(fighterInfo.getCardLvInterval()[0], fighterInfo.getCardLvInterval()[1]);
            tCard.setSkills(CardTool.getCardById(cardId).getSkills());
            cardParams.add(CCardParam.init(cardId, cardLv, cardHv, null));
        }
        return cardParams;
    }

    /**
     * 获取玩家战斗卡组
     *
     * @param uid
     * @param taskGroup
     * @param taskId
     * @return
     */
    public List<CCardParam> getOwnCardGroup(long uid, TaskGroupEnum taskGroup, int taskId) {
        List<Integer> userTimeLimitFightCards = timeLimitFightTaskService.getUserTimeLimitFightCards(uid);
        List<CCardParam> timeLimitCardLibrary = timeLimitFightTaskService.getOrCreateCardLibrary(uid, taskGroup, taskId);
        return timeLimitCardLibrary.stream().filter(c -> userTimeLimitFightCards.contains(c.getId())).collect(Collectors.toList());
    }

    /**
     * 卡牌获取技能
     *
     * @param info
     */
    public void cardInstallSkills(RDFightsInfo info) {
        List<TimeLimitFightCardPool> fightCardPool = timeLimitFightTaskService.getOrCreateRandomCardsSkills();
        for (RDFightsInfo.RDFightCard card : info.getCards()) {
            TimeLimitFightCardPool limitFightCard = fightCardPool.stream().filter(c -> c.getCardId().equals(card.getBaseId())).findFirst().orElse(null);
            if (null == limitFightCard) {
                continue;
            }
            UserCard.UserCardStrengthenInfo strengthenInfo = new UserCard.UserCardStrengthenInfo();
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_0,limitFightCard.getSkills().get(0));
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_5,limitFightCard.getSkills().get(1));
            strengthenInfo.updateCurrentSkill(CardSkillPosEnum.SKILL_10,limitFightCard.getSkills().get(2));
            card.setStrengthenInfo(strengthenInfo);
        }
    }

    /**
     * 获得对手战斗卡牌信息
     *
     * @param taskId
     * @param taskGroup
     * @return
     */
    public RDFightsInfo getOrCreatOpponentFightsInfo(int taskId, TaskGroupEnum taskGroup) {
        String cacheKey = String.valueOf(taskGroup.getValue() + taskId);
        RDFightsInfo fromCache = GameDataTimeLimitCacheUtil.getFromCache(cacheKey, RDFightsInfo.class);
        if (null != fromCache) {
            return CloneUtil.clone(fromCache);
        }
        RDFightsInfo info = generateOpponentCards(taskId, taskGroup);
        GameDataTimeLimitCacheUtil.cache(cacheKey, info, DateUtil.SECOND_ONE_DAY * 15);
        return info;
    }

    /**
     * 获得对手战斗卡牌信息
     *
     * @param uid
     * @param taskId
     * @param taskGroup
     * @return
     */
    public RDFightsInfo getOrCreatOpponentFightsInfo(long uid, int taskId, TaskGroupEnum taskGroup) {
        RDFightsInfo fightsInfo = TimeLimitCacheUtil.getFromCache(uid, taskId + "", RDFightsInfo.class);
        if (null != fightsInfo) {
            return fightsInfo;
        }
        String cacheKey = taskGroup.getName() + taskId;
        RDFightsInfo fromCache = GameDataTimeLimitCacheUtil.getFromCache(cacheKey, RDFightsInfo.class);
        if (null != fromCache) {
            return CloneUtil.clone(fromCache);
        }
        RDFightsInfo info = generateOpponentCards(taskId, taskGroup);
        GameDataTimeLimitCacheUtil.cache(cacheKey, info, DateUtil.SECOND_ONE_DAY * 15);
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, taskId + "", info);
        return info;
    }
}
