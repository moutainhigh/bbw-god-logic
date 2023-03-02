package com.bbw.god.gameuser.card;

import com.bbw.common.ListUtil;
import com.bbw.common.Rst;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.chengc.NightmareLogic;
import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPCardGroup;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.transmigration.TransmigrationCardGroupLogic;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.beast.UserLeaderBeastService;
import com.bbw.god.gameuser.leadercard.service.LeaderCardService;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.yaozu.YaoZuLogic;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.server.fst.game.FstGameService;
import com.bbw.god.server.fst.server.FstServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 卡组逻辑
 *
 * @author: suhq
 * @date: 2021/11/17 4:12 下午
 */
@Slf4j
@Service
public class UserCardGroupLogic {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardGroupService userCardGroupService;
    @Autowired
    private YaoZuLogic yaoZuLogic;
    @Autowired
    private NightmareLogic nightmareLogic;
    @Autowired
    private TransmigrationCardGroupLogic transmigrationCardGroupLogic;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private LeaderCardService leaderCardService;
    @Autowired
    private UserLeaderBeastService userLeaderBeastService;
    @Autowired
    private FstServerService fstServerService;
    @Autowired
    private FstGameService fstGameService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private OppCardService oppCardService;

    /**
     * 设定玩家可出战卡牌
     *
     * @param guId
     * @param cardIds
     * @return
     */
    public RDCommon setFightCards(Long guId, String cardIds) {
        RDCommon rd = new RDCommon();

        GameUser gu = this.gameUserService.getGameUser(guId);
        int cardLimit = UserCardService.getGroupingLimit(gu);
        String decks[] = cardIds.split(";");
        for (int i = 0; i < decks.length; i++) {
            if (decks[i].split(",").length > cardLimit) {
                throw new ExceptionForClientTip("card.grouping.outOfLimit");
            }
        }
        //去重后 设置编组卡牌 目前卡牌保存逻辑是一次性保存5个卡组
        for (int i = 0; i < 7; i++) {
            String idsInfo = "";
            if (decks.length > i) {
                idsInfo = decks[i];
//                idsinfo = Arrays.asList(decks[i].split(";")).stream().distinct().collect(Collectors.joining(";"));
            }
            String deckInfo = idsInfo + "!" + (i + 1);
            CardEventPublisher.pubCardGroupingEvent(guId, deckInfo, WayEnum.NONE, rd);
        }
        return rd;
    }

    /**
     * 设定玩家激战攻防卡组
     *
     * @param guId
     * @param cardIds 101,103,120
     * @return
     */
    public void setFierceFightingCards(Long guId, String cardIds) {
        String[] cardGroup = cardIds.split(";");
        if (cardGroup.length == 0) {
            throw new ExceptionForClientTip("card.grouping.not.blank");
        }
        List<Integer> attack = CardParamParser.parseGroupParam(cardGroup[0]);
        List<Integer> defense = new ArrayList<>();
        if (cardGroup.length > 1) {
            defense = CardParamParser.parseGroupParam(cardGroup[1]);
        }
        int attackCardGroupLimit = attack.contains(CardEnum.LEADER_CARD.getCardId()) ? attack.size() - 1 : attack.size();
        int defenseCardGroupLimit = defense.contains(CardEnum.LEADER_CARD.getCardId()) ? attack.size() - 1 : attack.size();

        GameUser gu = this.gameUserService.getGameUser(guId);
        int cardLimit = UserCardService.getGroupingLimit(gu);
        if (attackCardGroupLimit > cardLimit || defenseCardGroupLimit > cardLimit) {
            throw new ExceptionForClientTip("card.grouping.outOfLimit");
        }
        if (attack.size() == 0 && defense.size() == 0) {
            //不能为空卡组
            throw new ExceptionForClientTip("card.grouping.not.blank");
        }
        //设置编组
        userCardGroupService.setFierceFightingGroup(guId, attack, CardGroupWay.FIERCE_FIGHTING_ATTACK);
        userCardGroupService.setFierceFightingGroup(guId, defense, CardGroupWay.FIERCE_FIGHTING_DEFENSE);
        fstServerService.intoFstRanking(guId);
    }

    /**
     * 获取激战卡组
     *
     * @param guId
     * @param way
     * @return
     */
    public RDCardGroups getFierceFightingCards(Long guId, CardGroupWay way) {
        RDCardGroups rd = new RDCardGroups();
        CardGroup cardGroup = userCardGroupService.getFierceFightingCards(guId, way);
        if (CardGroupWay.FIERCE_FIGHTING_ATTACK.getValue() == way.getValue()) {
            rd.addCardIds(CardGroupWay.FIERCE_FIGHTING_ATTACK, cardGroup);
        } else {
            rd.addCardIds(CardGroupWay.FIERCE_FIGHTING_DEFENSE, cardGroup);
        }
        return rd;
    }

    /**
     * 获取激战卡组,未部署时返回空数组
     *
     * @param guId
     * @param way
     * @return
     */
    public List<CCardParam> getFierceFightingUserCards(Long guId, CardGroupWay way) {
        CardGroup cardGroup = userCardGroupService.getFierceFightingCards(guId, way);
        List<CCardParam> cardParams = new ArrayList<>();
        if (cardGroup == null || ListUtil.isEmpty(cardGroup.getCardIds())) {
            return cardParams;
        }
        List<UserCard> collect = userCardService.getUserCards(guId).stream().filter(p -> cardGroup.getCardIds().contains(p.getBaseId())).collect(Collectors.toList());
        for (UserCard userCard : collect) {
            cardParams.add(CCardParam.init(userCard));
        }
        if (cardGroup.hasLeaderCard()) {
            Optional<UserLeaderCard> cardOp = leaderCardService.getUserLeaderCardOp(guId);
            if (cardOp.isPresent()) {
                CCardParam param = CCardParam.getInstance(cardOp.get());
                param.getSkills().addAll(0, userLeaderBeastService.getSkills(guId));
                cardParams.add(param);
            }
        }
        return cardParams;
    }

    /**
     * 获取跨服封神台 攻击卡组
     *
     * @param uid
     * @return
     */
    public CPCardGroup getGameFstFightCards(Long uid, CardGroupWay way) {
        CardGroup cardGroup = userCardGroupService.getFierceFightingCards(uid, way);
        List<Integer> cardIds = cardGroup.getCardIds();
        List<UserCard> userCards = oppCardService.getOppAllCards(uid);
        List<CCardParam> cardParams = new ArrayList<>();
        List<UserCard> cards = userCards.stream().filter(p -> cardIds.contains(p.getBaseId()) || cardIds.contains(CardTool.getDeifyCardId(p.getBaseId()))).collect(Collectors.toList());
        for (UserCard card : cards) {
            cardParams.add(CCardParam.init(card));
        }
        if (cardGroup.hasLeaderCard()) {
            Optional<UserLeaderCard> op = leaderCardService.getUserLeaderCardOp(uid);
            if (op.isPresent()) {
                CCardParam param = CCardParam.getInstance(op.get());
                param.getSkills().addAll(0, userLeaderBeastService.getSkills(uid));
                param.addSkillId(CombatSkillEnum.LEADER_CARD_EXP.getValue());
                cardParams.add(param);
            }
        }
        return CPCardGroup.getInstance(uid, cardGroup.getFuCeId(), cardParams);
    }

    /**
     * 获取激战攻防卡组
     *
     * @param guId
     * @return
     */
    public RDCardGroups getFierceFightingCards(Long guId) {
        RDCardGroups rd = new RDCardGroups();
        CardGroup attackGroup = userCardGroupService.getFierceFightingCards(guId, CardGroupWay.FIERCE_FIGHTING_ATTACK);
        rd.addCardIds(CardGroupWay.FIERCE_FIGHTING_ATTACK, attackGroup);
        CardGroup defenceGroup = userCardGroupService.getFierceFightingCards(guId, CardGroupWay.FIERCE_FIGHTING_DEFENSE);
        rd.addCardIds(CardGroupWay.FIERCE_FIGHTING_DEFENSE, defenceGroup);
        return rd;
    }

    /**
     * 获取激战攻防卡组
     *
     * @param guId
     * @return
     */
    public RDCardGroups getGameFstCards(Long guId) {
        RDCardGroups rd = new RDCardGroups();
        List<UserCardGroup> groups = userCardGroupService.getUserCardGroups(guId);
        CardGroupWay[] cardGroupWays = {CardGroupWay.GAME_FST_ATTACK1, CardGroupWay.GAME_FST_ATTACK2, CardGroupWay.GAME_FST_ATTACK3,
                CardGroupWay.GAME_FST_DEFENSE1, CardGroupWay.GAME_FST_DEFENSE2, CardGroupWay.GAME_FST_DEFENSE3};
        for (CardGroupWay groupWay : cardGroupWays) {
            Optional<UserCardGroup> optional = groups.stream().filter(p -> p.getGroupWay() == groupWay.getValue()).findFirst();
            if (optional.isPresent()) {
                rd.addCardIds(groupWay, optional.get().getCards(), optional.get().getFuCe());
            }
        }
        return rd;
    }

    /**
     * 设置默认卡组
     *
     * @param uid
     * @param deck
     * @return
     */
    public RDSuccess setDefaultDeck(long uid, int deck) {
        //判断玩家是否拥有地灵印
        if (deck == UserCardGroupService.CARD_GROUP_6 && !privilegeService.isOwnDiLing(uid)) {
            throw ExceptionForClientTip.fromi18nKey("diling.not.exist");
        }
        //判断玩家是否有天灵印
        if (deck == UserCardGroupService.CARD_GROUP_7 && !privilegeService.isOwnTianLing(uid)) {
            throw ExceptionForClientTip.fromi18nKey("tianling.not.exist");
        }
        UserCardGroup userCardGroup = this.userCardGroupService.getUserCardGroup(uid, CardGroupWay.Normal_Fight.getValue(), deck);
        // 将原来使用中的设置为不使用
        UserCardGroup usingCardGroup = this.userCardGroupService.getUsingGroup(uid, CardGroupWay.Normal_Fight);
        if (usingCardGroup != null) {
            usingCardGroup.setIsUsing(false);
            this.gameUserService.updateItem(usingCardGroup);
        }
        // 设置新的卡组为使用中
        userCardGroup.setIsUsing(true);
        this.gameUserService.updateItem(userCardGroup);
        return new RDSuccess();
    }


    public void setCardGroupName(long uid, int deck, String name) {
        List<UserCardGroup> userCardGroups = this.userCardGroupService.getUserCardGroups(uid);
        for (UserCardGroup userCardGroup : userCardGroups) {
            if (userCardGroup.getName().equals(name)) {
                throw new ExceptionForClientTip("card.group.name.exists");
            }
        }
        UserCardGroup userCardGroup = this.userCardGroupService.getUserCardGroup(uid, CardGroupWay.Normal_Fight.getValue(), deck);
        if (null == userCardGroup) {
            //不存在的卡组
            throw new ExceptionForClientTip("card.not.validGroup");
        }
        userCardGroup.setNewName(name);
        this.gameUserService.updateItem(userCardGroup);
    }

    /**
     * 设置跨服封神台卡组
     *
     * @param guId
     * @param cardIds
     */
    public void setGameFstCards(Long guId, String cardIds) {
        String[] cardGroup = cardIds.split(";");
        if (cardGroup.length == 0) {
            throw new ExceptionForClientTip("card.grouping.not.blank");
        }
        List<List<Integer>> cGroups = new ArrayList<>();
        for (String str : cardGroup) {
            cGroups.add(CardParamParser.parseGroupParam(str));
        }
        List<Integer> ids = new ArrayList<>();
        ids.addAll(cGroups.get(0));
        //给所有攻击卡组 或者所有防御卡组去重
        for (int i = 1; i < cGroups.size(); i++) {
            if (i == 3) {
                ids.clear();
                ids.addAll(cGroups.get(i));
                continue;
            }
            List<Integer> cur = cGroups.get(i);
            Iterator<Integer> iterator = cur.iterator();
            while (iterator.hasNext()) {
                Integer next = iterator.next();
                if (ids.contains(next)) {
                    iterator.remove();
                } else {
                    ids.add(next);
                }
            }
        }
        List<CardGroupWay> ways = CardGroupWay.getGameFstWays();
        for (int i = 0; i < cGroups.size(); i++) {
            userCardGroupService.setFierceFightingGroup(guId, cGroups.get(i), ways.get(i));
        }
        fstGameService.joinToGameFst(guId);
    }

    /**
     * 同步卡组
     *
     * @param uid
     * @param syncWay 1：激战攻击 -> 激战防御
     *                2：激战防御 -> 激战攻击
     *                3：跨服封神台攻击 -> 跨服封神台防御
     *                4：跨服封神台防御 -> 跨服封神台攻击
     */
    public void syncCardGroups(Long uid, int syncWay) {
        switch (syncWay) {
            case 1:
                userCardGroupService.syncFightingGroup(uid, Arrays.asList(CardGroupWay.FIERCE_FIGHTING_ATTACK), Arrays.asList(CardGroupWay.FIERCE_FIGHTING_DEFENSE));
                return;
            case 2:
                userCardGroupService.syncFightingGroup(uid, Arrays.asList(CardGroupWay.FIERCE_FIGHTING_DEFENSE), Arrays.asList(CardGroupWay.FIERCE_FIGHTING_ATTACK));
                return;
            case 3:
                userCardGroupService.syncFightingGroup(uid,
                        Arrays.asList(CardGroupWay.GAME_FST_ATTACK1, CardGroupWay.GAME_FST_ATTACK2, CardGroupWay.GAME_FST_ATTACK3),
                        Arrays.asList(CardGroupWay.GAME_FST_DEFENSE1, CardGroupWay.GAME_FST_DEFENSE2, CardGroupWay.GAME_FST_DEFENSE3));
                return;
            case 4:
                userCardGroupService.syncFightingGroup(uid,
                        Arrays.asList(CardGroupWay.GAME_FST_DEFENSE1, CardGroupWay.GAME_FST_DEFENSE2, CardGroupWay.GAME_FST_DEFENSE3),
                        Arrays.asList(CardGroupWay.GAME_FST_ATTACK1, CardGroupWay.GAME_FST_ATTACK2, CardGroupWay.GAME_FST_ATTACK3));
                return;
        }

    }


    /**
     * 设置符册
     *
     * @param uid
     * @param fuCeId
     * @param cardGroupType
     * @param groupNumber
     * @return
     */
    public Rst setFuCe(long uid, int fuCeId, int cardGroupType, Integer groupNumber) {
        CardGroupWay cardGroupWay = CardGroupWay.fromValue(cardGroupType);
        switch (cardGroupWay) {
            case Normal_Fight:
                UserCardGroup userCardGroup = userCardGroupService.getUserCardGroup(uid, CardGroupWay.Normal_Fight.getValue(), groupNumber);
                if (userCardGroup != null) {
                    userCardGroup.setFuCe(fuCeId);
                    gameUserService.updateItem(userCardGroup);
                }
                break;
            case FIERCE_FIGHTING_ATTACK:
            case FIERCE_FIGHTING_DEFENSE:
            case GAME_FST_ATTACK1:
            case GAME_FST_ATTACK2:
            case GAME_FST_ATTACK3:
            case GAME_FST_DEFENSE1:
            case GAME_FST_DEFENSE2:
            case GAME_FST_DEFENSE3:
                userCardGroupService.setFierceFightingFuCe(uid, fuCeId, cardGroupWay);
                break;
            case YAO_ZU_MIRRORING:
            case YAO_ZU_ONTOLOGY:
                yaoZuLogic.setAttackFuCe(uid, fuCeId, cardGroupWay);
                break;
            case NIGHTMARE_HU_WEI:
            case NIGHTMARE_JIN_WEI:
                nightmareLogic.setAttackFuCe(uid, fuCeId, cardGroupWay);
                break;
            case TRANSMIGRATION:
                transmigrationCardGroupLogic.setAttackFuCe(uid, fuCeId);
                break;
        }
        return Rst.businessOK();
    }
}
