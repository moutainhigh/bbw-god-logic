package com.bbw.god.gm;

import com.bbw.common.Rst;
import com.bbw.god.activity.ActivityService;
import com.bbw.god.city.UserCityService;
import com.bbw.god.controller.AbstractController;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.task.daily.service.UserDailyTaskService;
import com.bbw.god.gameuser.task.main.UserMainTask;
import com.bbw.god.gameuser.task.main.UserMainTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 玩家数据相关的操作
 *
 * @author suhq
 * @date 2019年4月13日 下午1:49:18
 */
@RestController
@RequestMapping("/gm")
public class GMUserTaskCtrl extends AbstractController {
    @Autowired
    private UserGmService userGmService;
    @Autowired
    private UserDailyTaskService userDailyTaskService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private UserMainTaskService mainTaskService;


    /**
     * 设置第index个新手任务的状态
     *
     * @param sId      id
     * @param nickname 用户昵称
     * @param index    第几个任务
     * @param status   状态
     * @return
     */
    @RequestMapping("user!setIndexGrowTaskStatus")
    public Rst setGrowTaskStatus(int sId, String nickname, int index, int status) {
        return this.userGmService.setGrowTaskStatus(sId, nickname, index, status);
    }

    /**
     * 跳过新手进阶任务
     *
     * @param sId
     * @param nickname
     * @return
     */
    @RequestMapping("user!setPassGrowTasks")
    public Rst setPassGrowTasks(int sId, String nickname) {
        return this.userGmService.setPassGrowTasks(sId, nickname);
    }

    /**
     * 将新手进阶任务调整为 已完成未领取 状态
     *
     * @param sId
     * @param nickname
     * @return
     */
    @RequestMapping("user!setGrowTaskStatus")
    public Rst setGrowTaskStatus(int sId, String nickname) {
        return this.userGmService.setGrowTaskStatus(sId, nickname);
    }

    /**
     * 重置玩家回归活动
     *
     * @param uid
     * @return
     */
    @RequestMapping("user!initHeroback")
    public Rst getTasks(Long uid) {
        this.activityService.initHeroback(uid, this.gameUserService.getActiveSid(uid));
        return Rst.businessOK();
    }

    /**
     * 修复玩家的主线任务进度
     *
     * @param uid
     * @param taskId
     * @return
     */
    @RequestMapping("user!repairMainTaskProgress")
    public Rst repairMainTaskProgress(long uid, int taskId) {
        int size = 0;
        if (taskId == 1100) {
            //累计攻下%d座城池
            size = userCityService.getUserOwnCities(uid).size();
        } else if (taskId == 1200) {
            //累计%d座城所有建筑满5级
            size = userCityService.getOwnCityNumAsLevel(uid, 5);
        } else if (taskId == 1300) {
            //累计收集%d张卡牌
            size = userCardService.getUserCards(uid).size();
        } else {
            return Rst.businessFAIL("无效的任务ID");
        }
        UserMainTask umTask = mainTaskService.getUserMainTask(uid, taskId);
        umTask.setEnableAwardIndex(size);
        gameUserService.updateItem(umTask);
        return Rst.businessOK();
    }
}
