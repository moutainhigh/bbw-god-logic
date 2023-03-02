package com.bbw.god.game.zxz.rd.foursaints;

import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.config.card.equipment.randomrule.CardXianJueRandomRule;
import com.bbw.god.game.config.card.equipment.randomrule.CardZhiBaoRandomRule;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsEntity;
import com.bbw.god.game.zxz.cfg.foursaints.CfgFourSaintsTool;
import com.bbw.god.game.zxz.entity.ZxzCard;
import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaints;
import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaintsDefender;
import com.bbw.god.game.zxz.rd.RdDefenderCard;
import com.bbw.god.game.zxz.rd.RdZxzCardXianJue;
import com.bbw.god.game.zxz.rd.RdZxzCardZhiBao;
import com.bbw.god.game.zxz.service.ZxzAnalysisService;
import com.bbw.god.rd.RDSuccess;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 返回四圣区域的卡组
 * @author: hzf
 * @create: 2023-01-06 10:28
 **/
@Data
public class RdFourSaintsEnemyRegion extends RDSuccess implements Serializable {
    private static final long serialVersionUID = 1366511178569740530L;
    /** 四圣挑战类型 */
    private Integer challengeType;

    /** 关卡数据 */
    private List<RdZxzFourSaintsDefender> defenders;

    public static RdFourSaintsEnemyRegion getInstance(ZxzFourSaints zxzFourSaints){
        RdFourSaintsEnemyRegion rd = new RdFourSaintsEnemyRegion();
        rd.setChallengeType(zxzFourSaints.getChallengeType());
        List<RdZxzFourSaintsDefender> defenders = new ArrayList<>();
        for (ZxzFourSaintsDefender defender : zxzFourSaints.getFourSaintsDefenders()) {
            RdZxzFourSaintsDefender rdDefender = new RdZxzFourSaintsDefender();
            rdDefender.setDefenderId(defender.getDefenderId());
            rdDefender.setKind(defender.getKind());
            rdDefender.setSummonerHp(CombatInitService.getPlayerInitHp(defender.getSummonerLv()));
            rdDefender.setSummonerLv(defender.getSummonerLv());
            rdDefender.setRunes(defender.getRunes());
            rdDefender.setDefenderCards(RdZxzFourSaintsDefender.getRdDefenderCard(defender));
            defenders.add(rdDefender);
        }
        rd.setDefenders(defenders);
        return rd;
    }

    @Data
    public static class RdZxzFourSaintsDefender {
        /** 关卡Id**/
        private Integer defenderId;
        /** 种类 */
        private Integer kind;
        /** 召唤师等级 */
        private Integer summonerLv;
        /** 召唤师血量 */
        private Integer summonerHp;
        /** 卡组 */
        private List<RdDefenderCard> defenderCards;
        /** 符图数据 */
        private List<Integer> runes;

        private static List<RdDefenderCard> getRdDefenderCard(ZxzFourSaintsDefender defender){
            Integer challengeType = CfgFourSaintsTool.getChallengeType(defender.getDefenderId());
            CfgFourSaintsEntity.CfgFourSaintsChallenge fourSaintsChallenge = CfgFourSaintsTool.getFourSaintsChallenge(challengeType);
            //获取灵装词条等级
            int lingZhuangLv = fourSaintsChallenge.getLingCEntryLv();

            List<RdDefenderCard> rdDefenderCards = new ArrayList<>();
            List<ZxzCard> zxzCards = ZxzAnalysisService.gainCards(defender.getDefenderCards());
            for (ZxzCard card : zxzCards) {
                //处理至宝
                List<CardZhiBaoRandomRule> basicCardZhiBaos = ZxzAnalysisService.gainCardZhiBao(card.getCardId(), defender.getCardZhiBaos());
                List<CardZhiBaoRandomRule> cardZhiBaoRandomRules = ZxzAnalysisService.instanceCardZhiBaoByEntryLv(lingZhuangLv, basicCardZhiBaos);
                List<RdZxzCardZhiBao> rdZxzCardZhiBaos = RdZxzCardZhiBao.instanceEnemy(cardZhiBaoRandomRules);
                //处理仙决
                List<CardXianJueRandomRule> basicXianJue = ZxzAnalysisService.gainCardXianJue(card.getCardId(), defender.getCardXianJues());
                List<CardXianJueRandomRule> cardXianJueRandomRules = ZxzAnalysisService.instanceCardXianJueByEntryLv(lingZhuangLv, basicXianJue);
                List<RdZxzCardXianJue> rdZxzCardXianJues = RdZxzCardXianJue.instanceEnemy(cardXianJueRandomRules);
                RdDefenderCard rdCard = RdDefenderCard.getInstance(card, rdZxzCardZhiBaos, rdZxzCardXianJues);
                rdDefenderCards.add(rdCard);
            }
            return rdDefenderCards;
        }
    }
}
