package com.bbw.god.activity.holiday.processor;

import com.bbw.common.CloneUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityAbleChooseAwardStatusEnum;
import com.bbw.god.activity.config.ActivityAbleChooseAwardsTool;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.activity.rd.RDActivityItem;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDSuccess;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * 节日累充
 *
 * @author: huanghb
 * @date: 2021/12/30 13:47
 */
@Service
public class HolidayAccumulateProcessor extends AbstractActivityProcessor {
    /** 元宝进度转钻石进度倍率 */
    private static final Integer GOLD_PROGRESS_CHANGE_DIAMOND_PROGRESS = 10;

    public HolidayAccumulateProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_ACC_R, ActivityEnum.HOLIDAY_ACTIVITY_ACC_R);
    }

    @Override
    public boolean isShowInUi(long uid) {
        return true;
    }

    /**
     * 获取活动详情
     *
     * @param uid
     * @param activityType
     * @return
     */
    @Override
    public RDSuccess getActivities(long uid, int activityType) {
        RDActivityList rd = (RDActivityList) super.getActivities(uid, activityType);
        //获得需要添加可选奖励的活动
        List<Integer> ableChooseAwardActivityIds = ActivityAbleChooseAwardsTool.getAbleChooseAwardIds(activityType);
        List<RDActivityItem> rdActivityItems = rd.getItems();
        //获取活动实例
        int sid = gameUserService.getActiveSid(uid);
        IActivity activity = this.activityService.getActivity(sid, ActivityEnum.fromValue(activityType));
        //获得单个玩家节日每日累充所有活动集合
        List<UserActivity> userActivities = activityService.getUserActivities(uid, activity.gainId(), ActivityEnum.fromValue(activityType));
        for (RDActivityItem rdActivity : rdActivityItems) {
            //进度值由元宝转换为钻石
            rdActivity.setTotalProgress(rdActivity.getTotalProgress() / GOLD_PROGRESS_CHANGE_DIAMOND_PROGRESS);
            //是否需要添加可选择奖励
            if (!ableChooseAwardActivityIds.contains(rdActivity.getId())) {
                continue;
            }
            //添加可选择奖励（用于展示）
            List<Award> awards = ActivityAbleChooseAwardsTool.getAwards(rdActivity.getId());
            rdActivity.setAbleChooseAwards(awards);
            //获得玩家活动
            Integer activityId = ableChooseAwardActivityIds.stream().filter(aId -> aId.equals(rdActivity.getId())).findFirst().orElse(null);
            UserActivity ua = userActivities.stream().filter(tmp -> tmp.getBaseId().equals(activityId)).findFirst().orElse(null);
            //判断额外奖励是否选中 （0未选中，1选中）
            int ableChooseAwardStatus = ActivityAbleChooseAwardStatusEnum.UNSELECTED.getValue();
            boolean isSelecte = null != ua && null != ua.getAwardIndex();
            if (!isSelecte) {
                rdActivity.setExtraAwardStatus(ableChooseAwardStatus);
                continue;
            }
            //添加已选择奖励
            Award award = awards.get(ua.getAwardIndex());
            Award clone = CloneUtil.clone(award);
            rdActivity.addAward(clone);
            //设置可选择奖励状态为已添加
            ableChooseAwardStatus = ActivityAbleChooseAwardStatusEnum.SELECTED.getValue();
            rdActivity.setExtraAwardStatus(ableChooseAwardStatus);
        }
        rd.setTotalProgress(rd.getTotalProgress() / GOLD_PROGRESS_CHANGE_DIAMOND_PROGRESS);
        return rd;
    }

    /**
     * 获得活动奖励
     *
     * @param gu
     * @param ua
     * @param ca
     * @return
     */
    @Override
    public List<Award> getAwardsToSend(GameUser gu, UserActivity ua, CfgActivityEntity ca) {
        //获得可选择奖励活动id
        List<Integer> ableChooseAwardActivityIds = ActivityAbleChooseAwardsTool.getAbleChooseAwardIds(ca.getType());
        //是否可选择奖励
        boolean isAbleChooseAwards = null != ca && ableChooseAwardActivityIds.contains(ca.getId());
        if (!isAbleChooseAwards) {
            return getAwardsToShow(gu, ua, ca);
        }
        //是否选择奖励
        Integer awardIndex = ua.getAwardIndex();
        boolean isSelecte = null != ua && null != ua.getAwardIndex();
        if (!isSelecte) {
            throw new ExceptionForClientTip("rechargeActivity.not.select.Awards");
        }
        //可选择奖励活动添加奖励
        List<Award> ableChooseAwards = ActivityAbleChooseAwardsTool.getAwards(ua.getBaseId());
        return Arrays.asList(ableChooseAwards.get(awardIndex));
    }


    @Override
    protected long getRemainTime(long uid, int sid, IActivity a) {
        if (a.gainEnd() != null) {
            return a.gainEnd().getTime() - System.currentTimeMillis();
        }
        return NO_TIME;
    }


}
