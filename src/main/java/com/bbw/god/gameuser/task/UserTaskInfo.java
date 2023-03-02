package com.bbw.god.gameuser.task;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author suchaobin
 * @description 玩家任务信息父类
 * @date 2020/11/24 09:16
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class UserTaskInfo extends UserSingleObj implements Serializable {
    private static final long serialVersionUID = 5163029148304059277L;
    // 未完成的id集合
    private List<Integer> unFinishIds = new ArrayList<>();
    // 已完成未领取的id集合
    private List<Integer> accomplishIds = new ArrayList<>();
    // 已领取奖励的id集合
    private List<Integer> awardedIds = new ArrayList<>();
    // 生成时间
    private Date generateTime = new Date();
  //
    /**
     * 完成某个任务
     *
     * @param taskId 任务id
     */
    public void accomplish(Integer taskId) {
        // 从未完成中删除
        if (unFinishIds.contains(taskId)){
            unFinishIds.remove(taskId);
        }
        if (!this.accomplishIds.contains(taskId)) {
            this.accomplishIds.add(taskId);
        }
    }

    /**
     * 已领取某个任务的奖励
     *
     * @param taskId 任务id
     */
    public void awarded(Integer taskId) {
        this.accomplishIds.remove(taskId);
        if (!this.awardedIds.contains(taskId)) {
            this.awardedIds.add(taskId);
        }
    }

    /**
     * 获取任务状态
     *
     * @param taskId 任务id
     * @return
     */
    public TaskStatusEnum gainStatus(int taskId) {
        if (this.accomplishIds.contains(taskId)) {
            return TaskStatusEnum.ACCOMPLISHED;
        }
        if (this.awardedIds.contains(taskId)) {
            return TaskStatusEnum.AWARDED;
        }
        return TaskStatusEnum.DOING;
    }

    public boolean ifAccomplished(int taskId){
        if (ListUtil.isNotEmpty(accomplishIds)){
            return accomplishIds.contains(taskId);
        }
        return false;
    }

    public boolean ifAwarded(int taskId){
        if (ListUtil.isNotEmpty(awardedIds)){
            return awardedIds.contains(taskId);
        }
        return false;
    }
    /**
     * 是否是今日数据
     *
     * @return
     */
    public boolean isToday() {
        return DateUtil.toDateInt(this.generateTime) == DateUtil.getTodayInt();
    }
}
