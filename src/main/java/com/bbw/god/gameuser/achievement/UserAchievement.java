package com.bbw.god.gameuser.achievement;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 成就
 *
 * @author suhq
 * @date 2019年2月20日 上午11:17:12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Deprecated
public class UserAchievement extends UserCfgObj implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer value;
    private Integer status;
    private long accomplishTime = 0;
    private long lastUpdateTime = 0;

    public static UserAchievement instance(long guId, CfgAchievementEntity achievement, int addValue) {
        UserAchievement ua = new UserAchievement();
        ua.setId(ID.INSTANCE.nextId());
        ua.setGameUserId(guId);
        ua.setBaseId(achievement.getId());
        ua.setName(achievement.getName());
        int status = AchievementStatusEnum.NO_ACCOMPLISHED.getValue();
        if (addValue >= achievement.getValue()) {
            addValue = achievement.getValue();
            status = AchievementStatusEnum.ACCOMPLISHED.getValue();
            ua.setAccomplishTime(DateUtil.toDateTimeLong());
        }
        ua.setValue(addValue);
        ua.setStatus(status);
        ua.setLastUpdateTime(DateUtil.toDateTimeLong());
        return ua;
    }

    public void addValue(int addValue, int needValue) {
        this.value += addValue;
        if (this.value >= needValue) {
            this.value = needValue;
            this.status = AchievementStatusEnum.ACCOMPLISHED.getValue();
            this.accomplishTime = DateUtil.toDateTimeLong();
        }
        this.lastUpdateTime = DateUtil.toDateTimeLong();
    }

    public void updateValue(int newValue, int needValue) {
        this.value = newValue;
        if (this.value >= needValue) {
            this.value = needValue;
            this.status = AchievementStatusEnum.ACCOMPLISHED.getValue();
            this.accomplishTime = DateUtil.toDateTimeLong();
        }
        this.lastUpdateTime = DateUtil.toDateTimeLong();
    }

    public void updateStatus(int needValue) {
        if (this.status == AchievementStatusEnum.AWARED.getValue()) {
            return;
        }
        if (this.value >= needValue) {
            this.value = needValue;
            this.status = AchievementStatusEnum.ACCOMPLISHED.getValue();
            this.accomplishTime = DateUtil.toDateTimeLong();
        }
        this.lastUpdateTime = DateUtil.toDateTimeLong();
    }

    /**
     * 任务是否达成
     *
     * @return
     */
    public boolean ifAccomplished() {
        return this.status >= AchievementStatusEnum.ACCOMPLISHED.getValue();
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.ACHIEVEMENT;
    }

}
