package com.bbw.god.activityrank.server.killdemon;

import com.bbw.god.activity.holiday.lottery.event.EPHolidayLotteryDraw;
import com.bbw.god.activity.holiday.lottery.event.HolidayLotteryDrawEvent;
import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.gameuser.chamberofcommerce.event.CocTaskFinishedEvent;
import com.bbw.god.gameuser.chamberofcommerce.event.EPTaskFinished;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author suchaobin
 * @description 除魔卫道榜单监听器
 * @date 2020/8/25 14:33
 **/
@Component
public class KillDemonRankListener {
    private final ActivityRankEnum rankType = ActivityRankEnum.KILL_DEMON_RANK;
    private final ActivityRankEnum dayRankType = ActivityRankEnum.KILL_DEMON_DAY_RANK;

    @Autowired
    private ActivityRankService activityRankService;

    @Async
    @EventListener
    public void fightWin(CombatFightWinEvent event) {
        EPFightEnd epFightEnd = (EPFightEnd) event.getSource();
        Long uid = epFightEnd.getGuId();
        FightTypeEnum fightType = epFightEnd.getFightType();
        int value = 0;
        if (!fightType.equals(FightTypeEnum.YG)){
            return;
        }
        if ("冰霜雪怪（精英）".equals(epFightEnd.getFightSubmit().getOpponentName())) {
            value = 3;
        } else if ("冰霜雪怪".equals(epFightEnd.getFightSubmit().getOpponentName())) {
            value = 1;
        } else {
            return;
        }
        activityRankService.incrementRankValue(uid, value, rankType);
        activityRankService.incrementRankValue(uid, value, dayRankType);
    }

    @Async
    @EventListener
    public void finishCocTask(CocTaskFinishedEvent event) {
        EPTaskFinished ep = event.getEP();
        Long uid = ep.getGuId();
        activityRankService.incrementRankValue(uid, 5, rankType);
        activityRankService.incrementRankValue(uid, 5, dayRankType);
    }

    @Async
    @EventListener
    public void holidayLotteryDraw(HolidayLotteryDrawEvent event) {
        EPHolidayLotteryDraw ep = event.getEP();
        Long uid = ep.getGuId();
        activityRankService.incrementRankValue(uid, 2, rankType);
        activityRankService.incrementRankValue(uid, 2, dayRankType);
    }
}
