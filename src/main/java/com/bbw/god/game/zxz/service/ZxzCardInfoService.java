package com.bbw.god.game.zxz.service;

import com.bbw.god.game.combat.runes.RunesEnum;
import com.bbw.god.game.config.card.CardEnum;
import com.bbw.god.game.config.card.equipment.randomrule.CardXianJueRandomRule;
import com.bbw.god.game.config.card.equipment.randomrule.CardZhiBaoRandomRule;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsEntity;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.entity.*;
import com.bbw.god.game.zxz.entity.foursaints.UserZxzFourSaintsCardGroupInfo;
import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaintsDefender;
import com.bbw.god.game.zxz.rd.RdZxzCardXianJue;
import com.bbw.god.game.zxz.rd.RdZxzCardZhiBao;
import com.bbw.god.game.zxz.service.foursaints.GameZxzFourSaintsService;
import com.bbw.god.game.zxz.service.foursaints.UserZxzFourSaintsService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.RDCardStrengthen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 诛仙阵：战斗查看卡牌信息 service
 * @author: hzf
 * @create: 2022-10-12 09:28
 **/
@Service
public class ZxzCardInfoService {

    @Autowired
    private ZxzService zxzService;
    @Autowired
    private ZxzEnemyService zxzEnemyService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserZxzFourSaintsService zxzFourSaintsService;
    @Autowired
    private GameZxzFourSaintsService gameZxzFourSaintsService;

    /**
     * 查看诛仙阵战斗的卡牌数据
     * @param uid 查询uid
     * @param cardId
     * @param extraParam 额外参数包括 关卡id,uid
     * @return
     */
    public RDCardStrengthen getZxzCardInfo(long uid,int cardId,String extraParam){

        String[] split = extraParam.split(",");
        Integer defenderId = Integer.parseInt(split[0]);
        long userId = Long.parseLong(split[1]);
        //获取区域id
        Integer regionId = ZxzTool.getRegionId(defenderId);
        //自己卡组
        if (uid > 0) {
            UserZxzCardGroupInfo userCardGroup = zxzService.getUserCardGroup(uid, regionId);
            return handleUserCard(uid,cardId,userCardGroup);
        }
        //获取关卡数据
        ZxzRegionDefender defender = zxzEnemyService.getZxzRegionDefender(defenderId);

        RDCardStrengthen rdDefenderCard = handleEnemyCard(cardId, defender);

        //处理玩家带有灵装词条
        UserZxzRegionInfo userZxzRegion = zxzService.getUserZxzRegion(userId, regionId);
        ZxzEntry entry = userZxzRegion.gainEntrys().stream()
                .filter(tmp -> tmp.getEntryId() == RunesEnum.LING_ZHUANG_ENTRY.getRunesId())
                .findFirst().orElse(null);
        List<CardXianJueRandomRule> cardXianJues = new ArrayList<>();
        List<CardZhiBaoRandomRule> cardZhiBaos = new ArrayList<>();
        //如果有装配灵装词条才显示卡牌装备信息
        if (null != entry && entry.getEntryLv() > 0) {
            cardXianJues = ZxzAnalysisService.instanceCardXianJueByEntryLv(entry.getEntryLv(), ZxzAnalysisService.gainCardXianJue(cardId, defender.getCardXianJues()));
            cardZhiBaos = ZxzAnalysisService.instanceCardZhiBaoByEntryLv(entry.getEntryLv(), ZxzAnalysisService.gainCardZhiBao(cardId,defender.getCardZhiBaos()));
        }

        List<RdZxzCardZhiBao> rdZxzCardZhiBaos = RdZxzCardZhiBao.instanceEnemy(cardZhiBaos);
        rdDefenderCard.setZhiBaos(RdZxzCardZhiBao.gainCardZhiBaos(rdZxzCardZhiBaos));
        List<RdZxzCardXianJue> rdZxzCardXianJues = RdZxzCardXianJue.instanceEnemy(cardXianJues);
        rdDefenderCard.setXianJues(RdZxzCardXianJue.gainCardXianJues(rdZxzCardXianJues));
        return rdDefenderCard;
    }

    /**
     * 查看诛仙阵四圣战斗的卡牌数据
     * @param uid 查询uid
     * @param cardId
     * @param extraParam 额外参数包括 关卡id
     * @return
     */
    public RDCardStrengthen getZxzFourSaintsCardInfo(long uid, int cardId, String extraParam) {
        //关卡id
        Integer defenderId = Integer.parseInt(extraParam);
        //挑战类型
        Integer challengeType = CfgFourSaintsTool.getChallengeType(defenderId);
        if (uid > 0) {
            UserZxzFourSaintsCardGroupInfo userCardGroup = zxzFourSaintsService.getUserZxzFourSaintsCardGroup(uid, challengeType);
            return handleUserCard(uid,cardId,userCardGroup);
        }
        //敌方卡牌
        ZxzFourSaintsDefender zxzFourSaintsDefender = gameZxzFourSaintsService.getZxzFourSaintsDefender(challengeType, defenderId);
        RDCardStrengthen rdDefenderCard = handleEnemyCard(cardId, zxzFourSaintsDefender);
        //处理词条
        CfgFourSaintsEntity.CfgFourSaintsChallenge fourSaintsChallenge = CfgFourSaintsTool.getFourSaintsChallenge(challengeType);
        List<CardXianJueRandomRule> cardXianJues = ZxzAnalysisService.instanceCardXianJueByEntryLv(fourSaintsChallenge.getLingCEntryLv(), ZxzAnalysisService.gainCardXianJue(cardId, zxzFourSaintsDefender.getCardXianJues()));
        List<CardZhiBaoRandomRule> cardZhiBaos = ZxzAnalysisService.instanceCardZhiBaoByEntryLv(fourSaintsChallenge.getLingCEntryLv(), ZxzAnalysisService.gainCardZhiBao(cardId,zxzFourSaintsDefender.getCardZhiBaos()));

        List<RdZxzCardZhiBao> rdZxzCardZhiBaos = RdZxzCardZhiBao.instanceEnemy(cardZhiBaos);
        rdDefenderCard.setZhiBaos(RdZxzCardZhiBao.gainCardZhiBaos(rdZxzCardZhiBaos));
        List<RdZxzCardXianJue> rdZxzCardXianJues = RdZxzCardXianJue.instanceEnemy(cardXianJues);
        rdDefenderCard.setXianJues(RdZxzCardXianJue.gainCardXianJues(rdZxzCardXianJues));
        return rdDefenderCard;
    }

    /**
     * 处理玩家卡牌数据
     * @param uid
     * @param cardId
     * @param userCardGroup
     * @param
     * @return
     */
    public <T extends ZxzAbstractCardGroup> RDCardStrengthen handleUserCard(long uid,Integer cardId,T userCardGroup){
        //如果是主角卡
        if (cardId == CardEnum.LEADER_CARD.getCardId()) {
            ZxzUserLeaderCard zxzUserLeaderCard = userCardGroup.getZxzUserLeaderCard();
            String nickname = gameUserService.getGameUser(uid).getRoleInfo().getNickname();
            return RDCardStrengthen.getInstance(zxzUserLeaderCard, nickname);
        }
        //获取对应的卡组
        List<UserZxzCard> cards = userCardGroup.getCards();
        UserZxzCard uZxzCard = cards.stream()
                .filter(card -> card.getCardId().equals(cardId))
                .findFirst().orElse(null);
        if (null == uZxzCard) {
            return new RDCardStrengthen();
        }
        return RDCardStrengthen.getInstance(uZxzCard);
    }

    /***
     * 处理敌方卡牌
     * @param cardId
     * @param defender
     * @return
     */
    public <T extends ZxzAbstractDefender> RDCardStrengthen handleEnemyCard(Integer cardId, T defender){
        //获取对应的卡牌
        List<ZxzCard> zxzCards = ZxzAnalysisService.gainCards(defender.getDefenderCards());
        ZxzCard defenderCard = zxzCards.stream()
                .filter(card -> card.getCardId().equals(cardId))
                .findFirst().orElse(null);
        if (null == defenderCard) {
            return new RDCardStrengthen();
        }
        return RDCardStrengthen.getInstance(defenderCard);
    }

}
