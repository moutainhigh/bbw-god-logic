package com.bbw.god.game.combat.data.param;

import com.bbw.common.StrUtil;
import com.bbw.god.fight.RDFightsInfo;
import lombok.Data;

/**
 *
 * PVE战斗特殊参数
 * @author lwb
 * @date 2020/10/16 16:00
 */
@Data
public class CombatPVEParam {
    private Integer cityBaseId = null;
    private Integer cityLevel = null;//城市等级 攻城时需要用来编排王者卡位置
    private Integer cityHierarchy = null;
    private Integer ygType = null;// 野怪类型，参考YeGuaiEnum
    private Integer yeDEventType = null;// 野地事件类型，参考YdEventEnum
    private int fightType;
    private Long opponentId;// 好友野怪ID
    private Long fightTaskId;// 战斗任务ID
    private Integer awardkey = -1;//打野怪宝箱奖励（非打野怪默认-1）
    private String oppentName="";
    private CPlayerInitParam aiPlayer;
    private double cardDisparityHp=0;
    private double cardDisparityAtk=0;
    private boolean fightAgain=false;


    public static CombatPVEParam init(RDFightsInfo info,int fightType){
        CombatPVEParam param=new CombatPVEParam();
        param.setCityBaseId(info.getCityBaseId());
        param.setCityLevel(info.getCityLevel());
        param.setCityHierarchy(info.getCityHierarchy());
        param.setYgType(info.getYgType());
        param.setYeDEventType(info.getYeDEventType());
        param.setFightType(fightType);
        param.setOpponentId(info.getOpponentId());
        param.setAwardkey(info.getAwardkey());
        param.setOppentName(info.getNickname());
        return param;
    }

    public void setAiPlayer(CPlayerInitParam aiPlayer) {
        this.aiPlayer = aiPlayer;
        if (StrUtil.isBlank(oppentName)){
            this.oppentName=aiPlayer.getNickname();
        }
        if (this.opponentId==null){
            this.opponentId=aiPlayer.getUid();
        }
    }
}
