package com.bbw.god.game.zxz.controller;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.game.zxz.rd.RdUserCardGroup;
import com.bbw.god.game.zxz.rd.foursaints.*;
import com.bbw.god.game.zxz.service.foursaints.ZxzFourSaintsLogic;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 诛仙阵-四圣挑战 controller
 * @author: hzf
 * @create: 2022-12-26 16:51
 **/
@RestController
public class ZxzFourSaintsController extends AbstractController {

    @Autowired
    private ZxzFourSaintsLogic zxzFourSaintsLogic;


    /**
     * 进入四圣挑战
     * @return
     */
    @GetMapping(CR.Zxz.ENTER_FOUR_SAINTS)
    public RdUserZxzFourSaints enterFourSaints(){
        return zxzFourSaintsLogic.enterFourSaints(getUserId());
    }

    /**
     * 进入四圣区域
     * @param challengeType
     * @return
     */
    @GetMapping(CR.Zxz.ENTER_FOUR_SAINTS_REGION)
    public RdUserZxzFourSaintsRegion enterFourSaintsRegion(Integer challengeType){
        return zxzFourSaintsLogic.enterFourSaintsRegion(getUserId(),challengeType);

    }

    /**
     * 进入四圣挑战战斗前
     * @param challengeType
     * @return
     */
    @GetMapping(CR.Zxz.ENTER_FOUR_SAINTS_CHALLENGE)
    public RdUserZxzFourSaintsDefender enterFourSaintsChallenge(Integer challengeType){
        return zxzFourSaintsLogic.enterFourSaintsChallenge(getUserId(),challengeType);
    }



    /**
     * 四圣挑战：编辑卡组
     * @param cardIds
     * @param challengeType
     * @return
     */
    @GetMapping(CR.Zxz.EDIT_FOUR_SAINTS_CARD_GROUP)
    public RDSuccess editFourSaintsCardGroup(String cardIds,Integer challengeType){
        return zxzFourSaintsLogic.editFourSaintsCardGroup(getUserId(),cardIds, challengeType);
    }

    /**
     * 四圣挑战编辑符册
     * @param challengeType
     * @param fuCeDataId
     * @return
     */
    @GetMapping(CR.Zxz.SET_FOUR_SAINTS_FU_CE)
    public RDSuccess setFourSaintsFuCe(Integer challengeType,long fuCeDataId){
        return zxzFourSaintsLogic.setFourSaintsFuCe(getUserId(),challengeType,fuCeDataId);
    }

    /**
     * 四圣挑战查看卡组
     * @param challengeType
     * @return
     */
    @GetMapping(CR.Zxz.GET_FOUR_SAINTS_CARD_GROUP)
    public RdUserCardGroup getFourSaintsCardGroup(Integer challengeType){
        return zxzFourSaintsLogic.getFourSaintsCardGroup(getUserId(),challengeType);
    }

    /**
     * 查看词条
     * @param challengeType
     * @return
     */
    @GetMapping(CR.Zxz.GET_FOUR_SAINTS_ENTRY)
    public RdZxzFourSaintsEntry getFourSaintsEntry(Integer challengeType){
        return zxzFourSaintsLogic.getFourSaintsEntry(challengeType);

    }

    /**
     * 四圣查看敌方配置
     * @param challengeType
     * @return
     */
    @GetMapping(CR.Zxz.GET_FOUR_SAINTS_ENEMY_REGION)
    public RdFourSaintsEnemyRegion getFourSaintsEnemyRegion(Integer challengeType){
        return zxzFourSaintsLogic.getFourSaintsEnemyRegion(challengeType);

    }

    /**
     * 四圣挑战：开宝箱
     * @param challengeType
     * @return
     */
    @GetMapping(CR.Zxz.OPEN_FOUR_SAINTS_BOX)
    public RDCommon openFourSaintsBox(Integer challengeType){
        return zxzFourSaintsLogic.openFourSaintsBox(getUserId(),challengeType);
    }

}
