package com.bbw.god.gameuser.task.fshelper;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.businessgang.UserBusinessGangService;
import com.bbw.god.gameuser.businessgang.user.UserBusinessGangTaskInfo;
import com.bbw.god.gameuser.chamberofcommerce.server.UserCocTaskService;
import com.bbw.god.gameuser.task.RDTaskItem;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.businessgang.BusinessGangTaskProcessor;
import com.bbw.god.gameuser.task.daily.DailyTaskProcessor;
import com.bbw.god.gameuser.task.daily.UserDailyTaskInfo;
import com.bbw.god.gameuser.task.daily.service.UserDailyTaskService;
import com.bbw.god.gameuser.task.fshelper.FsHepler.Task;
import com.bbw.god.gameuser.task.godtraining.GodTrainingTaskService;
import com.bbw.god.gameuser.task.grow.NewbieTaskService;
import com.bbw.god.server.guild.GuildTask;
import com.bbw.god.server.guild.service.GuildEightDiagramsTaskService;
import com.bbw.mc.m2c.M2cService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年2月13日 上午11:37:13 类说明 任务助手
 */
@Service
public class FsHelperService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserDailyTaskService userDailyTaskService;
    @Autowired
    private UserCocTaskService userCocTaskService;
    @Autowired
    private GuildEightDiagramsTaskService guildEightDiagramsTaskService;
    @Autowired
    private NewbieTaskService newbieTaskService;
    @Autowired
    private M2cService m2cService;
    @Autowired
    private DailyTaskProcessor dailyTaskProcessor;
    @Autowired
    private GodTrainingTaskService godTrainingTaskService;
    @Autowired
    private BusinessGangTaskProcessor businessGangTaskProcessor;
    @Autowired
    private UserBusinessGangService businessGangService;

    private static int maxTaskSize = 4;

    private FsHepler getFsHepler(long uid) {
        FsHepler hepler = gameUserService.getSingleItem(uid, FsHepler.class);
        if (hepler == null) {
            return null;
        }
        List<Task> expireTasks = new ArrayList<FsHepler.Task>();
        for (Task task : hepler.getTasks()) {
            Date date = task.getExpire();
            long time = DateUtil.millisecondsInterval(date, new Date());
            if (time < 0) {
                expireTasks.add(task);
            }
        }
        hepler.getTasks().removeAll(expireTasks);
        gameUserService.updateItem(hepler);
        return hepler;
    }

    private FsHepler instance(long uid) {
        FsHepler hepler = FsHepler.instance(uid);
        gameUserService.addItem(uid, hepler);
        return hepler;
    }

    /**
     * 返回任务ID
     *
     * @param uid
     * @param type
     * @return
     */
    public List<Integer> getFsHeplerTaskIds(long uid, FsTaskEnum type) {
        FsHepler hepler = getFsHepler(uid);
        List<Integer> ids = new ArrayList<Integer>();
        if (hepler == null) {
            return ids;
        }
        for (Task task : hepler.getTasks()) {
            Date date = task.getExpire();
            long time = DateUtil.millisecondsInterval(new Date(), date);
            if (time < 0) {
                continue;
            }
            if (type.getVal() == task.getTaskType()) {
                ids.add(task.getTaskId());
            }
        }
        return ids;
    }

    public void addTask(long uid, int type, int taskId) {
        FsHepler fsHepler = getFsHepler(uid);
        if (fsHepler == null) {
            fsHepler = instance(uid);
        }
        FsTaskEnum tyEnum = FsTaskEnum.fromVal(type);
        if (tyEnum == null) {
            throw new ExceptionForClientTip("task.not.exist");
        }
        if (fsHepler.getTasks().size() >= maxTaskSize) {
            throw new ExceptionForClientTip("fshepler.task.fill");
        }
        GameUser user = gameUserService.getGameUser(uid);
        UserDailyTaskInfo taskInfo = userDailyTaskService.getTodayDailyTaskInfo(user);
        if (null == taskInfo) {
            throw new ExceptionForClientTip("task.daily.is.lock");
        }
        RDTaskItem rdtask = dailyTaskProcessor.getRdTask(user, taskId, taskInfo);
        if (rdtask == null) {
            throw new ExceptionForClientTip("task.not.exist");
        }
        Task task = Task.instance(tyEnum, taskId);
        fsHepler.addTask(task);
        gameUserService.updateItem(fsHepler);
        // 通知任务
        m2cService.sendFsHeplerMsg(uid);
    }

    public boolean delTask(long uid, int type, int taskId) {
        FsHepler fsHepler = getFsHepler(uid);
        if (fsHepler == null) {
            return false;
        }
        FsTaskEnum tyEnum = FsTaskEnum.fromVal(type);
        if (tyEnum == null) {
            return false;
        }
        fsHepler.delTask(tyEnum, taskId);
        gameUserService.updateItem(fsHepler);
        return true;
    }

    public boolean existTask(FsTaskEnum taskEnum, int taskId, long uid) {
        if (!taskEnum.equals(FsTaskEnum.Daily)) {
            return true;
        }
        FsHepler fsHepler = getFsHepler(uid);
        if (fsHepler == null) {
            return false;
        }
        for (Task task : fsHepler.getTasks()) {
            Date date = task.getExpire();
            long time = DateUtil.millisecondsInterval(date, new Date());
            if (time < 0) {
                continue;
            }
            if (taskEnum.getVal() == task.getTaskType() && task.getTaskId() == taskId) {
                return true;
            }
        }
        return false;
    }

    public RDFsHelper getTaskList(long uid) {
        RDFsHelper rd = new RDFsHelper();
        List<RDTaskItem> list = new ArrayList<>();
        FsHepler fsHepler = getFsHepler(uid);
        GameUser user = gameUserService.getGameUser(uid);
        // 目前对象内只存储了每日任务的ID
        UserDailyTaskInfo taskInfo = userDailyTaskService.getTodayDailyTaskInfo(user);
        if (fsHepler != null && taskInfo != null) {
            for (Task t : fsHepler.getTasks()) {
                RDTaskItem task = dailyTaskProcessor.getRdTask(user, t.getTaskId(), taskInfo);
                if (task == null) {
                    continue;
                }
                if (task.getStatus() >= TaskStatusEnum.AWARDED.getValue()) {
                    delTask(uid, t.getTaskType(), t.getTaskId());
                    continue;
                }
                task.setFsTaskType(t.getTaskType());
                list.add(task);
            }
        }
        if (list.isEmpty()) {
            // 当没有追踪每日任务时，默认返回可前往的每日任务追踪
            list = userDailyTaskService.getGotoTasks(uid, maxTaskSize);
        }
        list.removeIf(t -> t.getId() == 22020 || t.getId() == 23020);
        rd.setTasks(list);
        // 商会
//        CocTask cocRdTask = userCocTaskService.getAcceptedRDTask(uid);
//        if (cocRdTask != null) {
//            rd.setCocTask(cocRdTask);
//        }
//        rd.setCocTaskTimes(userCocTaskService.getTaskTimes(uid));
        // 行会
        GuildTask guildRdTask = guildEightDiagramsTaskService.getAcceptedRDTaskInfo(uid);
        if (guildRdTask != null) {
            rd.setGuildTask(guildRdTask);
        }
        rd.setGuildTaskTimes(guildEightDiagramsTaskService.getTaskTimes(uid));
        // 新手
        /*RDTask nbt = newbieTaskService.getCurrentProgressRDTask(uid);
        if (nbt != null) {
            rd.setNewbieTask(nbt);
        }*/
        // 上仙试炼
        List<RDTaskItem> trainingTasks = godTrainingTaskService.getCurAccomplishRDTasks(uid);
        if (ListUtil.isNotEmpty(trainingTasks)) {
            rd.setGodTrainingTask(trainingTasks);
        }
        //商帮运送任务
        List<RDTaskItem> specialtyShippingTasks = businessGangTaskProcessor.getSpecialtyShippingTask(uid);
        if (ListUtil.isNotEmpty(specialtyShippingTasks)) {
            rd.setBusinessGangTask(specialtyShippingTasks);
            UserBusinessGangTaskInfo gangTask = businessGangService.getOrCreateUserBusinessGangTask(uid);
            Integer awardableNum = gangTask.getAwardableNum();
            rd.setRemainAvailableNum(awardableNum);
        }
        return rd;
    }
}
