package com.bbw.god.activity.processor;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.UserActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;

import java.util.List;

/**
 * @author suhq
 * @description: 活动处理接口
 * @date 2019-11-13 10:15
 **/
public interface IActivityProcessor {

    boolean isShowInUi(long uid);

    /**
     * 获取活动详情
     *
     * @param uid
     * @return
     */
    public RDSuccess getActivities(long uid, int activityType);

    /**
     * 获得奖励
     *
     * @param uid
     * @param sId
     * @param ca
     * @param awardIndex
     * @return
     */
    public RDCommon joinActivity(Long uid, int sId, int caId, CfgActivityEntity ca, int awardIndex);

    /**
     * 设置多选一奖励的最终领取的奖励项
     *
     * @param uid
     * @param sId
     * @param ca
     * @param awardIndex
     * @return
     */
	public boolean setAwardItem(Long uid, int sId, CfgActivityEntity ca, int awardIndex);

	/**
	 * 补领
	 *
	 * @param uid
	 * @param sId
	 * @param ca
	 * @return
	 */
    public RDCommon replenish(long uid, int sId, CfgActivityEntity ca);

    /**
     * 是否领取的该类活动的所有奖励
     *
     * @param uid
     * @param a
     * @return
     */
    public Boolean isJoinAllActivities(long uid, IActivity a);

    /**
     * 该活动类别有多少个可领取的
     *
     * @param gu
     * @param a
     * @return
     */
    public int getAbleAwardedNum(GameUser gu, IActivity a);

    /**
     * 获得活动显示奖励
     *
     * @param gu
     * @param ua
     * @param ca
     * @return
     */
    public List<Award> getAwardsToShow(GameUser gu, UserActivity ua, CfgActivityEntity ca);

    /**
     * 获得发放奖励
     * @param gu
     * @param ua
     * @param ca
     * @return
     */
    public List<Award> getAwardsToSend(GameUser gu, UserActivity ua, CfgActivityEntity ca);

    /**
     * 获得活动状态
     *
     * @param gu
     * @param a
     * @param ua
     * @param ca
     * @return
     */
    public AwardStatus getUAStatus(GameUser gu, IActivity a, UserActivity ua, CfgActivityEntity ca);

    boolean isMatch(ActivityEnum activityType);

    /**
     * 是否领取所有奖励
     *
     * @param userActivities
     * @param activityEnum
     * @return
     */
    boolean isAwardedAllAwards(long uid, List<UserActivity> userActivities, ActivityEnum activityEnum);
}
