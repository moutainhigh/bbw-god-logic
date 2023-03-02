package com.bbw.god.fight.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.combat.runes.service.impl.Runes131006;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.statistic.StatisticServiceFactory;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.behavior.BehaviorStatisticService;
import com.bbw.god.gameuser.statistic.behavior.BehaviorType;
import com.bbw.god.gameuser.statistic.behavior.yaozu.YaoZuStatistic;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.gameuser.yaozu.*;
import com.bbw.god.gameuser.yaozu.event.YaoZuEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 妖族来犯战斗处理类
 *
 * @author fzj
 * @date 2021/9/6 15:57
 */
@Service
public class YaoZuFightProcessor extends AbstractFightProcessor {
    @Autowired
    UserYaoZuInfoService userYaoZuInfoService;
    @Autowired
    StatisticServiceFactory statisticServiceFactory;
    @Autowired
    YaoZuGenerateProcessor yaoZuGenerateProcessor;
    @Autowired
    Runes131006 runes131006;
    @Autowired
    YaoZuLogic yaoZuLogic;
    @Autowired
    private UserTreasureService userTreasureService;
    /** 传奇卷轴和至尊秘传宝箱 */
    private static List<Integer> treasures = Arrays.asList(11060, 11065);

    @Override
    public FightTypeEnum getFightType() {
        return FightTypeEnum.YAOZU_FIGHT;
    }

    @Override
    public WayEnum getWay() {
        return WayEnum.YAOZU_FIGHT;
    }

    @Override
    public CombatPVEParam getOpponentInfo(Long uid, Long oppId, boolean fightAgain) {
        ArriveYaoZuCache cache = TimeLimitCacheUtil.getArriveCache(uid, ArriveYaoZuCache.class);
        int yaoZuId = cache.getYaoZuId();
        //置为未结算状态
        TimeLimitCacheUtil.removeCache(uid, RDFightResult.class);
        CfgYaoZuEntity yaoZu = YaoZuTool.getYaoZu(yaoZuId);
        List<CfgYaoZuEntity.CardParam> cards = yaoZu.getYaoZuCards();
        List<CCardParam> cardParams = new ArrayList<>();
        for (CfgYaoZuEntity.CardParam card : cards) {
            cardParams.add(CCardParam.initYzCards(card, yaoZu.getYaoZuId()));
        }
        CombatPVEParam param = new CombatPVEParam();
        CPlayerInitParam cPlayerInitParam = new CPlayerInitParam();
        cPlayerInitParam.setHeadImg(YaoZuTool.getYaoZu(yaoZuId).getYaoZuType());
        cPlayerInitParam.setLv(yaoZu.getAiLv());
        cPlayerInitParam.setNickname(cache.getProgress() == YaoZuProgressEnum.NOT_ATTACKED.getType() ? yaoZu.getName() + "镜像" : yaoZu.getName());
        cPlayerInitParam.setCards(cardParams);
        //攻打本体时所有卡牌属性降低百分之30
        if (cache.getProgress() == YaoZuProgressEnum.BEAT_MIRRORING.getType()) {
            runes131006.doInitRunes(param);
        }
        cPlayerInitParam.addBuffs(yaoZu.getRunes());
        param.setAiPlayer(cPlayerInitParam);
        //对手信息缓存
        cache.setFightParam(param);
        TimeLimitCacheUtil.setArriveCache(uid, cache);
        return param;
    }

    @Override
    public void failure(GameUser gu, RDFightResult rd, FightSubmitParam param) {
    }

    @Override
    public void handleAward(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        ArriveYaoZuCache cache = TimeLimitCacheUtil.getArriveCache(gu.getId(), ArriveYaoZuCache.class);
        UserYaoZuInfo yaoZuInfo = userYaoZuInfoService.getUserYaoZuInfo(gu.getId(), cache.getYaoZuId());
        yaoZuInfo = yaoZuLogic.isExistYaoZuData(gu.getId(), yaoZuInfo, cache.getYaoZuId());
        //结算经验
        int gainExp = getExp(gu, param.getOppLostBlood(), param) * 8 / 10;
        gainJinYanDan(gu.getId(), param, gainExp, rd);
        ResEventPublisher.pubExpAddEvent(gu.getId(), gainExp, getWay(), rd);
        if (cache.getProgress() == YaoZuProgressEnum.NOT_ATTACKED.getType()) {
            //攻打镜像结算
            rd.setWin(1);
            //更新进度
            cache.setProgress(1);
        } else {
            //攻打本体结算
            rd.setWin(1);
            yaoZuInfo.setProgress(2);
            cache.setProgress(2);
            //本体奖励结算
            ontologySettlement(gu, rd);
            if (isFinalYaoZu(gu)) {
                List<UserYaoZuInfo> userAllYaoZu = userYaoZuInfoService.getUserYaoZu(gu.getId());
                gameUserService.deleteItems(gu.getId(), userAllYaoZu);
            } else {
                gameUserService.updateItem(yaoZuInfo);
            }
            //发布击败妖族事件
            YaoZuEventPublisher.pubYaoZuBeatEvent(gu.getId(), cache.getYaoZuId());
        }
        TimeLimitCacheUtil.setArriveCache(gu.getId(), cache);
    }

    /**
     * 攻打本体奖励结算
     */
    public void ontologySettlement(GameUser gu, RDFightResult rd) {
        List<Integer> userNotDrawingTreasures = new ArrayList<>();
        //根据法坛图纸的type获取全部法坛建筑图纸id
        List<CfgTreasureEntity> drawingIdList = new ArrayList<>(TreasureTool.getTreasuresByType(52));
        //获取玩家拥有的法宝id
        List<Integer> userTreasuresIdList = userTreasureService.getAllUserTreasures(gu.getId())
                .stream().map(UserTreasure::getBaseId).collect(Collectors.toList());
        for (int treasureId : drawingIdList.stream().map(CfgTreasureEntity::getId).collect(Collectors.toList())) {
            if (!userTreasuresIdList.contains(treasureId)) {
                userNotDrawingTreasures.add(treasureId);
            }
        }
        userNotDrawingTreasures.retainAll(getNotFaTanPic(gu.getId(), drawingIdList));
        //如果玩家击败最后一个妖族则发放至尊秘传宝箱
        if (isFinalYaoZu(gu)) {
            TreasureEventPublisher.pubTAddEvent(gu.getId(), treasures.get(1), 1, WayEnum.YAOZU_FIGHT, rd);
        }else {
            //如果玩家获取了85个图纸，发放传奇卷轴宝箱
            if (userNotDrawingTreasures.isEmpty() && !yaoZuGenerateProcessor.isPassYaoZu(gu.getId())) {
                TreasureEventPublisher.pubTAddEvent(gu.getId(), treasures.get(0), 1, WayEnum.YAOZU_FIGHT, rd);
            }
            //如果玩家未获取完所有图纸，则发放图纸
            if (!userNotDrawingTreasures.isEmpty()) {
                //随机一张图纸id
                Integer randomTreasure = PowerRandom.getRandomFromList(userNotDrawingTreasures);
                TreasureEventPublisher.pubTAddEvent(gu.getId(), randomTreasure, 1, WayEnum.YAOZU_FIGHT, rd);
            }
        }
    }

    /**
     * 获取未开启法坛的城池法坛图纸
     *
     * @param uid
     * @param drawingIdList
     * @return
     */
    private List<Integer> getNotFaTanPic(long uid, List<CfgTreasureEntity> drawingIdList) {
        //获取未开启法坛的城池
        List<Integer> citiesId = userCityService.getUserCities(uid).stream().filter(c -> c.getFt() == null).map(UserCfgObj::getBaseId).collect(Collectors.toList());
        List<String> citiesName = CityTool.getCities().stream().filter(c -> citiesId.contains(c.getId())).map(CfgCityEntity::getName).collect(Collectors.toList());
        return drawingIdList.stream().filter(f -> citiesName.contains(f.getName().substring(0, f.getName().length() - 4))).map(CfgTreasureEntity::getId).collect(Collectors.toList());

    }

    /**
     * 判断是否击败最后一只妖族
     *
     * @param gu
     * @return
     */
    public boolean isFinalYaoZu(GameUser gu) {
        //获取玩家已经战胜的妖族数量
        BehaviorStatisticService service = statisticServiceFactory.getByBehaviorType(BehaviorType.YAO_ZU_WIN);
        YaoZuStatistic statistic = service.fromRedis(gu.getId(), StatisticTypeEnum.NONE, DateUtil.getTodayInt());
        if (statistic.getTotal() == YaoZuTool.getAllYaoZu().size() - 1) {
            return true;
        }
        return false;
    }

}
