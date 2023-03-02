package com.bbw.god.activityrank.game.winter;

import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.activityrank.ActivityRankService;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.config.special.SpecialTool;
import com.bbw.god.gameuser.chamberofcommerce.event.CocTaskFinishedEvent;
import com.bbw.god.gameuser.chamberofcommerce.event.EPTaskFinished;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialDeductEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author suchaobin
 * @description 凛冬将至榜单监听器
 * @date 2020/11/11 15:42
 **/
@Component
public class WinterRankListener {
    private final ActivityRankEnum rankType = ActivityRankEnum.WINTER_RANK;
    private final ActivityRankEnum dayRankType = ActivityRankEnum.WINTER_DAY_RANK;

    @Autowired
    private ActivityRankService activityRankService;

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
    public void sellSpecial(SpecialDeductEvent event) {
        EPSpecialDeduct ep = event.getEP();
        Long uid = ep.getGuId();
        List<EPSpecialDeduct.SpecialInfo> specialInfoList = ep.getSpecialInfoList();
        long count = specialInfoList.stream().filter(tmp -> SpecialTool.getSpecialById(tmp.getBaseSpecialIds()).isSyntheticSpecialty()).count();
        activityRankService.incrementRankValue(uid, count, rankType);
        activityRankService.incrementRankValue(uid, count, dayRankType);
    }
}
