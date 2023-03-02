package com.bbw.god.rechargeactivities.wartoken.event;

import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.chanjie.event.ChanjieFightEvent;
import com.bbw.god.game.chanjie.event.ChanjieLdfsInvitationEvent;
import com.bbw.god.game.chanjie.event.EPChanjieFight;
import com.bbw.god.game.combat.event.*;
import com.bbw.god.game.wanxianzhen.event.WanXianInto64Event;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rechargeactivities.wartoken.UserWarToken;
import com.bbw.god.rechargeactivities.wartoken.UserWarTokenTask;
import com.bbw.god.rechargeactivities.wartoken.WarTokenLogic;
import com.bbw.god.rechargeactivities.wartoken.WarTokenTool;
import com.bbw.god.server.maou.alonemaou.event.AloneMaouPassEvent;
import com.bbw.god.server.maou.bossmaou.event.BossMaouAttackEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-04
 */
@Async
@Component
public class WarTokenListener {
    @Autowired
    private WarTokenLogic warTokenLogic;
    @Autowired
    private GameUserService gameUserService;

    @EventListener
    public void WarTokenAddExp(WarTokenAddExpEvent event){
        Long guId = event.getEP().getGuId();
        if (!warTokenLogic.openWarToken(guId)){
            return;
        }
        synchronized (guId){
            warTokenLogic.addWarTokenExp(guId,event.getEP().getAddVal(),event.getEP().isAddWeekExp());
        }
    }

    /**
     * 战斗初始化
     * #每参与1场神仙大会
     * --- {id: 200000,type: 20,exp: 200,need: 1}
     * #每参与1场斗法封神台
     * --- {id: 200010,type: 20,exp: 200,need: 1}
     * #每参与1场诛仙阵
     * --- {id: 200030,type: 20,exp: 200,need: 1}
     * @param event
     */
    @EventListener
    public void FightInit(CombatInitiateEvent event){
        long uid=event.getEP().getGuId();
        FightTypeEnum value = FightTypeEnum.fromValue(event.getEP().getFightType());
        int taskId=0;
        switch (value){
            case SXDH: taskId=200000;break;
            case FST: taskId=200010;break;
            case ZXZ: taskId=200030;break;
            default:
                return;
        }
        warTokenLogic.addTaskProgress(uid,taskId,1);
    }

    /**
     *
     * #每获得1场斗法封神台胜利
     * --- {id: 300000,type: 30,exp: 200,need: 1}
     * #斗法封神台获得20场胜利
     * --- {id: 400050,type: 40,exp: 2000,need: 20}
     * #每获得1场神仙大会胜利
     * --- {id: 300010,type: 30,exp: 200,need: 1}
     * #每击败1个诛仙阵对手
     * --- {id: 300020,type: 30,exp: 200,need: 1}
     * #每击败2个友怪
     * --- {id: 300030,type: 30,exp: 200,need: 2}
     *
     * @param winEvent
     */
    @EventListener
    public void FightWin(CombatFightWinEvent winEvent){
        EPFightEnd fightEnd = winEvent.getEP();
        Long uid = fightEnd.getGuId();
        FightTypeEnum value = fightEnd.getFightType();
        switch (value){
            case FST:
                warTokenLogic.addTaskProgress(uid,300000,1);
                warTokenLogic.addTaskProgress(uid,400050,1);
                break;
            case SXDH:
                warTokenLogic.addTaskProgress(uid,300010,1);
                break;
            case HELP_YG:
                warTokenLogic.addTaskProgress(uid,300030,1);
                break;
            case ZXZ:
                warTokenLogic.addTaskProgress(uid,300020,1);
                break;
            default:
                return;
        }
    }

    /**
     *
     *  #每获得1场阐截斗法胜利
     * --- {id: 300040,type: 30,exp: 200,need: 1}
     * @param event
     */
    @EventListener
    public void CJDFWin(ChanjieFightEvent event){
        EPChanjieFight ep = event.getEP();
        Long uid = ep.getGuId();
        warTokenLogic.addTaskProgress(uid,300040,1);
    }


    /**
     * #每累计使用5个战斗法宝
     * --- {id: 300050,type: 30,exp: 100,need: 5}
     * #神仙大会每击杀10张卡牌
     * --- {id: 300060,type: 30,exp: 100,need: 10}
     * #诛仙阵每击杀10张卡牌
     * --- {id: 300070,type: 30,exp: 100,need: 10}
     * #阐截斗法每击杀10张卡牌
     * --- {id: 300080,type: 30,exp: 100,need: 10}
     * @param event
     */
    @EventListener
    public void combatResultDataEvent(CombatResultDataEvent event){
        EPCombatResultData ep = event.getEP();
        long uid=ep.getGuId();
        if (ep.getUseTreasureNum()>0){
            warTokenLogic.addTaskProgress(uid,300050,ep.getUseTreasureNum());
        }
        switch (ep.getFightType()){
            case SXDH:
                warTokenLogic.addTaskProgress(uid,300060,ep.getKillCardsNum());
                break;
            case ZXZ:
                warTokenLogic.addTaskProgress(uid,300070,ep.getKillCardsNum());
                break;
            case CJDF:
                warTokenLogic.addTaskProgress(uid,300080,ep.getKillCardsNum());
                break;
            default:

        }
    }
    /**
     * #每通关1层独战魔王
     * --- {id: 200020,type: 20,exp: 200,need: 1}
     */
    @EventListener
    public void passAloneMaou(AloneMaouPassEvent event){
        warTokenLogic.addTaskProgress(event.getEP().getGuId(),200020,1);
    }
     /**
     * #魔王降临每攻击15次魔王
     * --- {id: 300090,type: 30,exp: 100,need: 15}
     * #魔王降临累计对魔王造成80万伤害
     * --- {id: 400030,type: 40,exp: 2000,need: 800000}
     */
    @EventListener
    public void attackBoosMaou(BossMaouAttackEvent event){
        warTokenLogic.addTaskProgress(event.getEP().getGuId(),300090,1);
        if (event.getEP().getBlood()>0){
            warTokenLogic.addTaskProgress(event.getEP().getGuId(),400030,event.getEP().getBlood());
        }
    }
    /**
     * #神仙大会获得100点积分
     * --- {id: 400020,type: 40,exp: 2000,need: 100}
     * @param event
     */
    @EventListener
    public void addSxdhPoint(CombatSxdhAddPointEvent event){
        warTokenLogic.addTaskProgress(event.getEP().getGuId(),400020,event.getEP().getPoint());
    }
     /**
     * #阐截斗法获得乱斗封神参赛资格
     * --- {id: 400040,type: 40,exp: 2000,need: 1}
     */
     @EventListener
     public void ldfsInvitation(ChanjieLdfsInvitationEvent event){
         warTokenLogic.addTaskProgress(event.getEP().getGuId(),400040,1);
     }



    /**
     * #万仙阵进入64强
     * --- {id: 400010,type: 40,exp: 2000,need: 1}
     * @param event
     */
    @EventListener
    public void wanXianInto64(WanXianInto64Event event){
        warTokenLogic.addTaskProgress(event.getEP().getGuId(),400010,1);
    }

    @EventListener
    public void warTokenActive(WarTokenActiveEvent event){
        long uid=event.getEP().getGuId();
        UserWarToken warToken = warTokenLogic.getOrCreateUserWarToken(uid);
        List<UserWarTokenTask> tasks = warTokenLogic.getUserTasks(warToken);
        Optional<UserWarTokenTask> optional = tasks.stream().filter(p -> WarTokenTool.LOGIN_TASK_IDS.contains(p.getBaseId())).findFirst();
        if (optional.isPresent()){
            UserWarTokenTask task = optional.get();
            if (task.getBaseId().equals(WarTokenTool.LOGIN_TASK_IDS.get(0))){
                if (task.getGainTimes()==0){
                    task.setBaseId(WarTokenTool.LOGIN_TASK_IDS.get(2));
                }else {
                    task.setGainTimes(0);
                    task.setBaseId(WarTokenTool.LOGIN_TASK_IDS.get(1));
                }
            }
            gameUserService.updateItem(task);
        }
        if (warToken.getSupToken()==2){
            WarTokenEventPublisher.pubAddExpEvent(uid,60000,false);
        }
    }
}
