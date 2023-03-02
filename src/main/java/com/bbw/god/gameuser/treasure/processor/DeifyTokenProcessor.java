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

import java.util.List;
import java.util.stream.Collectors;

/**
 * 卡牌封神
 *
 * @author lwb
 * @date 2020/6/29 9:10
 */
@Service
public class DeifyTokenProcessor extends TreasureUseProcessor {
    public static List<Integer> DEIFY_TOKENS;
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
    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        useDeifyToken(gu, param.getProId(), rd);
    }

    @Override
    public boolean isMatch(int tokenId) {
        if (ListUtil.isEmpty(DEIFY_TOKENS)) {
            List<CfgTreasureEntity> treasuresByType = TreasureTool.getTreasuresByType(TreasureType.DEIFY.getValue());
            DEIFY_TOKENS = treasuresByType.stream().map(CfgTreasureEntity::getId).collect(Collectors.toList());
        }
        return DEIFY_TOKENS.contains(tokenId);
    }

    /**
     * 使用封神令
     *
     * @param gu
     * @param tokenId
     * @param rd
     */
    public void useDeifyToken(GameUser gu, int tokenId, RDUseMapTreasure rd) {
        long uid = gu.getId();
        CfgDeifyToken token = DeifyTokenTool.getCfgDeifyToken(tokenId);
        UserCard userCard = userCardService.getUserCard(uid, token.getCardId());
        CardTool.getCardById(token.getDeifyCardId());//用于检查是否有配置该卡
        if (userCard == null) {
            //没有该卡
            throw new ExceptionForClientTip("deifyToken.not.exist.card");
        }
        int type = userCard.getBaseId() / 100 * 10;
        LogUtil.logDeletedUserData("玩家卡牌封神，此为封神时的卡牌数据", userCard);
        //卡牌数据转换  卡牌ID，符箓 卷轴的卸下
        RDUseMapTreasure.RdDeifyCardInfo deifyCardInfo = RDUseMapTreasure.RdDeifyCardInfo.instance(token.getDeifyCardId(), userCard.getLevel(), userCard.getHierarchy());
        userCard.setBaseId(token.getDeifyCardId());
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
        gameUserService.updateItem(userCard);
        rd.setDeifyCardInfo(deifyCardInfo);
        //更新头像信息
        if (gu.getRoleInfo().getHead() == token.getCardId()) {
            gu.getRoleInfo().setHead(token.getDeifyCardId());
            gu.updateRoleInfo();
        }
        //更新卡组信息
        userCardGroupService.replaceCardGroupByCardId(uid, token.getCardId(), token.getDeifyCardId());
        serverAloneMaouService.replaceCard(uid, token.getCardId(), token.getDeifyCardId());
        serverBossMaouService.replaceCard(uid, token.getCardId(), token.getDeifyCardId());
        userCardService.replaceShowCard(uid, token.getCardId(), token.getDeifyCardId());
        wanXianLogic.updateDeifyCard(uid, token.getCardId(), token.getDeifyCardId());

        TreasureEventPublisher.pubTUserDeifyTokenEvent(new BaseEventParam(uid), token.getCardId());
    }
}
