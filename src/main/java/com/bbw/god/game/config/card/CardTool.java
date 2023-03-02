package com.bbw.god.game.config.card;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.CoderException;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.card.CfgCard.CfgCardUpdateData;
import com.bbw.god.gameuser.card.UserCard;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 卡牌工具类
 *
 * @author suhq
 * @date 2018年10月22日 上午11:13:36
 */
public class CardTool {
    //封神卡差值
    public static final Integer DeifyBase = 10000;
    private static Map<String, CfgCardEntity> nameCardMap = new HashMap<>();

    public static void init() {
        List<CfgCardEntity> cards = getAllCards();
        for (CfgCardEntity card : cards) {
            nameCardMap.put(card.getName(), card);
        }
    }

    public static CfgCard getConfig() {
        return Cfg.I.getUniqueConfig(CfgCard.class);
    }

    public static int allCardsNum() {
        return getAllCards().size();
    }

    public static CfgCardUpdateData getCardUpdateData() {
        return getConfig().getCardUpdateData();
    }

    /**
     * 返回不含封神的所有卡牌
     *
     * @return
     */
    public static List<CfgCardEntity> getAllCards() {
        List<CfgCardEntity> allCards = ListUtil.copyList(Cfg.I.get(CfgCardEntity.class), CfgCardEntity.class);
        return allCards;
    }

    /**
     * 获取所有卡牌 包含封神卡牌
     *
     * @return
     */
    public static List<CfgCardEntity> getAllCardsIncludeDeifyCards() {
        List<CfgCardEntity> cards = getAllCards();
        List<CfgDeifyCardEntity> deifyCardEntities = getAllDeifyCards();
        if (!deifyCardEntities.isEmpty()) {
            for (CfgDeifyCardEntity cardEntity : deifyCardEntities) {
                cards.add(CfgCardEntity.instance(cardEntity));
            }
        }
        return cards;
    }

    public static List<CfgDeifyCardEntity> getAllDeifyCards() {
        List<CfgDeifyCardEntity> deifyCardEntities = Cfg.I.get(CfgDeifyCardEntity.class);
        return deifyCardEntities;
    }

    /**
     * 获得特定星级所有卡片
     *
     * @param star
     * @return
     */
    public static List<CfgCardEntity> getAllCards(int star) {
        return getConfig().getCardsMapByStar().get(star);
    }

    /**
     * 提供所有新卡牌
     *
     * @return
     */
    public static List<CfgCardEntity> getAllNewCards() {
        return getConfig().getNewCards();
    }

    /**
     * 获得特定属性星级所有卡片
     *
     * @param star
     * @return
     */
    public static List<CfgCardEntity> getAllCards(int type, int star) {
        return getConfig().getCardsMapByTypeStar().get(type + star);
    }

    public static int getCardCount() {
        return getAllCards().size();
    }

    public static CfgCardEntity getCardById(int cardId) {
        CfgCardEntity cce = null;
        if (cardId > 10000) {
            CfgDeifyCardEntity deifyCardEntity = Cfg.I.get(cardId, CfgDeifyCardEntity.class);
            if (null == deifyCardEntity) {
                throw CoderException.high("不存在id=[" + cardId + "]的卡牌封神数据！");
            }
            cce = CfgCardEntity.instance(deifyCardEntity);
        } else {
            cce = Cfg.I.get(cardId, CfgCardEntity.class);
        }
        if (null == cce) {
            cce = getHideCard(cardId);
            if (null == cce) {
                throw CoderException.high("不存在id=[" + cardId + "]的卡牌！");
            }
        }
        return CloneUtil.clone(cce);
    }

    public static List<CfgCardEntity> getCards(List<Integer> cardIds) {
        return cardIds.stream().map(tmp -> getCardById(tmp)).collect(Collectors.toList());
    }

    public static CfgCardEntity getCardByName(String name) {
        if (nameCardMap.keySet().size() == 0) {
            init();
        }
        return nameCardMap.get(name);
    }

    public static CfgCardEntity getRandomCard() {
        List<CfgCardEntity> cards = getAllCards();
        Collections.shuffle(cards);
        return PowerRandom.getRandomFromList(cards);
    }

    /**
     * 随机星级卡牌
     *
     * @param star
     * @return
     */
    public static CfgCardEntity getRandomCard(int star) {
        List<CfgCardEntity> cards = getAllCards(star);
        return PowerRandom.getRandomFromList(cards);
    }

    /**
     * 随机星级卡牌
     *
     * @param star
     * @return
     */
    public static List<CfgCardEntity> getRandomCard(int star, int num) {
        return getRandomCard(star, num, new ArrayList<>());
    }

    /**
     * 随机星级卡牌
     *
     * @param star
     * @param excludes 排除的Id
     * @return
     */
    public static List<CfgCardEntity> getRandomCard(int star, int num, List<Integer> excludes) {
        if (num <= 0) {
            return new ArrayList<>();
        }
        List<CfgCardEntity> cards = getAllCards(star);
        List<CfgCardEntity> list = ListUtil.copyList(cards, CfgCardEntity.class);
        if (ListUtil.isNotEmpty(excludes)) {
            list = list.stream().filter(p -> !excludes.contains(p.getId())).collect(Collectors.toList());
        }
        return PowerRandom.getRandomsFromList(num, list);
    }
    /**
     * 随机星级卡牌
     *
     * @param star
     * @param excludes 排除的Id
     * @return
     */
    public static List<CfgCardEntity> getRandomCard(int star, int num, List<Integer> excludes,List<Integer> cardTypes) {
        if (num <= 0) {
            return new ArrayList<>();
        }
        List<CfgCardEntity> cards = getAllCards(star);
        List<CfgCardEntity> list = ListUtil.copyList(cards, CfgCardEntity.class);
        if (ListUtil.isNotEmpty(excludes)) {
            list = list.stream().filter(p -> !excludes.contains(p.getId())).collect(Collectors.toList());
        }
        if (ListUtil.isNotEmpty(cardTypes)) {
            list = list.stream().filter(p -> cardTypes.contains(p.getType())).collect(Collectors.toList());

        }
        return PowerRandom.getRandomsFromList(num, list);
    }

    /**
     * 随机卡牌
     * @param star 星级
     * @param num  数量
     * @return
     */
    public static List<CfgCardEntity> getRandomCardsIncludeDeify(Integer star,Integer num,List<Integer> excludes,List<Integer> cardTypes){
        List<CfgCardEntity> cards = getAllCardsIncludeDeifyCards();
        cards = cards.stream().filter(card -> card.getStar().equals(star)).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(excludes)) {
            cards = cards.stream().filter(p -> !excludes.contains(p.getId())).collect(Collectors.toList());
        }
        if (ListUtil.isNotEmpty(cardTypes)) {
            cards = cards.stream().filter(p -> cardTypes.contains(p.getType())).collect(Collectors.toList());
        }
        return PowerRandom.getRandomsFromList(num, cards);
    }

    public static List<CfgCardEntity> getRandomCards(int num, List<Integer> excludes) {
        if (num <= 0) {
            return new ArrayList<>();
        }
        List<CfgCardEntity> cards = getAllCards();
        List<CfgCardEntity> list = ListUtil.copyList(cards, CfgCardEntity.class);
        if (ListUtil.isNotEmpty(excludes)) {
            list = list.stream().filter(p -> !excludes.contains(p.getId())).collect(Collectors.toList());
        }
        return PowerRandom.getRandomsFromList(num, list);
    }

    /**
     * 获得随机星级卡牌
     *
     * @param star
     * @param ways
     * @return
     */
    public static CfgCardEntity getRandomCard(int star, List<Integer> ways) {
        List<CfgCardEntity> cards = getAllCards(star).stream().filter(c -> ways.contains(c.getWay()))
                .collect(Collectors.toList());
        return PowerRandom.getRandomFromList(cards);
    }

    /**
     * 获得一定概率的随机卡牌， 如一级城一级聚贤庄的概率为16/1.6/0.2，对应的rand5=20,rand4=160,rand3=1600
     *
     * @return
     */
    public static CfgCardEntity getRandomNotSpecialCard(int rand5, int rand4, int rand3) {
        int random = PowerRandom.getRandomBySeed(10000);
        int star = 0;// 获得卡牌的星级
        if (random <= rand5) {// 五星
            star = 5;
        } else if (random <= rand5 + rand4) {// 四星
            star = 4;
        } else if (random <= rand5 + rand4 + rand3) {// 三星
            star = 3;
        } else {// 一二星
            random = PowerRandom.getRandomBySeed(2);
            if (random == 2) {
                star = 2;
            } else {
                star = 1;
            }

        }
        return getRandomNotSpecialCard(star);
    }

    public static List<CfgCardEntity> getRandomNotSpecialCards(int rand5, int rand4, int rand3, int num) {
        List<CfgCardEntity> ret = new ArrayList<CfgCardEntity>();
        int i = 0;
        while (i < num) {
            CfgCardEntity cfgCardEntity = getRandomNotSpecialCard(rand5, rand4, rand3);
            if (!ret.contains(cfgCardEntity)) {
                ret.add(cfgCardEntity);
                i++;
            }
        }
        return ret;
    }

    /**
     * 随机星级非限定卡牌
     *
     * @param star
     * @return
     */
    public static CfgCardEntity getRandomNotSpecialCard(int star) {
        List<CfgCardEntity> cards = getAllCards(star).stream().filter(card -> !isSpecialCard(card))
                .collect(Collectors.toList());
        // 洗牌
        return PowerRandom.getRandomFromList(cards);
    }

    /**
     * 随机特定星级非限定卡牌
     *
     * @param star
     * @param num
     * @return
     */
    public static List<CfgCardEntity> getRandomNotSpecialCards(int star, int num) {
        List<CfgCardEntity> cards = getAllCards(star).stream().filter(card -> !isSpecialCard(card))
                .collect(Collectors.toList());
        return PowerRandom.getRandomsFromList(cards, num);
    }

    /**
     * 随机特定属性星级非限定卡牌
     *
     * @param type
     * @param star
     * @return
     */
    public static CfgCardEntity getRandomNotSpecialCard(int type, int star) {
        List<CfgCardEntity> cards = getAllCards(type, star).stream().filter(card -> !isSpecialCard(card))
                .collect(Collectors.toList());
        return PowerRandom.getRandomFromList(cards);
    }

    /**
     * 随机N张特定属性星级非限定卡牌
     *
     * @param type
     * @param star
     * @param num
     * @return
     */
    public static List<CfgCardEntity> getRandomNotSpecialCards(int type, int star, int num) {
        List<CfgCardEntity> cards = getAllCards(type, star).stream().filter(card -> !isSpecialCard(card))
                .collect(Collectors.toList());
        return PowerRandom.getRandomsFromList(cards, num);
    }

    /**
     * 随机特定星级非特定奖励卡牌
     *
     * @param star
     * @return
     */
    public static CfgCardEntity getRandomNotAwardBmCard(int star) {
        List<CfgCardEntity> cards = getAllCards(star).stream()
                .filter(cfgCardEntity -> !isCardOnlyByAward(cfgCardEntity)).collect(Collectors.toList());
        return PowerRandom.getRandomFromList(cards);
    }

    /**
     * 随机特定属性星级非特定奖励卡牌
     *
     * @param type
     * @param star
     * @return
     */
    public static CfgCardEntity getRandomNotAwardBmCard(int type, int star) {
        List<CfgCardEntity> cards = getAllCards(type, star).stream().filter(card -> !isCardOnlyByAward(card))
                .collect(Collectors.toList());
        return PowerRandom.getRandomFromList(cards);
    }

    /**
     * 判断该卡牌是否为新卡
     *
     * @param card
     * @return
     */
    public static boolean isNewCard(CfgCardEntity card) {
        return card.getWay() == 2;
    }

    /**
     * 判断该卡牌是否只能通过特殊途径，如活动、卡包等获得
     *
     * @param card
     * @return
     */
    private static boolean isSpecialCard(CfgCardEntity card) {
        return card.getWay() >= 1;
    }

    /**
     * 判断该卡牌是否只能通过活动获得
     *
     * @param card
     * @return
     */
    private static boolean isCardOnlyByAward(CfgCardEntity card) {
        return card.getWay() >= 2;
    }

    /**
     * 殷红+殷红四天君
     *
     * @return
     */
    public static List<Integer> getYinHongCards() {
        return Arrays.asList(137, 237, 341, 436, 513);
    }

    /**
     * 获得群星册卡牌
     *
     * @return
     */
    public static List<Integer> getFlockStarBookCards() {
        return Cfg.I.get(CfgFlockStarBook.class).stream().map(CfgFlockStarBook::getId).collect(Collectors.toList());
    }

    /**
     * 获得星月皎洁成就卡牌
     *
     * @return
     */
    public static List<Integer> getStarAndMoonBrightAndClean() {
        return Arrays.asList(263, 559, 429, 156);
    }

    /**
     * 获得星盛世获麟成就卡牌
     *
     * @return
     */
    public static List<Integer> getDeityFourHolyBeast() {
        return Arrays.asList(10142, 10236, 10347, 10401);
    }

    /**
     * 获得五行荟聚成就卡牌
     *
     * @return
     */
    public static List<Integer> getFiveElementsGathering() {
        return Arrays.asList(147, 243, 354, 443, 541);
    }


    /**
     * 获得4.5星封神卡牌
     *
     * @return
     */
    public static List<Integer> getFourStarHalfDeifyCards() {
        return Arrays.asList(10106, 10204, 10331, 10435, 10532);
    }

    /**
     * 随机获取一张指定类别的卡
     *
     * @param way
     * @return
     */
    public static CfgCardEntity getRandomCardByWay(int way) {
        List<CfgCardEntity> cards = getAllCards().stream().filter(card -> card.getWay() == way)
                .collect(Collectors.toList());
        return PowerRandom.getRandomFromList(cards);
    }

    /**
     * 获取所有4,5星卡的Id
     *
     * @return
     */
    public static List<Integer> getAll4and5starCards() {
        return getAllCards().stream().filter(p -> p.getStar() >= 4).map(CfgCardEntity::getId)
                .collect(Collectors.toList());
    }

    /**
     * 是否含有封神信息
     *
     * @param cardId
     * @return
     */
    public static boolean hasDeifyInfo(int cardId) {
        if (cardId < 10000) {
            cardId += 10000;
        }
        CfgDeifyCardEntity deifyCardEntity = Cfg.I.get(cardId, CfgDeifyCardEntity.class);
        return null != deifyCardEntity;
    }

    /**
     * 根据卡片ID获取封神卡
     *
     * @param cardId
     * @return
     */
    public static int getDeifyCardId(int cardId) {
        return cardId + 10000;
    }

    /**
     * 根据卡牌ID（包括封神卡）获取基本卡牌ID
     *
     * @param cardId
     * @return
     */
    public static int getNormalCardId(int cardId) {
        return cardId % 10000;
    }

    /**
     * 根据卡牌属性 和星级 获取随机一张卡牌
     *
     * @param type
     * @param star
     * @param exclude 剔除的卡牌Id
     * @return
     */
    public static CfgCardEntity getRandomCardByTypeStar(TypeEnum type, int star, List<Integer> exclude) {
        List<CfgCardEntity> cards = new ArrayList<>();

        if (!TypeEnum.Null.equals(type)) {
            cards = getAllCards(type.getValue(), star);
        }
        // 如果属性星级都已选出，则随机一张星级卡牌
        if (ListUtil.isEmpty(cards)) {
            cards = getAllCards(star);
        }

        cards = cards.stream()
                .filter(card -> !exclude.contains(getDeifyCardId(card.getId())) && !exclude.contains(card.getId()))
                .collect(Collectors.toList());

        return PowerRandom.getRandomFromList(cards);
    }

    /**
     * 计算卡牌攻击力
     *
     * @param base 基础值
     * @param lv
     * @param hv
     * @return
     */
    public static int settleCardAtk(int base, int lv, int hv) {
        // 物理攻击=0级攻击+0级攻击*(0.1+阶级*0.025)*等级, 向下取整
        float att = base + base * (0.1f + hv * 0.025f) * lv;
        return (int) att;
    }

    /**
     * 计算卡牌防御力
     *
     * @param base 基础值
     * @param lv
     * @param hv
     * @return
     */
    public static int settleCardHp(int base, int lv, int hv) {
        float hp = base + base * (0.1f + hv * 0.025f) * lv;
        return (int) hp;
    }

    /**
     * 客栈中 随机获取指定星级产出且玩家拥有的卡牌
     *
     * @param userCards
     * @param star
     * @param needMaxNum 最大数量
     * @return
     */
    public static List<CfgCardEntity> getRandomCardsWithKez(List<UserCard> userCards, int star, int needMaxNum) {
        List<String> cardsToMap = getConfig().getCardsToMap();
        List<CfgCardEntity> list = new ArrayList<>();
        for (UserCard userCard : userCards) {
            CfgCardEntity entity = userCard.gainCard();
            if (entity.getStar() != star) {
                continue;
            }
            if (cardsToMap.contains(entity.getName()) || entity.getWay() == 0) {
                list.add(entity);
            }
        }
        if (list.isEmpty()) {
            return new ArrayList<>();
        }
        list = PowerRandom.getRandomsFromList(needMaxNum, list);
        return list;
    }

    /**
     * 获得完整的封神卡
     *
     * <br>此处会进行克隆复制，返回新对象
     */
    public static CardDeifyCardParam getPerfectDeifyCardSkills(int cardId) {
        CfgCardEntity cfgCardEntity = CardTool.getCardById(cardId);
        int[] skills = {cfgCardEntity.getZeroSkill(), cfgCardEntity.getFiveSkill(), cfgCardEntity.getTenSkill()};
        boolean change = false;
        switch (cardId) {
            case 10102://杨戬
                skills[1] = CombatSkillEnum.XIAO_TIAN.getValue();
                change = true;
                break;
            case 10201://哪吒
                skills[1] = CombatSkillEnum.QK.getValue();
                skills[2] = CombatSkillEnum.HUN_LING.getValue();
                change = true;
                break;
            case 10527://张奎
                skills[1] = CombatSkillEnum.QL.getValue();
                change = true;
                break;
            case 10325: //崇侯虎
                skills[1] = CombatSkillEnum.JIN_SHEN.getValue();
                change = true;
                break;
            case 10301: //妲己
                skills[1] = CombatSkillEnum.XI_YANG.getValue();
                change = true;
                break;
            case 10247: //玉鼎真人
                skills[2] = CombatSkillEnum.XUAN_HUAN.getValue();
                change = true;
                break;
            case 10101: //姜子牙
                skills[2] = CombatSkillEnum.FENG_SHEN.getValue();
                change = true;
                break;
            case 10302://赵公明
                skills[1] = CombatSkillEnum.FU_HU.getValue();
                change = true;
                break;
            case 10331://无当圣母
                skills[2] = CombatSkillEnum.SHANG_ZHOU.getValue();
                change = true;
                break;
            case 10204://袁洪
                skills[0] = CombatSkillEnum.HUAN_SHU.getValue();
                change = true;
                break;
            case 10106://韦护
                skills[0] = CombatSkillEnum.TU_JI.getValue();
                change = true;
                break;
            case 10435://天花娘娘
                skills[1] = CombatSkillEnum.DOU_XIAN.getValue();
                change = true;
                break;
            case 10532://龟灵圣母
                skills[1] = CombatSkillEnum.JG.getValue();
                change = true;
                break;
            default:
        }
        return CardDeifyCardParam.getInstance(skills, change);
    }

    public static CfgCardEntity getHideCard(int cardId) {
        CfgHideCard config = Cfg.I.getUniqueConfig(CfgHideCard.class);
        for (CfgCardEntity card : config.getCards()) {
            if (card.getId() == cardId) {
                return card;
            }
        }
        return null;
    }

    /**
     * 随机特定星级卡牌
     *
     * @param star
     * @param num
     * @return
     */
    public static List<CfgCardEntity> getRandomCards(int star, int num) {
        return PowerRandom.getRandomsFromList(getAllCards(star), num);
    }

    /**
     * 获得特殊封神卡数据
     *
     * @return
     */
    public static List<CfgSpecialGodCardEntity> getSpecialGodCard() {
        return Cfg.I.get(CfgSpecialGodCardEntity.class);
    }

    /**
     * 是否是特殊封神卡
     *
     * @param cardId
     * @return
     */
    public static boolean isSpecialGodCard(int cardId) {
        List<Serializable> ids = getSpecialGodCard().stream().map(CfgSpecialGodCardEntity::getId).collect(Collectors.toList());
        return ids.contains(cardId);
    }

    /**
     * 获得特殊封神卡的星级
     *
     * @param cardId
     * @return
     */
    public static Integer getSpecialGodCardStar(int cardId) {
        return getSpecialGodCard().stream().filter(s -> s.getCardId().equals(cardId)).map(CfgSpecialGodCardEntity::getStar).findFirst().orElse(null);
    }

    /**
     * 获得战斗时卡牌星级
     *
     * @param cardId 卡牌id
     * @param originalStar 原来星级
     * @return
     */
    public static Integer getCardStarForFight(int cardId, int originalStar) {
        boolean specialGodCard = isSpecialGodCard(cardId);
        if (specialGodCard) {
            return getSpecialGodCardStar(cardId);
        }
        return originalStar;
    }
}
