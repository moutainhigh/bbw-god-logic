package com.bbw.god.gameuser.task.timelimit.wansj;

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

import java.util.List;
import java.util.stream.Collectors;

/**
 * 万圣节任务监听
 *
 * @author: suhq
 * @date: 2021/10/21 9:09 上午
 */
@Component
public class WanSJTaskListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserWanSJTaskService userWanSJTaskService;
    
    @Async
    @EventListener
    @Order(1000)
    public void addTreasure(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        long uid = ep.getGuId();
        for (EVTreasure addTreasure : ep.getAddTreasures()) {
            if (addTreasure.getId() == TreasureEnum.XIONG_XRT.getValue()) {
                achieveTask(uid, 110004, addTreasure.getNum());
            } else if (addTreasure.getId() == TreasureEnum.TANG_SBG.getValue()) {
                achieveTask(uid, 110008, addTreasure.getNum());
            }
        }
    }

    private void achieveTask(long uid, int taskId, long addedNum) {
        List<UserTimeLimitTask> uts = userWanSJTaskService.getTasks(uid);
        uts = uts.stream()
                .filter(tmp -> tmp.getBaseId() == taskId && tmp.getStatus() == TaskStatusEnum.DOING.getValue())
                .collect(Collectors.toList());
        if (ListUtil.isEmpty(uts)) {
            return;
        }
        for (UserTimeLimitTask ut : uts) {
            ut.addValue(addedNum);
            if (ut.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()) {
                TimeLimitTaskEventPublisher.pubTimeLimitTaskAchievedEvent(uid, TaskGroupEnum.WAN_SHENG_JIE_TASK, taskId);
            }
        }
        gameUserService.updateItems(uts);
    }
}
