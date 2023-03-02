package com.bbw.god.gameuser.special;

import com.bbw.common.Rst;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 特产相关接口
 *
 * @author suhq
 * @date 2018年11月5日 下午4:26:30
 */
@RestController
public class UserSpecialCtrl extends AbstractController {
    @Autowired
    private UserSpecialLogic specialLogic;

    /**
     * 获得特产在各个城市的买入价和卖出价
     *
     * @param specialId
     * @return
     */
    @GetMapping(CR.Special.LIST_SPECIAL_CITIES)
    public RDSpecialBuinessInfo listSpecialCities(int specialId) {
        LoginPlayer player = this.getUser();
        return specialLogic.getSpecialCities(player.getUid(), player.getServerId(), specialId);
    }

    /**
     * 丢弃特产
     *
     * @param specialId 特产存储的dataId
     * @return
     */
    @GetMapping(CR.Special.DISCARD)
    public RDSuccess discardSpecial(long specialId) {
        return specialLogic.discardSpecial(getUserId(), specialId);
    }

    /**
     * 口袋特产操作
     *
     * @param uid
     * @param specialId
     * @return
     */
    @GetMapping(CR.Special.SPECIAL_LOCK)
    public RDSuccess lockSpecial(Long uid, long specialId) {
        if (uid == null) {
            uid = getUserId();
        }
        return specialLogic.lockSpecial(uid, specialId);
    }

    @GetMapping(CR.Special.SPECIAL_UNLOCK)
    public RDSuccess unlockSpecial(Long uid, long specialId) {
        if (uid == null) {
            uid = getUserId();
        }
        return specialLogic.unlockSpecial(uid, specialId);
    }

    @GetMapping(CR.Special.SPECIAL_SYNTHESIS)
    public RDCommon synthesisSpecial(int materialId1, int materialId2, int targetId) {
        return specialLogic.synthesisSpecial(getUserId(), materialId1, materialId2, targetId);
    }

    @GetMapping(CR.Special.ENTER_SPECIAL_SYNTHESIS)
    public RDEnterSynthesisSpecial enterSynthesisSpecial() {
        return specialLogic.enterSynthesisSpecial(getUserId());
    }

    @GetMapping(CR.Special.GET_SPECIAL_SETTINGS)
    public RDSpecialSetting getSpecialSettings() {
        return specialLogic.getSpecialSetting(getUserId());
    }

    @GetMapping(CR.Special.UPDATE_SPECIAL_SETTINGS)
    public Rst updateSpecialSettings(CPUserSpecialSeting cpUserSpecialSeting) {
        return specialLogic.updateSpecialSetting(getUserId(), cpUserSpecialSeting);
    }
}
