package com.bbw.god.gameuser.card;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserCardGroupService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserCardGroupShareService userCardGroupShareService;
    @Autowired
    private PrivilegeService privilegeService;

    /** 卡组6 */
    public final static int CARD_GROUP_6 = 6;
    /** 卡组7 */
    public final static int CARD_GROUP_7 = 7;

    /**
     * 获得卡牌编组，如果没有则将旧数据进行迁移
     *
     * @param gu
     * @param userCards
     * @return
     */
    public void initCardGroup(GameUser gu, List<UserCard> userCards) {
        long uid = gu.getId();
        List<UserCardGroup> userCardGroups = getUserCardGroups(uid);
        if (ListUtil.isEmpty(userCardGroups)) {
            List<Integer> deck1 = userCards.stream().filter(tmp -> tmp.getDeck1() != null && tmp.getDeck1()).map(UserCard::getBaseId)
                    .collect(Collectors.toList());
            List<Integer> deck2 = userCards.stream().filter(tmp -> tmp.getDeck2() != null && tmp.getDeck2()).map(UserCard::getBaseId)
                    .collect(Collectors.toList());
            List<Integer> deck3 = userCards.stream().filter(tmp -> tmp.getDeck3() != null && tmp.getDeck3()).map(UserCard::getBaseId)
                    .collect(Collectors.toList());
            List<Integer> deck4 = userCards.stream().filter(tmp -> tmp.getDeck4() != null && tmp.getDeck4()).map(UserCard::getBaseId)
                    .collect(Collectors.toList());
            List<Integer> deck5 = userCards.stream().filter(tmp -> tmp.getDeck5() != null && tmp.getDeck5()).map(UserCard::getBaseId)
                    .collect(Collectors.toList());
            List<Integer> deck6 = new ArrayList<>();
            List<Integer> deck7 = new ArrayList<>();
            List<List<Integer>> decks = Arrays.asList(deck1, deck2, deck3, deck4, deck5, deck6, deck7);
            userCardGroups = new ArrayList<>();
            int usingGroup = gu.getSetting().getDefaultDeck();
            for (int i = 0; i < decks.size(); i++) {
                UserCardGroup group = UserCardGroup.instance(uid, (i + 1), decks.get(i));
                if (usingGroup == 0 && i == 0) {
                    group.setIsUsing(true);
                } else if (usingGroup - 1 == i) {
                    group.setIsUsing(true);
                }
                userCardGroups.add(group);
            }
            gameUserService.addItems(userCardGroups);
            return;
        }
        List<UserCard> cardsToNullDeck = userCards.stream().filter(tmp -> tmp.getDeck1() != null || tmp.getDeck2() != null || tmp.getDeck3() != null || tmp.getDeck4() != null || tmp.getDeck5() != null).collect(Collectors.toList());
        if (ListUtil.isNotEmpty(cardsToNullDeck)) {
            cardsToNullDeck.forEach(card -> {
                card.setDeck1(null);
                card.setDeck2(null);
                card.setDeck3(null);
                card.setDeck4(null);
                card.setDeck5(null);
            });
            gameUserService.updateItems(cardsToNullDeck);
        }
    }

    /**
     * 获得所有卡组
     *
     * @param uid
     * @return
     */
    public List<UserCardGroup> getUserCardGroups(long uid) {
        List<UserCardGroup> userCardGroups = gameUserService.getMultiItems(uid, UserCardGroup.class);
        boolean needUpdate = false;
        // 此处循环为了修复卡组重复数据，即玩家通过某种方式造成卡组有重复卡牌
        for (UserCardGroup cardGroup : userCardGroups) {
            List<Integer> ids = cardGroup.getCards();
            int size = ids.size();
            ids = ids.stream().distinct().collect(Collectors.toList());
            if (ids.size() != size) {
                needUpdate = true;
                cardGroup.setCards(ids);
            }
        }
        if (needUpdate) {
            // 存在需要修复的数据，更新数据
            gameUserService.updateItems(userCardGroups);
        }
        return userCardGroups;
    }

    /**
     * 获得某种编组用途的卡组
     *
     * @param uid
     * @param groupWay
     * @return
     */
    public List<UserCardGroup> getUserCardGroups(long uid, CardGroupWay groupWay) {
        return getUserCardGroups(uid).stream().filter(tmp -> tmp.getGroupWay() == groupWay.getValue())
                .collect(Collectors.toList());
    }

    /**
     * 获取卡组
     *
     * @param uid
     * @param number 卡组编号
     * @return
     */
    public UserCardGroup getUserCardGroup(long uid, int groupWay, int number) {
        List<UserCardGroup> groups = getUserCardGroups(uid);
        return groups.stream().filter(tmp -> tmp.getGroupWay() == groupWay && tmp.getGroupNumber() == number).findFirst().orElse(null);
    }

    /**
     * 获取卡组
     *
     * @param uid
     * @param id  卡组编号
     * @return
     */
    public UserCardGroup getUserCardGroupById(long uid, long id) {
        List<UserCardGroup> groups = getUserCardGroups(uid);
        return groups.stream().filter(tmp -> tmp.getId() == id).findFirst().orElse(null);
    }

    /**
     * 获得卡组
     *
     * @param uid
     * @param name
     * @return
     */
    public UserCardGroup getUserCardGroup(long uid, String name) {
        List<UserCardGroup> groups = getUserCardGroups(uid);
        UserCardGroup cardGroup = groups.stream().filter(tmp -> tmp.getName().equals(name)).findFirst().orElse(null);
        if (cardGroup == null) {
            for (UserCardGroup cardG : groups) {
                if (cardG.getGroupWay() == CardGroupWay.FIERCE_FIGHTING_ATTACK.getValue()) {
                    cardG.setName("攻击阵容(封神台及诛仙阵)");
                } else if (cardG.getGroupWay() == CardGroupWay.FIERCE_FIGHTING_DEFENSE.getValue()) {
                    cardG.setName("防守阵容(封神台及诛仙阵)");
                }
                if (cardG.getName().equals(name)) {
                    cardGroup = cardG;
                }
            }
        }
        return cardGroup;
    }

    /**
     * 获得使用中的编组
     *
     * @param uid
     * @param groupWay
     * @return
     */
    public UserCardGroup getUsingGroup(long uid, CardGroupWay groupWay) {
        List<UserCardGroup> userCardGroups = getUserCardGroups(uid);
        //当前使用卡组
        UserCardGroup useCardGroup = userCardGroups.stream().filter(tmp -> tmp.getIsUsing() &&
                tmp.getGroupWay() == groupWay.getValue()).findFirst().orElse(null);
        if (null == useCardGroup) {
            return null;
        }
        if (groupWay != CardGroupWay.Normal_Fight) {
            return useCardGroup;
        }
        //判断玩家是否拥有地灵印
        if (useCardGroup.getGroupNumber() == CARD_GROUP_6 && !privilegeService.isOwnDiLing(uid)) {
            return userCardGroups.stream().filter(c -> c.getGroupNumber() == 1).findFirst().orElse(null);
        }
        //判断玩家是否有天灵印
        if (useCardGroup.getGroupNumber() == CARD_GROUP_7 && !privilegeService.isOwnTianLing(uid)) {
            return userCardGroups.stream().filter(c -> c.getGroupNumber() == 1).findFirst().orElse(null);
        }
        return useCardGroup;
    }

    /**
     * 设置 激战卡组
     *
     * @param uid
     * @param cardIds
     * @param groupWay
     */
    public void setFierceFightingGroup(long uid, List<Integer> cardIds, CardGroupWay groupWay) {
        if (!CardGroupWay.inFierceFighting(groupWay) || ListUtil.isEmpty(cardIds)) {
            return;
        }
        List<UserCardGroup> groups = getUserCardGroups(uid);
        List<UserCardGroup> cardGroups = groups.stream()
                .filter(tmp -> tmp.getGroupWay() == groupWay.getValue()).collect(Collectors.toList());
        UserCardGroup ucGroup = null;
        String cardNameStr = groupWay.getName();
        if (CardGroupWay.FIERCE_FIGHTING_ATTACK.equals(groupWay)) {
            cardNameStr = "攻击阵容(封神台及诛仙阵)";
        } else if (CardGroupWay.Normal_Fight.equals(groupWay) || CardGroupWay.FIERCE_FIGHTING_DEFENSE.equals(groupWay)) {
            cardNameStr = "防守阵容(封神台及诛仙阵)";
        }
        if (ListUtil.isEmpty(cardGroups)) {
            // 玩家没有卡组对象，需要创建
            ucGroup = UserCardGroup.instance(uid, groups.size() + 1, cardIds);
            ucGroup.setGroupWay(groupWay.getValue());
            ucGroup.setName(cardNameStr);
            gameUserService.addItem(uid, ucGroup);
            return;
        }
        if (cardGroups.size() > 1) {
            // 每个玩家只有一个激战进攻卡组，如果有多的需要删除掉
            List<UserCardGroup> dels = cardGroups.subList(1, cardGroups.size());
            gameUserService.deleteItems(uid, dels);
        }
        ucGroup = cardGroups.get(0);
        ucGroup.setCards(cardIds);
        ucGroup.setName(cardNameStr);
        gameUserService.updateItem(ucGroup);
    }

    /**
     * 同步卡组
     *
     * @param uid
     * @param cardGroupWays
     * @param targetGroupWays
     */
    public void syncFightingGroup(long uid, List<CardGroupWay> cardGroupWays, List<CardGroupWay> targetGroupWays) {
        if (cardGroupWays.size() != targetGroupWays.size()) {
            log.error("同步卡组失败，源卡组数 != 目标卡组数");
            return;
        }
        List<UserCardGroup> allGroups = getUserCardGroups(uid);
        List<UserCardGroup> groupsToUpdate = new ArrayList<>();
        List<UserCardGroup> groupsToAdd = new ArrayList<>();
        for (int i = 0; i < cardGroupWays.size(); i++) {
            UserCardGroup srcGroup = getCardGroup(allGroups, cardGroupWays.get(i));
            List<Integer> srcCardIds = new ArrayList<>();
            Integer srcFuCeId = 0;
            if (null != srcGroup) {
                srcCardIds = srcGroup.getCards();
                srcFuCeId = srcGroup.getFuCe();
            }
            UserCardGroup targetGroup = getCardGroup(allGroups, targetGroupWays.get(i));
            if (null == targetGroup) {
                CardGroupWay targetGroupWay = targetGroupWays.get(i);
                String cardNameStr = "";
                if (CardGroupWay.FIERCE_FIGHTING_ATTACK.equals(targetGroupWay)) {
                    cardNameStr = "攻击阵容(封神台及诛仙阵)";
                } else if (CardGroupWay.Normal_Fight.equals(targetGroupWay) || CardGroupWay.FIERCE_FIGHTING_DEFENSE.equals(targetGroupWay)) {
                    cardNameStr = "防守阵容(封神台及诛仙阵)";
                }
                targetGroup = UserCardGroup.instance(uid, allGroups.size() + 1, srcCardIds);
                targetGroup.setFuCe(srcFuCeId);
                targetGroup.setGroupWay(targetGroupWay.getValue());
                targetGroup.setName(cardNameStr);
                groupsToAdd.add(targetGroup);
            } else {
                targetGroup.setCards(srcCardIds);
                targetGroup.setFuCe(srcFuCeId);
                groupsToUpdate.add(targetGroup);
            }
            if (ListUtil.isNotEmpty(groupsToAdd)) {
                gameUserService.addItems(groupsToAdd);
            }
            if (ListUtil.isNotEmpty(groupsToUpdate)) {
                gameUserService.updateItems(groupsToUpdate);
            }
        }
    }

    /**
     * 设置符册
     *
     * @param uid
     * @param fuCeId
     * @param groupWay
     */
    public void setFierceFightingFuCe(long uid, Integer fuCeId, CardGroupWay groupWay) {
        List<UserCardGroup> groups = getUserCardGroups(uid);
        List<UserCardGroup> cardGroups = groups.stream()
                .filter(tmp -> tmp.getGroupWay() == groupWay.getValue()).collect(Collectors.toList());
        UserCardGroup ucGroup = null;
        String cardNameStr = groupWay.getName();
        if (CardGroupWay.FIERCE_FIGHTING_ATTACK.equals(groupWay)) {
            cardNameStr = "攻击阵容(封神台及诛仙阵)";
        } else if (CardGroupWay.Normal_Fight.equals(groupWay) || CardGroupWay.FIERCE_FIGHTING_DEFENSE.equals(groupWay)) {
            cardNameStr = "防守阵容(封神台及诛仙阵)";
        }
        if (ListUtil.isEmpty(cardGroups)) {
            // 玩家没有卡组对象，需要创建
            ucGroup = UserCardGroup.instance(uid, groups.size() + 1, new ArrayList<>());
            ucGroup.setGroupWay(groupWay.getValue());
            ucGroup.setFuCe(fuCeId);
            ucGroup.setName(cardNameStr);
            gameUserService.addItem(uid, ucGroup);
            return;
        }
        if (cardGroups.size() > 1) {
            // 每个玩家只有一个激战进攻卡组，如果有多的需要删除掉
            List<UserCardGroup> dels = cardGroups.subList(1, cardGroups.size());
            gameUserService.deleteItems(uid, dels);
        }
        ucGroup = cardGroups.get(0);
        ucGroup.setFuCe(fuCeId);
        ucGroup.setName(cardNameStr);
        gameUserService.updateItem(ucGroup);
    }

    /**
     * 获得激战卡牌集
     *
     * @param uid
     * @return
     */
    public CardGroup getFierceFightingCards(long uid, CardGroupWay groupWay) {
        if (!CardGroupWay.inFierceFighting(groupWay)) {
            return new CardGroup();
        }
        List<UserCardGroup> cardGroups = getUserCardGroups(uid, groupWay);
        if (cardGroups.isEmpty()) {
            return new CardGroup();
        }
        if (cardGroups.size() > 1) {
            // 每个玩家只有一个激战进攻卡组，如果有多的需要删除掉
            List<UserCardGroup> dels = cardGroups.subList(1, cardGroups.size());
            gameUserService.deleteItems(uid, dels);
        }
        UserCardGroup cardGroup = cardGroups.get(0);
        if (cardGroup.getCards() != null) {
            return new CardGroup(cardGroup.getFuCe(), cardGroup.getCards());
        }
        return new CardGroup();
    }

    /**
     * 将所有编组中的 旧卡信息替换成新的卡
     *
     * @param uid
     * @param oldCardId
     * @param newCardId
     */
    public void replaceCardGroupByCardId(long uid, Integer oldCardId, Integer newCardId) {
        List<UserCardGroup> userCardGroups = getUserCardGroups(uid);
        for (UserCardGroup cardGroup : userCardGroups) {
            if (cardGroup.getCards().contains(oldCardId)) {
                cardGroup.getCards().remove(oldCardId);
                cardGroup.getCards().add(newCardId);
                gameUserService.updateItem(cardGroup);
            }
        }
    }

    /**
     * 将所有编组中的 多个旧卡信息替换成新的卡
     *
     * @param uid
     * @param oldCardIds
     * @param newCardIds
     */
    public void replaceCardGroupByCardIds(long uid, List<Integer> oldCardIds, List<Integer> newCardIds) {
        List<UserCardGroup> userCardGroups = getUserCardGroups(uid);
        for (UserCardGroup cardGroup : userCardGroups) {
            for (Integer oldCardId : oldCardIds) {
                if (cardGroup.getCards().contains(oldCardId)) {
                    cardGroup.getCards().remove(oldCardId);
                    cardGroup.getCards().add(newCardIds.get(oldCardIds.indexOf(oldCardId)));
                }
            }
            gameUserService.updateItem(cardGroup);
        }
    }

    public void collectServerFstCardGroup(long uid) {
        UserCardGroup cardGroup = getUsingGroup(uid, CardGroupWay.FIERCE_FIGHTING_DEFENSE);
        if (cardGroup == null || ListUtil.isEmpty(cardGroup.getCards())) {
            return;
        }
        List<UserCard> cards = userCardService.getUserCards(uid);
        List<RDShareCardGroup.RDCard> rdCards = new ArrayList<>();
        for (Integer cardId : cardGroup.getCards()) {
            Optional<UserCard> optional = cards.stream().filter(p -> p.getBaseId().equals(cardId) || p.getBaseId().equals(CardTool.getDeifyCardId(cardId))).findFirst();
            if (optional.isPresent()) {
                rdCards.add(RDShareCardGroup.RDCard.instance(optional.get()));
            }
        }
        RDShareCardGroup shareCardGroup = new RDShareCardGroup();
        shareCardGroup.setCards(rdCards);
        shareCardGroup.setUid(uid);
        shareCardGroup.setName(CardGroupWay.FIERCE_FIGHTING_ATTACK.getName());
        shareCardGroup.setShareId(DateUtil.toDateTimeLong() + "_FST_" + uid);
        userCardGroupShareService.collectCardGroup(shareCardGroup, uid);
    }

    private UserCardGroup getCardGroup(List<UserCardGroup> cardGroups, CardGroupWay groupWay) {
        long uid = cardGroups.get(0).getGameUserId();
        List<UserCardGroup> srcCardGroups = cardGroups.stream()
                .filter(tmp -> tmp.getGroupWay() == groupWay.getValue())
                .collect(Collectors.toList());
        if (ListUtil.isEmpty(srcCardGroups)) {
            return null;
        }
        if (srcCardGroups.size() > 1) {
            // 每个玩家只有一个激战进攻卡组，如果有多的需要删除掉
            List<UserCardGroup> dels = srcCardGroups.subList(1, srcCardGroups.size());
            gameUserService.deleteItems(uid, dels);
        }
        UserCardGroup srcGroup = srcCardGroups.get(0);
        return srcGroup;
    }
}
