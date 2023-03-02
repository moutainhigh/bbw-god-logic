package com.bbw.god.activity;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 活动
 *
 * @author suhq 2018年9月30日 上午10:41:36
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserActivity extends UserCfgObj implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long aId;// 活动实例ID
    private Integer round = 1;
    private Integer progress;// 进度
    private Integer status;// 状态
    private Integer awardIndex;// 指定奖励
    private Date date;// 活动参与时间

    /**
     * 实例化userActivity,用于内建活动、福利
     *
     * @param guId
     * @param aId
     * @param progress
     * @param activity
     * @return
     */
    public static UserActivity fromActivity(Long guId, long aId, int progress, CfgActivityEntity activity) {
        int status = AwardStatus.UNAWARD.getValue();
        if (progress >= activity.getNeedValue()) {
            status = AwardStatus.ENABLE_AWARD.getValue();
            progress = activity.getNeedValue();
        }
        return instance(guId, aId, progress, status, activity);
    }

    /**
     * 实例化含有轮次的useractivity
     *
     * @param guId
     * @param aId
     * @param round
     * @param progress
     * @param activity
     * @return
     */
    public static UserActivity fromActivity(Long guId, long aId, int round, int progress, CfgActivityEntity activity) {
        UserActivity ua = fromActivity(guId, aId, progress, activity);
        ua.setRound(round);
        return ua;
    }

    /**
     * 用于节日用邮件发放奖励的活动
     *
     * @param guId
     * @param saId
     * @param progress
     * @param activity
     * @return
     */
    public static UserActivity fromActivityAsMailAward(Long guId, long saId, int progress, CfgActivityEntity activity) {
        int status = AwardStatus.UNAWARD.getValue();
        if (progress >= activity.getNeedValue()) {
            status = AwardStatus.AWARDED.getValue();
            progress = activity.getNeedValue();
            ;
        }
        return instance(guId, saId, progress, status, activity);
    }

    /**
     * 体力专用
     *
     * @param guId
     * @param saId
     * @param activity
     * @return
     */
    public static UserActivity fromActivityAsDice(Long guId, long saId, CfgActivityEntity activity) {
        return instance(guId, saId, activity.getNeedValue(), AwardStatus.AWARDED.getValue(), activity);
    }

    /**
     * 加进度
     *
     * @param addedNum
     * @param a
     */
    public void addProgress(int addedNum, CfgActivityEntity a) {
        this.progress += addedNum;
        if (this.progress >= a.getNeedValue()) {
            this.status = AwardStatus.ENABLE_AWARD.getValue();
            this.progress = a.getNeedValue();
        }
    }

    /**
     * 加进度，通过邮件发放建立的专用
     *
     * @param addedNum
     * @param a
     */
    public void addProgressAsMailAward(int addedNum, CfgActivityEntity a) {
        this.progress += addedNum;
        if (this.progress >= a.getNeedValue()) {
            this.status = AwardStatus.AWARDED.getValue();
            this.progress = a.getNeedValue();
        }
    }

    private static UserActivity instance(Long guId, long aId, int progress, int status, CfgActivityEntity activity) {
        UserActivity userActivity = new UserActivity();
        userActivity.setId(ID.INSTANCE.nextId());
        userActivity.setGameUserId(guId);
        userActivity.setAId(aId);
        userActivity.setBaseId(activity.getId());
        userActivity.setName(activity.getName());
        userActivity.setProgress(progress);
        userActivity.setStatus(status);
        userActivity.setAId(aId);
        userActivity.setDate(DateUtil.now());
        return userActivity;
    }

    public static UserActivity instance(Long guId, long aId, int progress, int status, int baseId, String name) {
        UserActivity userActivity = new UserActivity();
        userActivity.setId(ID.INSTANCE.nextId());
        userActivity.setGameUserId(guId);
        userActivity.setAId(aId);
        userActivity.setBaseId(baseId);
        userActivity.setName(name);
        userActivity.setProgress(progress);
        userActivity.setStatus(status);
        userActivity.setAId(aId);
        userActivity.setDate(DateUtil.now());
        return userActivity;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.ACTIVITY;
    }
}
