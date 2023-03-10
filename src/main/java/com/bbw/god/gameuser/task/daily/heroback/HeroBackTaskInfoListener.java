package com.bbw.god.gameuser.task.daily.heroback;

import com.bbw.god.activity.processor.HeroBackSignProcessor;
import com.bbw.god.city.miaoy.EPMiaoYDrawEnd;
import com.bbw.god.city.miaoy.MiaoYDrawEndEvent;
import com.bbw.god.event.EventParam;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.CombatInitiateEvent;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.treasure.CfgTreasureEntity;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.card.event.EPCardLevelUp;
import com.bbw.god.gameuser.card.event.UserCardAddEvent;
import com.bbw.god.gameuser.card.event.UserCardLevelUpEvent;
import com.bbw.god.gameuser.res.copper.CopperAddEvent;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.res.dice.DiceDeductEvent;
import com.bbw.god.gameuser.res.dice.EPDiceDeduct;
import com.bbw.god.gameuser.res.gold.EPGoldAdd;
import com.bbw.god.gameuser.res.gold.EPGoldDeduct;
import com.bbw.god.gameuser.res.gold.GoldAddEvent;
import com.bbw.god.gameuser.res.gold.GoldDeductEvent;
import com.bbw.god.gameuser.special.event.EPSpecialAdd;
import com.bbw.god.gameuser.special.event.EVSpecialAdd;
import com.bbw.god.gameuser.special.event.SpecialAddEvent;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskTool;
import com.bbw.god.gameuser.task.businessgang.event.BusinessGangTaskAchievedEvent;
import com.bbw.god.gameuser.task.daily.UserDailyTask;
import com.bbw.god.gameuser.task.daily.event.DailyTaskAddPointEvent;
import com.bbw.god.gameuser.treasure.event.EPTreasureAdd;
import com.bbw.god.gameuser.treasure.event.EPTreasureDeduct;
import com.bbw.god.gameuser.treasure.event.TreasureAddEvent;
import com.bbw.god.gameuser.treasure.event.TreasureDeductEvent;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.guild.event.GuildTaskFinishedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HeroBackTaskInfoListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private HeroBackTaskInfoProcessor heroBackTaskProcessor;
    @Autowired
    private HeroBackSignProcessor heroBackSignProcessor;

    /**
     * ????????????
     *
     * @param event
     */

    @Async
    @EventListener
    @Order(1000)
    public void addCopper(CopperAddEvent event) {
        EPCopperAdd ep = event.getEP();
        long guId = ep.getGuId();
        RDCommon rd = ep.getRd();
        long addedCopper = ep.gainAddCopper();
        achieveDailyTask(guId, 60113, addedCopper, rd);
    }

    /**
     * ????????????
     *
     * @param event
     */

    @Async
    @EventListener
    public void addGold(GoldAddEvent event) {
        EPGoldAdd ep = event.getEP();
        long guId = ep.getGuId();
        RDCommon rd = ep.getRd();
        if (ep.getWay().equals(WayEnum.FD)) {
            achieveDailyTask(guId, 60112, ep.gainAddGold(), rd);
        }
    }

    /**
     * ????????????
     *
     * @param event
     */

    @Async
    @EventListener
    public void uplevelCard(UserCardLevelUpEvent event) {
        EPCardLevelUp ep = event.getEP();
        long guId = ep.getGuId();
        RDCommon rd = ep.getRd();
        if (ep.getWay().equals(WayEnum.LT)) {
            achieveDailyTask(guId, 60313, 1, rd);
        }
    }

    /**
     * ????????????
     *
     * @param event
     */

    @Async
    @EventListener
    public void deductGold(GoldDeductEvent event) {
        EPGoldDeduct ep = event.getEP();
        long guId = ep.getGuId();
        RDCommon rd = ep.getRd();
        if (ep.getWay().equals(WayEnum.MALL_BUY)) {
            achieveDailyTask(guId, 60314, ep.getDeductGold(), rd);
        }
    }

    /**
     * ???????????????
     *
     * @param event
     */
    @Async
    @EventListener
    public void addCard(UserCardAddEvent event) {
        EPCardAdd ep = event.getEP();
        long guId = ep.getGuId();
        RDCommon rd = ep.getRd();
        switch (ep.getWay()) {
            case JXZ_AWARD:
            case KZ:
                achieveDailyTask(guId, 60212, 1, rd);
                break;
            default:
                break;
        }
    }


    @Async
    @EventListener
    public void addSpecials(SpecialAddEvent event) {
        EPSpecialAdd ep = event.getEP();
        RDCommon rd = ep.getRd();
        List<EVSpecialAdd> value = ep.getAddSpecials();
        if (ep.getWay().equals(WayEnum.YSG)) {
            achieveDailyTask(ep.getGuId(), 60114, value.size(), rd);
        }
    }


    @Async
    @EventListener
    public void addTreasure(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        long guId = ep.getGuId();
        RDCommon rd = ep.getRd();
        if (ep.getWay().equals(WayEnum.HEIS)) {
            achieveDailyTask(guId, 60213, 1, rd);
        }
    }

    @Async
    @EventListener
    public void deductTreasure(TreasureDeductEvent event) {
        EPTreasureDeduct ep = event.getEP();
        long guId = ep.getGuId();
        RDCommon rd = ep.getRd();
        CfgTreasureEntity treasure = TreasureTool.getTreasureById(ep.getDeductTreasure().getId());
        switch (ep.getWay()) {
            case SHAKE_DICE:// ?????????
            case TREASURE_USE:// ??????????????????
                if (treasure.getType() == 10) {
                    achieveDailyTask(guId, 60015, 1, rd);
                }
                break;
            default:
                break;
        }
    }

    @Async
    @EventListener
    public void finishBusinessGangTask(BusinessGangTaskAchievedEvent event) {
        TaskGroupEnum taskGroup = event.getEP().getTaskGroup();
        if (taskGroup == TaskGroupEnum.BUSINESS_GANG_WEEKLY_TASK) {
            return;
        }
        //??????????????????
        long guid = event.getEP().getGuId();
        RDCommon rd = event.getEP().getRd();
        achieveDailyTask(guid, 60315, 1, rd);

    }

    @Async
    @EventListener
    public void doCombatEvent(CombatInitiateEvent event) {
        //?????????????????????????????????????????????????????????
        long guId = event.getEP().getGuId();
        RDCommon rd = event.getEP().getRd();
        FightTypeEnum typeEnum = FightTypeEnum.fromValue(event.getEP().getFightType());
        switch (typeEnum) {
            case ZXZ:
                achieveDailyTask(guId, 60014, 1, rd);
                break;
            case YG:
            case HELP_YG:
                achieveDailyTask(guId, 60115, 1, rd);
                break;
            case SXDH:
                achieveDailyTask(guId, 60312, 1, rd);
                break;
            case ATTACK:
            case TRAINING:
                achieveDailyTask(guId, 60012, 1, rd);
                break;
            default:
                break;
        }
    }

    @Async
    @EventListener
    public void finishedGuidTask(GuildTaskFinishedEvent event) {
        // ??????????????????
        long guid = event.getEP().getGuId();
        RDCommon rd = event.getEP().getRd();
        achieveDailyTask(guid, 60215, 1, rd);
    }

    /**
     * ????????????
     *
     * @param event
     */
    @Async
    @EventListener
    public void miaoYDrawEnd(MiaoYDrawEndEvent event) {
        @SuppressWarnings("unchecked")
        EventParam<EPMiaoYDrawEnd> ep = (EventParam<EPMiaoYDrawEnd>) event.getSource();
        long guId = ep.getGuId();
        RDCommon rd = ep.getRd();
        achieveDailyTask(guId, 60214, 1, rd);
    }

    @Async
    @EventListener
    public void diceDeductEvent(DiceDeductEvent event) {
        long guId = event.getEP().getGuId();
        RDCommon rd = event.getEP().getRd();
        EPDiceDeduct ep = event.getEP();
        int val = ep.getDeductDice();
        achieveDailyTask(guId, 60013, val, rd);
    }

    @EventListener
    public void gainTaskPoint(DailyTaskAddPointEvent event) {
        //??????????????????
        long guId = event.getEP().getGuId();
        int val = event.getEP().getPoint();
        RDCommon rd = event.getEP().getRd();
        if (!event.getEP().getWay().equals(WayEnum.HERO_BACK_DAILY_TASK)) {
            return;
        }
        List<UserDailyTask> tasks = heroBackTaskProcessor.getShowTasks(guId);
        if (tasks.isEmpty()) {
            return;
        }
        int baseId = tasks.get(0).getBaseId();
        int boxBeginId = TaskTool.getDailyBoxIdBeginByTaskId(baseId);
        for (UserDailyTask task : tasks) {
            if (task.ifAccomplished()) {
                continue;
            }
            if (task.getBaseId() > boxBeginId) {
                task.addValue(val);
                if (task.ifAccomplished()) {
                    rd.setDailyTaskStatus(2);
                }
            }
        }
        gameUserService.updateItems(tasks);
        rd.addDailyDfz(val);
    }

    private void achieveDailyTask(long guId, int taskId, long addedNum, RDCommon rd) {
        UserDailyTask udTask = heroBackTaskProcessor.getTodayTask(guId, taskId);
        if (null == udTask) {
            return;
        }
        // ??????????????????????????????????????????
        if (udTask.ifAccomplished()) {
            return;
        }
        int day = heroBackSignProcessor.getSignDays(gameUserService.getGameUser(guId));
        int unlock = 600 + day - 1;

        if (udTask.getBaseId() / 100 > unlock) {
            return;
        }
        // ??????
        udTask.addValue(addedNum);
        gameUserService.updateItem(udTask);
        // ????????????????????????????????????????????????????????????
        int boxIdBegin = TaskTool.getDailyBoxIdBeginByTaskId(taskId);
        if (udTask.ifAccomplished() && taskId < boxIdBegin) {
            rd.setDailyHeroBackTaskStatus(2);
        }
    }
}
