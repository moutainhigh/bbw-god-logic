package com.bbw.god.gameuser.task.grow;

import com.bbw.common.ListUtil;
import com.bbw.god.city.chengc.in.event.BuildingLevelUpEvent;
import com.bbw.god.city.chengc.in.event.EPBuildingLevelUp;
import com.bbw.god.city.event.EPCityAdd;
import com.bbw.god.city.event.UserCityAddEvent;
import com.bbw.god.db.service.InsNewbieTaskDetailService;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.event.EventParam;
import com.bbw.god.event.common.CommonEventPublisher;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.combat.event.CombatFailEvent;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.buddy.event.BuddyAcceptEvent;
import com.bbw.god.gameuser.card.event.*;
import com.bbw.god.gameuser.guide.EPPassNewerGuide;
import com.bbw.god.gameuser.guide.NewerGuideService;
import com.bbw.god.gameuser.guide.PassNewerGuideEvent;
import com.bbw.god.gameuser.helpabout.event.EPReadMenuHelp;
import com.bbw.god.gameuser.helpabout.event.ReadMenuHelpEvent;
import com.bbw.god.gameuser.res.ele.EPEleAdd;
import com.bbw.god.gameuser.res.ele.EleAddEvent;
import com.bbw.god.gameuser.res.exp.EPExpAdd;
import com.bbw.god.gameuser.res.exp.ExpAddEvent;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialDeductEvent;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.gameuser.task.fshelper.FsTaskEnum;
import com.bbw.god.gameuser.task.fshelper.event.EpFsHelperChange;
import com.bbw.god.gameuser.task.fshelper.event.TaskEventPublisher;
import com.bbw.god.gameuser.treasure.event.EPTreasureDeduct;
import com.bbw.god.gameuser.treasure.event.TreasureDeductEvent;
import com.bbw.god.notify.rednotice.ModuleEnum;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UserGrowTaskListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private NewbieTaskService growTaskService;
    @Autowired
    private NewerGuideService newerGuideService;
    @Autowired
    private InsNewbieTaskDetailService insNewbieTaskDetailService;

    @EventListener
    @Order(1000)
    public void forward(PassNewerGuideEvent event) {
        EPPassNewerGuide ep = event.getEP();
        achieveGrowTask(ep.getGuId(), 10, 1, ep.getRd());
    }

    @EventListener
    @Order(1000)
    public void deductSpecials(SpecialDeductEvent event) {
        EPSpecialDeduct ep = event.getEP();
        long guId = ep.getGuId();
        RDCommon rd = ep.getRd();
        List<EPSpecialDeduct.SpecialInfo> specialInfoList = ep.getSpecialInfoList();
        if (ep.getWay() == WayEnum.TRADE) {// 交易
            achieveGrowTask(guId, 20, 1, rd);
            achieveGrowTask(guId, 70, specialInfoList.size(), rd);
        }
    }

    @EventListener
    @Order(1000)
    public void addCard(UserCardAddEvent event) {
        EPCardAdd ep = event.getEP();
        long guId = ep.getGuId();
        RDCommon rd = ep.getRd();
        switch (ep.getWay()) {
            case OPEN_GOLD_CARD_POOL:
            case OPEN_WOOD_CARD_POOL:
            case OPEN_WATER_CARD_POOL:
            case OPEN_FIRE_CARD_POOL:
            case OPEN_EARTH_CARD_POOL:
            case OPEN_WANWU_CARD_POOL:
                achieveGrowTask(guId, 210, 1, rd);
                break;
            default:
                break;
        }
    }

    @EventListener
    @Order(1000)
    public void levelUp(UserCardLevelUpEvent event) {
        EPCardLevelUp ep = event.getEP();
        long guId = ep.getGuId();
        RDCommon rd = ep.getRd();
        int newLevel = ep.getNewLevel();
        Integer oldLevel = ep.getOldLevel();
        Integer cardId = ep.getCardId();
        if (109 == cardId && newLevel > oldLevel) {
            achieveGrowTask(guId, 340, newLevel - oldLevel, rd);
        }
        if (newLevel >= 3 && oldLevel < 3) {
            achieveGrowTask(guId, 100, 1, rd);
        }
    }

    @EventListener
    public void fightWin(CombatFightWinEvent event) {
        EPFightEnd ep = (EPFightEnd) event.getSource();
        long guId = ep.getGuId();
        RDCommon rd = ep.getRd();
        switch (ep.getFightType()) {
            case YG:
                achieveGrowTask(guId, 50, 1, rd);
                break;
            case HELP_YG:
                achieveGrowTask(guId, 90, 1, rd);
                break;
            case FST:
                achieveGrowTask(guId, 280, 1, rd);
                break;
            default:
                break;
        }
        achieveGrowTask(guId, 270, 1, rd);
        if (newerGuideService.isPassNewerGuide(guId)) {
            // 该任务需要计算为非引导时的战斗
            achieveGrowTask(guId, 40, 1, rd);
        }
    }

    @EventListener
    public void fightFail(CombatFailEvent event) {
        EPFightEnd ep = (EPFightEnd) event.getSource();
        long guId = ep.getGuId();
        RDCommon rd = ep.getRd();
        switch (ep.getFightType()) {
            case FST:
                achieveGrowTask(guId, 280, 1, rd);
                break;
            case HELP_YG:
                achieveGrowTask(guId, 90, 1, rd);
                break;
            default:
                break;
        }
        achieveGrowTask(guId, 270, 1, rd);
    }

    @EventListener
    @Order(1000)
    public void addUserCity(UserCityAddEvent event) {
        EventParam<EPCityAdd> ep = (EventParam<EPCityAdd>) event.getSource();
        RDFightResult rd = (RDFightResult) ep.getRd();
        long guId = ep.getGuId();
        CfgCityEntity city = CityTool.getCityById(ep.getValue().getCityId());
        if (city.getLevel() == 3) {
            achieveGrowTask(guId, 300, 1, rd);
        }

        achieveGrowTask(guId, 111, 1, rd);
        achieveGrowTask(guId, 112, 1, rd);
        achieveGrowTask(guId, 113, 1, rd);
    }

    @EventListener
    @Order(1000)
    public void grouping(UserCardGroupingEvent event) {
        EPCardGrouping ep = event.getEP();
        String[] values = ep.getCardGroups().split("!");
        List<Integer> cardIds = ListUtil.parseStrToInts(values[0]);
        if (0 >= cardIds.size()) {
            return;
        }
        // 新手进阶任务
        int addValue = Math.min(cardIds.size(), 7);
        achieveGrowTask(ep.getGuId(), 60, addValue, ep.getRd());
    }

    @EventListener
    @Order(1000)
    public void deductTreasure(TreasureDeductEvent event) {
        EPTreasureDeduct ep = event.getEP();
        long guId = ep.getGuId();
        RDCommon rd = ep.getRd();
        int treasureId = ep.getDeductTreasure().getId();
        // 使用绝仙剑
        if (ep.getWay() == WayEnum.FIGHT_ATTACK_EXPEND && treasureId == TreasureEnum.JXJ.getValue()) {
            achieveGrowTask(guId, 260, 1, rd);
        }
        // 使用漫步靴
        if (ep.getWay() == WayEnum.SHAKE_DICE && treasureId == TreasureEnum.MBX.getValue()) {
            achieveGrowTask(guId, 230, 1, rd);
        }
        // 使用青鸾
        if (ep.getWay() == WayEnum.TREASURE_USE && treasureId == TreasureEnum.QL.getValue()) {
            achieveGrowTask(guId, 320, 1, rd);
        }
    }

    @EventListener
    public void acceptBuddy(BuddyAcceptEvent event) {
        EventParam<Long> ep = (EventParam<Long>) event.getSource();
        RDCommon rd = ep.getRd();
        long myId = ep.getGuId();
        long buddyId = ep.getValue();
        // 新手进阶任务
        achieveGrowTask(myId, 80, 1, rd);
        achieveGrowTask(buddyId, 80, 1, rd);
    }

    @EventListener
    @Order(1000)
    public void readMenuHelp(ReadMenuHelpEvent event) {
        EPReadMenuHelp ep = event.getEP();
        RDCommon rd = ep.getRd();

        // 新手进阶任务
        achieveGrowTask(ep.getGuId(), 310, 1, rd);
    }

    @Async
    @EventListener
    public void addEleEvent(EleAddEvent event) {
        EPEleAdd ep = event.getEP();
        long guId = ep.getGuId();
        if (ep.getWay().equals(WayEnum.OPEN_YuanSLB)) {
            achieveGrowTask(guId, 321, 1, ep.getRd());
        }
    }

    @Async
    @EventListener
    public void updateBuilding(BuildingLevelUpEvent event) {
        EventParam<EPBuildingLevelUp> ep = (EventParam<EPBuildingLevelUp>) event.getSource();
        Long uid = ep.getGuId();
        EPBuildingLevelUp value = ep.getValue();
        achieveGrowTask(uid, 330, value.getLevelUpBuildings().size(), ep.getRd());
    }

    @EventListener
    @Order(1000)
    public void userLevelUp(ExpAddEvent event) {
        EPExpAdd ep = event.getEP();
        Long uid = ep.getGuId();
        Integer level = gameUserService.getGameUser(uid).getLevel();
        achieveGrowTask(uid, 350, level, ep.getRd());
    }

    /*@Async
    @EventListener
    @Order(1000)
    public void log(FinishNewbieTaskEvent event) {
        EPFinishNewbieTask ep = event.getEP();
        Long uid = ep.getGuId();
        Integer sid = gameUserService.getGameUser(uid).getServerId();
        Integer step = ep.getStep();
        String name = ep.getStepName();
        InsNewbieTaskDetail detail = InsNewbieTaskDetail.getInstance(uid, sid, step, name, "v0");
        insNewbieTaskDetailService.insert(detail);
    }*/

    private void achieveGrowTask(long guId, int taskId, int addValue, RDCommon rd) {
        GameUser gu = this.gameUserService.getGameUser(guId);
        // 玩家已通过所有的新手进阶任务，则不做任何处理
        if (gu.getStatus().isGrowTaskCompleted()) {
            return;
        }
        Optional<UserGrowTask> ugTaskOp = growTaskService.getUserGrowTask(guId, taskId);
        // 如果任务达成不在做任何处理
        if (!ugTaskOp.isPresent() || ugTaskOp.get().ifAccomplished()) {
            return;
        }
        UserGrowTask ugTask = ugTaskOp.get();
        // 加值
        if (ugTask.getBaseId() == 60 || ugTask.getBaseId() == 350) {
            if (addValue <= ugTask.getValue()) {
                return;
            }
            ugTask.updateProgress(addValue);
        } else {
            ugTask.addValue(addValue);
        }
        this.gameUserService.updateItem(ugTask);
        // 如果达成通知客户端
        if (ugTask.ifAccomplished()) {
            CommonEventPublisher.pubAccomplishEvent(guId, ModuleEnum.TASK, TaskTypeEnum.NEWER_TASK.getValue(), taskId);
            rd.setGrowTaskStatus(1);
        }
        EpFsHelperChange dta = EpFsHelperChange.instanceUpdateTask(new BaseEventParam(guId), FsTaskEnum.NewBie, taskId);
        TaskEventPublisher.pubEpFsHelperChangeEvent(dta);
    }
}
