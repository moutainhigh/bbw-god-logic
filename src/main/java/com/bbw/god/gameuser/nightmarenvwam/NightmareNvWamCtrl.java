package com.bbw.god.gameuser.nightmarenvwam;

import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.nightmarenvwam.godheadwarehouse.GodHeadWareHouseLogic;
import com.bbw.god.gameuser.nightmarenvwam.godheadwarehouse.RDGodHeadWarehouse;
import com.bbw.god.gameuser.nightmarenvwam.godsaltar.GodsAltarLogic;
import com.bbw.god.gameuser.nightmarenvwam.godsaltar.RDGodsAltar;
import com.bbw.god.gameuser.nightmarenvwam.pinchpeople.PinchPeopleLogic;
import com.bbw.god.gameuser.nightmarenvwam.pinchpeople.RDPinchPeopleInfo;
import com.bbw.god.gameuser.nightmarenvwam.pinchpeople.RDPinchPeople;
import com.bbw.god.rd.RDCommon;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 梦魇女娲庙相关接口
 *
 * @author fzj
 * @date 2022/5/4 11:15
 */
@Api(tags = {"梦魇女娲庙接口"})
@RestController
public class NightmareNvWamCtrl extends AbstractController {
    @Autowired
    private PinchPeopleLogic kneadSoilLogic;
    @Autowired
    private GodsAltarLogic godsAltarLogic;
    @Autowired
    private GodHeadWareHouseLogic godHeadWareHouseLogic;

    @ApiOperation(value = "开启梦魇女娲庙")
    @GetMapping(CR.NightmareNvWM.IS_ACTIVE_NIGHTMARE_NWM)
    public RDIsActiveNightmareNWM isActive() {
        return kneadSoilLogic.isActiveNightmareNWM(getUserId());
    }

    @ApiOperation(value = "进入捏人总界面")
    @GetMapping(CR.NightmareNvWM.ENTER_KNEAD_SOIL)
    public RDPinchPeopleInfo enterKneadSoil() {
        return kneadSoilLogic.enterKneadSoil(getUserId());
    }

    @ApiOperation(value = "捏人")
    @GetMapping(CR.NightmareNvWM.PINCH_PEOPLE)
    public RDPinchPeople pinchPeople(int cursorId) {
        return kneadSoilLogic.pinchPeople(getUserId(), cursorId);
    }

    @ApiOperation(value = "获得累计分数奖励")
    @GetMapping(CR.NightmareNvWM.SEND_TOTAL_SCORE_AWARD)
    public RDCommon sendTotalScoreAward() {
        return kneadSoilLogic.sendTotalScoreAward(getUserId());
    }

    @ApiOperation(value = "获得卡牌相关道具")
    @GetMapping(CR.NightmareNvWM.CARD_RELATE_TREASURE)
    public RDGodHeadWarehouse getCardRelateTreasure(int cardId) {
        return godHeadWareHouseLogic.getCardRelateTreasure(getUserId(), cardId);
    }

    @ApiOperation(value = "进入封神祭坛")
    @GetMapping(CR.NightmareNvWM.ENTER_GODS_ALTAR)
    public RDGodsAltar enterGodsAltar() {
        return godsAltarLogic.enterGodsAltar(getUserId());
    }

    @ApiOperation(value = "消耗令牌")
    @GetMapping(CR.NightmareNvWM.CONSUME_BRAND)
    public RDCommon consumeBrand(int treasureId) {
        return godsAltarLogic.consumeBrand(getUserId(), treasureId);
    }
}
