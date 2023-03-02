package com.bbw.god.activity.holiday.processor.holidayspecialcity;

import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.rd.RDAdvance;

/**
 * 节日活动经过建筑有相应触发需要继承此类（重写到达建筑触发事件的方法即可）
 *
 * @author fzj
 * @date 2022/4/28 16:23
 */
public abstract class AbstractSpecialCityProcessor extends AbstractActivityProcessor {
    /**
     * 是否在ui中展示
     *
     * @return
     */
    @Override
    public boolean isShowInUi(long uid) {
        return false;
    }

    /**
     * 剩余时间
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
     * 是否在活动期间
     *
     * @param sid
     * @return
     */
    public boolean isOpened(int sid) {
        ActivityEnum activityEnum = this.activityTypeList.stream().findFirst().orElse(null);
        if (null == activityEnum) {
            return false;
        }
        IActivity a = this.activityService.getActivity(sid, activityEnum);
        return null != a;
    }

    /**
     * 村庄触发事件
     *
     * @param uid
     * @param rd
     */
    public void cunZTriggerEvent(long uid, RDAdvance rd) {
    }

    /**
     * 游商馆触发事件
     *
     * @param uid
     * @param rd
     */
    public void youSGTriggerEvent(long uid, RDAdvance rd) {
    }

    /**
     * 客栈触发事件
     *
     * @param uid
     * @param rd
     */
    public void keZTriggerEvent(long uid, RDAdvance rd) {
    }

}
