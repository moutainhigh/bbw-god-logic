package com.bbw.god.fight.processor;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.StrUtil;
import com.bbw.god.activity.monthlogin.MonthLoginEnum;
import com.bbw.god.activity.monthlogin.MonthLoginLogic;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightSubmitParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCard;
import com.bbw.god.game.config.city.ChengC;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.res.ResWayType;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.server.fst.server.FstServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 练兵结算
 *
 * @author mymme
 */
@Slf4j
@Service
public class TrainingFightProcessor extends AbstractCityFightProcessor {
    @Autowired
    private FstServerService fstService;
    @Autowired
    private AttackFightProcessor attackFightProcessor;
    @Autowired
    private MonthLoginLogic monthLoginLogic;

    @Override
    public FightTypeEnum getFightType() {
        return FightTypeEnum.TRAINING;
    }

    @Override
    public WayEnum getWay() {
        return WayEnum.FIGHT_TRAINING;
    }

    @Override
    public CombatPVEParam getOpponentInfo(Long uid, Long oppId, boolean fightAgain) {
        //城池缓存
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        GameUser gu = gameUserService.getGameUser(uid);
        checkAbleFight(gu, cache);
        //置为未结算状态
        TimeLimitCacheUtil.removeCache(uid, RDFightResult.class);
        return cache.getFightParam();
    }

    @Override
    public CPlayerInitParam getNormalOpponentParam(GameUser gu, ChengChiInfoCache cache, CombatPVEParam pveParam) {
        return null;
    }

    @Override
    public void handleAward(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(gu.getId());
        UserCity userCity = userCityService.getUserCity(gu.getId(), cache.getCityId());
        //结算经验
        int gainExp = getExp(gu, param.getOppLostBlood(), param);
        if (gu.getLevel() >= 30) {
            gainExp = gainExp * 11 / 20;
        } else {
            gainExp = gainExp * 14 / 20;
        }
        int baseCopper = gainExp;
        baseCopper = (int) (baseCopper * (1 + getBaseCopperBuf(gu)));
        gainExp *= (1 + getBaseExpBuf(gu, param.getAdditionExp()));
        // 木属性升级经验加成30%
        if (cache.getCityProperty() == TypeEnum.Wood.getValue()) {
            gainExp = gainExp * 13 / 10;
        }
        // 道场经验加成
        gainExp += gainExp * userCity.getDc() / 10;
        // 处理战斗经验
        if (monthLoginLogic.isExistEvent(gu.getId(), MonthLoginEnum.GOOD_LB)) {
            gainExp *= 1.3;
        }
        gainJinYanDan(gu.getId(), param, gainExp, rd);
        ResEventPublisher.pubExpAddEvent(gu.getId(), gainExp, getWay(), rd);
        // 铜钱加成
        int godNum = godService.getCopperAddRate(gu);
        double copperAddRate = getCopperAddRate(param.getZcTimes(), godNum);
        int extraCopper = (int) (baseCopper * copperAddRate);
        // 处理战斗铜钱
        EPCopperAdd copperAdd = new EPCopperAdd(new BaseEventParam(gu.getId(), getWay(), rd), baseCopper, baseCopper);
        copperAdd.addCopper(ResWayType.Extra, extraCopper);
//        log.info("{}练兵获得铜钱{},基础铜钱{},额外铜钱{}", copperAdd.getGuId(), copperAdd.gainAddCopper(), baseCopper, extraCopper);
        ResEventPublisher.pubCopperAddEvent(copperAdd);
        // 落宝金钱发放道具
        userTreasureEffectService.effectAsLBJQ(gu, rd);
        // 发放元素
        int random = PowerRandom.getRandomBySeed(100);
        int addedEleNum = 0;
        if (random > 95) {
            addedEleNum = 3;
        } else if (random > 60) {
            addedEleNum = 1;
        }
        ResEventPublisher.pubEleAddEvent(gu.getId(), addedEleNum, getWay(), rd);
        String opponentName = param.getOpponentName();
        if (StrUtil.isBlank(opponentName)) {
            rd.setWinDes("您在练兵中取得了胜利！");
        } else {
            rd.setWinDes("在练兵中战胜 " + opponentName + " ！");
        }
        cache.setTraining(true);
        TimeLimitCacheUtil.setChengChiInfoCache(gu.getId(), cache);
        rd.setOwnCity(1);
        TimeLimitCacheUtil.removeCache(gu.getId(), RDFightResult.class);
    }


    @Override
    public void failure(GameUser gu, RDFightResult rd, FightSubmitParam param) {
        // 未攻下
        rd.setOwnCity(1);
        TimeLimitCacheUtil.removeCache(gu.getId(), RDFightResult.class);
        this.userTreasureEffectService.effectAsLBJQ(gu, rd);
    }

    /**
     * 获取练兵信息
     *
     * @param gu
     * @param cache
     * @param pveParam
     * @return
     */
    public CPlayerInitParam buildFightInfo(GameUser gu, ChengChiInfoCache cache, CombatPVEParam pveParam) {
        int cityLv = cache.getCityLv();
        if (gu.getLevel() >= 10 && isTrainingVSFst(cityLv)) {
            //封神台对手
            // 如果匹配的是系统，则根据系统生成规则生成对手信息
            int myRank = fstService.getFstRankWithIntoRanking(gu.getId());
            if (myRank > 1) {
                int lowestRank = this.fstService.getLowestRank(gu.getServerId());
                int minRank = Math.max(myRank - (2 + cityLv), 1);
                int maxRank = Math.min(myRank + (8 - cityLv), lowestRank);
                int intervalNum = maxRank - minRank;
                int random = PowerRandom.getRandomBySeed(10);
                if (intervalNum == 1 || (intervalNum > 1 && random > intervalNum)) {
                    // 获取符合区间的玩家Id
                    List<Long> opponentIds = this.fstService.getRangeRank(gu.getServerId(), minRank, maxRank);
                    //获得 是真实玩家ID 且 不含自己的结果
                    List<Long> list = opponentIds.stream().filter(p -> p > 0 && !p.equals(gu.getId())).collect(Collectors.toList());
                    if (ListUtil.isNotEmpty(list)) {
                        CPlayerInitParam param = fstService.getMyDefenseInfo(PowerRandom.getRandomFromList(list));
                        if (ListUtil.isNotEmpty(param.getCards())) {
                            pveParam.setAiPlayer(param);
                            return param;
                        }
                    }
                    return attackFightProcessor.getNormalOpponentParam(gu, cache.getCityId(), cityLv, pveParam);
                }
            }
        }
        UserCity userCity = userCityService.getUserCity(gu.getId(), cache.getCityId());
        //练兵卡组
        int levelToAdd = this.userCityService.getCityNumAsHierarchy(gu.getId(), cityLv, userCity.getHierarchy());
        if (cityLv == 5) {
            levelToAdd *= 3;
        }
        if (cityLv <= 3) {
            int lv = gu.getLevel();
            for (int i = 30; i > 0; i -= 10) {
                if (lv < i) {
                    levelToAdd -= 1;
                } else {
                    break;
                }
            }
        }
        levelToAdd = Math.max(levelToAdd, 0);
        int cardHv = userCity.getHierarchy() * 2;
        ChengC chengc = CityTool.getChengc(cache.getCityId());
        int zhsLv = chengc.getSoliderLevel() + levelToAdd + userCity.getHierarchy() * 10;
        List<CCardParam> cardParams = null;
        if (gu.getStatus().ifNotInFsdlWorld()) {
            cardParams = getCardsBySoliderString(chengc.getSoliders(), levelToAdd, cardHv, null);
        } else {
            cardParams = getCardsBySoliderString(chengc.getSoliders(), levelToAdd, cardHv, CfgCard.AI_CARDS_NOT_TO_FSDL_1);
        }
        CCardParam cardParam = cardParams.get(0);
        //第一张卡作为AI召唤师的名字
        CPlayerInitParam param = new CPlayerInitParam();
        param.setHeadImg(cardParam.getId());
        param.setNickname(CardTool.getCardById(cardParam.getId()).getName());
        param.setLv(zhsLv);
        param.setCards(cardParams);
        pveParam.setAiPlayer(param);
        return param;
    }

    /**
     * 练兵是否和机器人对打
     *
     * @param cityLv 城池的等级
     * @return
     */
    private boolean isTrainingVSFst(int cityLv) {
        int random = PowerRandom.getRandomBySeed(100);
        switch (cityLv) {
            case 1:
                return random > 80;
            case 2:
                return random > 70;
            case 3:
                return random > 50;
            case 4:
                return random > 30;
            case 5:
                return random > 20;
            default:
                return false;
        }

    }

}
