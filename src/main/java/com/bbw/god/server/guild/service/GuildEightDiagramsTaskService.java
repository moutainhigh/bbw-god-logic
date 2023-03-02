package com.bbw.god.server.guild.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.bbw.god.server.ServerUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgGuild;
import com.bbw.god.game.config.CfgGuild.BoxReward;
import com.bbw.god.game.config.CfgGuild.TaskInfo;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.task.fshelper.FsTaskEnum;
import com.bbw.god.gameuser.task.fshelper.event.EpFsHelperChange;
import com.bbw.god.gameuser.task.fshelper.event.TaskEventPublisher;
import com.bbw.god.notify.rednotice.ModuleEnum;
import com.bbw.god.notify.rednotice.RedNoticeService;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.guild.GuildConstant;
import com.bbw.god.server.guild.GuildInfo;
import com.bbw.god.server.guild.GuildMark;
import com.bbw.god.server.guild.GuildRD;
import com.bbw.god.server.guild.GuildReward;
import com.bbw.god.server.guild.GuildTask;
import com.bbw.god.server.guild.GuildTools;
import com.bbw.god.server.guild.UserGuild;
import com.bbw.god.server.guild.UserGuildTaskInfo;
import com.bbw.god.server.guild.GuildRD.RdGuildTaskInfo;
import com.bbw.god.server.guild.event.EPGuildEightDiagramsTask;
import com.bbw.god.server.guild.event.EPGuildTaskFinished;
import com.bbw.god.server.guild.event.GuildEventPublisher;
import com.bbw.mc.m2c.M2cService;

/**
 * @author lwb 行会八卦任务
 * @version 1.0
 * @date 2019年5月15日
 */
@Slf4j
@Service
public class GuildEightDiagramsTaskService {
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GuildInfoService guildInfoService;
    @Autowired
    private M2cService m2cService;
    @Autowired
    private RedNoticeService redNoticeService;
    @Autowired
    private GuildUserService guildUserService;
	@Autowired
	private GuildAwardService guildAwardService;
	@Autowired
	private ServerUserService serverUserService;

    private static final Integer FIVE_MINUTES = 5;

	public Optional<UserGuildTaskInfo> getGuildTaskInfoOp(long uid) {
		if (!guildUserService.hasGuild(uid)) {
			return Optional.empty();
		}
		UserGuildTaskInfo info = gameUserService.getSingleItem(uid, UserGuildTaskInfo.class);
		if (info == null) {
			info = UserGuildTaskInfo.instance(uid);
			gameUserService.addItem(uid, info);
		}
		initTask(info);
		return Optional.of(info);
	}

	/**
	 * 为空时抛出提示
	 * 
	 * @param uid
	 * @return
	 */
	public UserGuildTaskInfo getGuildTaskInfo(long uid) {
		Optional<UserGuildTaskInfo> infOptional = getGuildTaskInfoOp(uid);
		if (infOptional.isPresent()) {
			return infOptional.get();
		}
		return null;
	}


	// 初始化任务信息
	public void initTask(UserGuildTaskInfo taskInfo) {
		if (taskInfo.getBuildDate() == DateUtil.getTodayInt()) {
			return;
		}
		taskInfo.setBuildDate(DateUtil.toDateInt(new Date()));
		List<GuildTask> tasks = new ArrayList<>();
		int level = guildUserService.getGuildLv(taskInfo.getGameUserId());
		tasks.add(bulidTask(tasks, level));
		tasks.add(bulidTask(tasks, level));
		tasks.add(bulidTask(tasks, level));
		taskInfo.setTasks(tasks);
		taskInfo.setBox(new ArrayList<>());
		taskInfo.setComplete( GuildConstant.COMPLETE);
		taskInfo.setRefreshCount(0);
		taskInfo.setGainBoxNum(0);
		gameUserService.updateItem(taskInfo);
	}

    // 获取任务列表
    public GuildRD list(int sid, long uid) {
        GuildRD rd = new GuildRD();
		Optional<UserGuildTaskInfo> infoOp = getGuildTaskInfoOp(uid);
		if (!infoOp.isPresent()) {
			// 不存在 说明玩家未加入行会
			throw new ExceptionForClientTip("guild.user.not.join");
		}
		UserGuildTaskInfo taskInfo = infoOp.get();
		int guildLv = 0;
        for (GuildTask task : taskInfo.getTasks()) {
			task.setRewards(GuildTools.getTaskAwards(task.getLevel(), guildLv));
        }
		UserGuild userGuild = guildUserService.getUserGuild(uid);
        GuildInfo info = this.guildInfoService.getGuildInfoBydataId(sid, userGuild.getGuildId());
        rd.setGainNum(taskInfo.getBox().size());
        rd.setMaxGainNum(info.getMaxBox());
        rd.setGainedNum(taskInfo.getGainBoxNum());
        RdGuildTaskInfo guildTaskInfo=RdGuildTaskInfo.instance(taskInfo);
        guildTaskInfo.setProgress(info.getEightDiagrams());
		int[] acceptStatus=new int[8];
		for (Long memberUid : info.getMembers()) {
			try {
				Optional<UserGuildTaskInfo> memberOp = getGuildTaskInfoOp(memberUid);
				if (!memberOp.isPresent()) {
					continue;
				}
				Optional<GuildTask> doingTaskOp = memberOp.get().doingTask();
				if (doingTaskOp.isPresent()) {
					Integer edNumber = doingTaskOp.get().getEdNumber();
					if (acceptStatus[edNumber-1]==2){
						continue;
					}
					acceptStatus[edNumber-1]=serverUserService.isOnline(memberUid)?2:1;
				}
			}catch (Exception e){
				log.error("行会：{},异常玩家ID：{}",info.getId(),memberUid);
				log.error(e.getMessage(),e);
			}
		}
		guildTaskInfo.setAcceptStatus(acceptStatus);
        rd.setGuildTask(guildTaskInfo);
        return rd;
    }

	/**
	 * 任务操作
	 * 
	 * @param uid
	 * @param taskId
	 * @param option
	 * @return
	 */
	public GuildRD taskOption(long uid, int taskId, int option) {
		Optional<UserGuildTaskInfo> taskInfoOp = getGuildTaskInfoOp(uid);
		if (!taskInfoOp.isPresent()) {
			// 没有任务
			throw new ExceptionForClientTip("guild.task.error");
		}
		UserGuildTaskInfo taskInfo = taskInfoOp.get();
		Optional<GuildTask> taskOp = taskInfo.getTasks().stream().filter(p -> p.getTaskId() == taskId).findFirst();
		if (!taskOp.isPresent()) {
			// 任务不存在
			throw new ExceptionForClientTip("guild.task.error");
		}
		GuildRD rd = new GuildRD();
		switch (option) {
		case GuildConstant.OPTION_ACCEPT:
			accepetTask(taskOp.get(), taskInfo);
			break;
		case GuildConstant.OPTION_CANCEL:
			cancelTask(taskInfo);
			break;
		case GuildConstant.OPTION_GAIN:
			gainTask(taskOp.get(), taskInfo, rd);
			break;
		default:
			break;
		}
		return rd;
	}

	// 接受任务
	private void accepetTask(GuildTask task, UserGuildTaskInfo taskInfo) {
		if (taskInfo.getComplete()<=0) {
			throw new ExceptionForClientTip("guild.task.not.accept");
		}
		for (GuildTask t : taskInfo.getTasks()) {
			if (GuildConstant.STATUS_NORMAL == t.getStatus().intValue()) {
				t.setStatus(GuildConstant.STATUS_NOT);
			} else {
				// 如果任务状态不是待接 则说明有进行中的任务
				throw new ExceptionForClientTip("guild.task.doing");
			}
		}
		// 其他任务正常，则将接受的任务状态设置为已接受
		task.setStatus(GuildConstant.STATUS_DO);// 接受任务
		gameUserService.updateItem(taskInfo);
	}

	// 取消任务
	private void cancelTask(UserGuildTaskInfo taskInfo) {
		for (GuildTask t : taskInfo.getTasks()) {
			t.setStatus(GuildConstant.STATUS_NORMAL);
		}
		gameUserService.updateItem(taskInfo);
	}

	// 完成任务
	private void gainTask(GuildTask task, UserGuildTaskInfo taskInfo, GuildRD rd) {
		if (task.getStatus() != GuildConstant.STATUS_FINISHED) {
			// 未完成
			throw new ExceptionForClientTip("guild.task.not.done");
		}
		// 发送奖励
		long uid = taskInfo.getGameUserId();
		int guildLv = guildUserService.getGuildLv(uid);
		List<GuildReward> rewards = GuildTools.getTaskAwards(task.getLevel(), guildLv);
		guildAwardService.sendTaskAward(rewards, uid, rd);
		taskInfo.setComplete(taskInfo.getComplete() - 1);
		rd.setAddedEdNumber(task.getEdNumber());
		taskInfo.getTasks().forEach(p -> p.setStatus(GuildConstant.STATUS_NORMAL));
		taskInfo.getTasks().remove(task);
		taskInfo.getTasks().add(bulidTask(taskInfo.getTasks(), guildLv));
		if (taskInfo.getComplete()<=0) {
			taskInfo.setComplete(0);
			for (GuildTask t:taskInfo.getTasks()) {
				t.setStatus(GuildConstant.STATUS_NOT);
			}
		}
		gameUserService.updateItem(taskInfo);
		// 完成任务事件
		EPGuildTaskFinished event = EPGuildTaskFinished.instance(new BaseEventParam(uid), task.getTaskIndex(),
				task.getEdNumber());
		GuildEventPublisher.pubGuildTaskFinished(event);
	}

    // 任务进度更新
	public void updateTaskProgress(long uid, int taskIndex, int num) {
		Optional<UserGuildTaskInfo> taskInfoOp = getGuildTaskInfoOp(uid);
		if (!taskInfoOp.isPresent()) {
			return;
		}
		UserGuildTaskInfo taskInfo = taskInfoOp.get();
		Optional<GuildTask> taskOptional = taskInfo.getTasks().stream()
				.filter(p -> p.getTaskIndex() == taskIndex && p.getStatus() == GuildConstant.STATUS_DO).findFirst();
		if (!taskOptional.isPresent()) {
			return;
		}
		GuildTask task = taskOptional.get();
		task.addVal(num);
		if (task.getStatus() == GuildConstant.STATUS_FINISHED) {
			// 任务完成
			String redNotice = this.redNoticeService.buildNoticeData3(ModuleEnum.GUILD, GuildMark.TASK.getValue(), 1);
			this.m2cService.sendRedNotice(uid, Arrays.asList(redNotice));
		}
		EpFsHelperChange dta = EpFsHelperChange.instanceUpdateTask(new BaseEventParam(uid), FsTaskEnum.Guild,
				task.getTaskId());
		TaskEventPublisher.pubEpFsHelperChangeEvent(dta);
		this.gameUserService.updateItem(taskInfo);
    }

	/**
	 * 更新八卦字
	 * 
	 * @param uid
	 * @param edNumber
	 */
	public void updateEDNumber(long uid, int edNumber) {
		GuildInfo guildInfo = guildInfoService.getGuildInfo(uid, gameUserService.getActiveSid(uid));
		guildInfo.getEightDiagrams().set(edNumber - 1, 1);
		if (guildInfo.getEightDiagrams().contains(0)) {
			serverDataService.updateServerData(guildInfo);
			return;
		}
		guildInfo.setEightDiagrams(new ArrayList<>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0)));
		BoxReward boxReward = GuildTools.getBoxReward(guildInfo.getLv());
		for (Long mb : guildInfo.getMembers()) {
			Optional<UserGuild> optional = guildUserService.getUserGuildOp(mb);
			if (optional.isPresent()) {
				Optional<UserGuildTaskInfo> taskInfoOp = getGuildTaskInfoOp(mb);
				if (taskInfoOp.isPresent()) {
					UserGuildTaskInfo userGuildTaskInfo = taskInfoOp.get();
					userGuildTaskInfo.addBox(boxReward);
					gameUserService.updateItem(userGuildTaskInfo);
				}
			}
		}
		serverDataService.updateServerData(guildInfo);
	}

	/**
	 * 任务求助
	 * 
	 * @param uid
	 */
    public void TaskHelp(long uid) {
        GameUser gu = this.gameUserService.getGameUser(uid);
        Integer serverId = gu.getServerId();
        GuildInfo guild = this.guildInfoService.getGuildInfo(uid, serverId);
        if (guild.getLastPushHelpTime() != null) {
            long minutesBetween = DateUtil.getMinutesBetween(guild.getLastPushHelpTime(), DateUtil.now());
            if (minutesBetween < FIVE_MINUTES) {
                return;
            }
        }
        GuildEventPublisher.pubGuildEightDiagramsTaskEvent(EPGuildEightDiagramsTask.instance(uid, WayEnum.Guild_Help, new GuildRD(), guild));
    }

    // 刷新任务第1次：免费 第2次：10元宝 第3次：15元宝 3次后：20元宝
	public GuildRD taskRefresh(long uid) {
        GuildRD rd = new GuildRD();
		UserGuildTaskInfo taskInfo = getGuildTaskInfo(uid);
		Optional<GuildTask> optional = taskInfo.getTasks().stream()
				.filter(p -> p.getStatus() == GuildConstant.STATUS_DO || p.getStatus() == GuildConstant.STATUS_FINISHED)
				.findFirst();
		if (optional.isPresent()) {
			throw new ExceptionForClientTip("guild.task.doing");
		}
        int refreshCount = taskInfo.getRefreshCount();
        int needGold = 0;
        if (refreshCount == 2) {
            needGold = 10;
        } else if (refreshCount == 3) {
            needGold = 15;
        }
        if (refreshCount > 3) {
            needGold = 20;
        }
		if (needGold > 0) {
			// 扣除刷新费用
			ResChecker.checkGold(this.gameUserService.getGameUser(uid), needGold);
			ResEventPublisher.pubGoldDeductEvent(uid, needGold, WayEnum.Guild_TASK_REFRESH, rd);
		}
        taskInfo.setRefreshCount(refreshCount + 1);
		int guildLv = guildUserService.getGuildLv(uid);
		List<GuildTask> tasks = new ArrayList<>();
		tasks.add(bulidTask(tasks, guildLv));
		tasks.add(bulidTask(tasks, guildLv));
		tasks.add(bulidTask(tasks, guildLv));
        taskInfo.setTasks(tasks);
		gameUserService.updateItem(taskInfo);
        return rd;
    }

    // 开宝箱
    public GuildRD opendBox(long uid, int sid) {
        GuildRD rd = new GuildRD();
		UserGuildTaskInfo taskInfo = getGuildTaskInfo(uid);
		if (taskInfo.getBox().size() == 0) {
            throw new ExceptionForClientTip("guild.task.not.box");
        }
		BoxReward box = taskInfo.getBox().get(0);
		guildAwardService.openBox(box, guildUserService.getGuildLv(uid), uid, rd);
		taskInfo.getBox().remove(0);
		this.gameUserService.updateItem(taskInfo);
        GuildEventPublisher.pubGuildOpenBoxEvent(box, new BaseEventParam(uid, WayEnum.Guild_Box, rd));
        return rd;
    }

    // 任务生成 只需要字牌不一样即可
	private GuildTask bulidTask(List<GuildTask> tasks, int guildLv) {
        Random rd = new Random();
        CfgGuild cfgGuild = Cfg.I.getUniqueConfig(CfgGuild.class);
        int edNumber = rd.nextInt(8) + 1;
        boolean isRepeat = false;
		TaskInfo taskInfo = GuildTools.getRandomTask();
        do {
            isRepeat = false;
			for (GuildTask t : tasks) {
                if (t.getEdNumber().intValue() == edNumber) {
                    edNumber = rd.nextInt(8) + 1;
                    isRepeat = true;
                }
            }
			for (GuildTask t : tasks) {
                if (t.getTaskIndex().equals(taskInfo.getId())) {
					taskInfo = GuildTools.getRandomTask();
                    isRepeat = true;
                }
            }
        } while (isRepeat);
        int taskId = taskInfo.getId();
        int lv = 1;
        if (guildLv == 2) {
            lv = rd.nextInt(2) + 1;
        } else if (guildLv > 2) {
            lv = rd.nextInt(3) + (guildLv - 2);
        }
        lv = lv > cfgGuild.getMaxLevel() ? cfgGuild.getMaxLevel() : lv;// 防止等级溢出
		GuildTask task = new GuildTask();
        String ids = String.format("%d%d", edNumber, taskId);
        int id = Integer.parseInt(ids);//生成任务ID    八卦字+任务ID
        task.setTaskId(id);
        task.setTaskIndex(taskId);
        task.setLevel(lv);
        task.setEdNumber(edNumber);
        task.setTarget(taskInfo.getTarget());
        task.setProgress("0/" + taskInfo.getTarget());
        return task;
    }

    public GuildRD getAcceptedTaskInfo(long uid) {
        GuildRD rd = new GuildRD();
        GuildTask task = getAcceptedRDTaskInfo(uid);
        if (task != null) {
            rd.setTaskInfo(task);
        }
        return rd;
    }

    public GuildTask getAcceptedRDTaskInfo(long uid) {
		Optional<UserGuildTaskInfo> infOptional = getGuildTaskInfoOp(uid);
		if (!infOptional.isPresent()) {
			return null;
		}
		Optional<GuildTask> taskOp = infOptional.get().getTasks().stream()
				.filter(p -> p.getStatus() == GuildConstant.STATUS_DO || p.getStatus() == GuildConstant.STATUS_FINISHED)
				.findFirst();
		if (taskOp.isPresent()) {
			return taskOp.get();
		}
        return null;
    }

    public Integer getTaskTimes(long uid) {
		Optional<UserGuildTaskInfo> infOptional = getGuildTaskInfoOp(uid);
		if (infOptional.isPresent()) {
			return infOptional.get().getComplete();
		}
        return 0;
    }


}
