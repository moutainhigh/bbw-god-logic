package com.bbw.god.rechargeactivities.wartoken;

import com.bbw.common.CloneUtil;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.game.config.Cfg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 说明：
 *
 * @author lwb
 * date 2021-06-02
 */
public class WarTokenTool {
    /**
     * 每周登录任务ID
     */
    public static final List<Integer> LOGIN_TASK_IDS= Arrays.asList(100000,100001,100002);

    public static List<CfgWarTokenLevelAward> getLevelAwards(){
        return ListUtil.copyList(Cfg.I.get(CfgWarTokenLevelAward.class),CfgWarTokenLevelAward.class);
    }

    public static CfgWarTokenLevelAward getLevelAward(int level){
        return CloneUtil.clone(Cfg.I.get(level,CfgWarTokenLevelAward.class));
    }

    /**
     * 活动大奖配置
     *
     * @return
     */
    public static CfgWarTokenBigAwards getBigAwards(){
        return Cfg.I.getUniqueConfig(CfgWarTokenBigAwards.class);
    }

    /**
     * 获取升到满级需要的总经验
     * @param currentExp
     * @return
     */
    public static final int getToFullLevelNeedExpNum(int currentExp){
        return Math.max(0,getFullLevelNeedExp()-currentExp);
    }

    /**
     * 获取满级所需要的经验
     * 满级180  恒定每级2000
     * @return
     */
    public static final int getFullLevelNeedExp(){
        return 180*getUpLevelNeedExp();
    }

    public static final int getUpLevelNeedExp(){
        return 2000;
    }

    /**
     * 获取任务
     * @param taskId
     * @return
     */
    public static CfgWarTokenTask getCfgWarTokenTask(Integer taskId){
        if (taskId==null){
            throw new ExceptionForClientTip("wartoken.not.exist.task.id","null");
        }
        return Cfg.I.get(taskId,CfgWarTokenTask.class);
    }

    /**
     * 获取任务
     * @param taskType 类型
     * @return
     */
    public static List<CfgWarTokenTask> getCfgWarTokenTasks(WarTokenTaskType taskType){
        List<CfgWarTokenTask> tasks = Cfg.I.get(CfgWarTokenTask.class);
        return tasks.stream().filter(p->p.getType().intValue()== taskType.getType()).collect(Collectors.toList());
    }

    /**
     * 第一周初始上限为：13600点，后续每周增加：700点上限。
     * 获取本周可获得的最大经验
     * 上限是 12周 21300
     * @return
     */
    public static int getWeekMaxExp(int sid){
        ActivityService activityService = SpringContextUtil.getBean(ActivityService.class);
        IActivity activity = activityService.getActivity(sid, ActivityEnum.WAR_TOKEN);
        if (activity==null){
            return 13600;
        }
        int today= DateUtil.getTodayInt();
        int addWeek=1;
        int activityDay=DateUtil.toDateInt(DateUtil.addWeeks(activity.gainBegin(),addWeek));
        while (addWeek<12 && activityDay<=today){
            addWeek++;
            activityDay=DateUtil.toDateInt(DateUtil.addWeeks(activity.gainBegin(),addWeek));
        }
        return Math.min(21300,(13600+700*(addWeek-1)));
    }
}
