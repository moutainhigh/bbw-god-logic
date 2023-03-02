package com.bbw.god.login.repairdata;

import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.task.main.UserMainTask;
import com.bbw.god.gameuser.task.main.UserMainTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.bbw.god.login.repairdata.RepairDataConst.REPAIR_MAIN_TASK_VALUE;

/**
 * @author suchaobin
 * @description 修复主线任务进度
 * @date 2020/9/29 14:09
 **/
@Service
public class RepairMainTaskService implements BaseRepairDataService {
    @Autowired
    private UserMainTaskService mainTaskService;
    @Autowired
    private GameUserService gameUserService;

    /**
     * 修复数据
     *
     * @param gu            玩家对象
     * @param lastLoginDate 上次登录时间
     */
    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (lastLoginDate.before(REPAIR_MAIN_TASK_VALUE)) {
            UserMainTask umTask = mainTaskService.getUserMainTask(gu.getId(), 1100);
            int ccCount = CityTool.getCcCount();
            if (null != umTask && umTask.getEnableAwardIndex() > ccCount) {
                umTask.setEnableAwardIndex(ccCount);
                gameUserService.updateItem(umTask);
            }
        }
    }
}
