package com.bbw.god.game.sxdh;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.SetUtil;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.sxdh.config.*;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.OppCardService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.server.ServerUserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SxdhRoboterService {
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private SxdhRankService sxdhRankService;
    @Autowired
    private SxdhFighterService sxdhFighterService;
    @Autowired
    private SxdhZoneService sxdhZoneService;
    @Autowired
    private OppCardService oppCardService;

    public SxdhMatchedRoboter matchRoboter(long uid, int title) {
        List<SxdhRoboterType> roboterTypes = getSxdhRoboterTypes(uid);
        log.info("{}称号{}可匹配{}", uid, title, roboterTypes.toString());
        GameUser roboter = null;
        List<UserCard> roboterCards = null;
        for (SxdhRoboterType roboterType : roboterTypes) {
            switch (roboterType) {
                case ONE:
                    roboter = getSxdhRoboterByScore(uid, title);
                    if (roboter != null) {
                        roboterCards = oppCardService.getOppAllCards(roboter.getId());
                        cacheOneRoboter(uid, roboter.getId());
                        return new SxdhMatchedRoboter(roboterType, roboter, roboterCards);
                    }
                    break;
                case TWO:
                    roboter = getSxdhRoboterByLevel(uid, title);
                    if (roboter != null) {
                        roboterCards = oppCardService.getOppAllCards(roboter.getId());
                        return new SxdhMatchedRoboter(roboterType, roboter, roboterCards);
                    }
                    break;
                default:
                    roboter = gameUserService.getGameUser(uid);
                    roboterCards = getSxdhRoboterCardsByGu(uid);
                    return new SxdhMatchedRoboter(roboterType, roboter, roboterCards);
            }
        }
        roboter = gameUserService.getGameUser(uid);
        roboterCards = getSxdhRoboterCardsByGu(uid);
        return new SxdhMatchedRoboter(SxdhRoboterType.THREE, roboter, CloneUtil.cloneList(roboterCards));
    }

    /**
     * 获得可匹配的优先级次序
     *
     * @param uid
     * @return
     */
    private List<SxdhRoboterType> getSxdhRoboterTypes(long uid) {
        SxdhFighter sxdhFighter = sxdhFighterService.getFighter(uid);
        // 三连胜
        if (sxdhFighter.getStreak() >= 3) {
            return Arrays.asList(SxdhRoboterType.THREE);
        }
        // 三连败
        if (sxdhFighter.getStreak() <= -3) {
            return Arrays.asList(SxdhRoboterType.ONE);
        }
        return Arrays.asList(SxdhRoboterType.ONE, SxdhRoboterType.TWO, SxdhRoboterType.THREE);
    }

    /**
     * 根据等级匹配机器人
     *
     * @param uid
     * @param title
     * @return
     */
    private GameUser getSxdhRoboterByLevel(long uid, int title) {
        CfgServerEntity server = gameUserService.getOriServer(uid);
        SxdhZone sxdhZone = sxdhZoneService.getZoneByServer(server);
        int baseLevel = SxdhTool.getSxdh().getPvpUnlockLevel();
        int zoneIndex = sxdhZone.getZone() / 10;
        int minLevel = baseLevel + (zoneIndex + 1) * (title - 3);
        int maxLevel = baseLevel + (zoneIndex + 1) * (title - 1);
        if (title <= 120) {//道师及以下
            minLevel = baseLevel + zoneIndex * (title - 1);
            maxLevel = baseLevel + zoneIndex * (title + 1);
        } else {
            minLevel = baseLevel + (zoneIndex + 1) * (title - 3);
            minLevel = baseLevel + (zoneIndex + 1) * (title - 1);
        }
        log.info(uid + "getSxdhRoboterByLevel:" + minLevel + "," + maxLevel);
        List<Integer> sids = sxdhZone.getSids();
        PowerRandom.shuffle(sids);
        GameUser gu = null;
        for (Integer tmp : sids) {
            gu = serverUserService.getRandomGu(tmp, minLevel, maxLevel);
            if (gu != null && gu.getId() != uid) {
                log.info(uid + "getSxdhRoboterByLevel,匹配到" + gu.getId());
                break;
            }
        }

        return gu;
    }

    /**
     * 根据段位获得神仙大会机器人
     *
     * @param uid
     * @param segment
     * @return
     */
    private GameUser getSxdhRoboterByScore(long uid, int segment) {
        CfgSxdhStageEntity sxdhStageEntity = SxdhTool.getStageBySegment(segment);
        CfgSxdhSegmentEntity minSegment = SxdhTool.getSegment(sxdhStageEntity.getMatchMinSegment());
        CfgSxdhSegmentEntity maxSegment = SxdhTool.getSegment(sxdhStageEntity.getMatchMaxSegment());
        int minScore = minSegment.getMinScore();
        int maxScore = maxSegment.getMaxScore();
        log.info(uid + "getSxdhRoboterByScore,score:" + minScore + "," + maxScore);
        CfgServerEntity server = gameUserService.getOriServer(uid);
        SxdhZone sxdhZone = sxdhZoneService.getZoneByServer(server);
        Set<Long> rankerIds = sxdhRankService.getRankersByScore(sxdhZone, SxdhRankType.PHASE_RANK, minScore, maxScore);
        if (SetUtil.isNotEmpty(rankerIds)) {
            rankerIds.remove(uid);// 排除自己
            if (SetUtil.isNotEmpty(rankerIds)) {
                GameUser me = serverUserService.getGameUser(uid);
                List<GameUser> users = serverUserService.getGameUser(rankerIds);
                int levelDiffLimit = SxdhTool.getSxdh().getRoboterLevelDiffLimit();
                users = users.stream().filter(tmp -> tmp.getLevel() - me.getLevel() <= levelDiffLimit).collect(Collectors.toList());
                if (ListUtil.isNotEmpty(users) && users.size() > 1) {
                    GameUser roboter = PowerRandom.getRandomFromList(users);
                    log.info(uid + "getSxdhRoboterByScore,匹配到" + roboter.getId());
                    return roboter;
                }

            }
        }
        return null;
    }

    private List<UserCard> getSxdhRoboterCardsByGu(long uid) {
        log.info(uid + "getSxdhRoboterCardsByGu");
        GameUser gu = gameUserService.getGameUser(uid);
        // 卡牌途径
        List<Integer> cardWays = null;
        if (gu.getLevel() >= 80) {
            cardWays = Arrays.asList(0, 1, 2, 3, 4);
        } else {
            cardWays = Arrays.asList(0, 1, 2, 3);
        }

        List<UserCard> uCards = oppCardService.getOppAllCards(uid);

        List<UserCard> placedCards = new ArrayList<>();
        List<Integer> placedCardIds = new ArrayList<Integer>();// 已随机的卡牌,不重复
        CfgCardEntity placeCard = null;
        for (UserCard userCard : uCards) {
            CfgCardEntity card = userCard.gainCard();
            // 如果100次内随机不到一张重复的卡牌，则跳过此次星级卡牌
            int randomTimes = 100;
            do {
                placeCard = CardTool.getRandomCard(card.getStar(), cardWays);
                randomTimes--;
            } while (placedCardIds.contains(placeCard.getId()) && randomTimes > 0);
            if (randomTimes == 0) {
                continue;
            }
            userCard.setBaseId(placeCard.getId());
            // System.out.println(userCard.toString());
            placedCards.add(userCard);
        }
        return placedCards;
    }

    public void cacheOneRoboter(long uid, long roboterId) {
        TimeLimitCacheUtil.cacheBothLocalAndRedis(uid, "roboter_one", roboterId);
    }

    public Long getOneRoboter(long uid) {
        return TimeLimitCacheUtil.getFromCache(uid, "roboter_one", Long.class);
    }

    /**
     * 匹配到的机器人
     *
     * @author suhq
     * @date 2019-07-15 18:02:45
     */
    @Data
    @AllArgsConstructor
    public static class SxdhMatchedRoboter {
        private SxdhRoboterType type;
        private GameUser gu;
        private List<UserCard> cards;
    }

}
