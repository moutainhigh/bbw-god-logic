package com.bbw.god.rechargeactivities.processor;

import com.bbw.common.DateUtil;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.pay.UserPayInfo;
import com.bbw.god.gameuser.pay.UserPayInfoService;
import com.bbw.god.gameuser.privilege.CfgPrivilege;
import com.bbw.god.gameuser.privilege.PrivilegeService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rechargeactivities.RDRechargeActivity;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lwb
 * @date 2020/7/2 11:45
 */
@Service
public class LingYinPackProcessor extends AbstractRechargeActivityProcessor {
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private UserPayInfoService userPayInfoService;

    @Override
    public RechargeActivityEnum getParent() {
        return RechargeActivityEnum.CARD_PACK;
    }

    @Override
    public RechargeActivityItemEnum getCurrentEnum() {
        return RechargeActivityItemEnum.LING_YIN;
    }

    @Override
    public boolean isShow(long uid) {
        return true;
    }

    /**
     * 获取展示的等级所需值
     *
     * @return
     */
    @Override
    protected int getShowNeedLevel() {
        return 7;
    }

    @Override
    public int getCanGainAwardNum(long uid) {
        AwardStatus status = privilegeService.getTianlingStatus(gameUserService.getGameUser(uid));
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
        AwardStatus status = privilegeService.getTianlingStatus(gu);
        rd.setStatus(-1);
        if (AwardStatus.ENABLE_AWARD.getValue() == status.getValue()) {
            rd.setStatus(1);
        }
        return rd;
    }

    @Override
    public RDRechargeActivity gainAwards(long uid, int pid) {
        GameUser gu = gameUserService.getGameUser(uid);
        AwardStatus status = privilegeService.getTianlingStatus(gu);
        if (AwardStatus.UNAWARD.equals(status)) {
            throw new ExceptionForClientTip("privilege.tianling.unaward");
        }
        if (AwardStatus.AWARDED.equals(status)) {
            throw new ExceptionForClientTip("privilege.tianling.awarded");
        }
        gu.getStatus().setLastTianlingAwardTime(DateUtil.now());
        gu.updateStatus();
        CfgPrivilege cfgPrivilege = Cfg.I.getUniqueConfig(CfgPrivilege.class);
        List<Award> tianlingBag = cfgPrivilege.getTianlingBag();
        //随机五星法宝
        List<Integer> fiveStarId = TreasureTool.getTreasureByStar(5).stream().map(CfgTreasureEntity::getId).collect(Collectors.toList());
        RDRechargeActivity rd = new RDRechargeActivity();
        TreasureEventPublisher.pubTAddEvent(uid,PowerRandom.getRandomFromList(fiveStarId),1, WayEnum.GET_TIANLING_BAG,rd);
        this.awardService.fetchAward(gu.getId(), tianlingBag,  WayEnum.GET_TIANLING_BAG, "", rd);
        return rd;
    }
}
