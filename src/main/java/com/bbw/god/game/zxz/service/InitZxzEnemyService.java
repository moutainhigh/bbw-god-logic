package com.bbw.god.game.zxz.service;

import com.bbw.cache.GameCacheService;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.card.FightCardGenerateRule;
import com.bbw.god.game.config.card.equipment.randomrule.CardXianJueRandomRule;
import com.bbw.god.game.config.card.equipment.randomrule.CardZhiBaoRandomRule;
import com.bbw.god.game.config.treasure.CfgSkillScrollLimitEntity;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.game.zxz.cfg.*;
import com.bbw.god.game.zxz.entity.*;
import com.bbw.god.game.zxz.enums.ZxzDifficultyEnum;
import com.bbw.god.gameuser.yuxg.Enum.FuTuTypeEnum;
import com.bbw.god.gameuser.yuxg.YuXGTool;
import com.bbw.god.mall.skillscroll.cfg.SkillScrollTool;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.statistics.CardSkillStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 诛仙阵 敌方初始化配置类
 * @author: hzf
 * @create: 2022-09-16 19:54
 **/
@Component
public class InitZxzEnemyService {

    @Autowired
    private CardSkillStatisticService cardSkillStatisticService;
    @Autowired
    private GameCacheService gameCacheService;
    @Autowired
    private ZxzEnemyService zxzEnemyService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private ZxzRandomService zxzRandomService;


    /**
     * 初始化敌方配置
     *
     * @return
     */
    public ZxzInfo initZxzEnemyConfig() {
        List<ZxzDifficulty> levels = new ArrayList<>();
        for (ZxzDifficultyEnum zxzDifficultyEnum : ZxzDifficultyEnum.values()) {
            //初始化诛仙阵难度
            ZxzDifficulty zxzDifficulty = initZxzLevel(zxzDifficultyEnum);
            levels.add(zxzDifficulty);
        }
        List<ZxzInfo> zxzLevelList = new ArrayList<>();
        ZxzInfo zxzInfoInstance = ZxzInfo.getInstance(levels);
        zxzLevelList.add(zxzInfoInstance);
        gameCacheService.addGameDatas(zxzLevelList);
        return zxzInfoInstance;
    }
    /**
     * 删除敌方配置
     * @param beginDate
     * @return
     */
    public RDSuccess delZxzInit(String beginDate) {
        RDSuccess rd = new RDSuccess();
        Date fromBeginDate = DateUtil.fromDateString(beginDate);
        int beginTime = DateUtil.toDateInt(fromBeginDate);
        List<ZxzInfo> zxzInfos = zxzEnemyService.getZxzInfos(beginTime);
        //保留最后一条数据
        zxzInfos.remove(zxzInfos.get(zxzInfos.size()-1));
        if (ListUtil.isEmpty(zxzInfos)) {
            return rd;
        }
        List<Long> collect = zxzInfos.stream().map(GameData::getId).collect(Collectors.toList());
        gameDataService.deleteGameDatas(collect,ZxzInfo.class);
        return rd;
    }

    /**
     * 初始化诛仙阵难度
     *
     * @param difficulty 难度枚举
     * @return
     */
    private ZxzDifficulty initZxzLevel(ZxzDifficultyEnum difficulty) {
        ZxzDifficulty zxzDifficulty = new ZxzDifficulty();
        //根据难度获取对应的配置
        CfgZxzLevel cfgZxzLevel = ZxzTool.getZxzLevel(difficulty.getDifficulty());
        //初始化各个区域的数据
        List<ZxzRegion> initedZxzRegions = cfgZxzLevel.getRegions().stream()
                .map(regionId -> initRegion(difficulty, regionId))
                .collect(Collectors.toList());
        zxzDifficulty.setDifficulty(cfgZxzLevel.getDifficulty());
        zxzDifficulty.setRegions(initedZxzRegions);
        return zxzDifficulty;
    }

    /**
     * 初始化区域关卡数据
     *
     * @param difficulty 难度枚举
     * @param regionId   区域Id
     * @return
     */
    private ZxzRegion initRegion(ZxzDifficultyEnum difficulty, Integer regionId) {
        //初始化关卡
        List<ZxzRegionDefender> initedRegionDefenders = initDefenders(difficulty, regionId);
        //构建初始化区域
        ZxzRegion regionInit = new ZxzRegion();
        regionInit.setRegionId(regionId);
        regionInit.setDefenders(initedRegionDefenders);
        regionInit.setEntries(initRegionEntry());
        //清理临时数据
        tmpExcludeEntryIds = new ArrayList<Integer>(){{add(RunesEnum.KUANG_BAO_ERTRY.getRunesId());}};
        return regionInit;
    }

    //临时数据：要过滤掉的词条id
    List<Integer> tmpExcludeEntryIds = new ArrayList<Integer>(){{add(RunesEnum.KUANG_BAO_ERTRY.getRunesId());}};
    public List<String> initRegionEntry(){
        List<String> entryStrings = new ArrayList<>();
        ZxzEntry changShengEntry = new ZxzEntry();
        changShengEntry.setEntryId(RunesEnum.CHANG_SHENG_ENTRY.getRunesId());
        changShengEntry.setEntryLv(0);
        String changShengEntryString = changShengEntry.toString();
        ZxzEntry kuangBaoEntry = new ZxzEntry();
        kuangBaoEntry.setEntryId(RunesEnum.KUANG_BAO_ERTRY.getRunesId());
        kuangBaoEntry.setEntryLv(0);
        String kuangBaoEntryString = kuangBaoEntry.toString();

        entryStrings.add(changShengEntryString);
        entryStrings.add(kuangBaoEntryString);

        List<CfgZxzEntryEntity> entrys = ZxzEntryTool.getEntryByType(40);
        List<Integer> entryIntegers = entrys.stream().map(CfgZxzEntryEntity::getEntryId).collect(Collectors.toList());
//
        Integer randomEntry = PowerRandom.getRandomFromList(entryIntegers, tmpExcludeEntryIds);
        tmpExcludeEntryIds.add(randomEntry);
        CfgZxzEntryEntity entry = ZxzEntryTool.getEntryById(randomEntry);
        ZxzEntry zuzhouEntry = new ZxzEntry();
        zuzhouEntry.setEntryId(entry.getEntryId());
        zuzhouEntry.setEntryLv(entry.getHighestLv());
        String zuzhouEntryEntryString = zuzhouEntry.toString();
        entryStrings.add(zuzhouEntryEntryString);
        return entryStrings;
    }

    /**
     * 初始化关卡数据
     *
     * @param difficulty 难度枚举
     * @param regionId   区域Id
     * @return
     */
    private List<ZxzRegionDefender> initDefenders(ZxzDifficultyEnum difficulty, Integer regionId) {
        List<CfgZxzDefenderCardRule> defenderCardRules = ZxzTool.getZxzDefenderCards(difficulty.getDifficulty());
        //初始化各个关卡
        List<ZxzRegionDefender> defenders = defenderCardRules.stream()
                .map(cfgDifficulty -> initDefender(cfgDifficulty, regionId))
                .collect(Collectors.toList());
        return defenders;
    }

    /**
     * 初始化每个关卡
     * @param cardRule 卡组规则
     * @param regionId 区域Id
     * @return
     */
    private ZxzRegionDefender initDefender(CfgZxzDefenderCardRule cardRule, Integer regionId) {
        ZxzRegionDefender defender = new ZxzRegionDefender();
        defender.setDefenderId(regionId, cardRule.getDefender());
        defender.setSummonerLv(cardRule.getSummonerLv());
        defender.setKind(cardRule.getKind());
        //获取卡牌id
        List<Integer> cardIds = zxzRandomService.getCardIds(cardRule.getCards(),new ArrayList<>());
        //实例关卡卡牌对象
        List<String> defenderCards = new ArrayList<>();
        for (Integer cardId : cardIds) {
            List<Integer> skills = null;
            //判断技能是否随机
            if (cardRule.getSkillRandom() == 0) {
                skills = CardTool.getCardById(cardId).getSkills();
            } else {
                //获取卡牌的推荐技能
                List<List<Integer>> cardsAllSkills = cardSkillStatisticService.getCardsAllSkills(cardId);
                if (ListUtil.isNotEmpty(cardsAllSkills)) {
                    skills =  PowerRandom.getRandomFromList(cardsAllSkills);
                } else {
                    //如果卡牌推荐技能为空，则随机技能
                    skills = zxzRandomService.getCardSkills(cardId);
                }

            }
            int lv = cardRule.getCardLv();
            int hv = cardRule.getCardHv();
            ZxzCard zCard = new ZxzCard(cardId, lv, hv, skills);
            defenderCards.add(zCard.toString());
        }
        //构建卡组
        defender.setDefenderCards(defenderCards);
        //构建仙决
        defender.setCardXianJues(CardXianJueRandomRule.instances(cardIds));
        //构建至宝
        defender.setCardZhiBaos(CardZhiBaoRandomRule.instances(cardIds));

        //实例关卡所带的符图
        List<Integer> fuTuIds = getFuTuIds(cardRule.getFuTus());
        defender.setRunes(fuTuIds);
        return defender;
    }

    /**
     * 符图的随机规则
     *
     * @param fuTus
     * @return
     */
    private List<Integer> getFuTuIds(List<CfgZxzDefenderCardRule.CfgFutTu> fuTus) {
        //诛仙阵要过滤的符图
        List<Integer> filterFutuIds = ZxzTool.getCfg().getFilterFutuIds();
        //攻击符图*5、防御符图*5、血量符图*3、技能符图*2
        Map<Integer, Integer> fuTuNums = new HashMap<>();
        fuTuNums.put(FuTuTypeEnum.ATTACK_FU_TU.getType(), 5);
        fuTuNums.put(FuTuTypeEnum.DEFENSE_FU_TU.getType(), 5);
        fuTuNums.put(FuTuTypeEnum.BLOOD_FU_TU.getType(), 3);
        fuTuNums.put(FuTuTypeEnum.SKILLS_FU_TU.getType(), 2);

        List<Integer> fuTuIdList = new ArrayList<>();
        for (CfgZxzDefenderCardRule.CfgFutTu fuTu : fuTus) {

            List<Integer> fuTuTypes = fuTu.getFuTuTypes();
            for (Integer fuTuType : fuTuTypes) {
                int num = fuTuNums.get(fuTuType);
                FuTuTypeEnum fuTuTypeEnum = FuTuTypeEnum.fromValue(fuTuType);
//                List<Integer> futuIds = YuXGTool.getFutuIds(fuTuTypeEnum, num);
                List<Integer> futuIds = YuXGTool.getFutuIds(fuTuTypeEnum, num,fuTu.getFuTuQualitys(),filterFutuIds);
                fuTuIdList.addAll(futuIds);
            }
        }
        return fuTuIdList;
    }




    /**
     * 判断 卡牌列表是否包含这个id
     *
     * @param cards
     * @param cardId
     * @return
     */
    public static boolean cardsContains(List<CfgCardEntity> cards, Integer cardId) {
        return cards.contains(cardId);
    }

}
