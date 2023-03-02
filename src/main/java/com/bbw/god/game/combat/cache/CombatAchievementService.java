package com.bbw.god.game.combat.cache;

import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.data.Combat;
import com.bbw.god.game.combat.data.CombatInfo;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.event.CombatEventPublisher;
import com.bbw.god.game.combat.event.EPCombatAchievement;
import com.bbw.god.game.combat.event.EPCombatLeaderCardParam;
import com.bbw.god.game.config.card.CardEnum;
import org.springframework.stereotype.Service;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-04-25
 */
@Service
public class CombatAchievementService {

    /**
     * 检查成就完成、进度变更
     * @param combat
     * @param info
     */
    public void checkAchievement(Combat combat, CombatInfo info){
        if (null == info) {
            return;
        }
        try {
            Player[] players = {combat.getFirstPlayer(), combat.getSecondPlayer()};
            for (Player player : players) {
                if (player.getUid() < 0) {
                    continue;
                }
                long uid = player.getUid();
                CombatCache cache = CombatCacheUtil.getCombatCache(player);
                if (cache == null) {
                    continue;
                }
                BaseEventParam bsp = new BaseEventParam(uid);
                FightTypeEnum fightType = combat.getFightType();
                boolean win = combat.getWinnerId() == player.getId().getValue();
                if (cache.getLeaderCardKillNum() > 0) {
                    //主角卡击杀卡牌
                    EPCombatLeaderCardParam ep = new EPCombatLeaderCardParam();
                    ep.setValues(bsp);
                    ep.setFightType(fightType);
                    ep.setWin(win);
                    ep.setKillCards(cache.getLeaderCardKillNum());
                    ep.setMainCity(info.getCityLevel()!=null && info.getCityLevel()==5);
                    ep.setCityId(info.getCityId());
                    CombatEventPublisher.pubCombatLeaderCardEvent(ep);
                }
                if (cache.getQiLingAttackZHS() > 0) {
                    //麒麟攻击召唤师
                    EPCombatAchievement ep = EPCombatAchievement.instance(new BaseEventParam(uid), 14910);
                    CombatEventPublisher.pubCombatAchievement(ep);
                }

                if (win){
                    if (cache.getGodYangJianToPlaying() > 0 && cache.getXiaoTianToPlaying() > 0 && info!=null) {
                        Player oppoPlayer = combat.getOppoPlayer(player.getId());
                        if (oppoPlayer.getUid()<0){
                            boolean zhouWang=info.getPlayer(oppoPlayer.getId()).getDrawCards().stream().filter(p->p.getImgId()== CardEnum.ZHOU_WANG.getCardId()).findFirst().isPresent();
                            if (zhouWang) {
                                //哮天犬与神·杨戬同时出战，击败纣王（npc卡组）获得胜利
                                EPCombatAchievement ep = EPCombatAchievement.instance(new BaseEventParam(uid), 15040);
                                CombatEventPublisher.pubCombatAchievement(ep);
                            }
                        }
                    }
                    /**
                     * 单回合场上同时有羽翼仙和其他三张飞行卡牌，并获得胜利
                     */
                    if (cache.isYuYiXianAndFourFlyCards()){
                        EPCombatAchievement ep = EPCombatAchievement.instance(new BaseEventParam(uid), 15010);
                        CombatEventPublisher.pubCombatAchievement(ep);
                    }
                    /**
                     * 单回合场上有四张疾驰卡牌，并获得胜利
                     */
                    if (cache.isFourJiChiCards()){
                        EPCombatAchievement ep = EPCombatAchievement.instance(new BaseEventParam(uid), 15020);
                        CombatEventPublisher.pubCombatAchievement(ep);
                    }
                    /**
                     * 领悟绝技IV
                     * 15230
                     * 神·崇侯虎抵挡5次法术效果，且至少1次为封禁类技能，并获得胜利（注：要求在一场战斗中实现）
                     */
                    if (cache.getGodChongHouHuDefenseFengJin()>0 && cache.getGodChongHouHuDefenseMagicAttack()>=5){
                        EPCombatAchievement ep = EPCombatAchievement.instance(new BaseEventParam(uid), 15230);
                        CombatEventPublisher.pubCombatAchievement(ep);
                    }
                }
            }
        }catch (Exception e){
           e.printStackTrace();
        }
    }

}
