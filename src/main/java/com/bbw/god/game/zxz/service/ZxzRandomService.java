package com.bbw.god.game.zxz.service;

import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.CfgCardEntity;
import com.bbw.god.game.config.card.FightCardGenerateRule;
import com.bbw.god.game.config.treasure.CfgSkillScrollLimitEntity;
import com.bbw.god.game.zxz.cfg.CfgZxzDefenderCardRule;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsEntity;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.entity.ZxzCard;
import com.bbw.god.game.zxz.entity.ZxzEntry;
import com.bbw.god.gameuser.yuxg.Enum.FuTuTypeEnum;
import com.bbw.god.gameuser.yuxg.YuXGTool;
import com.bbw.god.mall.skillscroll.cfg.SkillScrollTool;
import com.bbw.god.statistics.CardSkillStatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 诛仙阵随机服务类
 * @author: hzf
 * @create: 2022-12-26 13:48
 **/
@Service
public class ZxzRandomService {
    @Autowired
    private CardSkillStatisticService cardSkillStatisticService;

    /**
     * 卡牌的随机规则
     *
     * @param cards
     * @param randomAttributeLimit
     * @return
     */
    public  List<Integer> getCardIds(List<FightCardGenerateRule> cards,List<Integer> randomAttributeLimit) {

        List<Integer> cardIds = new ArrayList<>();
        List<Integer> excludes = new ArrayList<>();
        for (FightCardGenerateRule cardRule : cards) {
            if (ListUtil.isNotEmpty(cardRule.getCardIds())) {
               for (Integer cardId : cardRule.getCardIds()) {

                   if (cardId > 10000) {
                       int normalCardId = CardTool.getNormalCardId(cardId);
                       excludes.add(normalCardId);
                   } else {
                       //获取神卡
                       int deifyCardId = CardTool.getDeifyCardId(cardId);
                       //判断是否有神卡是否存在
                       if (CardTool.hasDeifyInfo(deifyCardId)) {
                           excludes.add(deifyCardId);
                       }
                   }
               }
                cardIds.addAll(cardRule.getCardIds());
                excludes.addAll(cardRule.getCardIds());
                continue;
            }
            int num = cardRule.getNum();
            for (int i = 0; i < num; i++) {
                int star = PowerRandom.getRandomFromList(cardRule.getStars());
                List<CfgCardEntity> card = CardTool.getRandomCardsIncludeDeify(star, 1, excludes,randomAttributeLimit);
                int cardId = card.get(0).getId();
                cardIds.add(cardId);
                excludes.add(cardId);
                //如果是神卡，需要额外去除本体
                if (cardId > 10000) {
                    int normalCardId = CardTool.getNormalCardId(cardId);
                    excludes.add(normalCardId);
                } else {
                    //获取神卡
                    int deifyCardId = CardTool.getDeifyCardId(cardId);
                    //判断是否有神卡是否存在
                    if (CardTool.hasDeifyInfo(deifyCardId)) {
                        excludes.add(deifyCardId);
                    }
                }
            }
        }
        return cardIds;
    }


    /**
     * 获得随机卡组
     * @param cardIds
     * @param skillRandom
     * @param lv
     * @param hv
     * @return
     */
    public List<String> getRandomCardGroup(List<Integer> cardIds,Integer skillRandom,Integer lv,Integer hv){
        List<String>  defenderCards = new ArrayList<String>();
        for (Integer cardId : cardIds) {
            List<Integer> skills = null;
            //判断技能是否随机
            if (skillRandom == 0) {
                skills = CardTool.getCardById(cardId).getSkills();
            } else {
                //获取卡牌的推荐技能
                List<List<Integer>> cardsAllSkills = cardSkillStatisticService.getCardsAllSkills(cardId);
                if (ListUtil.isNotEmpty(cardsAllSkills)) {
                    skills =  PowerRandom.getRandomFromList(cardsAllSkills);
                } else {
                    //如果卡牌推荐技能为空，则随机技能
                    skills = getCardSkills(cardId);
                }
            }
            int cardLv = lv;
            int cardHv = hv;
            ZxzCard zCard = new ZxzCard(cardId, cardLv, cardHv, skills);
            defenderCards.add(zCard.toString());
        }
        return defenderCards;
    }

    /**
     * 卡牌技能的随机规则
     *
     * @param cardId
     * @return
     */
    public List<Integer> getCardSkills(Integer cardId) {
        int maxRandomNum = 2;
        //获取卡牌的基本属性
        CfgCardEntity cfgCard = CardTool.getCardById(cardId);
        //获取卡牌基本技能
        List<Integer> cardOriginSkills = CardTool.getCardById(cardId).getSkills();
        //随机技能 技能位 -> 技能
        Map<Integer, Integer> randomSkills = null;
        //随机专属技能
        randomSkills = getRandomExclusiveSkills(cardId, maxRandomNum);
        if (randomSkills.size() < maxRandomNum) {
            //随机可升级技能
            int maxRandomUpdateableNum = maxRandomNum - randomSkills.size();
            Set<Integer> excludePoses = randomSkills.keySet();
            Collection<Integer> excludeSkills = randomSkills.values();
            Map<Integer, Integer> randomUpgradeableSkills = getRandomUpgradeableSkills(cardOriginSkills, maxRandomUpdateableNum, excludePoses, excludeSkills);
            randomSkills.putAll(randomUpgradeableSkills);

            if (randomSkills.size() < maxRandomNum) {
                //随机属性技能
                int maxRandomTypeNum = maxRandomNum - randomSkills.size();
                excludePoses = randomSkills.keySet();
                excludeSkills = randomSkills.values();
                Map<Integer, Integer> randomTypeSkills = getRandomTypeSkills(cfgCard, maxRandomTypeNum, excludePoses, excludeSkills);
                randomSkills.putAll(randomTypeSkills);
            }
        }

        // 处理返回的结果
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < cardOriginSkills.size(); i++) {
            int skillPos = i * 5;
            Integer skill = randomSkills.get(skillPos);
            if (null == skill) {
                skill = cardOriginSkills.get(i);
            }
            result.add(skill);
        }
        return result;
    }

    /**
     * 获取某张卡牌随机专属技能
     * 专属技能只能装配的特定位置上，且只有一个位置
     *
     * @param cardId
     * @param maxRandomNum 最多随机多少个专属技能
     * @return 技能位 -> 技能
     */
    private Map<Integer, Integer> getRandomExclusiveSkills(int cardId, int maxRandomNum) {
        // 随机结果map: 技能位 -> 技能
        Map<Integer, Integer> result = new HashMap<>();
        //获取卡牌的专属技能
        List<CfgSkillScrollLimitEntity> exclusiveSkills = SkillScrollTool.getExclusiveCardSkillLimits(cardId);
        //没有专属技能返回空结果
        if (ListUtil.isEmpty(exclusiveSkills)) {
            return result;
        }

        //打乱专属技能的顺序(相当于随机)
        PowerRandom.shuffle(exclusiveSkills);

        //每个专属技能最多只有一个位置。取前连个不同位置的技能
        for (CfgSkillScrollLimitEntity exclusiveSkill : exclusiveSkills) {
            if (ListUtil.isEmpty( exclusiveSkill.getLimitLevels())) {
                continue;
            }
            Integer skillPos = exclusiveSkill.getLimitLevels().get(0);
            if (null != result.get(skillPos)) {
                continue;
            }
            result.put(skillPos, exclusiveSkill.getSkillId());
            if (result.size() >= maxRandomNum) {
                return result;
            }
        }
        return result;
    }

    /**
     * 获取某批技能的随机可升级技能(跟位置无关)
     *
     * @param skills       技能集合。固定为[0级技能,5级技能,10级技能]
     * @param maxRandomNum 最多随机多少个专属技能
     * @param excludePoses 需要排除的技能位 值为0、5、10的一个或者多个
     * @return
     */
    private Map<Integer, Integer> getRandomUpgradeableSkills(List<Integer> skills, int maxRandomNum, Collection<Integer> excludePoses, Collection<Integer> excludeSkills) {
        // 随机结果map: 技能位 -> 技能
        Map<Integer, Integer> result = new HashMap<>();
        for (int i = 0; i < skills.size(); i++) {
            int skillPos = i * 5;
            //排除技能位
            if (excludePoses.contains(skillPos)) {
                continue;
            }
            int skill = skills.get(i);
            List<CfgSkillScrollLimitEntity> upgradeableSkillLimits = SkillScrollTool.getUpgradeableCardSkillLimits(skill);
            //过滤不能出现的技能
            upgradeableSkillLimits = upgradeableSkillLimits.stream()
                    .filter(tmp -> !excludeSkills.contains(tmp.getSkillId()))
                    .collect(Collectors.toList());
            if (ListUtil.isEmpty(upgradeableSkillLimits)) {
                continue;
            }
            CfgSkillScrollLimitEntity randomSkillLimit = PowerRandom.getRandomFromList(upgradeableSkillLimits);
            result.put(skillPos, randomSkillLimit.getSkillId());
            if (result.size() >= maxRandomNum) {
                return result;
            }
        }
        return result;
    }

    /**
     * 随机属性技能，优先放在空位
     *
     * @param card
     * @param maxRandomNum
     * @param excludePoses
     * @param excludeSkills
     * @return
     */
    private Map<Integer, Integer> getRandomTypeSkills(CfgCardEntity card, int maxRandomNum, Collection<Integer> excludePoses, Collection<Integer> excludeSkills) {
        //添加卡牌技能位置的限制
        CfgFourSaintsEntity.CfgRandomCardSkillLimit randomCardSkillLimit = CfgFourSaintsTool.getRandomCardSkillLimit(card.getId());
        if (null != randomCardSkillLimit) {
            if (excludePoses.isEmpty()) {
                excludePoses = new ArrayList<>();
            }
            excludePoses.add(randomCardSkillLimit.getPos());
        }
        // 随机结果map: 技能位 -> 技能
        Map<Integer, Integer> result = new HashMap<>();
        List<Integer> cardOriginSkills = card.getSkills();
        //可随机的技能
        List<CfgSkillScrollLimitEntity> typeSkillLimits = SkillScrollTool.getTypeSkillLimits(card.getType());
        //过滤不能出现的技能
        typeSkillLimits = typeSkillLimits.stream()
                .filter(tmp -> !excludeSkills.contains(tmp.getSkillId()))
                .collect(Collectors.toList());

        //优先随机空位置
        //获取位置集合，并将空位置放在前面
        List<Integer> skillPoses = new ArrayList<>();
        List<Integer> posWithSkills = new ArrayList<>();
        for (int i = 0; i < cardOriginSkills.size(); i++) {
            int skillPos = i * 5;
            if (cardOriginSkills.get(i) == 0) {
                skillPoses.add(skillPos);
            } else {
                posWithSkills.add(skillPos);
            }
        }
        skillPoses.addAll(posWithSkills);
        //给位置随机技能
        for (Integer skillPos : skillPoses) {
            if (excludePoses.contains(skillPos)) {
                continue;
            }
            List<CfgSkillScrollLimitEntity> availableSkillLimits = typeSkillLimits.stream().
                    filter(tmp -> tmp.getLimitLevels().size() == 0 || tmp.getLimitLevels().contains(skillPos))
                    .collect(Collectors.toList());
            if (ListUtil.isEmpty(availableSkillLimits)) {
                continue;
            }
            CfgSkillScrollLimitEntity randomReuslt = PowerRandom.getRandomFromList(availableSkillLimits);
            result.put(skillPos, randomReuslt.getSkillId());
            if (result.size() >= maxRandomNum) {
                return result;
            }
        }
        return result;
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
     * 随机词条
     *
     * @param entryIds 要随机的词条集合
     * @param lvLimit  随机等级上限
     * @param lvStock  词条等级存量
     * @return 随机词条及等级
     */
    public List<ZxzEntry> randomEntry(List<Integer> entryIds, Integer lvLimit, int lvStock) {
        List<ZxzEntry> randomResults = new ArrayList<>();
        //剩余等级存量
        int remainLvStock = lvStock;
        // 退出本次随机
        boolean exist;
        //开始时间
        long begin  = System.currentTimeMillis();
        //持续时间
        long duration = 5000L;
        int entryIdNum = entryIds.size();
        //等级上限的数量
        int maxNum = getMaxLvLimitNum(entryIdNum);
        //第三等级的等级上限数量
        int threeNum =  getThreeLvLimitNum(entryIdNum);
        //中转上限最大值，保证 每次都是最大值 - 拥有值 eg:maxNum:12,拥有上限等级数量为4，参与随机为8，下次随机拥有上限等级为7，参与随机为12-7
        int transferMaxNum = maxNum;
        //中转第三等级
        int transferThreeNum = threeNum;
        int lv1BeforeLimit = lvLimit - 1;
        int lv2BeforeLimit = lvLimit - 2;
        do {
            doRandomEntry(randomResults, entryIds, lvLimit, remainLvStock,maxNum,threeNum);
            //已随机的等级总量
            int sumLv = randomResults.stream().mapToInt(ZxzEntry::getEntryLv).sum();
            remainLvStock = lvStock - sumLv;
            //计算等级上限数量
            long countMaxNum = randomResults.stream().filter(tmp -> tmp.getEntryLv().equals(lvLimit)).count();
            maxNum = transferMaxNum - (int) countMaxNum;

            //计算第三等级上限数量
            long countThreeNum = randomResults.stream().filter(tmp -> tmp.getEntryLv().equals(lv2BeforeLimit)).count();
            threeNum = transferThreeNum - (int) countThreeNum;

            //过滤 等级为上限 或者 为 上限 - 2（第三等级）
            List<Integer> filterLvs = new ArrayList<>();
            filterLvs.add(lvLimit);
            filterLvs.add(lv2BeforeLimit);
            //如：等级上限的数量已经满了，上限 - 1 的等级 也过滤
            if (maxNum <= 0) {
                filterLvs.add(lv1BeforeLimit);
            }
            List<Integer> collect = randomResults.stream().filter(tmp -> filterLvs.contains(tmp.getEntryLv())).map(ZxzEntry::getEntryId).collect(Collectors.toList());
            entryIds.removeAll(collect);

            //如果存量为0退出本次随机
            exist = remainLvStock > 0;
            long useDate = System.currentTimeMillis() - begin;
            //超过五秒的时候强制退出
//            if (useDate - 5000 > 0) {
//                exist = false;
//            }
        } while (exist);

        return randomResults;
    }

    /**
     * 随机词条
     *
     * @param randomResults 随机结果
     * @param entryIds      要随机的词条集合
     * @param lvLimit       随机等级上限
     * @param lvStock       词条等级存量
     * @return
     */
    private void doRandomEntry(List<ZxzEntry> randomResults, List<Integer> entryIds, Integer lvLimit, Integer lvStock,Integer maxNum,Integer threeNum) {
        if (lvStock <= 0) {
            return;
        }
        //第三等级
        int lv2BeforeLimit = lvLimit - 2;
        //第四等级
        int lv3BeforeLimit = lvLimit - 3;
        //第五等级
        int lv4BeforeLimit = lvLimit - 4;

        List<Integer> excludeEntryIds = new ArrayList<>();
        while (true) {
            //存量等级为0退出循环
            if (lvStock <= 0) {
                break;
            }
            //如果全部随机完，退出
            if (excludeEntryIds.containsAll(entryIds)) {
                break;
            }
            //随机一个词条
            Integer randomEntryId = PowerRandom.getRandomFromList(entryIds, excludeEntryIds);
            //添加随机过的词条
            excludeEntryIds.add(randomEntryId);
            Optional<ZxzEntry> entryOptional = randomResults.stream().filter(tmp -> tmp.getEntryId().equals(randomEntryId)).findFirst();
            //当前等级
            int currentEntryLevel = entryOptional.isPresent() ? entryOptional.get().getEntryLv() : 0;

            //随机加多少等级
            CfgFourSaintsEntity.CfgEntryRandomLv cfgEntryRandomLv = getRandomLv();
            int addEntryLv = cfgEntryRandomLv.getAddLv();
            //如果随机的加值为0，则忽略本次词条随机
            if (addEntryLv == 0) {
                continue;
            }
            //超过剩下的存量
            if (addEntryLv > lvStock) {
                continue;
            }
            List<Integer> lv3BeforeLimitIds = randomResults.stream().filter(tmp -> tmp.getEntryLv() == lv3BeforeLimit).map(ZxzEntry::getEntryId).collect(Collectors.toList());
            boolean limit4Equal = lv3BeforeLimitIds.containsAll(entryIds) && entryIds.containsAll(lv3BeforeLimitIds);
            if (limit4Equal && threeNum <= 0 && lvStock == 1) {
                threeNum ++;
            }
            List<Integer> lv4BeforeLimitIds = randomResults.stream().filter(tmp -> tmp.getEntryLv() == lv4BeforeLimit).map(ZxzEntry::getEntryId).collect(Collectors.toList());
            boolean limit5Equal = lv4BeforeLimitIds.containsAll(entryIds) && entryIds.containsAll(lv4BeforeLimitIds);
            if (limit5Equal && threeNum <= 0 && lvStock == 2) {
                threeNum ++;
            }
            //预计等级
            int estimateLv = currentEntryLevel + addEntryLv;
            //控制等级第三档的数量
            if (threeNum <= 0 && estimateLv  == lv2BeforeLimit || currentEntryLevel == lv2BeforeLimit) {
                continue;
            }
            //优先处理第三档的词条等级数量
            if (threeNum > 0 && estimateLv >= lv2BeforeLimit) {
                addEntryLv = lv2BeforeLimit - currentEntryLevel;
            }
            //排除随机出来大于顶级
            if (maxNum <= 0 && estimateLv >= lvLimit) {
                continue;
            }
            //超额处理
            if (threeNum <= 0 && maxNum > 0 && estimateLv >= lvLimit) {
                addEntryLv = lvLimit - currentEntryLevel;
            }



            //最终修改的等级
            int finalUpdateLv = currentEntryLevel + addEntryLv;
            // 处理第三等级可以随机到的数量
            if (finalUpdateLv == lv2BeforeLimit) {
                threeNum --;
            }
            // 处理第一等级可以随机的到的数量
            if (finalUpdateLv == lvLimit) {
                maxNum --;
            }
            //处理等级
            if (entryOptional.isPresent()) {
                entryOptional.get().setEntryLv(finalUpdateLv);
            } else {
                ZxzEntry zxzEntry = new ZxzEntry(randomEntryId, addEntryLv);
                randomResults.add(zxzEntry);
            }
            lvStock -= addEntryLv;
        }

    }

    /**
     * 获取词条加值的配置
     * @return
     */
    private CfgFourSaintsEntity.CfgEntryRandomLv getRandomLv(){
        List<CfgFourSaintsEntity.CfgEntryRandomLv> entryRandomLvs = CfgFourSaintsTool.getEntryRandomLvs();
        //取出所有的概率
        List<Integer> probabilitys = entryRandomLvs.stream()
                .map(CfgFourSaintsEntity.CfgEntryRandomLv::getProbability)
                .collect(Collectors.toList());
        //获取中的索引
        int index = PowerRandom.getIndexByProbs(probabilitys, 100);
        return entryRandomLvs.get(index);
    }

    /**
     * 获取等级上限的数量
     * 10-30%获取到该数量
     * @param entryNum 词条的数量
     * @return
     */
    private int getMaxLvLimitNum(int entryNum){
        int probability = PowerRandom.getRandomBetween(10, 30);
        return (int) entryNum * probability /100;
    }

    /**
     * 获取第三等级上限的数量
     * 30-50%获取到该数量
     * @param entryNum 词条的数量
     * @return
     */
    private int getThreeLvLimitNum(int entryNum){
        int probability = PowerRandom.getRandomBetween(30, 50);
        return (int) entryNum * probability /100;
    }


}
