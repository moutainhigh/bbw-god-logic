package com.bbw.god.activityrank.game.moneydrawing;

import com.bbw.god.activity.holiday.lottery.HolidayLotteryType;
import com.bbw.god.activity.holiday.lottery.event.EPHolidayLotteryDraw;
import com.bbw.god.activity.holiday.lottery.event.HolidayLotteryDrawEvent;
import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.treasure.event.EPTreasureDeduct;
import com.bbw.god.gameuser.treasure.event.TreasureDeductEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author lzc
 * @description 招财进宝榜单监听器
 * @date 2021/03/09 15:42
 **/
@Slf4j
@Component
public class MoneyDrawingRankListener {
    private final ActivityRankEnum rankType = ActivityRankEnum.MONEY_DRAWING_RANK;
    private final ActivityRankEnum dayRankType = ActivityRankEnum.MONEY_DRAWING_DAY_RANK;

    @Autowired
    private ActivityRankService activityRankService;

    /**
     * 击败盗宝贼寇
     **/
    @Async
    @EventListener
    public void fightWin(CombatFightWinEvent event) {
        EPFightEnd epFightEnd = (EPFightEnd) event.getSource();
        Long uid = epFightEnd.getGuId();
        FightTypeEnum fightType = epFightEnd.getFightType();
        int value = 0;
        if (!fightType.equals(FightTypeEnum.YG)) {
            return;
        }
        if ("盗宝贼寇（精英）".equals(epFightEnd.getFightSubmit().getOpponentName())) {
            value = 3;
        } else if ("盗宝贼寇".equals(epFightEnd.getFightSubmit().getOpponentName())) {
            value = 1;
        } else {
            return;
        }
        log.info("{}招财进宝榜单增加:{}", uid, value);
        activityRankService.incrementRankValue(uid, value, rankType);
        activityRankService.incrementRankValue(uid, value, dayRankType);
    }

    /**
     * 五气朝元
     **/
    @Async
    @EventListener
    public void deductTreasure(TreasureDeductEvent event) {
        EPTreasureDeduct ep = event.getEP();
        Long uid = ep.getGuId();
        if (ep.getDeductTreasure().getId() != TreasureEnum.CHANG_ZI.getValue()) {
            return;
        }
        int value = 5 * ep.getDeductTreasure().getNum();
        log.info("{}招财进宝榜单增加:{}", uid, value);
        activityRankService.incrementRankValue(uid, value, rankType);
        activityRankService.incrementRankValue(uid, value, dayRankType);
    }

    /**
     * 博饼
     *
     * @param event
     */
    @Async
    @EventListener
    public void deductTreasure(HolidayLotteryDrawEvent event) {
        EPHolidayLotteryDraw ep = event.getEP();
        Long uid = ep.getGuId();
        if (ep.getLotteryType() != HolidayLotteryType.ZQBB) {
            return;
        }
        int value = 1;
        log.info("{}招财进宝榜单通过博饼增加:{}", uid, value);
        activityRankService.incrementRankValue(uid, value, rankType);
        activityRankService.incrementRankValue(uid, value, dayRankType);
    }
}
