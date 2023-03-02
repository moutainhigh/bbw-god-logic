package com.bbw.god.game.transmigration.cfg;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.game.combat.runes.CfgRunes;
import com.bbw.god.game.combat.runes.RunesTool;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgBuff;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.card.FightCardGenerateRule;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.transmigration.entity.TransmigrationCard;
import com.bbw.god.game.transmigration.entity.TransmigrationDefender;
import com.bbw.god.statistics.CardSkillStatisticService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 轮回工具类
 *
 * @author: suhq
 * @date: 2021/9/10 11:08 上午
 */
public class TransmigrationTool {
    private static CardSkillStatisticService cardSkillStatisticService = SpringContextUtil.getBean(CardSkillStatisticService.class);

    /**
     * 获取配置类
     *
     * @return
     */
    public static CfgTransmigration getCfg() {
        return CloneUtil.clone(Cfg.I.getUniqueConfig(CfgTransmigration.class));
    }

    /**
     * 创建城池防守者
     *
     * @param mainCityType
     * @param city
     * @return
     */
    public static TransmigrationDefender createDefender(int mainCityType, CfgCityEntity city) {
        CfgTransmigration cfg = getCfg();
        List<CfgTransmigrationDefender> defenders = cfg.getDefenders();
        //守卫属性
        int defenderType = 0;
        if (city.getLevel() == 5) {
            defenderType = mainCityType;
        }
        int finalDefenderType = defenderType;
        //获取城池对应的守卫配置
        CfgTransmigrationDefender cfgDefender = defenders.stream()
                .filter(tmp -> tmp.getCityLv() == city.getLevel() && (tmp.getType() == 0 || tmp.getType() == finalDefenderType))
                .findFirst().get();
        //获取守卫卡牌ID
        List<Integer> cardIds = new ArrayList<>();
        for (FightCardGenerateRule cardRule : cfgDefender.getCards()) {
            if (ListUtil.isNotEmpty(cardRule.getCardIds())) {
                cardIds.addAll(cardRule.getCardIds());
                continue;
            }
            for (int i = 0; i < cardRule.getNum(); i++) {
                List<Integer> cardPool;
                do {
                    int star = PowerRandom.getRandomFromList(cardRule.getStars());
                    int cardType = getCardType(mainCityType, city);
                    cardPool = CardTool.getConfig().getCardsIncludeDeifyGroupByTypeStar().get(cardType + star);
                } while (cardIds.containsAll(cardPool));
                int cardId = PowerRandom.getRandomFromList(cardPool, cardIds);
                cardIds.add(cardId);
            }
        }
        //构建守卫卡牌对象
        List<String> defenderCards = new ArrayList<>();
        for (Integer cardId : cardIds) {
            TransmigrationCard tCard = new TransmigrationCard();
            tCard.setId(cardId);
            tCard.setHv(PowerRandom.getRandomBetween(cfgDefender.getCardHvInterval()[0], cfgDefender.getCardHvInterval()[1]));
            tCard.setLv(PowerRandom.getRandomBetween(cfgDefender.getCardLvInterval()[0], cfgDefender.getCardLvInterval()[1]));
            if (city.getLevel() <=3){
                tCard.setSkills(CardTool.getCardById(cardId).getSkills());
            }else {
                tCard.setSkills(cardSkillStatisticService.getCardSkills(cardId));
            }

            defenderCards.add(tCard.toSting());
        }
        //构建护身符
        List<CfgRunes> defenderRunes = RunesTool.getRandomRune(cfgDefender.getDefenderRunes(), cfgDefender.getRuneNum());
        //构建守卫
        TransmigrationDefender defender = new TransmigrationDefender();
        defender.setLv(cfgDefender.getDefenderLv());
        defender.setCards(defenderCards);
        defender.setRunes(defenderRunes.stream().map(CfgRunes::getId).collect(Collectors.toList()));
        return defender;
    }

    /**
     * 获取城池分区
     *
     * @author: suhq
     * @date: 2021/9/23 11:20 上午
     */
    public static int getCityArea(CfgCityEntity city) {
        String cityName = city.getName();
        CfgTransmigration cfg = getCfg();
        for (Integer areaType : cfg.getAreaDevision().keySet()) {
            if (cfg.getAreaDevision().get(areaType).contains(cityName)) {
                return areaType;
            }
        }
        return 0;
    }

    /**
     * 获得守方卡牌属性
     *
     * @param mainCityType
     * @param city
     * @return
     */
    private static int getCardType(int mainCityType, CfgCityEntity city) {
        boolean isCity5 = city.getLevel() == 5;
        if (isCity5) {
            return mainCityType;
        }
        return TypeEnum.getRandomNotCounterattackType(mainCityType);
    }

    /**
     * 获得轮回额外加成
     *
     * @param successNum
     * @return
     */
    public static double getTransmigrationAdd(int successNum) {
        if (successNum == 0) {
            return 0.0;
        }
        CfgTransmigration cfg = getCfg();
        for (CfgBuff buffAdd : cfg.getTransmigrationBuff()) {
            if (buffAdd.getMin() <= successNum && successNum <= buffAdd.getMax()) {
                return buffAdd.getAdd();
            }
        }
        return 0.0;
    }

    /**
     * 获取积分奖励
     *
     * @return
     */
    public static List<CfgTransmigrationTarget> getTargets() {
        CfgTransmigration cfg = getCfg();
        return cfg.getTargets();
    }

    /**
     * 获取某个目标积分奖励
     *
     * @param targetId
     * @return
     */
    public static CfgTransmigrationTarget getTarget(int targetId) {
        List<CfgTransmigrationTarget> scoreAwards = getTargets();
        CfgTransmigrationTarget scoreAward = scoreAwards.stream().filter(tmp -> tmp.getId() == targetId).findFirst().orElse(null);
        return scoreAward;
    }
}
