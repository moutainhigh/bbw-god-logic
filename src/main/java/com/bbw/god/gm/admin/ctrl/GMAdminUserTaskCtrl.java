package com.bbw.god.gm.admin.ctrl;

import com.bbw.common.Rst;
import com.bbw.god.gm.UserGmService;
import com.bbw.god.gm.admin.CRAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台接口-玩家任务相关操作
 *
 * @author：lzc
 * @date: 2021/03/17 11:28
 * @version: 1.0
 */
@RequestMapping("/gm/admin")
@RestController
public class GMAdminUserTaskCtrl {
    @Autowired
    private UserGmService userGmService;

    /**
     * 跳过新手进阶任务
     *
     * @param sId
     * @param nickname
     * @return
     */
    @RequestMapping(CRAdmin.UserTask.SET_PASS_GROW_TASKS)
    public Rst setPassGrowTasks(int sId, String nickname) {
        return userGmService.setPassGrowTasks(sId, nickname);
    }

    /**
     * 将新手进阶任务调整为 已完成未领取 状态
     *
     * @param sId
     * @param nickname
     * @return
     */
    @RequestMapping(CRAdmin.UserTask.SET_GROW_TASK_STATUS)
    public Rst setGrowTaskStatus(int sId, String nickname) {
        return userGmService.setGrowTaskStatus(sId, nickname);
    }

    /**
     * 调整指定的新手进阶任务状态
     *
     * @param sId
     * @param nickname
     * @param index
     * @param status
     * @return
     */
    @RequestMapping(CRAdmin.UserTask.SET_GROW_TASK_STATUS_OF_INDEX)
    public Rst setGrowTaskStatusOfIndex(int sId, String nickname, int index, int status) {
        return userGmService.setGrowTaskStatus(sId, nickname, index, status);
    }
}
