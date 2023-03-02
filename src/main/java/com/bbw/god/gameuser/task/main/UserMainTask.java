package com.bbw.god.gameuser.task.main;

import com.bbw.common.ID;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * 角色创建时生成所有主线任务
 *
 * @author suhq
 * @date 2019年2月21日 下午9:36:01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserMainTask extends UserCfgObj implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer enableAwardIndex = 0;// 可领取的ID
    private Integer awardedIndex = 0;// 已领取的ID

    public static UserMainTask fromTask(long guId, CfgTaskEntity task) {
        UserMainTask umTask = new UserMainTask();
        umTask.setId(ID.INSTANCE.nextId());
        umTask.setGameUserId(guId);
        umTask.setBaseId(task.getId());
        umTask.setName(task.getName());
        return umTask;
    }

    /**
     * 更新可领取的ID
     */
    public void addEnableAwardIndex(int addValue) {
        this.enableAwardIndex += addValue;
    }

    /**
     * 是否可领取
     *
     * @return
     */
    public boolean isEnableAward() {
        return this.enableAwardIndex > this.awardedIndex;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.MAIN_TASK;
    }
}
