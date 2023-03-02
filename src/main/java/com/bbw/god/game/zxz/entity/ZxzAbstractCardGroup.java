package com.bbw.god.game.zxz.entity;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.UserData;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 玩家卡组抽象类型
 * @author: hzf
 * @create: 2023-01-04 09:10
 **/
@Data
public abstract class ZxzAbstractCardGroup extends UserData {

    /** 玩家角色等级 */
    private Integer userLevel;
    /** 玩家角色血量 */
    private Integer hp;
    /** 玩家角色最大血量 */
    private Integer maxHp;
    /** 卡牌数据 */
    private List<UserZxzCard> cards = new ArrayList<>();
    /** 诛仙阵 主角卡信息 */
    private ZxzUserLeaderCard zxzUserLeaderCard;

    /** 符册Id */
    private long fuCeDataId;
    /** 符图数据 fuTuId@fuTuLv@pos*/
    private List<String> runes = new ArrayList<>();

    /**记录战斗前的 玩家： 玩家id->血量 */
    private Map<String,Integer> lastUserHp = new HashMap<>();
    /** 记录战斗前，血量为0，卡牌的Id */
    private List<Integer> lastCardId = new ArrayList<>();


    /**
     * 回复召唤师血量
     * @param percentage 百分比
     */
    public void recoverHp(double percentage) {
        //判断玩家当前是不是满血
        if (hp.equals(maxHp)) {
            throw  new ExceptionForClientTip("zxz.userHp.full");
        }
        //恢复percentage的血量
        double replyHp = maxHp * percentage;
        //计算血量
        hp = hp + (int) replyHp > maxHp ? maxHp : hp + (int) replyHp;
    }

    /**
     * 回复召唤师血量到上次血量（战胜后剩余的血量）
     * @param
     */
    public void recoverHpToLastHp(long uid) {
        // 回复到上次血量
        hp = lastUserHp.get(String.valueOf(uid)) == null ? maxHp : lastUserHp.get(String.valueOf(uid));
        // 将卡牌战败状态 ===战败还是战败
        for (Integer cardId : lastCardId) {
            UserZxzCard userZxzCard = gainUserZxzCard(cardId);
            if (userZxzCard != null) {
                userZxzCard.setAlive(false);
            }
        }
    }


    /**
     * 复活卡牌
     * @param cardId
     */
    public void resurrectionCard(Integer cardId){
        //判断是不是主角卡
        if (cardId == CardEnum.LEADER_CARD.getCardId()) {
            //判断卡牌是否战败
            if (zxzUserLeaderCard.getAlive()) {
                throw  new ExceptionForClientTip("zxz.card.not.defeat");
            }
            zxzUserLeaderCard.setAlive(true);
            //移除战败的卡牌记录
            delLastCardId(cardId);
            return;
        }
        UserZxzCard zxzCard = gainUserZxzCard(cardId);
        if (null == zxzCard) {
            throw new ExceptionForClientTip("zxz.card.not");
        }
        //判断卡牌是否战败
        if (zxzCard.getAlive()) {
            throw  new ExceptionForClientTip("zxz.card.not.defeat");
        }
        zxzCard.setAlive(true);
        //移除战败的卡牌记录
        delLastCardId(cardId);
    }
    /**
     * 添加战斗前的玩家卡牌血量为0的数据
     * @param cardId
     */
    public void addLastCardId(Integer cardId){
        lastCardId.add(cardId);
    }
    /**
     * 添加战斗前的玩家数据
     * @param key
     * @param value
     */
    public void addLastUser(String key, Integer value){
        lastUserHp.put(key,value);
    }

    /**
     * 删除玩家卡牌血量为0的数据
     * @param cardId
     */
    public void delLastCardId(Integer cardId) {
        lastCardId.remove(cardId);
    }
    /**
     * 获取单张卡牌
     *
     * @param cardId
     * @return
     */
    public UserZxzCard gainUserZxzCard(Integer cardId) {
        return getCards().stream()
                .filter(card -> card.getCardId().equals(cardId))
                .findFirst().orElse(null);
    }
    /**
     * 锁定卡组
     * @param gameUser
     * @param cards
     */
    public void lockCardGroup(GameUser gameUser, List<UserZxzCard> cards, ZxzUserLeaderCard zxzUserLeaderCard){
        //玩家等级
        Integer level = gameUser.getLevel();
        //玩家血量
        Integer userHp = CombatInitService.getPlayerInitHp(level);
        userLevel = level;
        hp = userHp;
        maxHp = userHp;
        lastCardId = new ArrayList<>();
        lastUserHp = new HashMap<>();
        this.cards = cards;
        this.setZxzUserLeaderCard(zxzUserLeaderCard);
    }

}
