package com.bbw.god.game.zxz.rd;

import com.bbw.god.game.combat.CombatInitService;
import com.bbw.god.game.config.card.equipment.randomrule.CardXianJueRandomRule;
import com.bbw.god.game.config.card.equipment.randomrule.CardZhiBaoRandomRule;
import com.bbw.god.game.zxz.service.ZxzAnalysisService;
import com.bbw.god.rd.RDSuccess;
import com.bbw.god.game.zxz.entity.ZxzCard;
import com.bbw.god.game.zxz.entity.ZxzRegion;
import com.bbw.god.game.zxz.entity.ZxzRegionDefender;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 返回敌方区域信息
 * @author: hzf
 * @create: 2022-09-21 13:25
 **/
@Data
public class RdEnemyRegion extends RDSuccess {
    /** 诛仙阵区域Id*/
    private Integer regionId;
    /** 关卡数据 */
    private List<RdZxzRegionDefender> defenders;


    public static RdEnemyRegion getInstance(ZxzRegion zxzRegion){
        RdEnemyRegion rd = new RdEnemyRegion();
        rd.setRegionId(zxzRegion.getRegionId());
        List<RdEnemyRegion.RdZxzRegionDefender> regionDefenders = new ArrayList<>();
        for (ZxzRegionDefender defender : zxzRegion.getDefenders()) {
            RdEnemyRegion.RdZxzRegionDefender rdDefender = new RdEnemyRegion.RdZxzRegionDefender();
            rdDefender.setKind(defender.getKind());
            rdDefender.setDefenderId(defender.getDefenderId());
            rdDefender.setSummonerHp(CombatInitService.getPlayerInitHp(defender.getSummonerLv()));
            rdDefender.setSummonerLv(defender.getSummonerLv());
            rdDefender.setRunes(defender.getRunes());
            rdDefender.setDefenderCards(RdZxzRegionDefender.getRdDefenderCard(defender));
            regionDefenders.add(rdDefender);
        }
        rd.setDefenders(regionDefenders);
        return rd;
    }

    @Data
    public static class RdZxzRegionDefender{
        /** 关卡Id**/
        private String defenderId;
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

        private static List<RdDefenderCard> getRdDefenderCard(ZxzRegionDefender defender){
            List<RdDefenderCard> rdDefenderCards = new ArrayList<>();
            List<ZxzCard> zxzCards = ZxzAnalysisService.gainCards(defender.getDefenderCards());
            for (ZxzCard card : zxzCards) {
                RdDefenderCard rdCard = RdDefenderCard.getInstance(card, new ArrayList<>(), new ArrayList<>());
                rdDefenderCards.add(rdCard);
            }
            return rdDefenderCards;
        }
    }
}
