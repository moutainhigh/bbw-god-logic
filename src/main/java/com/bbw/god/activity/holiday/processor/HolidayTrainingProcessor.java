package com.bbw.god.activity.holiday.processor;

import com.bbw.common.PowerRandom;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.processor.AbstractActivityProcessor;
import com.bbw.god.fight.RDFightsInfo;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * @author lsb
 * @description: 百鬼夜行活动2—恶鬼缠身
 * @date 2020-08-27 14:00
 **/
@Service
public class HolidayTrainingProcessor extends AbstractActivityProcessor {

    public HolidayTrainingProcessor() {
        this.activityTypeList = Arrays.asList(ActivityEnum.HOLIDAY_TRAINING);
    }

    /**
	 * 是否在ui中展示
	 *
	 * @return
	 */
	@Override
	public boolean isShowInUi(long uid) {
		return false;
	}

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
	public boolean opened(int sid) {
		ActivityEnum activityEnum = ActivityEnum.fromValue(ActivityEnum.HOLIDAY_TRAINING.getValue());
		IActivity a = this.activityService.getActivity(sid, activityEnum);
		if (a == null) {
			return false;
		}
		return a.ifTimeValid();
	}

	/**
	 * 1.活动期间，进行城池练兵时有10%遭遇恶鬼缠身的对手，该对手的头像将会变更为恶鬼头像（鬼兵头像），名称变为“恶鬼”。
	 * 击败恶鬼时，获取对应数额的驱魔点（用于参与除魔卫道榜）
	 * @param uid
	 * @param info
	 */
	public void changeFightInfo(long uid,RDFightsInfo info){
		if (opened(gameUserService.getActiveSid(uid))){
			if (PowerRandom.hitProbability(10)){
				info.setNickname("恶鬼");
				info.setHead(424);
			}
		}
	}
}
