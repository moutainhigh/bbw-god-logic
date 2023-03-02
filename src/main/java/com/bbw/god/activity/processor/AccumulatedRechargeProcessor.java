package com.bbw.god.activity.processor;

import com.bbw.common.CloneUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityAbleChooseAwardStatusEnum;
import com.bbw.god.activity.config.ActivityAbleChooseAwardsTool;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityTool;
import com.bbw.god.activity.rd.RDActivityItem;
import com.bbw.god.activity.rd.RDActivityList;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDSuccess;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author suhq
 * @description: 今日累充
 * @date 2019-11-07 09:20
 **/
@Service
public class AccumulatedRechargeProcessor extends AbstractActivityProcessor {

    public AccumulatedRechargeProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.ACC_R);
    }

    /** 展示UI需要等级 */
    private final int SHOW_UI_NEED_LEVEL = 25;
    /** 展示UI需要充值金额 */
    private final int SHOW_UI_NEED_RECHARGE = 98;

    /**
     * 是否在ui中展示
     *
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        GameUser gu = gameUserService.getGameUser(uid);
        //等级达到25级时，显示节日累充
        if (gu.getLevel() >= getShowNeedLevel()) {
            return true;
        }
        //获得获得实例
        IActivity a = this.activityService.getActivity(gameUserService.getActiveSid(uid), ActivityEnum.ACC_R);
        List<CfgActivityEntity> activities = ActivityTool.getActivitiesByType(ActivityEnum.ACC_R);
        //获得玩家活动信息
        List<Long> aIds = Arrays.asList(a.gainId());
        List<UserActivity> uas = activityService.getUserActivities(uid, activities, aIds);
        int minRoundeChargeValue = 0;
        //如果存在玩家活动信息择获取第一轮的充值进度
        if (ListUtil.isNotEmpty(uas)) {
            int minRound = uas.stream().mapToInt(UserActivity::getRound).min().getAsInt();
            minRoundeChargeValue += uas.stream().filter(tmp -> tmp.getRound() == minRound).mapToInt(UserActivity::getProgress).max().getAsInt();
        }
        //当玩家第一轮充值满98元
        if (minRoundeChargeValue >= getShowNeedRecharge()) {
            return true;
        }
        return false;
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
        RDActivityList rdActivities = (RDActivityList) super.getActivities(uid, activityType);
        List<RDActivityItem> items = rdActivities.getItems();
        IActivity a = this.activityService.getActivity(gameUserService.getActiveSid(uid), ActivityEnum.ACC_R);
        if (a == null) {
            throw new ExceptionForClientTip("activity.not.exist");
        }
        List<UserActivity> userActivities = this.activityService.getUserActivities(uid, a.gainId(), ActivityEnum.ACC_R);
        int currentRound = 1;
        if (ListUtil.isNotEmpty(userActivities)) {
            currentRound = userActivities.get(0).getRound();
        }
        int finalCurrentRound = currentRound;
        items = items.stream().filter(tmp -> tmp.getSeries() == finalCurrentRound).collect(Collectors.toList());
        //获得需要添加可选奖励的活动
        List<Integer> ableChooseAwardActivityIds = ActivityAbleChooseAwardsTool.getAbleChooseAwardIds(ActivityEnum.ACC_R.getValue());
        List<RDActivityItem> rdActivityItems = items.stream().filter(tmp -> ableChooseAwardActivityIds.contains(tmp.getId())).collect(Collectors.toList());
        //获得单个玩家节日每日累充所有活动集合
        for (RDActivityItem rdActivity : rdActivityItems) {
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
        rdActivities.setItems(items);
        return rdActivities;
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
        List<Integer> ableChooseAwardActivityIds = ActivityAbleChooseAwardsTool.getAbleChooseAwardIds(ActivityEnum.ACC_R.getValue());
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

    /**
     * 获取展示的等级所需值
     *
     * @return
     */
    private int getShowNeedLevel() {
        return SHOW_UI_NEED_LEVEL;
    }

    /**
     * 获取展示的充值所需值
     *
     * @return
     */
    private int getShowNeedRecharge() {
        return SHOW_UI_NEED_RECHARGE;
    }
}
