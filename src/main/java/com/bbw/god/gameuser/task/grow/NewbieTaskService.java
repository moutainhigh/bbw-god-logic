package com.bbw.god.gameuser.task.grow;

import com.bbw.cache.UserCacheService;
import com.bbw.common.ListUtil;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGame;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.buddy.BuddyService;
import com.bbw.god.gameuser.task.*;
import com.bbw.god.gameuser.task.CfgTaskConfig.CfgBox;
import com.bbw.god.login.DynamicMenuEnum;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.ServerUserService;
import com.bbw.god.server.monster.MonsterService;
import com.bbw.god.server.monster.ServerMonster;
import com.bbw.mc.m2c.M2cService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 作者 ：lwb
 * @version 创建时间：2020年2月11日 下午10:27:24
 * 类说明  新手任务
 */
@Slf4j
@Service
public class NewbieTaskService {

    private static final TaskGroupEnum taskGroupEnum = TaskGroupEnum.TASK_NEWBIE;
    private static final Integer boxBeginIndex = taskGroupEnum.getValue() + 900;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private BuddyService buddyService;
    @Autowired
    private ServerUserService serverUserService;
    @Autowired
    private M2cService m2cService;
    @Autowired
    private MonsterService monsterService;
    @Autowired
    private ServerService serverService;
    @Autowired
    private UserCacheService userCacheService;

    public int getBoxbeginIndex() {
        return boxBeginIndex;
    }

    /**
     * 获取玩家所有新手任务记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @return
     */
    public List<UserGrowTask> getAllNewbieTasks(long uid) {
        List<UserGrowTask> list = this.userCacheService.getUserDatas(uid, UserGrowTask.class);
        if (ListUtil.isEmpty(list)) {
            return new ArrayList<UserGrowTask>();
        }
        return list;
    }

    /**
     * 添加玩家新手任务记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @param userGrowTask
     */
    public void addUserGrowTasks(long uid, UserGrowTask userGrowTask) {
        userCacheService.addUserData(userGrowTask);
    }

    /**
     * 获取玩家单个新手任务记录，游戏业务逻辑必须调用该方法！！！<br/>
     * 不能直接使用GameUserService
     *
     * @param uid
     * @param baseId
     * @return
     */
    public Optional<UserGrowTask> getUserGrowTask(long uid, int baseId) {
        List<UserGrowTask> tasks = getAllNewbieTasks(uid);
        Optional<UserGrowTask> taskOp = tasks.stream().filter(p -> p.getBaseId() == baseId).findFirst();
        return taskOp;
    }

    public List<RDTaskItem> toRdDailyTasks(long uid) {
        List<UserGrowTask> tasks = getCurrentProgress(uid);
        List<RDTaskItem> rdTasks = new ArrayList<>();
        CfgTaskConfig cfg = TaskTool.getTaskConfig(taskGroupEnum);
        List<CfgTaskEntity> cfgTasks = cfg.getTasks();
        List<CfgBox> cfgBoxs = cfg.getBoxs();
        if (tasks.isEmpty()) {
            for (CfgBox box : cfgBoxs) {
                // 玩家没有任务 说明是完成了所有新手任务
                // 构造4个已领取的宝箱
                rdTasks.add(RDNewbieTask.fromCfgBoxTask(box));
            }
        } else {
            for (UserGrowTask task : tasks) {
                if (boxBeginIndex < task.getBaseId()) {
                    Optional<CfgBox> box = cfgBoxs.stream()
                            .filter(p -> p.getId().intValue() == task.getBaseId().intValue()).findFirst();
                    rdTasks.add(RDNewbieTask.fromUserBoxTask(task, box.get()));
                } else {
                    Optional<CfgTaskEntity> cfgOp = cfgTasks.stream()
                            .filter(p -> p.getId().intValue() == task.getBaseId().intValue()).findFirst();
                    rdTasks.add(RDNewbieTask.fromUserTask(task, cfgOp.get()));
                }
            }
        }
        return rdTasks;
    }

    /**
     * 获取当前进度的任务
     *
     * @param uid
     * @return
     */
    public List<UserGrowTask> getCurrentProgress(long uid) {
        GameUser gu = this.gameUserService.getGameUser(uid);
        if (gu.getStatus().isGrowTaskCompleted()) {
            return new ArrayList<UserGrowTask>();
        }
        int seq = getTaskCurrentSeq(uid);
        UserGrowTask task = getCurrentTask(uid, seq + 1);
        List<UserGrowTask> rdTasks = getUpdateBoxs(uid, seq);
        if (task != null) {
            if (!task.ifAccomplished()) {
                if (task.getBaseId() == 80) {
                    addFriend(uid);
                } else if (task.getBaseId() == 90) {
                    addMonster(uid);
                }
            }
            rdTasks.add(0, task);
        }
        return rdTasks;
    }

    private void addFriend(long uid) {
        int count = this.buddyService.getAskCount(uid);
        if (count == 0) {
            // 添加请求,给对方添加一条请求数据
            int sid = this.gameUserService.getActiveSid(uid);
            String firstAccount = Cfg.I.getUniqueConfig(CfgGame.class).getFirstAccount();
            long applierId = this.serverUserService.getUidByUsername(sid, firstAccount).get();
            this.buddyService.sendAsk(applierId, uid);
        }
    }

    private void addMonster(long uid) {
        GameUser gu = this.gameUserService.getGameUser(uid);
        List<ServerMonster> sMonsters = this.monsterService.getBuddyMonsters(uid, gu.getServerId());
        int monsterCount = sMonsters.size();
        if (monsterCount > 0) {
            this.m2cService.sendDynamicMenu(uid, DynamicMenuEnum.MG, monsterCount);
            return;
        }
        Set<Long> buddyIds = this.buddyService.getFriendUids(uid);
        if (buddyIds.size() > 0) {
            monsterCount++;
            long finderId = PowerRandom.getRandomFromSet(buddyIds);
            GameUser finder = this.gameUserService.getGameUser(finderId);
            ServerMonster sMonster = ServerMonster.fromGuForNewerGuide(uid, gu.getRoleInfo().getCountry(), finder);
            this.serverService.addServerData(gu.getServerId(), sMonster);
            this.m2cService.sendDynamicMenu(uid, DynamicMenuEnum.MG, monsterCount);
        } else {
            addFriend(uid);
        }

    }

    public RDTaskItem getCurrentProgressRDTask(long uid) {
        int seq = getTaskCurrentSeq(uid);
        UserGrowTask task = getCurrentTask(uid, seq + 1);
        if (task == null) {
            return null;
        }
        CfgTaskConfig cfg = TaskTool.getTaskConfig(taskGroupEnum);
        List<CfgTaskEntity> cfgTasks = cfg.getTasks();
        Optional<CfgTaskEntity> cfgOp = cfgTasks.stream()
                .filter(p -> p.getId().intValue() == task.getBaseId().intValue()).findFirst();
        if (!cfgOp.isPresent()) {
            return null;
        }
        return RDNewbieTask.fromUserTask(task, cfgOp.get());
    }

    private Integer getTaskCurrentSeq(long uid) {
        CfgTaskConfig cfg = TaskTool.getTaskConfig(taskGroupEnum);
        List<CfgTaskEntity> cfgtasks = cfg.getTasksOrderBySeq();
        List<UserGrowTask> tasks = getAllNewbieTasks(uid);
        int progress = 0;
        for (CfgTaskEntity task : cfgtasks) {
            Optional<UserGrowTask> optional = tasks.stream().filter(p -> p.getBaseId().equals(task.getId()))
                    .findFirst();
            if (!optional.isPresent()) {
                // 缺少当前序列的任务，新任务
                updateTask(uid);
                return progress;
            }
            UserGrowTask ugTask = optional.get();
            if (!ugTask.ifAwarded()) {
                return progress;
            }
            progress = task.getSeq();
        }
        return progress;
    }

    /**
     * 获取当前宝箱并修正进度
     *
     * @param uid
     * @param progress
     * @return
     */
    public List<UserGrowTask> getUpdateBoxs(long uid, int progress) {
        List<UserGrowTask> tasks = getAllNewbieTasks(uid);
        List<UserGrowTask> boxs = tasks.stream().filter(p -> p.getBaseId() > boxBeginIndex)
                .collect(Collectors.toList());
        if (ListUtil.isEmpty(boxs)) {
            // 说明当前的任务还是旧的任务 需要升级成新版任务
            updateTask(uid);
            tasks = getAllNewbieTasks(uid);
            boxs = tasks.stream().filter(p -> p.getBaseId() > boxBeginIndex).collect(Collectors.toList());
        }
        for (UserGrowTask boxTask : boxs) {
            if (boxTask.ifAccomplished()) {
                continue;
            }
            boxTask.updateProgress(progress);
            this.gameUserService.updateItem(boxTask);
        }
        return boxs;
    }

    /**
     * 获取当前宝箱状态
     *
     * @param uid
     * @return
     */
    public List<UserGrowTask> getBoxs(long uid) {
        List<UserGrowTask> tasks = getCurrentProgress(uid);
        if (tasks.isEmpty()) {
            return new ArrayList<UserGrowTask>();
        }
        return tasks.stream().filter(p -> p.getBaseId() > boxBeginIndex).collect(Collectors.toList());
    }

    public UserGrowTask getCurrentTask(long uid, int progress) {
        CfgTaskConfig cfg = TaskTool.getTaskConfig(taskGroupEnum);
        Optional<CfgTaskEntity> taskOptional = cfg.getTasks().stream().filter(p -> p.getSeq() == progress).findFirst();
        if (!taskOptional.isPresent()) {
            return null;
        }
        int taskId = taskOptional.get().getId();
        List<UserGrowTask> tasks = getAllNewbieTasks(uid);
        Optional<UserGrowTask> taskOp = tasks.stream().filter(p -> p.getBaseId() == taskId).findFirst();
        return taskOp.isPresent() ? taskOp.get() : null;
    }

    /**
     * 更新旧版任务 只针对为达成的任务
     *
     * @param uid
     */
    public void updateTask(long uid) {
        GameUser gu = this.gameUserService.getGameUser(uid);
        if (gu.getStatus().isGrowTaskCompleted()) {
            return;
        }
        CfgTaskConfig cfg = TaskTool.getTaskConfig(taskGroupEnum);
        List<UserGrowTask> tasks = getAllNewbieTasks(uid);
        List<Integer> taskIds = cfg.getAllIds();
        List<UserGrowTask> unValidTasks = tasks.stream().filter(p -> !taskIds.contains(p.getBaseId()))
                .collect(Collectors.toList());
        tasks.removeAll(unValidTasks);
        // 删除失效的任务
        this.gameUserService.deleteItems(uid, unValidTasks);
        // 核对任务
        for (CfgTaskEntity task : cfg.getTasksOrderBySeq()) {
            Optional<UserGrowTask> op = tasks.stream().filter(p -> p.getBaseId().equals(task.getId())).findFirst();
            if (!op.isPresent()) {
                UserGrowTask nugt = UserGrowTask.fromTask(uid, task);
                addUserGrowTasks(uid, nugt);
                continue;
            }
            UserGrowTask ugt = op.get();
            if (ugt.ifAccomplished()) {
                ugt.setValue(ugt.getNeedValue());
            } else {
                ugt.updateInfo(task.getValue(), task.getName());
            }
            this.gameUserService.updateItem(ugt);
        }
        // 核对宝箱
        for (CfgBox box : cfg.getBoxs()) {
            Optional<UserGrowTask> op = tasks.stream().filter(p -> p.getBaseId().equals(box.getId())).findFirst();
            if (!op.isPresent()) {
                UserGrowTask nugt = UserGrowTask.fromTask(uid, box);
                addUserGrowTasks(uid, nugt);
                continue;
            }
            UserGrowTask ugt = op.get();
            if (ugt.ifAccomplished()) {
                continue;
            }
            ugt.updateInfo(box.getScore(), "宝箱" + box.getId());
            this.gameUserService.updateItem(ugt);
        }
    }

    /**
     * 删除新手进阶任务
     *
     * @param guId
     * @return
     */
    public void delGrowTasks(long guId) {
        List<UserGrowTask> ugTasks = getAllNewbieTasks(guId);
        if (ListUtil.isNotEmpty(ugTasks)) {
            this.gameUserService.deleteItems(guId, ugTasks);
        }
    }

    /**
     * 是否通过新手引导任务
     *
     * @param gu
     * @return
     */
    public boolean isPassGrowTask(GameUser gu) {
        if (gu.getStatus().isGrowTaskCompleted()) {
            return true;
        }
        List<UserGrowTask> ugTasks = getAllNewbieTasks(gu.getId());
        for (UserGrowTask ugTask : ugTasks) {
            if (!ugTask.ifAwarded()) {
                return false;
            }
        }
        gu.getStatus().setGrowTaskCompleted(true);
        gu.updateStatus();
        return true;
    }
}
