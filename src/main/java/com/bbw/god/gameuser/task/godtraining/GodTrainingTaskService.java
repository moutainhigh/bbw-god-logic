package com.bbw.god.gameuser.task.godtraining;

import com.bbw.cache.UserCacheService;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.common.lock.SyncLockUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardEnum;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.city.CityResStatisticService;
import com.bbw.god.gameuser.statistic.resource.city.CityStatistic;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.grow.event.EPFinishNewbieTask;
import com.bbw.god.gameuser.task.grow.event.NewbieTaskEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author suchaobin
 * @description 上仙试炼service
 * @date 2021/1/19 20:44
 **/
@Service
public class GodTrainingTaskService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private SyncLockUtil syncLockUtil;
    @Autowired
    private AwardService awardService;
    @Autowired
    private CityResStatisticService cityResStatisticService;
    @Autowired
    private UserCacheService userCacheService;

    protected static final List<Integer> TIME_LIMITED_TASKS = Arrays.asList(95701, 95702, 95703, 95704, 95705, 95706, 95707);
    /** 宝箱任务 */
    private static final List<Integer> BOX_TASK = Arrays.asList(90901, 90902, 90903, 90904, 90905);

    public List<UserGodTrainingTask> getTrainingTasks(long uid) {
        List<UserGodTrainingTask> tasks = userCacheService.getUserDatas(uid, UserGodTrainingTask.class);
        return tasks;
    }

    public void addTrainingTasks(List<UserGodTrainingTask> uts) {
        userCacheService.addUserDatas(uts);
    }

    public void delTrainingTasks(List<UserGodTrainingTask> uts) {
        userCacheService.delUserDatas(uts);
    }

    public RDTaskList getTasks(long uid, Integer days) {
        RDTaskList rd = new RDTaskList();
        GameUser gu = gameUserService.getGameUser(uid);
        if (gu.getStatus().isGrowTaskCompleted()) {
            return rd;
        }
        List<UserGodTrainingTask> tasks = getCurUserTrainingTasks(uid);
        Date beginTime = DateUtil.fromDateLong(tasks.stream().findFirst().get().getGenerateTime());
        Date now = DateUtil.now();
        int daysBetween = DateUtil.getDaysBetween(beginTime, now);
        if (null != days && days > daysBetween + 1) {
            throw new ExceptionForClientTip("task.god.training.is.lock");
        }
        int matchDays = null == days ? daysBetween + 1 : days;
        matchDays = Math.min(matchDays, 7);
        int finalMatchDays = matchDays;
        tasks = tasks.stream().filter(tmp -> finalMatchDays == tmp.getDays() || tmp.getDays() == 0).collect(Collectors.toList());
        List<RDTaskItem> items = tasks.stream().map(tmp -> {
            RDTaskItem item = RDTaskItem.getInstance(tmp, TaskTool.getAwards(TaskGroupEnum.GOD_TRAINING, tmp.getBaseId()));
            if (TIME_LIMITED_TASKS.contains(tmp.getBaseId())) {
                if (95701 != tmp.getBaseId()) {
                    item.setRemainTime(DateUtil.getDateEnd(now).getTime() - now.getTime());
                } else {
                    // 第一天限时任务是从生成任务开始倒计时24小时
                    item.setRemainTime(DateUtil.addHours(beginTime, 24).getTime() - now.getTime());
                }
            }
            if (null != tmp.getAwardIndex()) {
                item.setAwards(Collections.singletonList(item.getAwards().get(tmp.getAwardIndex())));
            }
            return item;
        }).collect(Collectors.toList());
        rd.setItems(items);
        rd.setCurDays(matchDays);
        return rd;
    }

    @SuppressWarnings("unchecked")
    public List<UserGodTrainingTask> getCurUserTrainingTasks(long uid) {
        List<UserGodTrainingTask> tasks = getTrainingTasks(uid);
        // 任务不存在
        if (ListUtil.isEmpty(tasks)) {
            // 生成任务
            tasks = (List<UserGodTrainingTask>) syncLockUtil.doSafe(String.valueOf(uid), tmp -> {
                List<UserGodTrainingTask> toAddTasks = new ArrayList<>();
                CfgTaskConfig config = TaskTool.getTaskConfig(TaskGroupEnum.GOD_TRAINING);
                List<CfgTaskEntity> cfgTasks = config.getTasks();
                List<CfgTaskConfig.CfgBox> boxes = config.getBoxs();
                // 转对象
                List<UserGodTrainingTask> trainingTasks = cfgTasks.stream().map(t ->
                        UserGodTrainingTask.fromTask(uid, t)).collect(Collectors.toList());
                List<UserGodTrainingTask> trainingBoxes = boxes.stream().map(t ->
                        UserGodTrainingTask.fromTask(uid, t)).collect(Collectors.toList());
                // 保存数据
                toAddTasks.addAll(trainingTasks);
                toAddTasks.addAll(trainingBoxes);
                // 加进度
                CityStatistic statistic = cityResStatisticService.fromRedis(uid, StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
                // 三级城
                addProgress(uid, 95702, statistic.getThreeStarCity());
                // 四级城
                addProgress(uid, 95703, statistic.getFourStarCity());
                // 五级城
                addProgress(uid, 95704, statistic.getFiveStarCity());
                // 总的
                addProgress(uid, 95707, statistic.getTotal());
                addTrainingTasks(toAddTasks);
                return toAddTasks;
            });
        }
        // 过滤已经失效的期限任务
        List<UserGodTrainingTask> inValidTask = tasks.stream().filter(tmp ->
                !tmp.ifValid()).collect(Collectors.toList());
        tasks.removeAll(inValidTask);
        delTrainingTasks(inValidTask);
        //修复宝箱任务进度
        fixBoxProgress(uid, tasks);
        return tasks;
    }

    /**
     * 修复宝箱任务进度
     *
     * @param uid
     * @param tasks
     */
    private void fixBoxProgress(long uid, List<UserGodTrainingTask> tasks) {
        //任务是否为空
        if (ListUtil.isEmpty(tasks)) {
            return;
        }
        //宝箱任务
        List<UserGodTrainingTask> boxTasks = tasks.stream().filter(tmp -> tmp.getName().contains("宝箱") && TaskStatusEnum.DOING.getValue() == tmp.getStatus()).collect(Collectors.toList());
        //宝箱任务是否为空
        if (ListUtil.isEmpty(boxTasks)) {
            return;
        }
        //是否有未完成的宝箱任务
        List<Integer> taskIds = tasks.stream().filter(tmp -> TaskStatusEnum.AWARDED.getValue() == tmp.getStatus()).map(UserGodTrainingTask::getBaseId).collect(Collectors.toList());
        if (ListUtil.isEmpty(taskIds)) {
            return;
        }
        //上仙试炼任务配置
        CfgTaskConfig config = TaskTool.getTaskConfig(TaskGroupEnum.GOD_TRAINING);
        List<CfgTaskEntity> cfgTasks = config.getTasks();
        //试炼值
        int trialValue = 0;
        //计算已获得的试炼值
        for (CfgTaskEntity cfgTaskEntity : cfgTasks) {
            if (!taskIds.contains(cfgTaskEntity.getId())) {
                continue;
            }
            List<Award> awards = cfgTaskEntity.getAwards();
            for (Award award : awards) {
                if (AwardEnum.SLZ.getValue() != award.getItem()) {
                    continue;
                }
                trialValue += award.getNum();
            }

        }
        //没有试炼值
        if (0 == trialValue) {
            return;
        }
        //修复进度
        for (UserGodTrainingTask boxTask : boxTasks) {
            //需要增加的进度值
            int needAddProgress = (int) (trialValue - boxTask.getValue());
            //不需要增加
            if (needAddProgress <= 0) {
                continue;
            }
            //增加进度值
            addProgress(uid, boxTask.getBaseId(), needAddProgress);
            //增加进度值（用于本次显示）
            boxTask.setValue(trialValue > boxTask.getNeedValue() ? boxTask.getNeedValue() : trialValue);

        }
    }

    public UserGodTrainingTask getUserTrainingTask(long uid, int taskId) {
        return userCacheService.getCfgItem(uid, taskId, UserGodTrainingTask.class);
    }

    public RDCommon gainTaskAward(long uid, int taskId, String awardIndex) {
        RDCommon rd = new RDCommon();
        UserGodTrainingTask task = getUserTrainingTask(uid, taskId);
        // 任务不存在
        if (null == task) {
            throw new ExceptionForClientTip("task.not.exist");
        }
        // 奖励已领取
        if (TaskStatusEnum.AWARDED.getValue() == task.getStatus()) {
            throw new ExceptionForClientTip("task.already.award");
        }
        // 未达成
        if (TaskStatusEnum.DOING.getValue() == task.getStatus()) {
            throw new ExceptionForClientTip("task.not.accomplish");
        }
        List<Award> awards = TaskTool.getAwards(TaskGroupEnum.GOD_TRAINING, taskId);
        Integer index = task.getAwardIndex();
        if (null != index && index >= 0 && index <= awards.size()) {
            awards = Collections.singletonList(awards.get(index));
        }
        boolean isBoxTask = TaskTool.isBoxTask(taskId);
        WayEnum way = isBoxTask ? WayEnum.OPEN_GOD_TRAINING_BOX : WayEnum.OPEN_GOD_TRAINING_TASK;
        // 发送奖励
        awardService.fetchAward(uid, awards, way, "通过上仙试炼任务获得", rd);
        // 修改状态
        task.setStatus(TaskStatusEnum.AWARDED.getValue());
        gameUserService.updateItem(task);
        // 发布事件
        int step = task.getBaseId();
        String stepName = task.getName();
        BaseEventParam bep = new BaseEventParam(uid);
        // 记录日志(当做新手任务记录)
        NewbieTaskEventPublisher.pubFinishNewbieTaskEvent(new EPFinishNewbieTask(step, stepName, bep));
        // 如果全部都领取了，标记通过新手任务
        List<UserGodTrainingTask> userTrainingTasks = getCurUserTrainingTasks(uid);
        int count = (int) userTrainingTasks.stream().filter(tmp ->
                tmp.getStatus() == TaskStatusEnum.AWARDED.getValue()).count();
        if (count == userTrainingTasks.size()) {
            GameUser gu = gameUserService.getGameUser(uid);
            gu.getStatus().setGrowTaskCompleted(true);
            gu.updateStatus();
        }
        return rd;
    }

    public boolean ifShowTrainingTask(GameUser gu) {
        long uid = gu.getId();
        // 任务结束的不展示入口
        if (gu.getStatus().isGrowTaskCompleted()) {
            return false;
        }
        // 当前拥有的任务都领取了，修改状态，不展示入口
        List<UserGodTrainingTask> userTrainingTasks = getCurUserTrainingTasks(uid);
        int count = (int) userTrainingTasks.stream().filter(tmp ->
                tmp.getStatus() == TaskStatusEnum.AWARDED.getValue()).count();
        if (count == userTrainingTasks.size()) {
            gu.getStatus().setGrowTaskCompleted(true);
            gu.updateStatus();
            return false;
        }
        return true;
    }

    public List<RDTaskItem> getCurAccomplishRDTasks(long uid) {
        GameUser gu = gameUserService.getGameUser(uid);
        if (gu.getStatus().isGrowTaskCompleted()) {
            return new ArrayList<>();
        }
        List<UserGodTrainingTask> tasks = getCurUserTrainingTasks(uid);
        Date beginTime = DateUtil.fromDateLong(tasks.stream().findFirst().get().getGenerateTime());
        Date now = DateUtil.now();
        int daysBetween = DateUtil.getDaysBetween(beginTime, now) + 1;
        tasks = tasks.stream().filter(tmp ->
                tmp.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()
                        && daysBetween >= tmp.getDays()
                        && !BOX_TASK.contains(tmp.getBaseId())
        ).collect(Collectors.toList());
        return tasks.stream().map(RDTaskItem::getInstance).collect(Collectors.toList());
    }

    public void addProgress(long uid, int taskId, int value) {
        UserGodTrainingTask userTrainingTask = getUserTrainingTask(uid, taskId);
        if (null == userTrainingTask) {
            return;
        }
        Integer status = userTrainingTask.getStatus();
        if (status >= TaskStatusEnum.ACCOMPLISHED.getValue()) {
            return;
        }
        userTrainingTask.addValue(value);
        gameUserService.updateItem(userTrainingTask);
    }

    public void updateProgress(long uid, int taskId, int value) {
        UserGodTrainingTask userTrainingTask = getUserTrainingTask(uid, taskId);
        if (null == userTrainingTask) {
            return;
        }
        Integer status = userTrainingTask.getStatus();
        if (status >= TaskStatusEnum.ACCOMPLISHED.getValue()) {
            return;
        }
        userTrainingTask.updateValue(value);
        gameUserService.updateItem(userTrainingTask);
    }
}