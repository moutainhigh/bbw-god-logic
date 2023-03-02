package com.bbw.god.gameuser.task.timelimit.newyearandchrist;

import com.bbw.common.ListUtil;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.maou.event.EPAttackGameMaou;
import com.bbw.god.game.maou.event.GameMaouAttackEvent;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.timelimit.UserTimeLimitTask;
import com.bbw.god.gameuser.task.timelimit.event.TimeLimitTaskEventPublisher;
import com.bbw.god.gameuser.treasure.event.*;
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
 * 双旦任务监听
 *
 * @author fzj
 * @date 2021/11/19 9:16
 */
@Component
public class NewYearAndChristTaskListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserNewYearAndChristTaskService userNewYearAndChristTaskService;
    /** 任务集合 */
    private final static List<Integer> TASKS_LIST_1 = Arrays.asList(130014, 130019, 130020, 130021, 130039);
    /** 任务集合 */
    private final static List<Integer> TASKS_LIST_2 = Arrays.asList(130004, 130009);

    @Async
    @EventListener
    @Order(1000)
    public void addTreasure(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        if (ep.getWay() != WayEnum.BUILDING_ALTAR) {
            return;
        }
        long uid = ep.getGuId();
        EVTreasure cunZCoin = ep.getAddTreasures().stream().filter(t -> t.getId() == TreasureEnum.CUNZ_COIN.getValue()).findFirst().orElse(null);
        if (null == cunZCoin) {
            return;
        }
        achieveTask(uid, TASKS_LIST_1, cunZCoin.getNum());
    }

    @Async
    @EventListener
    @Order(1000)
    public void deductTreasure(TreasureDeductEvent event) {
        EPTreasureDeduct ep = event.getEP();
        EVTreasure deductTreasure = ep.getDeductTreasure();
        if (ep.getWay() != WayEnum.CUNZ_YIYUN) {
            return;
        }
        if (deductTreasure.getId() != TreasureEnum.CUNZ_COIN.getValue()) {
            return;
        }
        long uid = ep.getGuId();
        achieveTask(uid, TASKS_LIST_2, deductTreasure.getNum());
    }

    @Async
    @EventListener
    @Order(1000)
    public void attackMaou(GameMaouAttackEvent event) {
        EPAttackGameMaou ep = event.getEP();
        long uid = ep.getGuId();
        int maoTaskId = 130041;
        UserTimeLimitTask uts = userNewYearAndChristTaskService.getTasks(uid).stream()
                .filter(t -> t.getBaseId() == maoTaskId && t.getStatus() == TaskStatusEnum.DOING.getValue()).findFirst().orElse(null);
        if (null == uts) {
            return;
        }
        int blood = ep.getBlood();
        achieveTask(uid, Collections.singletonList(maoTaskId), blood);
    }

    private void achieveTask(long uid, List<Integer> taskIds, long addedNum) {
        List<UserTimeLimitTask> uts = userNewYearAndChristTaskService.getTasks(uid);
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
            TimeLimitTaskEventPublisher.pubTimeLimitTaskAchievedEvent(uid, TaskGroupEnum.NEW_YEAR_AND_CHRISTMAS_TASK, ut.getBaseId());
        }
        gameUserService.updateItems(uts);
    }
}
