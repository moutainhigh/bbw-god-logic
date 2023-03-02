package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.treasure.*;
import com.bbw.god.game.wanxianzhen.service.WanXianLogic;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardGroupService;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.server.maou.alonemaou.ServerAloneMaouService;
import com.bbw.god.server.maou.bossmaou.ServerBossMaouService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 群体封神令
 *
 * @author lwb
 * @date 2020/6/29 9:10
 */
@Service
public class DeifysTokenProcessor extends TreasureUseProcessor {
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardGroupService userCardGroupService;
    @Autowired
    private ServerAloneMaouService serverAloneMaouService;
    @Autowired
    private ServerBossMaouService serverBossMaouService;
    @Autowired
    private WanXianLogic wanXianLogic;
    /** 群体封神令 */
    public static List<Integer> DEIFYS_TOKENS;


    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        useDeifyToken(gu, param.getProId(), rd);
    }

    @Override
    public boolean isMatch(int tokenId) {
        if (ListUtil.isEmpty(DEIFYS_TOKENS)) {
            List<CfgTreasureEntity> treasuresByType = TreasureTool.getTreasuresByType(TreasureType.DEIFYS.getValue());
            DEIFYS_TOKENS = treasuresByType.stream().map(CfgTreasureEntity::getId).collect(Collectors.toList());
        }
        return DEIFYS_TOKENS.contains(tokenId);
    }

    /**
     * 使用神力横扫封神令
     *
     * @param gu
     * @param tokenId
     * @param rd
     */
    public void useDeifyToken(GameUser gu, int tokenId, RDUseMapTreasure rd) {
        long uid = gu.getId();
        CfgDeifysToken token = DeifyTokenTool.getCfgDeifyTokens(tokenId);
        List<UserCard> userCards = userCardService.getUserCards(uid, token.getCardIds());
        //用于检查是否有配置该卡
        CardTool.getCards(token.getDeifyCardIds());
        if (ListUtil.isEmpty(userCards) || userCards.size() < token.getCardIds().size()) {
            //没有该卡
            throw new ExceptionForClientTip("deifyToken.not.exist.card");
        }
        LogUtil.logDeletedUserDatas(userCards, "玩家卡牌封神，此为封神时的卡牌数据");
        //卡牌数据转换  卡牌ID，符箓 卷轴的卸下
        List<RDUseMapTreasure.RdDeifyCardInfo> deifyCardInfos = deifyDataConversion(rd, uid, token, userCards);

        gameUserService.updateItems(userCards);
        rd.setDeifyCardInfos(deifyCardInfos);
        //更新头像信息
        if (token.getCardIds().contains(gu.getRoleInfo().getHead())) {
            gu.getRoleInfo().setHead(token.getDeifyCardIds().get(token.getCardIds().indexOf(gu.getRoleInfo().getHead())));
            gu.updateRoleInfo();
        }
        //更新卡组信息
        userCardGroupService.replaceCardGroupByCardIds(uid, token.getCardIds(), token.getDeifyCardIds());
        serverAloneMaouService.replaceCards(uid, token.getCardIds(), token.getDeifyCardIds());
        serverBossMaouService.replaceCards(uid, token.getCardIds(), token.getDeifyCardIds());
        userCardService.replaceShowCards(uid, token.getCardIds(), token.getDeifyCardIds());
        wanXianLogic.updateDeifyCards(uid, token.getCardIds(), token.getDeifyCardIds());
        for (Integer cardId : token.getCardIds()) {
            TreasureEventPublisher.pubTUserDeifyTokenEvent(new BaseEventParam(uid), cardId);

        }
    }

    /**
     * 封神数据转换 卡牌ID，符箓 卷轴的卸下
     *
     * @param rd
     * @param uid
     * @param token
     * @param userCards
     */
    private List<RDUseMapTreasure.RdDeifyCardInfo> deifyDataConversion(RDUseMapTreasure rd, long uid, CfgDeifysToken token, List<UserCard> userCards) {
        List<RDUseMapTreasure.RdDeifyCardInfo> deifyCardInfos = new ArrayList<>();
        for (UserCard userCard : userCards) {
            int index = token.getCardIds().indexOf(userCard.getBaseId());
            RDUseMapTreasure.RdDeifyCardInfo deifyCardInfo = RDUseMapTreasure.RdDeifyCardInfo.instance(token.getDeifyCardIds().get(index), userCard.getLevel(), userCard.getHierarchy());
            int type = userCard.getBaseId() / 100 * 10;
            userCard.setBaseId(token.getDeifyCardIds().get(index));
            UserCard.UserCardStrengthenInfo strengthenInfo = userCard.getStrengthenInfo();
            if (strengthenInfo != null) {
                if (strengthenInfo.gainSkill0() != null && strengthenInfo.gainSkill0() > 0) {
                    int skillScrollL = TreasureTool.getSkillScrollId(type, strengthenInfo.gainSkill0(), userCard.getBaseId());
                    TreasureEventPublisher.pubTAddEvent(uid, skillScrollL, 1, WayEnum.DEIFY_CARD, rd);
                }
                if (strengthenInfo.gainSkill5() != null && strengthenInfo.gainSkill5() > 0) {
                    int skillScrollL = TreasureTool.getSkillScrollId(type, strengthenInfo.gainSkill5(), userCard.getBaseId());
                    TreasureEventPublisher.pubTAddEvent(uid, skillScrollL, 1, WayEnum.DEIFY_CARD, rd);
                }
                if (strengthenInfo.gainSkill10() != null && strengthenInfo.gainSkill10() > 0) {
                    int skillScrollL = TreasureTool.getSkillScrollId(type, strengthenInfo.gainSkill10(), userCard.getBaseId());
                    TreasureEventPublisher.pubTAddEvent(uid, skillScrollL, 1, WayEnum.DEIFY_CARD, rd);
                }
                userCard.getStrengthenInfo().clearSkillScroll();
                //卸下符箓
                if (strengthenInfo.gainAttackSymbol() != null && strengthenInfo.gainAttackSymbol() > 0) {
                    TreasureEventPublisher.pubTAddEvent(uid, strengthenInfo.gainAttackSymbol(), 1, WayEnum.CARD_UNLOAD_SYMBOL, rd);
                    strengthenInfo.setAttackSymbol(null);
                }
                if (strengthenInfo.gainDefenceSymbol() != null && strengthenInfo.gainDefenceSymbol() > 0) {
                    TreasureEventPublisher.pubTAddEvent(uid, strengthenInfo.gainDefenceSymbol(), 1, WayEnum.CARD_UNLOAD_SYMBOL, rd);
                    strengthenInfo.setDefenceSymbol(null);
                }
            }
            deifyCardInfos.add(deifyCardInfo);
        }
        return deifyCardInfos;
    }
}
