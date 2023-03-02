package com.bbw.god.gameuser.businessgang;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.businessgang.rd.RDBusinessGangInfo;
import com.bbw.god.gameuser.businessgang.rd.RDEnterBusinessGang;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商帮请求接口
 *
 * @author fzj
 * @date 2022/1/13 15:08
 */
@Api(tags = {"商帮接口"})
@RestController
public class BusinessGangCtrl extends AbstractController {
    @Autowired
    BusinessGangLogic businessGangLogic;

    @ApiOperation(value = "进入商帮")
    @GetMapping(CR.BusinessGang.ENTER_BUSINESS_GANG)
    public RDEnterBusinessGang enter() {
        return businessGangLogic.enter(getUserId());
    }

    @ApiOperation(value = "加入商帮")
    @GetMapping(CR.BusinessGang.JOIN_BUSINESS_GANG)
    public RDEnterBusinessGang joinBusinessGang(Integer joinBusinessGangId) {
        return businessGangLogic.joinBusinessGang(getUserId(), joinBusinessGangId);
    }

    @ApiOperation(value = "退出商帮")
    @GetMapping(CR.BusinessGang.QUIT_BUSINESS_GANG)
    public RDSuccess quitBusinessGang() {
        return businessGangLogic.quitBusinessGang(getUserId());
    }

    @ApiOperation(value = "增送礼物")
    @GetMapping(CR.BusinessGang.SEND_GIFTS)
    public RDCommon sendGifts(Integer npcId, String giftsInfo) {
        return businessGangLogic.sendGifts(getUserId(), npcId, giftsInfo);
    }

    @ApiOperation(value = "拜访")
    @GetMapping(CR.BusinessGang.VISIT_BUSINESS_GANG)
    public RDEnterBusinessGang visitBusinessGang(int visitGangId) {
        return businessGangLogic.visitBusinessGang(getUserId(), visitGangId);
    }

    @ApiOperation(value = "刷新任务")
    @GetMapping(CR.BusinessGang.REFRESH_TASK)
    public RDCommon refreshTask(long dataId, int type) {
        return businessGangLogic.refreshTask(getUserId(), dataId, type);
    }

    @ApiOperation(value = "获取所有商帮掌舵人信息")
    @GetMapping(CR.BusinessGang.GAIN_BUSINESS_NPC_INFO)
    public RDBusinessGangInfo gainBusinessGangNpcInfo() {
        return businessGangLogic.gainBusinessGangNpcInfo(getUserId());
    }

    @ApiOperation(value = "兑换可领取奖励次数")
    @GetMapping(CR.BusinessGang.EXCHANGE_AVAILABLE_TIMES)
    public RDCommon exchangeAvailableTimes() {
        return businessGangLogic.exchangeAvailableTimes(getUserId());
    }
}
