package com.bbw.god.gameuser.task.timelimit;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.cache.GameDataTimeLimitCacheUtil;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.fight.FighterInfo;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.statistics.CardSkillStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 限时村庄疑云战斗任务服务类
 *
 * @author fzj
 * @date 2022/1/5 14:18
 */
@Service
public class TimeLimitFightTaskService {

    public static final String CACHEKEY = "TimeLimitFightCardsPool";

    @Autowired
    CardSkillStatisticService cardSkillStatisticService;
    @Autowired
    CunZYiYunFightService cunZYiYunFightService;

    /**
     * 卡牌随机刷新技能
     */
    private List<TimeLimitFightCardPool> randomAllCardsSkills() {
        List<CfgCardEntity> allCards = CardTool.getAllCards();
        List<TimeLimitFightCardPool> cardPools = new ArrayList<>();
        for (CfgCardEntity card : allCards) {
            TimeLimitFightCardPool fightCard = new TimeLimitFightCardPool();
            fightCard.setCardId(card.getId());
            //获取推荐技能
            List<List<Integer>> cardAllSkills = cardSkillStatisticService.getCardsAllSkills(card.getId());
            if (cardAllSkills.isEmpty()) {
                fightCard.getSkills().add(card.getZeroSkill());
                fightCard.getSkills().add(card.getFiveSkill());
                fightCard.getSkills().add(card.getTenSkill());
                cardPools.add(fightCard);
                continue;
            }
            //随机一组
            List<Integer> randomSkills = PowerRandom.getRandomFromList(cardAllSkills);
            fightCard.getSkills().addAll(randomSkills);
            cardPools.add(fightCard);
        }
        return cardPools;
    }

    /**
     * 获取随机卡库
     *
     * @param taskGroup
     * @param taskId
     */
    public List<CCardParam> getRandomCardLibrary(TaskGroupEnum taskGroup, int taskId) {
        FighterInfo attackerInfo = TimeLimitTaskTool.getAttackerCfgInfo(taskGroup, taskId);
        List<CCardParam> cardParams = cunZYiYunFightService.getFightBaseData(attackerInfo);
        //根据配置获取卡牌参数
        List<TimeLimitFightCardPool> fightCardPool = getOrCreateRandomCardsSkills();
        for (CCardParam cCardParam : cardParams){
            int cardId = cCardParam.getId();
            TimeLimitFightCardPool fightCard = fightCardPool.stream().filter(f -> f.getCardId().equals(cardId)).findFirst().orElse(null);
            if (null == fightCard) {
                continue;
            }
            cCardParam.setSkills(fightCard.getSkills());
        }
        return cardParams;
    }

    /**
     * 获取卡牌技能
     *
     * @return
     */
    public List<TimeLimitFightCardPool> getOrCreateRandomCardsSkills() {
        List<TimeLimitFightCardPool> fightCardPool = GameDataTimeLimitCacheUtil.getFromCache(CACHEKEY, List.class);
        if (null == fightCardPool) {
            fightCardPool = randomAllCardsSkills();
        }
        //存入缓存
        GameDataTimeLimitCacheUtil.cache(CACHEKEY, fightCardPool, DateUtil.SECOND_ONE_DAY * 15);
        return fightCardPool;
    }

    /**
     * 获取卡牌库
     *
     * @param uid
     * @param taskGroup
     * @param taskId
     * @return
     */
    public List<CCardParam> getOrCreateCardLibrary(long uid, TaskGroupEnum taskGroup, int taskId) {
        List<CCardParam> timeLimitCardLibrary = TimeLimitCacheUtil.getFromCache(uid, "timeLimitCardLibrary", List.class);
        if (null == timeLimitCardLibrary) {
            timeLimitCardLibrary = getRandomCardLibrary(taskGroup, taskId);
        }
        //存缓存
        saveCardLibrary(uid, timeLimitCardLibrary);
        return timeLimitCardLibrary;
    }

    /**
     * 保存卡牌库
     *
     * @param uid
     * @param cardLibrary
     */
    public void saveCardLibrary(long uid, List<CCardParam> cardLibrary) {
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, "timeLimitCardLibrary", cardLibrary, DateUtil.SECOND_ONE_DAY * 15);
    }

    /**
     * 获取玩家限时战斗卡组
     *
     * @param uid
     * @return
     */
    public List<Integer> getUserTimeLimitFightCards(long uid) {
        List<Integer> timeLimitCardGroup = TimeLimitCacheUtil.getFromCache(uid, "timeLimitCardGroup", List.class);
        if (null == timeLimitCardGroup) {
            return new ArrayList<>();
        }
        return timeLimitCardGroup;
    }

    /**
     * 保存玩家临时卡组
     *
     * @param uid
     * @param cardsList
     */
    public void saveUserTimeLimitFightCards(long uid, List<Integer> cardsList) {
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, "timeLimitCardGroup", cardsList, DateUtil.SECOND_ONE_DAY * 15);
    }
}
