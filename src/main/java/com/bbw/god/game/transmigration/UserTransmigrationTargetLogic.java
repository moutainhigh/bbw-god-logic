package com.bbw.god.game.transmigration;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.transmigration.cfg.CfgTransmigrationTarget;
import com.bbw.god.game.transmigration.cfg.TransmigrationTool;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.game.transmigration.entity.UserTransmigration;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.item.RDAchievableItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 轮回目标服务类
 *
 * @author: suhq
 * @date: 2021/10/18 11:43 上午
 */
@Service
public class UserTransmigrationTargetLogic {
    @Autowired
    private UserTransmigrationService userTransmigrationService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GameTransmigrationService gameTransmigrationService;


    /**
     * 获取目标及状态
     *
     * @param uid
     * @return
     */
    public List<RDAchievableItem> getTargets(long uid) {
        UserTransmigration ut = userTransmigrationService.getTransmigration(uid);
        List<CfgTransmigrationTarget> scoreAwards = TransmigrationTool.getTargets();
        List<RDAchievableItem> rdAwards = new ArrayList<>();
        for (CfgTransmigrationTarget scoreAward : scoreAwards) {
            RDAchievableItem rdItem = new RDAchievableItem();
            rdItem.setId(scoreAward.getId());
            rdItem.setTotalProgress(scoreAward.getNeedScore());
            rdItem.setAwards(scoreAward.getAwards());
            AwardStatus status = getStatus(ut, scoreAward);
            rdItem.setStatus(status.getValue());
            rdAwards.add(rdItem);
        }
        return rdAwards;
    }

    /**
     * 领取目标奖励
     *
     * @param uid
     * @param targetId
     * @return
     */
    public RDCommon gainTargetAwards(long uid, int targetId) {
        UserTransmigration ut = userTransmigrationService.getTransmigration(uid);
        CfgTransmigrationTarget targetAward = TransmigrationTool.getTarget(targetId);
        if (null == targetAward) {
            throw ExceptionForClientTip.fromi18nKey("transmigration.target.not.exist");
        }
        AwardStatus status = getStatus(ut, targetAward);
        if (status == AwardStatus.UNAWARD) {
            throw new ExceptionForClientTip("transmigration.target.unaward");
        }
        if (status == AwardStatus.AWARDED) {
            throw new ExceptionForClientTip("transmigration.target.awarded");
        }
        RDCommon rd = new RDCommon();
        awardService.fetchAward(uid, targetAward.getAwards(), WayEnum.TRANSMIGRATION_TARGET_AWARD, "", rd);
        ut.addAwardedTarget(targetId);
        gameUserService.updateItem(ut);
        return rd;
    }

    /**
     * 获取目标奖励状态
     *
     * @param userTransmigration
     * @param scoreAward
     * @return
     */
    private AwardStatus getStatus(UserTransmigration userTransmigration, CfgTransmigrationTarget scoreAward) {
        AwardStatus status = AwardStatus.UNAWARD;
        if (scoreAward.getNeedScore() <= userTransmigration.gainTotalScore()) {
            status = AwardStatus.ENABLE_AWARD;
        }
        if (userTransmigration.getAwardedTargets().contains(scoreAward.getId())) {
            status = AwardStatus.AWARDED;
        }
        return status;
    }

    /**
     * 获取可领取的次数
     *
     * @param uid
     * @return
     */
    public int getEnableAwardNum(long uid) {
        int sgId = gameUserService.getActiveGid(uid);
        GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(sgId);
        if (null == curTransmigration) {
            return 0;
        }
        UserTransmigration ut = userTransmigrationService.getTransmigration(uid);
        List<CfgTransmigrationTarget> scoreAwards = TransmigrationTool.getTargets();
        int enableAwardNum = 0;
        for (CfgTransmigrationTarget scoreAward : scoreAwards) {
            AwardStatus status = getStatus(ut, scoreAward);
            if (status == AwardStatus.ENABLE_AWARD) {
                enableAwardNum++;
            }
        }
        return enableAwardNum;
    }

}
