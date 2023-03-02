package com.bbw.god.game.zxz.service.foursaints;

import com.bbw.cache.GameCacheService;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.card.equipment.randomrule.CardXianJueRandomRule;
import com.bbw.god.game.config.card.equipment.randomrule.CardZhiBaoRandomRule;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsEntity;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.entity.ZxzEntry;
import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaints;
import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaintsDefender;
import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaintsInfo;
import com.bbw.god.game.zxz.enums.ZxzFourSaintsEnum;
import com.bbw.god.game.zxz.service.ZxzRandomService;
import com.bbw.god.gameuser.yuxg.Enum.FuTuTypeEnum;
import com.bbw.god.gameuser.yuxg.YuXGTool;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 四圣挑战实例化野怪
 * @author: hzf
 * @create: 2022-12-26 12:10
 **/
@Service
public class InitZxzFourSaintsService {
    @Autowired
    private ZxzRandomService zxzRandomService;
    @Autowired
    private GameCacheService gameCacheService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private GameZxzFourSaintsService gameZxzFourSaintsService;

    /**
     * 实例化四圣挑战
     * @return
     */
    public ZxzFourSaintsInfo initZxzFourSaints(){

        List<ZxzFourSaints> zxzFourSaintss = new ArrayList<>();
        for (ZxzFourSaintsEnum saintsEnum : ZxzFourSaintsEnum.values()) {
            //获取四圣规则
            CfgFourSaintsEntity.CfgFourSaintsChallenge fourSaintsChallenge = CfgFourSaintsTool.getFourSaintsChallenge(saintsEnum.getChallengeType());
            //限制的卡种类
            List<Integer> randomAttributeLimit = fourSaintsChallenge.getRandomAttributeLimit();
            //随机获取三个，卡牌属性只能是下面几个
            List<Integer> randomCardTypes = PowerRandom.getRandomsFromList(randomAttributeLimit, 3);
            //初始化关卡
            List<ZxzFourSaintsDefender> zxzFourSaintsDefenders = initializeZxzFourSaintsDefenders(saintsEnum.getChallengeType(),randomCardTypes);
            //初始化词条
            List<String> entryToString = initZxzFourSaintsEntry(saintsEnum.getChallengeType());
            //限制编组属性
            List<Integer> attributeLimits = PowerRandom.getRandomsFromList(fourSaintsChallenge.getAttributeLimit(), 2);

            ZxzFourSaints fourSaints = ZxzFourSaints.instance(saintsEnum.getChallengeType(),attributeLimits, zxzFourSaintsDefenders, entryToString);
            zxzFourSaintss.add(fourSaints);
        }

        List<ZxzFourSaintsInfo> zxzFourSaintsInfos = new ArrayList<>();
        ZxzFourSaintsInfo zxzFourSaintsInfo = ZxzFourSaintsInfo.instance(zxzFourSaintss);
        zxzFourSaintsInfos.add(zxzFourSaintsInfo);
        gameCacheService.addGameDatas(zxzFourSaintsInfos);
        return zxzFourSaintsInfo;
    }

    /**
     * 初始化诛仙阵四圣词条
     * @param challengeType
     * @return
     */
    public List<String> initZxzFourSaintsEntry(Integer challengeType){
        // 词条池
        List<Integer> entryPool = CfgFourSaintsTool.getEntryPool();
        //四圣规则
        CfgFourSaintsEntity.CfgFourSaintsChallenge fourSaintsChallenge = CfgFourSaintsTool.getFourSaintsChallenge(challengeType);
        //词条随机规则
        CfgFourSaintsEntity.CfgEntryRandom entryRandom = CfgFourSaintsTool.getEntryRandom(challengeType);
        List<ZxzEntry> zxzEntryList = new ArrayList<>();
        //获取随机词条
        List<ZxzEntry> zxzEntries = zxzRandomService.randomEntry(entryPool, entryRandom.getLvUpperLimit(), entryRandom.getLvStock());
        //添加玩家的灵装词条
        ZxzEntry entry = new ZxzEntry();
        entry.setEntryId(RunesEnum.LING_ZHUANG_ENTRY.getRunesId());
        entry.setEntryLv(fourSaintsChallenge.getLingCEntryLv());
        zxzEntryList.add(entry);
        zxzEntryList.addAll(zxzEntries);
        List<String> entryToString = ZxzEntry.gainEntryToString(zxzEntryList);
        return entryToString;
    }

    /**
     * 实例每个挑战类型的数据
     * @param challengeType
     * @param randomCardTypes 限制卡牌种类
     * @return
     */
    public List<ZxzFourSaintsDefender> initializeZxzFourSaintsDefenders(Integer challengeType,List<Integer> randomCardTypes){
        List<ZxzFourSaintsDefender> zxzFourSaintsDefenders = new ArrayList<>();
        List<CfgFourSaintsEntity.CfgFourSaintsDefenderCardRule> defenderCardRules = CfgFourSaintsTool.getDefenderCardRules(challengeType);

        //实例化关卡信息
        for (CfgFourSaintsEntity.CfgFourSaintsDefenderCardRule defenderCardRule : defenderCardRules) {
            ZxzFourSaintsDefender zxzFourSaintsDefender = initDefender(defenderCardRule,randomCardTypes);
            zxzFourSaintsDefenders.add(zxzFourSaintsDefender);
        }
        return zxzFourSaintsDefenders;
    }

    /**
     * 实例化每关数据
     * @param randomCardTypes 限制随机的卡牌种类
     * @param defenderCardRule
     * @return
     */
    public ZxzFourSaintsDefender initDefender(CfgFourSaintsEntity.CfgFourSaintsDefenderCardRule defenderCardRule, List<Integer> randomCardTypes){
        ZxzFourSaintsDefender zxzFourSaintsDefender = new ZxzFourSaintsDefender();
        zxzFourSaintsDefender.setSummonerLv(defenderCardRule.getSummonerLv());
        zxzFourSaintsDefender.setDefenderId(defenderCardRule.getDefenderId());
        zxzFourSaintsDefender.setKind(defenderCardRule.getKind());

        //获取随机卡牌id
        List<Integer> cardIds = zxzRandomService.getCardIds(defenderCardRule.getCards(), randomCardTypes);
        //随机卡组
        List<String> defenderCards = zxzRandomService.getRandomCardGroup(cardIds, defenderCardRule.getSkillRandom(), defenderCardRule.getCardLv(), defenderCardRule.getCardHv());
        zxzFourSaintsDefender.setDefenderCards(defenderCards);
        //随机符图
        List<Integer> fuTuIds = getFuTuIds(defenderCardRule.getFuTus(), randomCardTypes);
        zxzFourSaintsDefender.setRunes(fuTuIds);
        //仙决
        zxzFourSaintsDefender.setCardXianJues(CardXianJueRandomRule.instances(cardIds));
        //至宝
        zxzFourSaintsDefender.setCardZhiBaos(CardZhiBaoRandomRule.instances(cardIds));
        return zxzFourSaintsDefender;
    }

    /**
     * 符图的随机规则
     *
     * @param fuTus
     * @return
     */
    private List<Integer> getFuTuIds(List<CfgFourSaintsEntity.CfgFourSaintsFuTus> fuTus, List<Integer> randomsFromList) {
        //诛仙阵四圣挑战要过滤的符图
        List<Integer> filterFutuIds = CfgFourSaintsTool.getCfg().getFilterFutuIds();
        //攻击符图*5、防御符图*5、血量符图*3、技能符图*2
        Map<Integer, Integer> fuTuNums = new HashMap<>();
        fuTuNums.put(FuTuTypeEnum.ATTACK_FU_TU.getType(), 5);
        fuTuNums.put(FuTuTypeEnum.DEFENSE_FU_TU.getType(), 5);
        fuTuNums.put(FuTuTypeEnum.BLOOD_FU_TU.getType(), 3);
        fuTuNums.put(FuTuTypeEnum.SKILLS_FU_TU.getType(), 2);

        /**
         * 攻击符图Map key：卡牌类型  value ：符图id
         */
        Map<Integer,List<Integer>> attackFuTuMap = new HashMap<>();
        attackFuTuMap.put(10, Arrays.asList(232001,232002,232003,232004,232003));
        attackFuTuMap.put(20, Arrays.asList(232101,232102,232103,232104,232105));
        attackFuTuMap.put(30, Arrays.asList(232201,232202,232203,232204,232205));
        attackFuTuMap.put(40, Arrays.asList(232301,232302,232303,232304,232305));
        attackFuTuMap.put(50, Arrays.asList(232401,232402,232403,232404,232405));

        /**
         * 防御符图Map key：卡牌类型  value ：符图id
         */
        Map<Integer,List<Integer>> defenseFuTuIdMap = new HashMap<>();
        defenseFuTuIdMap.put(10, Arrays.asList(233001,233002,233003,233004,233005));
        defenseFuTuIdMap.put(20, Arrays.asList(233101,233102,233103,233104,233105));
        defenseFuTuIdMap.put(30, Arrays.asList(233201,233203,233203,233204,233205));
        defenseFuTuIdMap.put(40, Arrays.asList(233301,233302,233303,233304,233305));
        defenseFuTuIdMap.put(50, Arrays.asList(233401,233402,233403,233404,233405));

        List<Integer> fuTuIdList = new ArrayList<>();
        for (CfgFourSaintsEntity.CfgFourSaintsFuTus fuTu : fuTus) {
            //防御符图
            List<Integer> defenseFuTuIds = new ArrayList<>();
            //攻击符图
            List<Integer> attackFuTuIds = new ArrayList<>();
            for (Integer type : randomsFromList) {
                defenseFuTuIds.addAll(defenseFuTuIdMap.get(type));
                attackFuTuIds.addAll(attackFuTuMap.get(type));
            }
            //随机获五个出来
            List<Integer> randomDefenFuTuIds = PowerRandom.getRandomsFromList(defenseFuTuIds, 5);
            List<Integer> randomAttackFuTuIds = PowerRandom.getRandomsFromList(attackFuTuIds, 5);
            fuTuIdList.addAll(randomDefenFuTuIds);
            fuTuIdList.addAll(randomAttackFuTuIds);
            List<Integer> fuTuTypes = fuTu.getFuTuTypes();
            for (Integer fuTuType : fuTuTypes) {
                int num = fuTuNums.get(fuTuType);
                FuTuTypeEnum fuTuTypeEnum = FuTuTypeEnum.fromValue(fuTuType);
                List<Integer> futuIds = YuXGTool.getFutuIds(fuTuTypeEnum, num,fuTu.getFuTuQualitys(),filterFutuIds);
                fuTuIdList.addAll(futuIds);
            }
        }
        return fuTuIdList;
    }


    /**
     * 删除敌方配置
     * @param beginDate
     * @return
     */
    public RDSuccess delZxzFourSaints(String beginDate) {
        RDSuccess rd = new RDSuccess();
        Date fromBeginDate = DateUtil.fromDateString(beginDate);
        int beginTime = DateUtil.toDateInt(fromBeginDate);

        List<ZxzFourSaintsInfo> zxzFourSaintsInfos = gameZxzFourSaintsService.getZxzFourSaintsInfos(beginTime);
        //保留最后一条数据
        zxzFourSaintsInfos.remove(zxzFourSaintsInfos.get(zxzFourSaintsInfos.size()-1));
        if (ListUtil.isEmpty(zxzFourSaintsInfos)) {
            return rd;
        }
        List<Long> dataIds = zxzFourSaintsInfos.stream().map(GameData::getId).collect(Collectors.toList());
        gameDataService.deleteGameDatas(dataIds,ZxzFourSaintsInfo.class);
        return rd;
    }
}
