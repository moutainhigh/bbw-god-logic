package com.bbw.god.activity.holiday.processor;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityItem;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.CfgProductGroup;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.mall.RDMallList;
import com.bbw.god.mall.processor.HolidayGiftPackMallProcessor;
import com.bbw.god.pay.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 节日礼包-51，活动业务相关
 *
 * @author longwh
 * @date 2022/12/12 14:50
 */
@Service
public class HolidayGiftPackProcessor extends AbstractActivityProcessor {
    @Autowired
    private ProductService productService;
    @Autowired
    private HolidayGiftPackMallProcessor mallProcessor;

    private static final List<Integer> HOLIDAY_GIFT_PACK_51_PID = Arrays.asList(99001801, 99001802, 99001803, 99001804);

    public HolidayGiftPackProcessor() {
        this.activityTypeList = Collections.singletonList(ActivityEnum.HOLIDAY_GIFT_PACK_51);
    }
    /**
     * 是否在ui中展示
     *
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    /**
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    public boolean isOpened(int sid) {
        ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.HOLIDAY_GIFT_PACK_51.getValue());
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return a != null;
    }

    @Override
    public RDActivityList getActivities(long uid, int activityType) {
        int sid = this.gameUserService.getActiveSid(uid);
        if (!isOpened(sid)) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        ActivityEnum activityEnum = ActivityEnum.fromValue(activityType);
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        if (a == null) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        RDActivityList rd = new RDActivityList();
        GameUser gu = this.gameUserService.getGameUser(uid);

        List<CfgActivityEntity> cas = ActivityTool.getActivitiesByType(activityEnum);
        List<UserActivity> uas = this.activityService.getUserActivities(uid, a.gainId(), activityEnum);

        List<RDActivityItem> rdActivities = new ArrayList<>();
        for (CfgActivityEntity ca : cas) {
            int caId = ca.getId();
            UserActivity ua = uas.stream().filter(tmp -> tmp.getBaseId() == caId).findFirst().orElse(null);
            // 获取活动的状态
            AwardStatus status = this.getUAStatus(gu, a, ua, ca);
            // 封装活动信息
            RDActivityItem rdActivity = this.toRdActivity(gu, ua, ca, status.getValue());
            if (rdActivity == null) {
                continue;
            }
            rdActivity.setSign(a.gainSign());
            rdActivities.add(rdActivity);
        }
        rd.setItems(rdActivities);

        //获得商品
        RDMallList malls = mallProcessor.getGoods(uid);
        //返回竞猜商店物品
        rd.setRdMallList(malls);
        rd.setRemainTime(getRemainTime(uid, sid, a));
        return rd;
    }

    /**
     * 获得活动剩余时间
     *
     * @param uid
     * @param sid
     * @param a
     * @return
     */
    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }

    /**
     * 是否存在商品
     *
     * @param pid
     * @return
     */
    public boolean hadProduct(int pid) {
        CfgProductGroup.CfgProduct cfgProduct = productService.getCfgProduct(pid);
        return cfgProduct != null;
    }

    /**
     * 是否已充值
     *
     * @param gu
     * @return
     */
    public boolean isRecharged(GameUser gu) {
        IActivity a = this.activityService.getActivity(gu.getServerId(), ActivityEnum.HOLIDAY_GIFT_PACK_51);
        if (null == a) {
            return true;
        }
        CfgActivityEntity rca = ActivityTool.getActivityByType(ActivityEnum.HOLIDAY_GIFT_PACK_51);

        UserActivity rechargeActivity = this.activityService.getUserActivity(gu.getId(), a.gainId(), rca.getId());
        if (rechargeActivity == null) {
            return true;
        }
        if (rechargeActivity.getStatus() >= AwardStatus.ENABLE_AWARD.getValue()) {
            return false;
        }
        return true;
    }

    /**
     * 是否可以继续充值
     *
     * @param uid
     * @param pid
     * @return
     */
    public boolean canBuy(long uid, int pid) {
        if (HOLIDAY_GIFT_PACK_51_PID.contains(pid)) {
            return isRecharged(gameUserService.getGameUser(uid));
        }
        return true;
    }

}