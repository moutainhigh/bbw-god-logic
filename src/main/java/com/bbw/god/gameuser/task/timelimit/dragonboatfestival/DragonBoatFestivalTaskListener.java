package com.bbw.god.gameuser.task.timelimit.dragonboatfestival;

import com.bbw.common.ListUtil;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.event.TimeLimitTaskEventPublisher;
import com.bbw.god.gameuser.treasure.event.EPTreasureAdd;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureAddEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 端午节任务监听
 *
 * @author: huanghb
 * @date: 2022/5/23 16:03
 */
@Component
public class DragonBoatFestivalTaskListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserDragonBoatFestivalTaskService userDragonBoatFestivalTaskService;
    /** 活动需要糕点 */
    private static final Map<Integer, List<Integer>> ACTIVITY_NEED_PASTRIES = new HashMap<Integer, List<Integer>>() {
        private static final long serialVersionUID = 9164514101940834092L;

        {
            put(TreasureEnum.HEMP_BALL.getValue(), Arrays.asList(210011));
            put(TreasureEnum.GREEN_BEAN_CAKE.getValue(), Arrays.asList(210029));
            put(TreasureEnum.BIG_ZONGZI.getValue(), Arrays.asList(210033));
        }
    };

    @Async
    @EventListener
    @Order(1000)
    public void addTreasure(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        List<EVTreasure> evTreasures = ep.getAddTreasures();
        List<EVTreasure> activityNeedPastries = evTreasures.stream().filter(tmp -> ACTIVITY_NEED_PASTRIES.keySet()
                .contains(tmp.getId())).collect(Collectors.toList());
        if (ListUtil.isEmpty(activityNeedPastries)) {
            return;
        }
        long uid = ep.getGuId();
        for (EVTreasure activityNeedPastry : activityNeedPastries) {
            achieveTask(uid, ACTIVITY_NEED_PASTRIES.get(activityNeedPastry.getId()), activityNeedPastry.getNum());
        }
    }

    /**
     * 完成任务
     *
     * @param uid
     * @param taskIds
     * @param addedNum
     */
    private void achieveTask(long uid, List<Integer> taskIds, long addedNum) {
        //获得限时任务
        List<UserTimeLimitTask> uts = userDragonBoatFestivalTaskService.getTasks(uid);
        //获得正在进行的搜集糕点任务
        uts = uts.stream().filter(tmp -> taskIds.contains(tmp.getBaseId()) &&
                tmp.getStatus() == TaskStatusEnum.DOING.getValue()).collect(Collectors.toList());
        if (ListUtil.isEmpty(uts)) {
            return;
        }
        for (UserTimeLimitTask ut : uts) {
            //添加进度
            ut.addValue(addedNum);
            //是否完成任务
            if (ut.getStatus() != TaskStatusEnum.ACCOMPLISHED.getValue()) {
                continue;
            }
            //发布限时任务完成事件
            TimeLimitTaskEventPublisher.pubTimeLimitTaskAchievedEvent(uid, TaskGroupEnum.DRAGON_BOAT_FESTIVAL_TASK, ut.getBaseId());
        }
        //更新任务信息
        gameUserService.updateItems(uts);
    }
}
