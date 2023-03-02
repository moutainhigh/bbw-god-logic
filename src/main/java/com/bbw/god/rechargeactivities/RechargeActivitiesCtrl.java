package com.bbw.god.rechargeactivities;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.game.CR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 奇珍入口
 *
 * @author lwb
 * @date 2020/7/1 15:40
 */
@RestController
public class RechargeActivitiesCtrl extends AbstractController {
    @Autowired
    private RechargeActivitiesLogic rechargeActivitiesLogic;

    @RequestMapping(CR.RechargeActivities.LIST)
    public RDRechargeActivity list(Integer parentType, Integer itemType) {
        if (parentType != null) {
            return rechargeActivitiesLogic.listActivities(getUserId(), parentType);
        } else if (itemType != null) {
            return rechargeActivitiesLogic.listAwards(getUserId(), itemType);
        }
        return rechargeActivitiesLogic.listParentActivities(getUserId());
    }

    @RequestMapping(CR.RechargeActivities.GAIN_AWARD)
    public RDRechargeActivity gainAward(Integer itemType, Integer pId) {
        if (itemType == null) {
            throw new ExceptionForClientTip("rechargeActivity.not.param", itemType);
        }
        if (pId == null && itemType !=RechargeActivityItemEnum.GOLD_GIFT_PACK.getType()&& itemType !=RechargeActivityItemEnum.WAR_TOKEN_TASK.getType()) {
            throw new ExceptionForClientTip("rechargeActivity.not.param", pId);
        }
        return rechargeActivitiesLogic.gainAwards(getUserId(), itemType, pId);
    }

    @RequestMapping(CR.RechargeActivities.BUY_AWARD)
    public RDRechargeActivity buyAward(Integer itemType, Integer mallId) {
        if (itemType == null) {
            throw new ExceptionForClientTip("rechargeActivity.not.param", itemType);
        }
        if (mallId == null) {
            throw new ExceptionForClientTip("rechargeActivity.not.param", mallId);
        }
        return rechargeActivitiesLogic.buyAwards(getUserId(), itemType, mallId);
    }

    @RequestMapping(CR.RechargeActivities.PICK_AWARD)
    public RDRechargeActivity pickAward(Integer itemType, Integer mallId, String awardIds) {
        if (itemType == null) {
            throw new ExceptionForClientTip("rechargeActivity.not.param", itemType);
        }
        if (mallId == null) {
            throw new ExceptionForClientTip("rechargeActivity.not.param", mallId);
        }
        return rechargeActivitiesLogic.pickAwards(getUserId(), itemType, mallId, awardIds);
    }


    @RequestMapping(CR.RechargeActivities.GAIN_ALL_AVAILABLE_AWARDS)
    public RDRechargeActivity gainAllAvailableAwards(int itemType) {
        return rechargeActivitiesLogic.gainAllAvailableAwards(getUserId(),itemType);
    }

    @RequestMapping(CR.RechargeActivities.REFRESH_ITEM)
    public RDRechargeActivity refreshItem(int itemType, @RequestParam(defaultValue = "-1") Integer id) {
        return rechargeActivitiesLogic.refreshItem(getUserId(),itemType,id);
    }
}
