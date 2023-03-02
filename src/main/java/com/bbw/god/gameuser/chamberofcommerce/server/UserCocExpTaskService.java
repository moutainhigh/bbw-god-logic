package com.bbw.god.gameuser.chamberofcommerce.server;

import com.bbw.common.DateUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.detail.LogUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.config.CfgCoc;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.chamberofcommerce.*;
import com.bbw.god.notify.rednotice.ModuleEnum;
import com.bbw.god.notify.rednotice.RedNoticeService;
import com.bbw.mc.m2c.M2cService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 跑商历练任务
 *
 * @author lwb
 * @version 1.0
 * @date 2019年4月15日
 */
@Service
public class UserCocExpTaskService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCocInfoService cocInfoService;
    @Autowired
    private M2cService m2cService;
    @Autowired
    private RedNoticeService redNoticeService;
	@Autowired
	private UserCocAwardService userCocAwardService;

    /**
     * 初始化任务
     *
     * @param info
     */
	private void initTask(UserCocExpTaskInfo taskInfo) {
		if (taskInfo == null) {
			return;
		}
		if (taskInfo.getTaskBuildDate() == DateUtil.toDateInt(new Date())) {
            return;
        }
		List<CocExpTask> expList = Cfg.I.getUniqueConfig(CfgCoc.class).getExpList();
        expList.forEach(p -> p.setProgressStr(0l));
        taskInfo.setExpList(expList);
        taskInfo.setSpecialTotal(0l);
        taskInfo.setTradeTotal(0l);
        taskInfo.setTrainWinTotal(0l);
		taskInfo.setTaskBuildDate(DateUtil.toDateInt(new Date()));
		gameUserService.updateItem(taskInfo);
    }

	public UserCocExpTaskInfo getCocExpTaskInfo(long uid) {
		if (!cocInfoService.opened(uid)) {
			return null;
		}
		UserCocExpTaskInfo info = gameUserService.getSingleItem(uid, UserCocExpTaskInfo.class);
		if (info == null) {
			info = UserCocExpTaskInfo.instance(uid);
			gameUserService.addItem(uid, info);
		}
		initTask(info);
		return info;
	}
    /**
     * 获取跑商任务列表
     *
     * @param uid
     * @return
     */
    public RDCoc list(long uid) {
        RDCoc rd = new RDCoc();
		UserCocExpTaskInfo taskInfo = getCocExpTaskInfo(uid);
		if (taskInfo == null) {
			throw new ExceptionForClientTip("coc.not.creat");
		}
		int[] types = { CocConstant.TASK_TYPE_SPECIAL, CocConstant.TASK_TYPE_TRIANING, CocConstant.TASK_TYPE_TRADE };
		List<CocExpTask> tasks = new ArrayList<CocExpTask>();
		for (int type : types) {
			CocExpTask task = taskInfo.getCurrentCocExpTask(type);
			if (task == null) {
				continue;
			}
			task.setRewards(userCocAwardService.getRewards(CocTools.getCocExpTaskAwards(task.getId()), task.getId()));
			tasks.add(task);
		}
		rd.setExpList(tasks);
        return rd;
    }

    /**
     * 领奖
     *
     * @param uid
     * @param taskId
     * @return
     */
    public RDCoc gainReward(long uid, int taskId) {
        RDCoc rd = new RDCoc();
        rd.setExpId(taskId);
		UserCocInfo info = cocInfoService.getUserCocInfo(uid);
		UserCocExpTaskInfo taskInfo = getCocExpTaskInfo(uid);
		List<CocExpTask> taskslist = taskInfo.getExpList();
		for (CocExpTask task : taskslist) {
			if (task.getId() == taskId && task.getStatus() == CocConstant.EXP_TASK_STATUS_FINISHED) {
                // 完成
				LogUtil.logDeletedUserData(taskInfo);
				List<Award> awards = CocTools.getExpTaskRewards(taskId);
				userCocAwardService.gainReward(awards, uid, rd, WayEnum.Chamber_Of_Commerce_EXP_TASK);
				task.setStatus(CocConstant.EXP_TASK_STATUS_GONE);
                this.gameUserService.updateItem(info);
				this.gameUserService.updateItem(taskInfo);
                return rd;
            }
        }
        throw new ExceptionForClientTip("coc.task.not.exist");
    }

	/**
	 * 更新进度
	 *
	 * @param uid
	 * @param type
	 * @param num
	 */
	public void updateTaskProgress(int type, long num, Long uid) {
		Optional<UserCocInfo> infoOp = cocInfoService.getUserCocInfoOp(uid);
		if (!infoOp.isPresent()) {
			return;
		}
		UserCocInfo info = infoOp.get();
		UserCocExpTaskInfo taskInfo = getCocExpTaskInfo(uid);
		long total = 0;
		switch (type) {
			case CocConstant.TASK_TYPE_TRADE:
				// 收入任务
			total = taskInfo.getTradeTotal() + num;
                if (total < 0) {
                    total = 0;// 因为该记录的是用户的特产收益，当今日首次卖特产亏时 避免出现负数，所以当总收入小于0时 设为0
                }
			taskInfo.setTradeTotal(total);
                break;
            case CocConstant.TASK_TYPE_TRIANING:
                // 练兵任务
			taskInfo.setTrainWinTotal(taskInfo.getTrainWinTotal() + num);
			total = taskInfo.getTrainWinTotal();
                break;
            case CocConstant.TASK_TYPE_SPECIAL:
                // 特殊任务
			taskInfo.setSpecialTotal(taskInfo.getSpecialTotal() + num);
			total = taskInfo.getSpecialTotal();
                break;
        }
        if (total == 0) {
            return;// 无符合的类型
        }
		List<CocExpTask> tasks = taskInfo.getExpList();
		for (CocExpTask task : tasks) {
			if (task.getType() == type && task.getStatus() == CocConstant.EXP_TASK_STATUS_DOING) {
                if (task.getTarget() <= total) {
					task.setStatus(CocConstant.EXP_TASK_STATUS_FINISHED);
                    task.setProgressStr(task.getTarget());
                    //TODO 带重构
					String redNotice = this.redNoticeService.buildNoticeData3(ModuleEnum.COC, CocMark.TASK.getValue(),
							1);
                    this.m2cService.sendRedNotice(info.getGameUserId(), Arrays.asList(redNotice));
                } else {
                    task.setProgressStr(total);
                }
            }
        }
        this.gameUserService.updateItem(info);
		this.gameUserService.updateItem(taskInfo);
    }

}
