package com.bbw.god.random.service;

import com.bbw.exception.CoderException;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.card.UserCard;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 随机参数，封装最常用的三种
 *
 * @author lsj@bamboowind.cn
 * @version 1.0.0
 * @date 2019-04-07 16:51
 */
public class RandomParam {
    private HashMap<String, Object> param = new HashMap<>();
    private static String roleTypeKey = "$角色属性";
    private static String roleCardsKey = "$角色卡牌集";
    private static String cityCardsKey = "$城池卡牌集";
    private static String cards0Key = "$0类卡";
    private static String cards1Key = "$1类卡";
    private static String cards2Key = "$2类卡";
    private static String cards3Key = "$3类卡";
    private static String preSelectedCardsKey = "$前几张卡";
    private static String extraCardsToPoolKey = "$可洗入卡池卡牌集";
    private static String extraCardsToMapKey = "$可洗入大地图卡牌集";
    private static String cardsProb1Key = "$1星概率";
    private static String cardsProb2Key = "$2星概率";
    private static String cardsProb3Key = "$3星概率";
    private static String cardsProb4Key = "$4星概率";
    private static String cardsProb5Key = "$5星概率";


    public RandomParam() {
        Map<Integer, List<String>> cardMapByWay = CardTool.getConfig().getCardsMapyByWay();
        param.put(cards0Key, cardMapByWay.get(0));
        param.put(cards1Key, cardMapByWay.get(1));
        param.put(cards2Key, cardMapByWay.get(2));
        param.put(cards3Key, cardMapByWay.get(3));
    }


    /**
     * 设置玩家属性
     *
     * @param type
     */
    public void setRoleType(int type) {
        param.put(roleTypeKey, String.valueOf(type));
    }

    /**
     * 设置玩家卡牌集
     *
     * @param cards
     */
    public void setRoleCards(@NonNull List<UserCard> cards) {
        ArrayList<String> names = new ArrayList<>();
        for (UserCard card : cards) {
            names.add(card.getName());
        }
        param.put(roleCardsKey, names);
    }

    /**
     * 设置玩家卡牌集
     *
     * @param cardsIds
     */
    public void setRoleCardsByIds(List<Integer> cardsIds) {
        ArrayList<String> names = new ArrayList<>();
        for (Integer id : cardsIds) {
            names.add(id.toString());
        }
        param.put(roleCardsKey, names);
    }

    public void setExtraCardsToPool(List<UserCard> cards) {
        List<String> ownCards = cards.stream().map(UserCard::getName).collect(Collectors.toList());
        List<String> ableToPool = new ArrayList<>(CardTool.getConfig().getCardsToPool());
        List<String> names = ableToPool.stream().filter(tmp -> ownCards.contains(tmp)).collect(Collectors.toList());
        param.put(extraCardsToPoolKey, names);
    }

    public void setExtraCardsToMap(List<UserCard> cards) {
        List<String> ownCards = cards.stream().map(UserCard::getName).collect(Collectors.toList());
        List<String> ableToMap = new ArrayList<>(CardTool.getConfig().getCardsToMap());
        List<String> names = ableToMap.stream().filter(tmp -> ownCards.contains(tmp)).collect(Collectors.toList());
        param.put(extraCardsToMapKey, names);
    }

    public void setPreSelectedCards(List<String> cards) {
        param.put(preSelectedCardsKey, cards);
    }

    //	@SuppressWarnings("unchecked")
    //	public List<String> getRoleCardsByIds() {
    //		if (param.containsKey(roleCardsKey)) {
    //			return (List<String>) param.get(roleCardsKey);
    //		}
    //		return new ArrayList<>();
    //	}

    /**
     * 设置城池卡牌集
     *
     * @param cardsIds
     */
    public void setCityCards(List<Integer> cardsIds) {
        ArrayList<String> names = new ArrayList<>();
        for (Integer id : cardsIds) {
            names.add(id.toString());
        }
        set(cityCardsKey, names);
    }

    /**
     * 设置星级概率
     *
     * @param star1 30 -> 30%
     * @param star2 30 -> 30%
     * @param star3 30 -> 30%
     * @param star4 9 -> 9%
     * @param star5 1 -> 1%
     */
    public void setStarProbability(double star1, double star2, double star3, double star4, double star5) {
        param.put(cardsProb1Key, String.valueOf(star1));
        param.put(cardsProb2Key, String.valueOf(star2));
        param.put(cardsProb3Key, String.valueOf(star3));
        param.put(cardsProb4Key, String.valueOf(star4));
        param.put(cardsProb5Key, String.valueOf(star5));
    }

    //	@SuppressWarnings("unchecked")
    //	public List<String> getCityCards() {
    //		if (param.containsKey(cityCardsKey)) {
    //			return (List<String>) param.get(cityCardsKey);
    //		}
    //		return new ArrayList<>();
    //	}

    @SuppressWarnings("unchecked")
    public List<String> getCards(String key) {
        if (param.containsKey(key)) {
            return (List<String>) param.get(key);
        }
        throw CoderException.fatal("选卡参数不存在！");
    }

    public String get(String key) {
        return (String) param.get(key);
    }

    public Object set(String key, String value) {
        return param.put(key, value);
    }

    public Object set(String key, List<String> values) {
        return param.put(key, values);
    }

    public boolean containsKey(String key) {
        return param.containsKey(key);
    }

    public boolean isEmpty() {
        return param.isEmpty();
    }
}
