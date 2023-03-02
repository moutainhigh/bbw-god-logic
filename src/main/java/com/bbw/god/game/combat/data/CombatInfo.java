package com.bbw.god.game.combat.data;

import com.bbw.god.game.combat.data.param.CombatPVEParam;
import lombok.Data;

import java.io.Serializable;

/**
 * @author lwb
 * @date 2020/10/15 16:47
 */
@Data
public class CombatInfo implements Serializable {
    private static final long serialVersionUID = 2258222170462000444L;
    private long id;
    private Player p1;//玩家1
    private Player p2;//玩家2
    private Long opponent = -1L;// 对手ID；
    private String opponentName;// 对手昵称
    private Long monsterId;// 好友野怪ID
    private Long fightTaskId;// 战斗任务ID
    private Integer cityId = 0;// 城池ID 目前只有城战才有赋值
    private Integer cityLevel = null;//城池等级
    private Integer cityHv = null;//城池阶级
    private Integer yeguaiType = null;//野怪类型
    private boolean fightAgain = false;// 是否是重新挑战
    private int awardId = -1;// 野怪额外条件奖励 [1,5]种情况
    /** 战斗类型 */
    private Integer fightType;
    /** 战斗世界类型 */
    private Integer worldType;
    @Deprecated
    private boolean nightmare = false;
    private boolean gainJYD = false;
    /** 妖族id */
    private Long yaoZuId = null;

    public Player getPlayer(PlayerId playerId) {
        if (getP1().getId().equals(playerId)) {
            return p1;
        }
        return p2;
    }


    public static CombatInfo instance(Combat combat) {
        CombatInfo info = new CombatInfo();
        info.setId(combat.getId());
        info.setP1(combat.getP1());
        info.setP2(combat.getP2());
        info.setFightType(combat.getFightType().getValue());
        return info;
    }

    public void addPVEParam(CombatPVEParam param) {
        this.cityId = param.getCityBaseId();
        this.cityLevel = param.getCityLevel();
        this.cityHv = param.getCityHierarchy();
        this.yeguaiType=param.getYgType();
        this.awardId=param.getAwardkey();
        this.monsterId=param.getOpponentId();
        this.opponent = param.getOpponentId();
        this.fightTaskId = param.getFightTaskId();
        this.opponentName = param.getOppentName();
        this.fightAgain=param.isFightAgain();
        this.yaoZuId = param.getOpponentId();
    }
}
