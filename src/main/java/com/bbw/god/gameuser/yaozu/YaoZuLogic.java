package com.bbw.god.gameuser.yaozu;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.game.combat.data.param.CPCardGroup;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.achievement.AchievementServiceFactory;
import com.bbw.god.gameuser.achievement.BaseAchievementService;
import com.bbw.god.gameuser.achievement.UserAchievementInfo;
import com.bbw.god.gameuser.card.*;
import com.bbw.god.gameuser.leadercard.LeaderCardTool;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.gameuser.statistic.StatisticServiceFactory;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.yaozu.YaoZuStatistic;
import com.bbw.god.gameuser.yaozu.rd.RDArriveYaoZu;
import com.bbw.god.gameuser.yaozu.rd.RDYaoZuInfo;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.god.ServerGodDayConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 妖族来犯逻辑
 *
 * @author fzj
 * @date 2021/9/7 14:12
 */
@Service
public class YaoZuLogic {
    @Autowired
    UserYaoZuInfoService userYaoZuInfoService;
    @Autowired
    GameUserService gameUserService;
    @Autowired
    LeaderCardService leaderCardService;
    @Autowired
    ServerGodDayConfigService serverGodDayConfigService;
    @Autowired
    YaoZuGenerateProcessor yaoZuGenerateProcessor;
    @Autowired
    private AchievementServiceFactory achievementServiceFactory;
    @Autowired
    StatisticServiceFactory statisticServiceFactory;
    @Autowired
    UserCardService userCardService;
    /** 妖族成就id集合 */
    private static List<Integer> yaoZuAchievement = Arrays.asList(15740, 15690, 15700, 15710, 15720, 15730);

    /**
     * 触发妖族的获得的信息
     *
     * @param uid
     * @return
     */
    public RDArriveYaoZu arriveYaoZuInfo(long uid) {
        ArriveYaoZuCache cache = TimeLimitCacheUtil.getArriveCache(uid, ArriveYaoZuCache.class);
        int yaoZuId = cache.getYaoZuId();
        //回传到客户端
        RDArriveYaoZu rd = new RDArriveYaoZu();
        rd.setYaoZuId(yaoZuId);
        rd.setType(cache.getType());
        rd.setRunes(cache.getRunes());
        // 成就添加
        List<RDArriveYaoZu.AchievementInfos> achievementInfos = increaseAchievement(uid, yaoZuId);
        rd.setAchievementInfos(achievementInfos);
        return rd;
    }

    /**
     * 获取妖族对应成就
     *
     * @param uid
     * @param yaoZuId
     * @return
     */
    public List<RDArriveYaoZu.AchievementInfos> increaseAchievement(Long uid, int yaoZuId) {
        Integer yaoZuType = YaoZuTool.getYaoZu(yaoZuId).getYaoZuType();
        List<Integer> index = Arrays.asList(0, yaoZuType / 100);
        List<RDArriveYaoZu.AchievementInfos> achievementInfos = new ArrayList<>();
        //添加成就
        UserAchievementInfo info = gameUserService.getSingleItem(uid, UserAchievementInfo.class);
        //击败妖族成就
        for (int subscript : index) {
            BaseAchievementService attackAllYaoZuAchievementService = achievementServiceFactory.getById(yaoZuAchievement.get(subscript));
            achievementInfos.add(RDArriveYaoZu.AchievementInfos.instance(yaoZuAchievement.get(subscript), attackAllYaoZuAchievementService.getMyProgress(uid, info)));
        }
        return achievementInfos;
    }

    /**
     * 根据id获取妖族信息
     *
     * @param yaoZuId
     * @return
     */
    public RDYaoZuInfo yaoZuInfoByYaoZuId(long uid, int yaoZuId) {
        CfgYaoZuEntity yaoZuInfo = YaoZuTool.getYaoZu(yaoZuId);
        BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(BehaviorType.YAO_ZU_WIN);
        YaoZuStatistic statistic = service.fromRedis(uid, StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        int yaoZuAllNum = YaoZuTool.getYaoZuByYaoZuType(yaoZuInfo.getYaoZuType()).size();
        int yaoZuRemainingNum = yaoZuAllNum - statistic.getBeatYaoZuNums().get(YaoZuEnum.fromValue(yaoZuInfo.getYaoZuType()).getName());
        RDYaoZuInfo rd = new RDYaoZuInfo();
        rd.setRunes(yaoZuInfo.getRunes());
        rd.setType(yaoZuInfo.getType());
        List<RDFightsInfo.RDFightCard> yaoZuCards = yaoZuInfo.cardsInstance(yaoZuInfo);
        rd.setYaoZuCards(yaoZuCards);
        rd.setYaoZuAllNum(yaoZuAllNum);
        rd.setYaoZuRemainingNum(yaoZuRemainingNum);
        return rd;
    }

    /**
     * 设置攻击卡组
     *
     * @param uid
     * @param cardIds
     * @param type    0是镜像卡组 1是本体卡组
     */
    public RDCardGroups setAttackCardGroup(long uid, String cardIds, int type) {
        //获取缓存信息
        ArriveYaoZuCache cache = TimeLimitCacheUtil.getArriveCache(uid, ArriveYaoZuCache.class);
        int yaoZuId = cache.getYaoZuId();
        UserYaoZuInfo yaoZuInfo = userYaoZuInfoService.getUserYaoZuInfo(uid, yaoZuId);
        yaoZuInfo = isExistYaoZuData(uid, yaoZuInfo, yaoZuId);
        List<Integer> cards = CardParamParser.parseGroupParam(cardIds);
        if (ListUtil.isEmpty(cards)) {
            //不允许保存空数组
            throw new ExceptionForClientTip("card.grouping.not.blank");
        }
        Integer yaoZuAttribute = cache.getType();
        if (!CardChecker.isSameType(yaoZuAttribute, cards)) {
            //不可以编组相克属性的卡牌
            throw new ExceptionForClientTip("yaozu.grouping.not.the.same.type");
        }
        List<Integer> attackCardsByType = cardGroupsJudge(type, yaoZuInfo);
        if (!attackCardsByType.isEmpty()) {
            Optional<Integer> optional = cards.stream().filter(p -> LeaderCardTool.getLeaderCardId() != p && attackCardsByType.contains(p)).findFirst();
            if (optional.isPresent()) {
                //不允许保存相同卡牌
                throw new ExceptionForClientTip("yaozu.card.no.repeat");
            }
        }
        if (cache.getProgress() == YaoZuProgressEnum.BEAT_MIRRORING.getType() && type == YaoZuCardsEnum.MIRRORING_CARDS.getType()) {
            //不允许保存
            throw new ExceptionForClientTip("yaozu.cardGroup.no.edit");
        }
        gameUserService.updateItem(yaoZuInfo.setAttackCard(type, yaoZuInfo, cards));
        RDCardGroups rd = new RDCardGroups();
        rd.addCardIds(CardGroupWay.YAO_ZU_MIRRORING, yaoZuInfo.getMirroringCards(), yaoZuInfo.getMirroringFuCe());
        rd.addCardIds(CardGroupWay.YAO_ZU_ONTOLOGY, yaoZuInfo.getOntologyCards(), yaoZuInfo.getOntologyFuCe());
        return rd;
    }

    /**
     * 设置攻击符册
     *
     * @param uid
     * @param fuCeId
     * @param cardGroupWay
     * @return
     */
    public RDSuccess setAttackFuCe(long uid, Integer fuCeId, CardGroupWay cardGroupWay) {
        //获取缓存信息
        ArriveYaoZuCache cache = TimeLimitCacheUtil.getArriveCache(uid, ArriveYaoZuCache.class);
        int yaoZuId = cache.getYaoZuId();
        UserYaoZuInfo yaoZuInfo = userYaoZuInfoService.getUserYaoZuInfo(uid, yaoZuId);
        yaoZuInfo = isExistYaoZuData(uid, yaoZuInfo, yaoZuId);
        yaoZuInfo.setAttackFuCe(cardGroupWay, fuCeId);
        gameUserService.updateItem(yaoZuInfo);
        return new RDSuccess();
    }

    /**
     * 根据当前卡组获取另一套卡组
     *
     * @param type
     * @param yaoZuInfo
     * @return
     */
    public List<Integer> cardGroupsJudge(int type, UserYaoZuInfo yaoZuInfo) {
        return yaoZuInfo.gainAttackCardsByType(YaoZuCardsEnum.MIRRORING_CARDS.getType() == type ? YaoZuCardsEnum.ONTOLOGY_CARDS.getType() : YaoZuCardsEnum.MIRRORING_CARDS.getType());
    }

    /**
     * 获取攻击卡组
     *
     * @param uid
     * @return
     */
    public RDCardGroups getAttackCardGroup(long uid) {
        ArriveYaoZuCache cache = TimeLimitCacheUtil.getArriveCache(uid, ArriveYaoZuCache.class);
        UserYaoZuInfo yaoZuInfo = userYaoZuInfoService.getUserYaoZuInfo(uid, cache.getYaoZuId());
        RDCardGroups rd = new RDCardGroups();
        if (yaoZuInfo.getMirroringCards() != null || yaoZuInfo.getOntologyCards() != null) {
            //判断是否有主角卡
            boolean presentLeaderCard = leaderCardService.getUserLeaderCardOp(uid).isPresent();
            int leaderCardId = LeaderCardTool.getLeaderCardId();
            if (!yaoZuInfo.getMirroringCards().contains(leaderCardId) && !presentLeaderCard) {
                yaoZuInfo.getMirroringCards().add(0, leaderCardId);
            }
            if (!yaoZuInfo.getOntologyCards().contains(leaderCardId) && !presentLeaderCard) {
                yaoZuInfo.getOntologyCards().add(0, leaderCardId);
            }
            //检查封神卡牌是否和未封神卡牌同时存在，存在则删除未封神卡牌id
            checkCards(uid, yaoZuInfo);
            rd.addCardIds(CardGroupWay.YAO_ZU_MIRRORING, yaoZuInfo.getMirroringCards(), yaoZuInfo.getMirroringFuCe());
            rd.addCardIds(CardGroupWay.YAO_ZU_ONTOLOGY, yaoZuInfo.getOntologyCards(), yaoZuInfo.getOntologyFuCe());
        }
        return rd;
    }

    /**
     * 同步卡组
     *
     * @return
     */
    public RDCardGroups synchronizeAttackCards(long uid) {
        ArriveYaoZuCache cache = TimeLimitCacheUtil.getArriveCache(uid, ArriveYaoZuCache.class);
        if (cache.getProgress() == YaoZuProgressEnum.BEAT_MIRRORING.getType()) {
            //不允许同步
            throw new ExceptionForClientTip("yaozu.cardGroup.no.syn");
        }
        //同步卡组筛选
        UserYaoZuInfo yaoZuToSync = getYaoZuToSync(uid, cache);

        int yaoZuId = cache.getYaoZuId();
        UserYaoZuInfo yaoZuInfo = userYaoZuInfoService.getUserYaoZuInfo(uid, yaoZuId);
        yaoZuInfo = isExistYaoZuData(uid, yaoZuInfo, yaoZuId);
        yaoZuInfo.setMirroringCards(yaoZuToSync.getMirroringCards());
        yaoZuInfo.setMirroringFuCe(yaoZuToSync.getMirroringFuCe());
        yaoZuInfo.setOntologyCards(yaoZuToSync.getOntologyCards());
        yaoZuInfo.setOntologyFuCe(yaoZuToSync.getOntologyFuCe());
        checkCards(uid, yaoZuInfo);
        //更新数据
        gameUserService.updateItem(yaoZuInfo);

        RDCardGroups rd = new RDCardGroups();
        rd.addCardIds(CardGroupWay.YAO_ZU_MIRRORING, yaoZuInfo.getMirroringCards(), yaoZuInfo.getMirroringFuCe());
        rd.addCardIds(CardGroupWay.YAO_ZU_ONTOLOGY, yaoZuInfo.getOntologyCards(), yaoZuInfo.getOntologyFuCe());
        return rd;
    }

    /**
     * 检查封神卡牌是否和未封神卡牌同时存在，存在则删除未封神卡牌
     *
     * @param uid
     * @param yaoZuInfo
     */
    private void checkCards(long uid, UserYaoZuInfo yaoZuInfo) {
        List<Integer> userCards = userCardService.getUserCards(uid).stream().map(UserCfgObj::getBaseId).collect(Collectors.toList());
        //移除镜像卡组已封神的未封神卡牌id
        boolean isRemoveMirroringCards = yaoZuInfo.getMirroringCards().removeIf(c -> userCards.contains(c + 10000));
        //移除本体卡组已封神的未封神卡牌id
        boolean isRemoveOntologyCards = yaoZuInfo.getOntologyCards().removeIf(c -> userCards.contains(c + 10000));
        //保存数据
        if (isRemoveMirroringCards || isRemoveOntologyCards) {
            gameUserService.updateItem(yaoZuInfo);
        }
    }

    /**
     * 同步卡组筛选
     *
     * @param uid
     * @param cache
     * @return
     */
    public UserYaoZuInfo getYaoZuToSync(long uid, ArriveYaoZuCache cache) {
        int yaoZuId = cache.getYaoZuId();
        //获取同属性的妖族,按类型难度由高到低排序
        List<UserYaoZuInfo> yaoZus = getYaoZuInfos(uid, yaoZuId);
        for (UserYaoZuInfo yaoZu : yaoZus) {
            if (yaoZu.getMirroringCards().isEmpty() || yaoZu.getOntologyCards().isEmpty()) {
                continue;
            }
            //如果是胜利的
            if (yaoZu.getProgress() == YaoZuProgressEnum.BEAT_ONTOLOGY.getType()) {
                return yaoZu;
            }
            //判断是否只有主角卡
            boolean isNotHasMirroringCards = yaoZu.getMirroringCards().size() == 1 && yaoZu.getMirroringCards().contains(LeaderCardTool.getLeaderCardId());
            boolean isNotHasOntologyCards = yaoZu.getOntologyCards().size() == 1 && yaoZu.getOntologyCards().contains(LeaderCardTool.getLeaderCardId());
            if (isNotHasMirroringCards || isNotHasOntologyCards) {
                //没有可以选择的
                throw new ExceptionForClientTip("yaozu.cardGroup.not.syn");
            }
            return yaoZu;
        }
        //没有可以选择的
        throw new ExceptionForClientTip("yaozu.cardGroup.not.syn");
    }

    /**
     * 根据妖族属性按难度排序
     * 同属性，按类型难度由高到低排序
     *
     * @param
     * @return
     */
    public List<UserYaoZuInfo> getYaoZuInfos(long uid, int yaoZuId) {
        int type = YaoZuTool.getYaoZu(yaoZuId).getType();
        List<UserYaoZuInfo> yaoZuInfos = userYaoZuInfoService.getUserYaoZu(uid)
                .stream().filter(userYaoZuInfo -> YaoZuTool.getYaoZu(userYaoZuInfo.getBaseId()).getType() == type)
                .sorted(Comparator.comparing(UserYaoZuInfo::getYaoZuType)).collect(Collectors.toList());
        return yaoZuInfos;
    }

    /**
     * 获取妖族战斗卡组
     *
     * @param cache
     * @return
     */
    public CPCardGroup getUserYaoZuCards(Long uid, ArriveYaoZuCache cache) {
        int yaoZuId = cache.getYaoZuId();
        UserYaoZuInfo yaoZuInfo = userYaoZuInfoService.getUserYaoZuInfo(uid, yaoZuId);
        yaoZuInfo = isExistYaoZuData(uid, yaoZuInfo, yaoZuId);
        //判断是否已经编组
        boolean isNotHasMirroringCards = yaoZuInfo.getMirroringCards().size() == 1 && yaoZuInfo.getMirroringCards().contains(LeaderCardTool.getLeaderCardId());
        boolean isNotHasOntologyCards = yaoZuInfo.getOntologyCards().size() == 1 && yaoZuInfo.getOntologyCards().contains(LeaderCardTool.getLeaderCardId());
        if (ListUtil.isEmpty(yaoZuInfo.getMirroringCards()) || isNotHasMirroringCards) {
            throw new ExceptionForClientTip("yaozu.mirroring.cardGroup.empty");
        }
        if (ListUtil.isEmpty(yaoZuInfo.getOntologyCards()) || isNotHasOntologyCards) {
            throw new ExceptionForClientTip("yaozu.ontology.cardGroup.empty");
        }
        //判断返回哪组战斗卡组
        List<UserCard> userCards = null;
        int fuCeId = 0;
        if (cache.getProgress() == YaoZuProgressEnum.NOT_ATTACKED.getType()) {
            userCards = userCardService.getUserCards(uid, yaoZuInfo.getMirroringCards());
            fuCeId = yaoZuInfo.getMirroringFuCe();
        } else {
            userCards = userCardService.getUserCards(uid, yaoZuInfo.getOntologyCards());
            fuCeId = yaoZuInfo.getOntologyFuCe();
        }
        return CPCardGroup.getInstanceByUserCards(uid, fuCeId, userCards);
    }

    /**
     * 判断是否存在妖族数据，不在则生成
     *
     * @param uid
     * @param yaoZuInfo
     * @param yaoZuId
     */
    public UserYaoZuInfo isExistYaoZuData(long uid, UserYaoZuInfo yaoZuInfo, int yaoZuId) {
        if (null == yaoZuInfo && !yaoZuGenerateProcessor.isPassYaoZu(uid)) {
            yaoZuInfo = UserYaoZuInfo.getInstance(uid, yaoZuId, gameUserService.getGameUser(uid).getLocation().getPosition());
            gameUserService.addItem(uid, yaoZuInfo);
            return yaoZuInfo;
        }
        return yaoZuInfo;
    }
}
