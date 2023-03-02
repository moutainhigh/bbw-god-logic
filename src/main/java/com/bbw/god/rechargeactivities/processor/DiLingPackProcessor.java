package com.bbw.god.rechargeactivities.processor;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.pay.UserPayInfo;
import com.bbw.god.gameuser.pay.UserPayInfoService;
import com.bbw.god.gameuser.privilege.CfgPrivilege;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.rechargeactivities.RDRechargeActivity;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 地灵印礼包
 *
 * @author fzj
 * @date 2021/12/7 20:00
 */
@Service
public class DiLingPackProcessor extends AbstractRechargeActivityProcessor {
    @Autowired
    PrivilegeService privilegeService;
    @Autowired
    UserPayInfoService userPayInfoService;
    @Override
    public RechargeActivityEnum getParent() {
        return RechargeActivityEnum.CARD_PACK;
    }

    @Override
    public RechargeActivityItemEnum getCurrentEnum() {
        return RechargeActivityItemEnum.DI_LING_YIN;
    }

    @Override
    protected int getShowNeedLevel() {
        return 7;
    }

    @Override
    public boolean isShow(long uid) {
        return false;
    }

    @Override
    public int getCanGainAwardNum(long uid) {
        AwardStatus status = privilegeService.getDiLingStatus(gameUserService.getGameUser(uid));
        if (AwardStatus.ENABLE_AWARD.getValue() == status.getValue()) {
            return 1;
        }
        return 0;
    }

    @Override
    public RDRechargeActivity listAwards(long uid) {
        RDRechargeActivity rd = new RDRechargeActivity();
        UserPayInfo userPayInfo = userPayInfoService.getUserPayInfo(uid);
        if (userPayInfo.getYkEndTime() != null && DateUtil.millisecondsInterval(userPayInfo.getYkEndTime(), new Date()) > 0) {
            rd.setYkStatus(1);
        } else {
            rd.setYkStatus(-1);
        }
        if (userPayInfo.getJkEndTime() != null && DateUtil.millisecondsInterval(userPayInfo.getJkEndTime(), new Date()) > 0) {
            rd.setJkStatus(1);
        } else {
            rd.setJkStatus(-1);
        }
        if (userPayInfo.getEndFightBuyTime() != null) {
            rd.setSzkStatus(1);
        } else {
            rd.setSzkStatus(-1);
        }
        GameUser gu = gameUserService.getGameUser(uid);
        AwardStatus status = privilegeService.getDiLingStatus(gu);
        rd.setDiLingStatus(-1);
        if (AwardStatus.ENABLE_AWARD.getValue() == status.getValue()) {
            rd.setDiLingStatus(1);
        }
        return rd;
    }

    @Override
    public RDRechargeActivity gainAwards(long uid, int pid) {
        GameUser gu = this.gameUserService.getGameUser(uid);
        AwardStatus status = privilegeService.getDiLingStatus(gu);
        if (status == AwardStatus.UNAWARD) {
            throw new ExceptionForClientTip("privilege.diling.unaward");
        }
        if (status == AwardStatus.AWARDED) {
            throw new ExceptionForClientTip("privilege.diling.awarded");
        }
        gu.getStatus().setLastDilingAwardTime(DateUtil.now());
        gu.updateStatus();
        CfgPrivilege cfgPrivilege = Cfg.I.getUniqueConfig(CfgPrivilege.class);
        RDRechargeActivity rd = new RDRechargeActivity();
        this.awardService.fetchAward(gu.getId(), cfgPrivilege.getDilingBag(), WayEnum.DILING_BAG, "", rd);
        return rd;
    }
}
