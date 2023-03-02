package com.bbw.god.login.repairdata;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.statistic.StatisticTypeEnum;
import com.bbw.god.gameuser.statistic.resource.city.CityResStatisticService;
import com.bbw.god.gameuser.statistic.resource.city.CityStatistic;
import com.bbw.god.gameuser.task.godtraining.GodTrainingTaskService;
import com.bbw.god.gameuser.task.godtraining.UserGodTrainingTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.bbw.god.login.repairdata.RepairDataConst.GOD_TRAINING_REPAIR_TIME;

/**
 * @author suchaobin
 * @description 上仙试炼修复
 * @date 2021/2/7 14:11
 **/
@Service
public class GodTrainingRepairService implements BaseRepairDataService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GodTrainingTaskService godTrainingTaskService;
    @Autowired
    private CityResStatisticService cityResStatisticService;

    /**
     * 修复数据
     *
     * @param gu            玩家对象
     * @param lastLoginDate 上次登录时间
     */
    @Override
    public void repair(GameUser gu, Date lastLoginDate) {
        if (lastLoginDate.before(GOD_TRAINING_REPAIR_TIME)) {
            List<UserGodTrainingTask> datas = godTrainingTaskService.getTrainingTasks(gu.getId());
            // 任务生成了，但是新手任务已经过完了
            if (ListUtil.isNotEmpty(datas) && gu.getStatus().isGrowTaskCompleted()) {
                gameUserService.deleteItems(gu.getId(), datas);
                return;
            }
            // 修改攻城的上仙试炼任务进度
            CityStatistic statistic = cityResStatisticService.fromRedis(gu.getId(), StatisticTypeEnum.GAIN, DateUtil.getTodayInt());
            // 三级城
            godTrainingTaskService.updateProgress(gu.getId(), 95702, statistic.getThreeStarCity());
            // 四级城
            godTrainingTaskService.updateProgress(gu.getId(), 95703, statistic.getFourStarCity());
            // 五级城
            godTrainingTaskService.updateProgress(gu.getId(), 95704, statistic.getFiveStarCity());
            // 总的
            godTrainingTaskService.updateProgress(gu.getId(), 95707, statistic.getTotal());
        }
    }
}
