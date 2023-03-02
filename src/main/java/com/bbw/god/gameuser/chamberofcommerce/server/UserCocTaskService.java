package com.bbw.god.gameuser.chamberofcommerce.server;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.holiday.processor.HolidayCocProcessor;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgCoc;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.chamberofcommerce.*;
import com.bbw.god.gameuser.chamberofcommerce.CocTask.CocSpecial;
import com.bbw.god.gameuser.chamberofcommerce.CocTask.Task;
import com.bbw.god.gameuser.chamberofcommerce.event.CocEventPublisher;
import com.bbw.god.gameuser.chamberofcommerce.event.EPTaskFinished;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.task.fshelper.FsTaskEnum;
import com.bbw.god.gameuser.task.fshelper.event.EpFsHelperChange;
import com.bbw.god.gameuser.task.fshelper.event.TaskEventPublisher;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.notify.rednotice.ModuleEnum;
import com.bbw.god.notify.rednotice.RedNoticeService;
import com.bbw.mc.m2c.M2cService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 商会任务Service
 *
 * @author lwb
 * @version 1.0
 * @date 2019年4月12日
 */
@Service
public class UserCocTaskService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCocExpTaskService expTaskService;
    @Autowired
    private UserCocInfoService userCocInfoService;
    @Autowired
    private M2cService m2cService;
    @Autowired
    private RedNoticeService redNoticeService;
	@Autowired
	private UserCocAwardService userCocAwardService;
	@Autowired
    private HolidayCocProcessor holidayCocProcessor;

	public Optional<UserCocTaskInfo> getTaskInfoOp(long uid) {
		if (!userCocInfoService.opened(uid)) {
			return Optional.empty();
		}
		UserCocTaskInfo taskInfo = gameUserService.getSingleItem(uid, UserCocTaskInfo.class);
		if (taskInfo == null) {
			taskInfo = UserCocTaskInfo.instance(uid);
			gameUserService.addItem(uid, taskInfo);
		}
		init(taskInfo);
		return Optional.of(taskInfo);
	}
    /**
     * 任务初始化
     *
     * @param uid
     */
	public void init(UserCocTaskInfo taskInfo) {
		if (taskInfo.getTaskBuildDate() == DateUtil.toDateInt(new Date())) {
			return;
        }
		long uid = taskInfo.getGameUserId();
        List<CocTask> cocTasks = new ArrayList<>();
        cocTasks.add(buildCocTask(uid, CocConstant.LEVEL_LOW, true, 101));// 设置初始化的默认任务Id 101-103
        cocTasks.add(buildCocTask(uid, CocConstant.LEVEL_MIDDLE, true, 102));
        cocTasks.add(buildCocTask(uid, CocConstant.LEVEL_HEIGH, true, 103));
        taskInfo.setCocTasks(cocTasks);
		taskInfo.setRefreshCount(0);
		taskInfo.setFinishedCount(3);
		taskInfo.setTaskBuildDate(DateUtil.toDateInt(new Date()));
		gameUserService.updateItem(taskInfo);
    }

	public void repair(long uid) {
		Optional<UserCocTaskInfo> infoOp = getTaskInfoOp(uid);
		if (!infoOp.isPresent()) {
			return;
		}
		UserCocTaskInfo taskInfo = infoOp.get();
        List<CocTask> cocTasks = new ArrayList<>();
        cocTasks.add(buildCocTask(uid, CocConstant.LEVEL_LOW, true, 101));// 设置初始化的默认任务Id 101-103
        cocTasks.add(buildCocTask(uid, CocConstant.LEVEL_MIDDLE, true, 102));
        cocTasks.add(buildCocTask(uid, CocConstant.LEVEL_HEIGH, true, 103));
        taskInfo.setCocTasks(cocTasks);
		taskInfo.setRefreshCount(0);
		taskInfo.setFinishedCount(3);
		taskInfo.setTaskBuildDate(DateUtil.toDateInt(new Date()));
		gameUserService.updateItem(taskInfo);
	}
    /**
     * 刷新一组任务
     */
    public RDCoc refreshAll(long uid) {
        RDCoc rd = new RDCoc();
		Optional<UserCocTaskInfo> infoOp = getTaskInfoOp(uid);
		if (!infoOp.isPresent()) {
			throw new ExceptionForClientTip("coc.not.creat");
		}
		UserCocTaskInfo info = infoOp.get();
		List<CocTask> tlist = info.getCocTasks();
        for (CocTask coc : tlist) {
            if (coc.getStatus() == CocConstant.TASK_STATUS_DOING || coc.getStatus() == CocConstant.TASK_STATUS_FINISHED) {
                throw new ExceptionForClientTip("coc.task.doing");
            }
        }
		int taskid = 100 * (info.getRefreshCount() + 2);
		if (info.getRefreshCount() > 1) {
            // 扣除20元宝 首次免费刷新
            ResChecker.checkGold(this.gameUserService.getGameUser(uid), 20);
            ResEventPublisher.pubGoldDeductEvent(uid, 20, WayEnum.Chamber_Of_Commerce_TASK_REFRESH, rd);
            LogUtil.logDeletedUserData(info);
        }
        List<CocTask> newList = new ArrayList<>();
        newList.add(buildCocTask(uid, CocConstant.LEVEL_LOW, false, taskid));
        newList.add(buildCocTask(uid, CocConstant.LEVEL_MIDDLE, false, taskid + 1));
        newList.add(buildCocTask(uid, CocConstant.LEVEL_HEIGH, false, taskid + 2));
		if (info.getFinishedCount() == 0) {
            // 当前任务可完成次数为0 刷新的任务设置为不可接受
            newList.forEach(p -> p.setStatus(CocConstant.TASK_STATUS_STOP));
        }

		info.setCocTasks(newList);
		info.setRefreshCount(info.getRefreshCount() + 1);
        this.gameUserService.updateItem(info);
		for (CocTask task : newList) {
			if (CocConstant.TASK_STATUS_GONE == task.getStatus()) {
				continue;
			}
			List<Award> aRewards = CocTools.getCocTaskAwards(task.getLevel(), false);
			task.setRewards(userCocAwardService.getRewards(aRewards, task.getLevel()));
		}
		rd.setTaskList(newList);
        return rd;
    }

    /**
     * 获取任务列表
     *
     * @param uid
     * @return
     */
    public RDCoc list(long uid) {
		RDCoc rd = new RDCoc();
		UserCocInfo info = userCocInfoService.getUserCocInfo(uid);
		rd.setUnclaimed(info.getUnclaimed());
		Optional<UserCocTaskInfo> infoOp = getTaskInfoOp(uid);
		if (!infoOp.isPresent()) {
			throw new ExceptionForClientTip("coc.not.creat");
		}
		int num=0;
		UserCocTaskInfo taskinfo = infoOp.get();
		for (CocTask task : taskinfo.getCocTasks()) {
			if (CocConstant.TASK_STATUS_GONE == task.getStatus()) {
				continue;
			}
			if (task.getStatus()==CocConstant.TASK_STATUS_DOING||task.getStatus()==CocConstant.TASK_STATUS_FINISHED) {
				num++;
			}
		}
		if (num==0 && taskinfo.getFinishedCount()>0) {
			for (CocTask task : taskinfo.getCocTasks()) {
				if (CocConstant.TASK_STATUS_GONE == task.getStatus()) {
					continue;
				}
				task.setStatus(CocConstant.TASK_STATUS_WAIT);
			}
			gameUserService.updateItem(taskinfo);
		}
		for (CocTask task : taskinfo.getCocTasks()) {
			if (CocConstant.TASK_STATUS_GONE == task.getStatus()) {
				continue;
			}
			task.setRewards(userCocAwardService.getRewards(CocTools.getCocTaskAwards(task.getLevel(), false), task.getLevel()));
		}
		rd.setTaskList(taskinfo.getCocTasks());
		rd.setRefreshCount(taskinfo.getRefreshCount());
		rd.setComplete(taskinfo.getFinishedCount());
		rd.setHonor(userCocInfoService.getHonor(uid));
		rd.setGoldCoin(userCocInfoService.getGoldCoin(uid));
		rd.setTokenQuantity(userCocInfoService.getTokenQuantity(uid));
        Date tomorrow = DateUtil.fromDateInt(DateUtil.toDateInt(DateUtil.addDays(new Date(), 1)));
        rd.setTime(DateUtil.millisecondsInterval(tomorrow, new Date()));
        return rd;
    }

    /**
     * 接受任务
     *
     * @param uid
     * @param taskId
     * @return
     */
    public RDCoc acceptTask(long uid, int taskId) {
		Optional<UserCocTaskInfo> infoOp = getTaskInfoOp(uid);
		if (!infoOp.isPresent()) {
			throw new ExceptionForClientTip("coc.not.creat");
		}
		UserCocTaskInfo info = infoOp.get();
		RDCoc rd = new RDCoc();
		if (info.getFinishedCount() == 0) {
            throw new ExceptionForClientTip("coc.task.not.accept");
        }
		List<CocTask> tlist = info.getCocTasks();
        for (CocTask task : tlist) {
            if (task.getStatus() == CocConstant.TASK_STATUS_DOING || task.getStatus() == CocConstant.TASK_STATUS_GONE) {
                // 存在完成或者进行中的任务时 不可接受其他任务
                if (task.getTaskId() != taskId || CocConstant.TASK_STATUS_DOING != task.getStatus()) {
                    throw new ExceptionForClientTip("coc.task.exist");
                }
            }
            if (task.getTaskId() == taskId) {
                task.setStatus(CocConstant.TASK_STATUS_DOING);
            } else if (CocConstant.TASK_STATUS_WAIT == task.getStatus()) {
                task.setStatus(CocConstant.TASK_STATUS_STOP);// 当有任务接受时 其他任务设置为不可接受
            }
        }
        this.gameUserService.updateItem(info);
        rd.setTaskId(taskId);
        return rd;
    }

    /**
     * 取消任务
     *
     * @param uid
     * @param taskId
     * @return
     */
    public RDCoc cancelTask(long uid, int taskId) {
        RDCoc rd = new RDCoc();
		Optional<UserCocTaskInfo> infoOp = getTaskInfoOp(uid);
		if (!infoOp.isPresent()) {
			throw new ExceptionForClientTip("coc.not.creat");
		}
		UserCocTaskInfo info = infoOp.get();
		List<CocTask> tlist = info.getCocTasks();
        for (CocTask task : tlist) {
            if (task.getStatus() == CocConstant.TASK_STATUS_DOING || task.getStatus() == CocConstant.TASK_STATUS_STOP) {
                task.setStatus(CocConstant.TASK_STATUS_WAIT);
                task.getTargetSpecial().forEach(p -> p.setProcess(0));
            } else if (task.getStatus() != CocConstant.TASK_STATUS_GONE) {
                throw new ExceptionForClientTip("coc.task.error");
            }
        }
        rd.setTaskId(taskId);
        this.gameUserService.updateItem(info);
        return rd;
    }

    /**
     * 完成任务
     *
     * @param uid
     * @param taskId
     * @return
     */
    public RDCoc gainAward(long uid, int taskId) {
        RDCoc rd = new RDCoc();
        UserCocInfo info = this.userCocInfoService.getUserCocInfo(uid);
		Optional<UserCocTaskInfo> infoOp = getTaskInfoOp(uid);
		if (!infoOp.isPresent()) {
			throw new ExceptionForClientTip("coc.not.creat");
		}
		UserCocTaskInfo taskInfo = infoOp.get();
		List<CocTask> tlist = taskInfo.getCocTasks();
        CocTask newTask = null;
        int index = 0;
        for (CocTask task : tlist) {
            if (task.getTaskId() == taskId && task.getStatus() == CocConstant.TASK_STATUS_FINISHED) {
                // 奖励发放
                task.setStatus(CocConstant.TASK_STATUS_GONE);
                index = tlist.indexOf(task);
				userCocAwardService.gainReward(CocTools.getCocTaskAwards(task.getLevel(), task.getUrgent() == 1), uid,
						rd, WayEnum.Chamber_Of_Commerce_TASK);
                // 重新生成任务
                newTask = buildCocTask(uid, task.getLevel(), false, taskId + 10);
                newTask.setStatus(CocConstant.TASK_STATUS_STOP);
				taskInfo.setFinishedCount(taskInfo.getFinishedCount() - 1);
//                int reNum = null == info.getTaskRemind() ? 0 : info.getTaskRemind() - 1;
//                info.setTaskRemind(reNum);
				String broadcast = "";
                if (task.getLevel() > CocConstant.LEVEL_LOW) {
                    // 更新历练
                    this.expTaskService.updateTaskProgress(CocConstant.TASK_TYPE_SPECIAL, 1, uid);
                    String level = task.getLevel() > CocConstant.LEVEL_MIDDLE ? "高级" : "中级";
                    String nickname = this.gameUserService.getGameUser(uid).getRoleInfo().getNickname();
                    broadcast = LM.I.getMsgByUid(uid, "broadcast.coc.achieved", nickname, task.getTargetCityName(), level);
                }
				EPTaskFinished finished = EPTaskFinished.instance(new BaseEventParam(uid), task.getLevel(),
						task.getTaskExplain(), broadcast);
//                CocEventPublisher.pubCocTaskFinishedEvent(finished);
				break;
            }
        }
        if (newTask == null) {
            throw new ExceptionForClientTip("coc.task.not.finished");
        } else {
			LogUtil.logDeletedUserData(taskInfo);
            tlist.set(index, newTask);
			tlist.forEach(p -> p.setStatus(
					taskInfo.getFinishedCount() > 0 ? CocConstant.TASK_STATUS_WAIT : CocConstant.TASK_STATUS_STOP));
            this.gameUserService.updateItem(info);
			this.gameUserService.updateItem(taskInfo);
        }
        rd.setTaskId(taskId);
        return rd;
    }

    /**
     * 增加任务完成数量
     */
    public RDCoc addTask(long uid) {
        RDCoc rd = new RDCoc();
		Optional<UserCocTaskInfo> infoOp = getTaskInfoOp(uid);
		if (!infoOp.isPresent()) {
			throw new ExceptionForClientTip("coc.not.creat");
		}
		UserCocTaskInfo info = infoOp.get();
		TreasureChecker.checkIsEnough(TreasureEnum.SHLP.getValue(), 1, uid);
		TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.SHLP.getValue(), 1,WayEnum.Chamber_Of_Commerce_TASK_ADD, rd);
		if (info.getFinishedCount() == 0) {
			// 如果目前可完成数为0 则先把任务变成可接受
			info.getCocTasks().forEach(p -> {
                if (p.getStatus() == CocConstant.TASK_STATUS_STOP) {
                    p.setStatus(CocConstant.TASK_STATUS_WAIT);
                }
            });
        }
		info.setFinishedCount(info.getFinishedCount() + 1);
        this.gameUserService.updateItem(info);
        return rd;
    }

    /**
     * 商会任务生成
     *
     * @param level
     */
    private CocTask buildCocTask(long uid, int level, boolean init, int taskid) {
        CocTask cocTask = new CocTask();
        cocTask.setTaskId(taskid);
        cocTask.setLevel(level);
        Random rd = new Random();
        if (init) {
            // 初始刷新加急概率为 0% 5% 3%
            if (level == CocConstant.LEVEL_MIDDLE) {
                if ((rd.nextInt(100) + 1) <= 5) {
					cocTask.setUrgent(1);
                }
            } else if (level == CocConstant.LEVEL_HEIGH) {
                if ((rd.nextInt(100) + 1) <= 3) {
					cocTask.setUrgent(1);
                }
            }
        } else {
            // 非初始刷新加急概率为 5%
			int num = this.userCocInfoService.getPrivilegeUrgentDouble(uid) * 5;
            if (num == 0) {
                num = 5;
            }
            if ((rd.nextInt(100) + 1) <= num) {
				cocTask.setUrgent(1);
            }
        }
        //TODO: 凛冬将至后可删除
        if (holidayCocProcessor.opened(gameUserService.getActiveSid(uid))){
            cocTask.setUrgent(1);
        }
        // 派送地区
        int targetAreaId = (rd.nextInt(5) + 1) * 10;// 随机区域 10-50
        cocTask.setTargetAreaId(targetAreaId);
        switch (targetAreaId) {
            case 10:
                cocTask.setTargetAreaName("金区城区");
                break;
            case 20:
                cocTask.setTargetAreaName("木区城区");
                break;
         case 30:
                cocTask.setTargetAreaName("水区城区");
                break;
            case 40:
                cocTask.setTargetAreaName("火区城区");
                break;
            case 50:
                cocTask.setTargetAreaName("土区城区");
                break;
        }

        int excludeId = 0;// 排除的地区 如高级任务需要2个地区的特产则需要排除2个地区
        int specialNum = 3;// 特产数量 默认为3
        String ex = "【" + cocTask.getTargetAreaName() + "】急需";
        if (CocConstant.LEVEL_LOW != level) {
            CfgCityEntity city = getRandomCity(targetAreaId);// 随机一个城池
            cocTask.setTargetCityId(city.getId());
            cocTask.setTargetCityName(cocTask.getTargetAreaName().substring(0, 1) + "●" + city.getName());
            ex = "【" + city.getName() + "】急需";
            specialNum = 2; // 非低级任务为2个
        }
        // 生成任务要求
        List<Task> tasks = new ArrayList<CocTask.Task>();

        int Tasknum = 1;// 初 中级 任务都为1种特长
        if (CocConstant.LEVEL_HEIGH == level) {
            Tasknum = 2;// 高级有2种特产
        }
        for (int i = 0; i < Tasknum; i++) {
            // 随机品级
            int grade = 0;
            switch (level) {
                case CocConstant.LEVEL_LOW:
                    // 特产范围：70%低级特产，30%中级特产
                    if ((rd.nextInt(100) + 1) <= 30) {
                        grade = CocConstant.SPECIAL_MIDDEL;
                    } else {
                        grade = CocConstant.SPECIAL_LOW;
                    }
                    break;
                case CocConstant.LEVEL_MIDDLE:
                    // 特产范围：100%中级特产
                    grade = CocConstant.SPECIAL_MIDDEL;
                    break;
                case CocConstant.LEVEL_HEIGH:
                    if (i == 0) {
                        // 首次为中
                        grade = CocConstant.SPECIAL_MIDDEL;
                    } else {
                        ex += "和";
                        // 特产范围：70%中级特产，30%高级特产
                        if ((rd.nextInt(100) + 1) <= 30) {
                            grade = CocConstant.SPECIAL_HEIGH;
                        } else {
                            grade = CocConstant.SPECIAL_MIDDEL;
                        }
                    }
                    break;
            }
            Task task = new Task();
            task.setNum(specialNum);
            CocSpecial special = getRandomSpecial(grade);
            int prodAreaId = special.getAreaId();// 出产区域
            while ((prodAreaId == targetAreaId) || (prodAreaId == excludeId)) {
                special = getRandomSpecial(grade);// 随机特产 20，25，30
                prodAreaId = special.getAreaId();
            }
            ex += specialNum + "个【" + special.getName() + "】";
            task.setSpecialName(special.getName());
            task.setSpecialId(special.getId());
            excludeId = prodAreaId;
            tasks.add(task);
        }
        cocTask.setTaskExplain(ex);
        cocTask.setTargetSpecial(tasks);
        return cocTask;
    }

    /**
     * 任务进度更新 目前监听 一种产品一个个数为1
     *
     * @param city
     * @param special
     */
    public void updateProgress(long uid, int pos, int specialId) {
		Optional<UserCocTaskInfo> infoOp = getTaskInfoOp(uid);
		if (!infoOp.isPresent()) {
			return;
		}
		UserCocTaskInfo taskInfo = infoOp.get();
        Optional<CocTask> taskOp=taskInfo.getCocTasks().stream().filter(p->p.getStatus()==CocConstant.TASK_STATUS_DOING).findFirst();
        if (!taskOp.isPresent()) {
			return;
		}
        CfgCityEntity city = CityTool.getCityByRoadId(pos);
        CocTask task=taskOp.get();
        if (task.getLevel()==CocConstant.LEVEL_LOW) {
        	// 低级任务需要核对区域 
            CfgRoadEntity road = RoadTool.getRoadById(pos);
        	if (task.getTargetAreaId() != road.getCountry()) {
        		return;
			}
		}else if (task.getTargetCityId() != city.getId().intValue()) {
			//非低级任务需要核对城市ID
			return;
		}
        for (Task t : task.getTargetSpecial()) {
            if (t.getSpecialId() == specialId) {
                if (t.getProcess() != t.getNum()) {
                    t.setProcess(t.getProcess() + 1);
                }
				EpFsHelperChange dta = EpFsHelperChange.instanceUpdateTask(new BaseEventParam(uid),FsTaskEnum.Coc, task.getTaskId());
				TaskEventPublisher.pubEpFsHelperChangeEvent(dta);
                break;
            }
        }
        boolean isFinished =! task.getTargetSpecial().stream().filter(p -> p.getProcess() != p.getNum()).findFirst().isPresent();
        if (isFinished) {
            task.setStatus(CocConstant.TASK_STATUS_FINISHED);
            //TODO 带重构
			String redNotice = this.redNoticeService.buildNoticeData3(ModuleEnum.COC, CocMark.TASK.getValue(), 1);
            this.m2cService.sendRedNotice(uid, Arrays.asList(redNotice));
        }
		this.gameUserService.updateItem(taskInfo);
    }

    /**
     * 获取当前接受的任务
     *
     * @param uid
     * @return
     */
	public RDCoc getAcceptedTask(long uid) {
		CocTask task = getAcceptedRDTask(uid);
        RDCoc rd = new RDCoc();
		if (task != null) {
			rd.setTaskInfo(task);
        }
        return rd;
    }

	/**
	 * 不含奖励
	 * 
	 * @param uid
	 * @return
	 */
	public CocTask getAcceptedRDTask(long uid) {
		Optional<UserCocTaskInfo> infoOp = getTaskInfoOp(uid);
		if (!infoOp.isPresent()) {
			return null;
		}
		UserCocTaskInfo taskInfo = infoOp.get();
		for (CocTask task : taskInfo.getCocTasks()) {
			if (task.getStatus() == CocConstant.TASK_STATUS_DOING
					|| task.getStatus() == CocConstant.TASK_STATUS_FINISHED) {
				return task;
			}
		}
		return null;
	}

	/**
	 * 获取剩余任务次数
	 * 
	 * @param uid
	 * @return
	 */
	public Integer getTaskTimes(long uid) {
		Optional<UserCocTaskInfo> infoOp = getTaskInfoOp(uid);
		if (!infoOp.isPresent()) {
			return 0;
		}
		UserCocTaskInfo info = infoOp.get();
		return info.getFinishedCount();
	}

	/**
	 * 获取任务完成标识
	 * 
	 * @param uid
	 * @return
	 */
	public Integer getTaskRemaind(long uid) {
		Optional<UserCocTaskInfo> infoOp = getTaskInfoOp(uid);
		if (!infoOp.isPresent()) {
			return 0;
		}
		UserCocTaskInfo info = infoOp.get();
		if (null != info) {
			for (CocTask task : info.getCocTasks()) {
				if (task.getStatus() == CocConstant.TASK_STATUS_FINISHED) {
					return 1;
				}
			}
		}
		return 0;
	}

    /**
     * 随机生成一个指定等级的特产
     *
     * @param grade
     * @return
     */
    private CocSpecial getRandomSpecial(int grade) {
        CfgCoc cfg = Cfg.I.getUniqueConfig(CfgCoc.class);
        if (grade == CocConstant.SPECIAL_LOW) {
            return PowerRandom.getRandomFromList(cfg.getLowSpecials());
        } else if (grade == CocConstant.SPECIAL_MIDDEL) {
            return PowerRandom.getRandomFromList(cfg.getMiddleSpecials());
        } else {
            return PowerRandom.getRandomFromList(cfg.getHeighSpecials());
        }
    }

    /**
     * 随机返回指定属性的一个城市
     *
     * @param item
     * @return
     */
    private CfgCityEntity getRandomCity(int areaId) {
        List<CfgCityEntity> cfgData = CityTool.getCities();
        List<CfgCityEntity> citys = new ArrayList<CfgCityEntity>();
        for (CfgCityEntity entity : cfgData) {
            if (entity.getProperty() != null && entity.getProperty() != 0 && RoadTool.getRoadById(entity.getAddress1()).getCountry() == areaId) {
                citys.add(entity);
            }
        }
        return PowerRandom.getRandomFromList(citys);
    }


}
