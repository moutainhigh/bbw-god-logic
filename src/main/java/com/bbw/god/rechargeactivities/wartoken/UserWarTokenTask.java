package com.bbw.god.rechargeactivities.wartoken;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.UserDataType;
import lombok.Data;

/**
 * 说明：战令任务
 *
 * @author lwb
 * date 2021-06-02
 */
@Data
public class UserWarTokenTask extends UserCfgObj {
    private Long activityId=0L;
    /**
     * 当前总进度
     */
    private Integer total=0;
    /**
     * 当前领取的总次数
     */
    private Integer gainTimes=0;
    /**
     * 任务生成日期
     */
    private Integer initDate;
    /**
     *
     * 轮询任务  可重复完成
     */
    private boolean pollTask=true;

    public static UserWarTokenTask getInstance(long uid,CfgWarTokenTask cfgTask,long activityId){
        boolean pollTask=true;
        if (cfgTask.getType()==WarTokenTaskType.LOGIN_TASK.getType()||cfgTask.getType()==WarTokenTaskType.RANDOM_UNIQUE_TASK.getType()){
            pollTask=false;
        }
        return getInstance(uid,cfgTask.getId(),activityId,pollTask);
    }

    public static UserWarTokenTask getInstance(long uid,int cfgTaskId,long activityId,boolean pollTask){
        UserWarTokenTask task=new UserWarTokenTask();
        task.setGameUserId(uid);
        task.setId(ID.INSTANCE.nextId());
        task.setBaseId(cfgTaskId);
        task.setInitDate(DateUtil.getTodayInt());
        task.setActivityId(activityId);
        task.setPollTask(pollTask);
        if (WarTokenTool.LOGIN_TASK_IDS.contains(cfgTaskId)){
            task.setTotal(1);
        }
        return task;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.USER_WAR_TOKEN_TASK;
    }

    public boolean ifValid(long activityId){
        int mondayInt=DateUtil.toDateInt(DateUtil.getWeekBeginDateTime(DateUtil.now()));
        return this.initDate!=null && this.initDate>=mondayInt && this.activityId==activityId;
    }

    /**
     * 是否 可以领取奖励
     *
     * @return
     * @param task
     */
    public int gainAwards(CfgWarTokenTask task){
        int need = task.getNeed();
        int maxGainTimes=total/need;
        int canGainTimes=maxGainTimes-this.gainTimes;
        this.gainTimes=maxGainTimes;
        return canGainTimes;
    }

    /**
     * 加进度
     * @param val
     */
    public void addVal(int val){
        if (val<0){
            return;
        }
        this.total+=val;
    }

    public int nextCondition(){
        CfgWarTokenTask task = WarTokenTool.getCfgWarTokenTask(getBaseId());
        return (gainTimes+1)*task.getNeed();
    }
}
