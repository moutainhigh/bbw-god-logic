package com.bbw.god.rechargeactivities.processor;

import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.rechargesign.RechargeSignActivityProcessor;
import com.bbw.god.rechargeactivities.RDRechargeActivity;
import com.bbw.god.rechargeactivities.RechargeActivityEnum;
import com.bbw.god.rechargeactivities.RechargeActivityItemEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 充值签到礼包
 *
 * @author: suhq
 * @date: 2021/8/3 12:05 下午
 */
@Service
public class RechargeSignProcessor extends AbstractRechargeActivityProcessor {
    @Autowired
    private RechargeSignActivityProcessor rechargeSignActivityProcessor;
    @Autowired
    private ActivityService activityService;

    @Override
    public RechargeActivityEnum getParent() {
        return RechargeActivityEnum.RECHARGE_SIGN_BAG;
    }

    @Override
    public RechargeActivityItemEnum getCurrentEnum() {
        return RechargeActivityItemEnum.RECHARGE_SIGN_BAG;
    }

    @Override
    public boolean isShow(long uid) {
        IActivity activity = activityService.getActivity(gameUserService.getActiveSid(uid), ActivityEnum.RECHARGE_SIGN);
        if (null == activity) {
            return false;
        }
        Integer level = gameUserService.getGameUser(uid).getLevel();
        if (level < getShowNeedLevel()) {
            return false;
        }
        return true;
    }

    /**
     * 获取展示的等级所需值
     *
     * @return
     */
    @Override
    protected int getShowNeedLevel() {
        return 15;
    }

    /**
     * 获取展示的充值所需值
     *
     * @return
     */
    @Override
    protected int getShowNeedRecharge() {
        return 0;
    }

    @Override
    public int getCanGainAwardNum(long uid) {
        IActivity activity = activityService.getActivity(gameUserService.getActiveSid(uid), ActivityEnum.RECHARGE_SIGN);
        if (null == activity) {
            return 0;
        }
        return rechargeSignActivityProcessor.getAbleAwardedNum(gameUserService.getGameUser(uid), activity);
    }

    @Override
    public RDRechargeActivity listAwards(long uid) {
        return new RDRechargeActivity();
    }

    @Override
    public RDRechargeActivity buyAwards(long uid, int mallId) {
        RDRechargeActivity rd = new RDRechargeActivity();
        return rd;
    }

    @Override
    public RDRechargeActivity gainAwards(long uid, int realId) {
        return new RDRechargeActivity();
    }
}
