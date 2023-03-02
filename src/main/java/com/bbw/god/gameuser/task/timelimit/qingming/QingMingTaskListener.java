package com.bbw.god.gameuser.task.timelimit.qingming;

import com.bbw.common.ListUtil;
import com.bbw.god.game.maou.event.EPAttackGameMaou;
import com.bbw.god.game.maou.event.GameMaouAttackEvent;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.event.TimeLimitTaskEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 清明任务监听
 *
 * @author fzj
 * @date 2022/3/28 10:59
 */
@Component
public class QingMingTaskListener {
    @Autowired
    UserQingMingTaskService userQingMingTaskService;
    @Autowired
    GameUserService gameUserService;
    /** 任务集合 */
    private final static List<Integer> TASKS_LIST = Arrays.asList(200022, 200027);

    @Async
    @EventListener
    @Order(1000)
    public void attackMaou(GameMaouAttackEvent event) {
        EPAttackGameMaou ep = event.getEP();
        long uid = ep.getGuId();
        UserTimeLimitTask uts = userQingMingTaskService.getTasks(uid).stream()
                .filter(t -> TASKS_LIST.contains(t.getBaseId()) && t.getStatus() == TaskStatusEnum.DOING.getValue()).findFirst().orElse(null);
        if (null == uts) {
            return;
        }
        int blood = ep.getBlood();
        achieveTask(uid, TASKS_LIST, blood);
    }

    private void achieveTask(long uid, List<Integer> taskIds, long addedNum) {
        List<UserTimeLimitTask> uts = userQingMingTaskService.getTasks(uid);
        uts = uts.stream().filter(tmp -> taskIds.contains(tmp.getBaseId()) &&
                tmp.getStatus() == TaskStatusEnum.DOING.getValue()).collect(Collectors.toList());
        if (ListUtil.isEmpty(uts)) {
            return;
        }
        for (UserTimeLimitTask ut : uts) {
            ut.addValue(addedNum);
            if (ut.getStatus() != TaskStatusEnum.ACCOMPLISHED.getValue()) {
                continue;
            }
            TimeLimitTaskEventPublisher.pubTimeLimitTaskAchievedEvent(uid, TaskGroupEnum.QING_MING_TASK, ut.getBaseId());
        }
        gameUserService.updateItems(uts);
    }
}
