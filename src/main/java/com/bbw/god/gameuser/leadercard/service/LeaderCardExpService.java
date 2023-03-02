package com.bbw.god.gameuser.leadercard.service;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.leadercard.CfgLeaderCard;
import com.bbw.god.gameuser.leadercard.LeaderCardTool;
import com.bbw.god.gameuser.leadercard.RDLeaderCardInfo;
import com.bbw.god.gameuser.leadercard.UserLeaderCard;
import com.bbw.god.gameuser.leadercard.event.EPLeaderCardAddLv;
import com.bbw.god.gameuser.leadercard.event.LeaderCardEventPublisher;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasure;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeaderCardExpService {
    @Autowired
    private LeaderCardService leaderCardService;
    @Autowired
    private GameUserService userService;
    @Autowired
    private UserTreasureService userTreasureService;

    /**
     * 加经验
     * @param uid
     * @param addExp
     */
    public RDLeaderCardInfo addExp(long uid,long addExp){
        UserLeaderCard leaderCard=leaderCardService.getUserLeaderCard(uid);
        if (leaderCard.ifFullLv()){
            throw new ExceptionForClientTip("leader.card.max.lv");
        }
        CfgLeaderCard.ExpCondition expCondition = LeaderCardTool.getMaxExpCondition();
        leaderCard.setExp(Math.min(leaderCard.getExp()+addExp,expCondition.getExp()));
        int maxLv = expCondition.getLv();
        int oldLv = leaderCard.getLv();
        //检查等级
        for (int i = leaderCard.getLv(); i <= expCondition.getLv() ; i++) {
            int newLv = i +1;
            if (leaderCard.getExp() >= LeaderCardTool.getLvNeedExp(newLv) && newLv <= maxLv){
                leaderCard.setLv(newLv);
            }else {
                break;
            }
        }
        userService.updateItem(leaderCard);
        if(leaderCard.getLv() > oldLv){
            //分身升级事件
            BaseEventParam bep = new BaseEventParam(uid, WayEnum.LEADER_CARD_UPDATE, new RDCommon());
            EPLeaderCardAddLv ep = EPLeaderCardAddLv.instance(bep,leaderCard.getLv());
            LeaderCardEventPublisher.pubLeaderCardAddLvEvent(ep);
        }
        return RDLeaderCardInfo.getInstance(leaderCard);
    }

    /**
     * 使用经验丹
     * @param uid
     * @param useAll  是否全部使用
     * @return
     */
    public void useJinYanDan(long uid, boolean useAll, RDUseMapTreasure rd){
        UserLeaderCard leaderCard=leaderCardService.getUserLeaderCard(uid);
        if (leaderCard.ifFullLv()){
            throw new ExceptionForClientTip("leader.card.max.lv");
        }
        UserTreasure treasure = userTreasureService.getUserTreasure(uid, TreasureEnum.FEN_SHEN_JYD.getValue());
        if (treasure==null || treasure.getOwnNum()<=0){
            throw new ExceptionForClientTip("treasure.not.exist", TreasureEnum.FEN_SHEN_JYD.getName());
        }
        int useNum=useAll?treasure.getOwnNum():1;
        int successNum=0;
        int addExp=1000;
        CfgLeaderCard.ExpCondition expCondition = LeaderCardTool.getMaxExpCondition();
        long maxExp=expCondition.getExp();
        for (int i = 0; i < useNum; i++) {
            leaderCard.setExp(Math.min(leaderCard.getExp()+addExp,maxExp));
            if (leaderCard.getExp()>=maxExp){
                break;
            }
            successNum++;
        }
        int maxLv = expCondition.getLv();
        int oldLv = leaderCard.getLv();
        //检查等级
        for (int i = leaderCard.getLv(); i <= expCondition.getLv() ; i++) {
            int newLv = i +1;
            if (leaderCard.getExp() >= LeaderCardTool.getLvNeedExp(newLv) && newLv <= maxLv){
                leaderCard.setLv(newLv);
            }else {
                break;
            }
        }
        TreasureEventPublisher.pubTDeductEvent(uid,TreasureEnum.FEN_SHEN_JYD.getValue(),successNum, WayEnum.LEADER_CARD_LV,rd);
        userService.updateItem(leaderCard);
        if(leaderCard.getLv() > oldLv){
            //分身升级事件
            BaseEventParam bep = new BaseEventParam(uid, WayEnum.LEADER_CARD_UPDATE, new RDCommon());
            EPLeaderCardAddLv ep = EPLeaderCardAddLv.instance(bep,leaderCard.getLv());
            LeaderCardEventPublisher.pubLeaderCardAddLvEvent(ep);
        }
        rd.setLeaderCardInfo(RDLeaderCardInfo.getInstance(leaderCard));
        rd.setLeaderCardFreePoint(leaderCard.settleFreePoint());
    }

    /**
     * 使用超级经验丹:直接升到5级或者 +10W经验
     * @param uid
     */
    public void useJinYanDanPlus(long uid,RDUseMapTreasure rd){
        UserLeaderCard leaderCard=leaderCardService.getUserLeaderCard(uid);
        if (leaderCard.ifFullLv()){
            throw new ExceptionForClientTip("leader.card.max.lv");
        }
        TreasureChecker.checkIsEnough(TreasureEnum.CHAO_JI_JYD.getValue(),1,uid);
        TreasureEventPublisher.pubTDeductEvent(uid,TreasureEnum.CHAO_JI_JYD.getValue(),1,WayEnum.LEADER_CARD_LV,rd);
        if (leaderCard.getLv()<5){
            leaderCard.setLv(5);
            leaderCard.setExp(LeaderCardTool.getLvNeedExp(5));
            userService.updateItem(leaderCard);
            rd.setLeaderCardInfo(RDLeaderCardInfo.getInstance(leaderCard));
            rd.setLeaderCardFreePoint(leaderCard.settleFreePoint());
            return;
        }
        rd.setLeaderCardInfo(addExp(uid,10*10000));
        leaderCard=leaderCardService.getUserLeaderCard(uid);
        rd.setLeaderCardFreePoint(leaderCard.settleFreePoint());
    }
}
