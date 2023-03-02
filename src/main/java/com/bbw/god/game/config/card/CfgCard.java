package com.bbw.god.game.config.card;

import com.bbw.god.game.config.CfgInterface;
import com.bbw.god.game.config.CfgPrepareListInterface;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Data
public class CfgCard implements CfgInterface, CfgPrepareListInterface, Serializable {
    private static final long serialVersionUID = 6223779702493163783L;
    /** 封神大陆战斗不出现的卡 */
    public static final List<Integer> AI_CARDS_NOT_TO_FSDL_1 = Arrays.asList(CardEnum.WEN_DAO_REN.getCardId(), CardEnum.GAO_JI_NENG.getCardId(), CardEnum.HONG_YAO.getCardId(), CardEnum.JIN_LING_SHENG_MU.getCardId(), CardEnum.TAI_YXJ.getCardId(), CardEnum.QIU_YU.getCardId(), CardEnum.CHI_JING_ZI.getCardId());
    /** 封神大陆战斗不出现的卡 */
    public static final List<Integer> AI_CARDS_NOT_TO_FSDL_2 = Arrays.asList(CardEnum.WEN_DAO_REN.getCardId(), CardEnum.GAO_JI_NENG.getCardId(), CardEnum.HONG_YAO.getCardId(), CardEnum.QIU_YU.getCardId());

    private String key;
    /** 技能组所需要的元宝 */
    private int skillGroupActiveGold;

    // 战斗金币收益
    private Integer zcCopperAddRate;
    private Integer skillScrollUseLimitPerCard;
    private List<String> cardsToPool;
    private List<String> cardsToMap;
    private List<Integer> skillChangePrice;// 更换技能价格
    private CfgCardUpdateData cardUpdateData;

    /**
     * 新卡牌 way = 2
     **/
    private List<CfgCardEntity> newCards;
    /**
     * key = star
     **/
    private Map<Integer, List<CfgCardEntity>> cardsMapByStar;
    /**
     * key = type + star
     **/
    private Map<Integer, List<CfgCardEntity>> cardsMapByTypeStar;
    /** key = way */
    private Map<Integer, List<String>> cardsMapyByWay;
    /** key = type + star */
    private Map<Integer, List<Integer>> cardsIncludeDeifyGroupByTypeStar;

    @Override
    public void prepare() {
        newCards = CardTool.getAllCards().stream().filter(card -> CardTool.isNewCard(card)).collect(Collectors.toList());
        cardsMapByStar = CardTool.getAllCards().stream().collect(Collectors.groupingBy(CfgCardEntity::getStar));
        cardsMapByTypeStar = CardTool.getAllCards().stream().collect(Collectors.groupingBy(CfgCardEntity::getTypeStar));
        cardsMapyByWay = CardTool.getAllCards().stream()
                .collect(Collectors.groupingBy(CfgCardEntity::getWay, Collectors.mapping(CfgCardEntity::getName, Collectors.toList())));
        cardsIncludeDeifyGroupByTypeStar = CardTool.getAllCardsIncludeDeifyCards().stream()
                .collect(Collectors.groupingBy(CfgCardEntity::getTypeStar, Collectors.mapping(CfgCardEntity::getId, Collectors.toList())));
        log.info("卡牌集合预备完成");
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }

    @Data
    public static class CfgCardUpdateData implements Serializable {
        private static final long serialVersionUID = 8690468387280658991L;
        // 卡牌等级上限
        private Integer cardTopLevel;
        // 卡牌阶数上限
        private Integer cardTopHierarchy;
        // 每个元素消耗多少铜钱
        private Integer copperPerEle;
        // 一个元素对应多少经验，与卡牌非同属性元素获得经验的减半
        private Integer expPerEle;

        /**
         * 一星卡牌前十级升级所需经验
         **/
        private int[] needExp1;
        private int tenExp1;
        /**
         * 二星卡牌前十级升级所需经验
         **/
        private int[] needExp2;
        private int tenExp2;
        /**
         * 三星卡牌前十级升级所需经验
         **/
        private int[] needExp3;
        private int tenExp3;
        /**
         * 四星卡牌前十级升级所需经验
         **/
        private int[] needExp4;
        private int tenExp4;
        /**
         * 五星卡牌前十级升级所需经验
         **/
        private int[] needExp5;
        private int tenExp5;
    }
}