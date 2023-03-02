package com.bbw.god.game.cjdf2;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.db.entity.InsGamePvpDetailEntity;
import com.bbw.god.detail.async.PvpDetailAsyncHandler;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.fight.fsfight.CPFsFightSubmit;
import com.bbw.god.game.CR;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 神仙大会接口
 *
 * @author suhq
 * @date 2019-06-21 09:33:11
 */
@RestController
public class Cjdf2Ctrl {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private PvpDetailAsyncHandler pvpDetailAsyncHandler;

    @GetMapping(CR.FsFight.SUBMIT_CDJF2_FIGHT_RESULT)
    public RDSuccess submitFightResult(CPFsFightSubmit param) {
        return doSubmitFightResult(param);
    }


    private RDSuccess doSubmitFightResult(CPFsFightSubmit param) {
        long winner = param.getWinner();
        long loser = param.getLoser();
        CfgServerEntity server = gameUserService.getOriServer(param.getWinner());
        InsGamePvpDetailEntity detailData = new InsGamePvpDetailEntity();
        detailData.setId(ID.INSTANCE.nextId());
        detailData.setServerGroup(server.getGroupId());
        detailData.setFightType(FightTypeEnum.DFDJ.getValue());
        detailData.setFightTypeName(FightTypeEnum.DFDJ.getName());
        detailData.setRoomId(param.getRoomId());
        detailData.setUser1(winner);
        detailData.setUser2(loser);
        detailData.setWinner(winner);
        detailData.setFightTime(DateUtil.toDateTimeLong());
        detailData.setDataJson("{}");
        pvpDetailAsyncHandler.log(detailData);
        return new RDSuccess();
    }
}
