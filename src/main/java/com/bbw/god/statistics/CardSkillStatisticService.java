package com.bbw.god.statistics;

import com.alibaba.fastjson.JSON;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.db.dao.InsRoleInfoDao;
import com.bbw.god.db.entity.InsRoleInfoEntity;
import com.bbw.god.db.entity.InsUserDataEntity;
import com.bbw.god.db.entity.StatisticInCardSkillEntity;
import com.bbw.god.db.pool.PlayerDataDAO;
import com.bbw.god.db.service.StatisticInCardSkillService;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.card.UserCard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 卡牌技能装配统计
 *
 * @author: suhq
 * @date: 2021/9/10 5:11 下午
 */
@Slf4j
@Service
public class CardSkillStatisticService {
    @Autowired
    private InsRoleInfoDao insRoleInfoDao;
    @Autowired
    private StatisticInCardSkillService statisticInCardSkillService;
    private Map<Integer, List<StatisticInCardSkillEntity>> cardSkillsCache = new HashMap<>();


    /**
     * 获取卡牌技能首个推荐
     *
     * @param cardId
     * @return
     */
    public List<Integer> getCardSkills(int cardId) {
        List<StatisticInCardSkillEntity> cardSkillEntities = getCardSkillEntities(cardId);
        if (ListUtil.isNotEmpty(cardSkillEntities)) {
            return ListUtil.parseStrToInts(cardSkillEntities.get(0).getSkills());
        }
        CfgCardEntity card = CardTool.getCardById(cardId);
        List<Integer> skills =card.getSkills();
        return skills;
    }

    /**
     * 获取卡牌前五条技能推荐
     *
     * @param cardId
     */
    public List<List<Integer>> getCardsAllSkills(int cardId){
        List<StatisticInCardSkillEntity> cardSkillEntities = getCardSkillEntities(cardId);
        List<List<Integer>> cardAllSkills = new ArrayList<>();
        if (ListUtil.isEmpty(cardSkillEntities)) {
            return cardAllSkills;
        }
        for (int i = 0; i < cardSkillEntities.size(); i++){
            List<Integer> cardSkillsList = ListUtil.parseStrToInts(cardSkillEntities.get(i).getSkills());
            cardAllSkills.add(cardSkillsList);
        }
        return cardAllSkills;
    }
    /**
     * 获取卡牌技能推荐
     *
     * @param cardId
     * @return
     */
    public List<StatisticInCardSkillEntity> getCardSkillEntities(int cardId) {
        if (cardSkillsCache.size() == 0) {
            setCardSkillCache();
        }
        return cardSkillsCache.get(cardId);
    }

    /**
     * 设置本地技能推荐缓存
     */
    public void setCardSkillCache() {
        List<StatisticInCardSkillEntity> cardsSkills = statisticInCardSkillService.getCardsSkills();
        Map<Integer, List<StatisticInCardSkillEntity>> groups = cardsSkills.stream().collect(Collectors.groupingBy(StatisticInCardSkillEntity::getCardId));
        for (Integer cardId : groups.keySet()) {
            List<StatisticInCardSkillEntity> skillEntities = groups.get(cardId);
            if (ListUtil.isEmpty(skillEntities)){
                cardSkillsCache.put(cardId, new ArrayList<>());
                continue;
            }
            skillEntities = skillEntities.stream().filter(tmp->!tmp.getSkills().equals(CardTool.getCardById(cardId).getSkillsInfo())).collect(Collectors.toList());
            int size = skillEntities.size();
            int toIndex = size >= 5 ? 5 : size;
            cardSkillsCache.put(cardId, skillEntities.subList(0, toIndex));
        }
    }

    /**
     * 统计
     *
     * @param sinceDateInt
     */
    public void doStatistic(int sinceDateInt) {
        long starTime = System.currentTimeMillis();

        int todayInt = DateUtil.getTodayInt();
        //获取要统计的角色
        List<InsRoleInfoEntity> rolesOnlyWithUidAndSid = insRoleInfoDao.getUidsWithSid(sinceDateInt, 20);
        if (ListUtil.isEmpty(rolesOnlyWithUidAndSid)) {
            return;
        }
        //获取卡牌信息
        List<InsUserDataEntity> allCardEntities = new ArrayList<>();
        Map<Integer, String> sidMapUids = rolesOnlyWithUidAndSid.stream().collect(Collectors.groupingBy(InsRoleInfoEntity::getSid, Collectors.mapping(tmp -> tmp.getUid().toString(), Collectors.joining(","))));
        for (Integer sid : sidMapUids.keySet()) {
            try {
                PlayerDataDAO pdd = SpringContextUtil.getBean(PlayerDataDAO.class, sid);
                List<InsUserDataEntity> entityList = pdd.dbSelectUserDatas(UserDataType.CARD.getRedisKey(), sidMapUids.get(sid));
                allCardEntities.addAll(entityList);
                log.error("{}待统计卡牌查询数据量{}", sid, entityList.size());
            } catch (Exception e) {
                log.error(sid + "统计出错," + e.getMessage(), e);
            }

        }
        log.error("卡牌强化统计数据库查询时间：{},待统计查询数据量{}", (System.currentTimeMillis() - starTime), allCardEntities.size());
        List<UserCard> allCards = new ArrayList<>();
        for (InsUserDataEntity entity : allCardEntities) {
            UserCard userData = JSON.parseObject(entity.getDataJson(), UserCard.class);
            allCards.add(userData);
        }
        //统计结果
        Map<Integer, Map<String, Long>> groupReuslt = allCards.stream()
                .collect(Collectors.groupingBy(UserCard::getBaseId,
                        Collectors.groupingBy(tmp -> tmp.gainSkill0() + "," + tmp.gainSkill5() + "," + tmp.gainSkill10(), Collectors.counting())));
        List<StatisticInCardSkillEntity> results = new ArrayList<>();
        for (Integer cardId : groupReuslt.keySet()) {
            Map<String, Long> skillCounts = groupReuslt.get(cardId);
            for (String skills : skillCounts.keySet()) {
                StatisticInCardSkillEntity entity = new StatisticInCardSkillEntity();
                entity.setCardId(cardId);
                entity.setSkills(skills);
                entity.setNum(skillCounts.get(skills).intValue());
                entity.setStatisticDate(todayInt);
                results.add(entity);
            }
        }
        //排序
        results.sort(Comparator.comparingInt(StatisticInCardSkillEntity::getCardId).thenComparing(StatisticInCardSkillEntity::getNum).reversed());
        //删除当日数据
        statisticInCardSkillService.delete(todayInt);
        //持久化新数据
        log.error("生成统计条数{}", results.size());
        statisticInCardSkillService.insertBatch(results);
        log.error("卡牌强化统计总时间：" + (System.currentTimeMillis() - starTime));
    }
}
