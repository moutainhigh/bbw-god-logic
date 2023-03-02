package com.bbw.god.rechargeactivities.processor;

import com.bbw.god.activity.ActivityService;
import com.bbw.god.rechargeactivities.RDRechargeActivity;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.wartoken.UserWarToken;
import com.bbw.god.rechargeactivities.wartoken.WarTokenLogic;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 说明： 战令
 *
 * @author lwb
 * date 2021-06-02
 */
public abstract class AbstractWarTokenProcessor extends AbstractRechargeActivityProcessor{
    @Autowired
    protected ActivityService activityService;
    @Autowired
    protected WarTokenLogic warTokenLogic;

    @Override
    public RechargeActivityEnum getParent() {
        return RechargeActivityEnum.WAR_TOKEN;
    }

    /**
     * 18级
     * @param uid
     * @return
     */
    @Override
    public boolean isShow(long uid) {
        return warTokenLogic.openWarToken(uid);
    }

    public UserWarToken getUserWarToken(long uid){
        return warTokenLogic.getOrCreateUserWarToken(uid);
    }

    @Override
    public RDRechargeActivity buyAwards(long uid, int realId) {
        return new RDRechargeActivity();
    }
}
