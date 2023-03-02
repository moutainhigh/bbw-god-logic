package com.bbw.god.game.combat.runes;

import com.bbw.common.ListUtil;
import com.bbw.god.game.combat.PositionService;
import com.bbw.god.game.combat.data.Player;
import com.bbw.god.game.combat.data.attack.Effect;
import com.bbw.god.game.combat.data.card.BattleCard;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lwb
 * @date 2020/9/16 14:49
 */
@Data
public class CombatRunesParam {
    private Player performPlayer;
    private Player oppoPlayer;
    private int seq;
    private List<Effect> receiveEffect;
    private int round = 0;//注意赋值
    /** 可能为空的两种情况：不传或者是召唤师 */
    private BattleCard performCard = null;
    /** 可能为空的两种情况：不传或者是召唤师 */
    private BattleCard targetCard = null;
    /** 移动的卡牌的原始位置 */
    private Integer cardSourcePos;
    private long combatId;

    public static CombatRunesParam instance(Player performPlayer, Player oppoPlayer, long combatId) {
        CombatRunesParam param = new CombatRunesParam();
        param.setPerformPlayer(performPlayer);
        param.setOppoPlayer(oppoPlayer);
        param.setCombatId(combatId);
        return param;
    }

    public static CombatRunesParam instance(Player performPlayer,Player oppoPlayer,BattleCard targetCard,long combatId){
        CombatRunesParam param=instance(performPlayer,oppoPlayer,combatId);
        param.setTargetCard(targetCard);
        return param;
    }

    public static CombatRunesParam instance(Player performPlayer, Player oppoPlayer, Effect effect, long combatId) {
        CombatRunesParam param = instance(performPlayer, oppoPlayer, combatId);
        param.setReceiveEffect(new ArrayList<>());
        param.getReceiveEffect().add(effect);
        return param;
    }

    public static CombatRunesParam instance(Player performPlayer, Player oppoPlayer, List<Effect> effects, long combatId) {
        CombatRunesParam param = instance(performPlayer, oppoPlayer, combatId);
        param.setReceiveEffect(effects);
        return param;
    }

    public int getNextSeq() {
        seq++;
        return seq;
    }

    /**
     * 获取当前玩家的符文
     *
     * @return
     */
    public List<Integer> getPerformPlayerRunesId() {
        return performPlayer.getBuffs();
    }

    public int getMyPlayerPos() {
        return PositionService.getZhaoHuanShiPos(performPlayer.getId());
    }

    public int getOppoPlayerPos(){
        return PositionService.getZhaoHuanShiPos(oppoPlayer.getId());
    }

    /**
     * 目标卡是否是对方的卡
     * @return
     */
    public boolean isEnemyTargetCard(){
        if (this.targetCard==null){
            return false;
        }
        if (PositionService.getPlayerIdByPos(this.getTargetCard().getPos())!=performPlayer.getId()){
            return true;
        }
        return false;
    }

    /**
     * 是否是针对对方的效果
     * @return
     */
    public boolean isEffectToEnemy(){
        if (ListUtil.isEmpty(this.getReceiveEffect())){
            return false;
        }
        if (PositionService.getPlayerIdByPos(this.getReceiveEffect().get(0).getTargetPos())!=performPlayer.getId()){
            return true;
        }
        return false;
    }
}
