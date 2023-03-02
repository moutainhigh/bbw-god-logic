package com.bbw.god.gameuser.task.timelimit.thanksgiving;

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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 感恩节任务监听
 *
 * @author fzj
 * @date 2021/11/19 9:16
 */
@Component
public class ThanksgivingTaskListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserThanksgivingTaskService userThanksgivingTaskService;
    /** 感恩节食物 */
    private static final List<Integer> THANKS_GIVING_FOODS = Arrays.asList(50135, 50136, 50137, 50138, 50139);

    @Async
    @EventListener
    @Order(1000)
    public void addTreasure(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        long uid = ep.getGuId();
        List<EVTreasure> treasures = ep.getAddTreasures().stream().filter(t -> THANKS_GIVING_FOODS.contains(t.getId())).collect(Collectors.toList());
        for (EVTreasure addTreasure : treasures) {
            achieveTask(uid, Arrays.asList(120004, 120009, 120019), addTreasure.getNum());
            if (addTreasure.getId() == TreasureEnum.PUMPKIN_PIE.getValue()) {
                achieveTask(uid, Collections.singletonList(120029), addTreasure.getNum());
            }
        }
    }

    private void achieveTask(long uid, List<Integer> taskIds, long addedNum) {
        List<UserTimeLimitTask> uts = userThanksgivingTaskService.getTasks(uid);
        uts = uts.stream()
                .filter(tmp -> taskIds.contains(tmp.getBaseId()) && tmp.getStatus() == TaskStatusEnum.DOING.getValue())
                .collect(Collectors.toList());
        if (!ListUtil.isEmpty(uts)) {
            for (UserTimeLimitTask ut : uts) {
                ut.addValue(addedNum);
                if (ut.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()) {
                    TimeLimitTaskEventPublisher.pubTimeLimitTaskAchievedEvent(uid, TaskGroupEnum.THANKS_GIVING_TASK, ut.getBaseId());
                }
            }
            gameUserService.updateItems(uts);
        }
    }
}
