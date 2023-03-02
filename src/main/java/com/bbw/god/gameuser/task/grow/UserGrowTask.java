package com.bbw.god.gameuser.task.grow;

import com.bbw.common.ID;
import com.bbw.common.StrUtil;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.task.CfgTaskConfig.CfgBox;
import com.bbw.god.gameuser.task.CfgTaskEntity;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.UserTask;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 角色创建时生成所有的新手任务和进阶任务
 *
 * @author suhq 2018年10月9日 上午9:25:47
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class UserGrowTask extends UserTask implements Serializable {

    private static final long serialVersionUID = 1L;

    public static UserGrowTask fromTask(long guId, CfgTaskEntity task) {
        UserGrowTask ugTask = new UserGrowTask();
        ugTask.setId(ID.INSTANCE.nextId());
        ugTask.setGameUserId(guId);
        ugTask.setBaseId(task.getId());
        ugTask.setNeedValue(task.getValue());
        ugTask.setName(task.getName());
        return ugTask;
    }

    public static UserGrowTask fromTask(long guId, CfgBox task) {
        UserGrowTask ugTask = new UserGrowTask();
        ugTask.setId(ID.INSTANCE.nextId());
        ugTask.setGameUserId(guId);
        ugTask.setBaseId(task.getId());
        ugTask.setNeedValue(task.getScore());
        ugTask.setName("宝箱" + task.getId());
        return ugTask;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.GROW_TASK;
    }

    /**
     * 更新新的任务信息
     *
     * @param needVal
     * @param name
     */
    public void updateInfo(int needVal, String name) {
        if (this.ifAccomplished()) {
            return;
        }
        if (!StrUtil.isBlank(name)) {
            this.setName(name);
        }
        this.setNeedValue(needVal);
        if (this.getValue() >= needVal) {
            this.setStatus(TaskStatusEnum.ACCOMPLISHED.getValue());
        }
    }

    /**
     * 将进度替换成新的进度
     *
     * @param progress
     */
    public void updateProgress(int progress) {
        if (ifAccomplished()) {
            return;
        }
        this.setValue(progress);
        if (this.getValue() >= this.getNeedValue()) {
            this.setValue(getNeedValue());
            this.setStatus(TaskStatusEnum.ACCOMPLISHED.getValue());
        }
    }

    public void setTaskAward() {
        if (ifAccomplished()) {
            return;
        }
        setValue(getNeedValue());
        setStatus(TaskStatusEnum.ACCOMPLISHED.getValue());
    }
}
