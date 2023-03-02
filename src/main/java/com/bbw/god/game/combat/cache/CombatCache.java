package com.bbw.god.game.combat.cache;

import com.bbw.god.game.combat.data.CombatSkillEnum;
import com.bbw.god.game.combat.skill.magicdefense.BattleSkillDefenseTableService;
import com.bbw.god.game.config.card.CardEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 说明：战斗缓存
 *
 * @author lwb
 * date 2021-04-23
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CombatCache implements Serializable {
    private long combatId;
    private long uid;
    /**
     * 麒麟攻击召唤师次数
     */
    private Integer qiLingAttackZHS=0;
    /**
     * 分身卡牌击杀卡牌数
     */
    private Integer leaderCardKillNum=0;
    /**
     * 神·杨戬出战
     */
    private Integer godYangJianToPlaying=0;
    /**
     * 哮天犬出战
     */
    private Integer xiaoTianToPlaying=0;

    /**
     * 单回合场上同时有羽翼仙和其他三张飞行卡牌，并获得胜利
     */
    private boolean yuYiXianAndFourFlyCards=false;
    /**
     * 单回合场上有四张疾驰卡牌，并获得胜利
     */
    private boolean fourJiChiCards=false;
    /**
     * 神·崇侯虎抵挡5次法术效果，且至少1次为封禁类技能，并获得胜利（注：要求在一场战斗中实现）
     * @param val
     */
    private Integer godChongHouHuDefenseMagicAttack=0;
    private Integer godChongHouHuDefenseFengJin=0;

    /**
     * 改变麒麟攻击召唤师的次数
     * @param val
     */
    public void incQiLingAttackZHS(int val){
        qiLingAttackZHS+=val;
    }

    public void incLeaderCardKillNum(int val){
        leaderCardKillNum+=val;
    }
    public void toPlaying(int cardId){
        if (CardEnum.XIAO_TIAN.getCardId()==cardId){
            xiaoTianToPlaying=1;
        }else if (CardEnum.GOD_YANG_JIAN.getCardId()==cardId){
            godYangJianToPlaying=1;
        }
    }

    public void addGodChongHouHuDefenseMagicAttack(int skillId){
        godChongHouHuDefenseMagicAttack++;
        int[] bySkillIds = BattleSkillDefenseTableService.getDefenseTableBySkillId(CombatSkillEnum.FA_SHEN.getValue());
        for (int bySkillId : bySkillIds) {
            if (bySkillId==skillId){
                godChongHouHuDefenseFengJin++;
                return;
            }
        }
    }
}
