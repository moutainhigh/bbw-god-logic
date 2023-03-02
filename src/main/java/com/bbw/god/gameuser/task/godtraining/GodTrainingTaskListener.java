package com.bbw.god.gameuser.task.godtraining;

import com.bbw.common.ListUtil;
import com.bbw.god.city.chengc.in.event.BuildingLevelUpEvent;
import com.bbw.god.city.chengc.in.event.EPBuildingLevelUp;
import com.bbw.god.city.event.EPCityAdd;
import com.bbw.god.city.event.UserCityAddEvent;
import com.bbw.god.db.entity.InsNewbieTaskDetail;
import com.bbw.god.db.service.InsNewbieTaskDetailService;
import com.bbw.god.event.EventParam;
import com.bbw.god.event.common.CommonEventPublisher;
import com.bbw.god.fight.FightTypeEnum;
import com.bbw.god.game.combat.event.CombatFailEvent;
import com.bbw.god.game.combat.event.CombatFightWinEvent;
import com.bbw.god.game.combat.event.EPFightEnd;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.treasure.TreasureTool;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.*;
import com.bbw.god.gameuser.helpabout.event.EPReadMenuHelp;
import com.bbw.god.gameuser.helpabout.event.ReadMenuHelpEvent;
import com.bbw.god.gameuser.res.ele.EPEleAdd;
import com.bbw.god.gameuser.res.ele.EleAddEvent;
import com.bbw.god.gameuser.res.exp.EPExpAdd;
import com.bbw.god.gameuser.res.exp.ExpAddEvent;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialDeductEvent;
import com.bbw.god.gameuser.task.TaskGroupEnum;
import com.bbw.god.gameuser.task.TaskStatusEnum;
import com.bbw.god.gameuser.task.businessgang.event.BusinessGangTaskAchievedEvent;
import com.bbw.god.gameuser.task.businessgang.event.EPBusinessGangTask;
import com.bbw.god.gameuser.task.godtraining.event.EPGodTrainingTaskAddPoint;
import com.bbw.god.gameuser.task.godtraining.event.GodTrainingTaskAddPointEvent;
import com.bbw.god.gameuser.task.grow.event.EPFinishNewbieTask;
import com.bbw.god.gameuser.task.grow.event.FinishNewbieTaskEvent;
import com.bbw.god.gameuser.treasure.event.EPTreasureDeduct;
import com.bbw.god.gameuser.treasure.event.EVTreasure;
import com.bbw.god.gameuser.treasure.event.TreasureDeductEvent;
import com.bbw.god.login.LoginPlayer;
import com.bbw.god.login.event.LoginEvent;
import com.bbw.god.mall.cardshop.event.DrawEndEvent;
import com.bbw.god.mall.cardshop.event.EPDraw;
import com.bbw.god.notify.rednotice.ModuleEnum;
import com.bbw.god.server.guild.event.EPGuildTaskFinished;
import com.bbw.god.server.guild.event.GuildTaskFinishedEvent;
import com.bbw.god.server.maou.bossmaou.ServerBossMaou;
import com.bbw.god.server.maou.bossmaou.attackinfo.BossMaouAttackSummary;
import com.bbw.god.server.maou.bossmaou.attackinfo.BossMaouAttackSummaryService;
import com.bbw.god.server.maou.bossmaou.event.BossMaouAwardSendEvent;
import com.bbw.god.server.maou.bossmaou.event.EPBossMaou;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author suchaobin
 * @description 上仙试炼任务监听器
 * @date 2021/1/21 14:52
 **/
@Slf4j
@Component
@Async
public class GodTrainingTaskListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GodTrainingTaskService godTrainingTaskService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private BossMaouAttackSummaryService bossMaouAttackSummaryService;
    @Autowired
    private InsNewbieTaskDetailService insNewbieTaskDetailService;

    private static final List<Integer> NORMAL_STAR5_CARDS = Arrays.asList(101, 236, 302, 401, 502);
    private static final List<Integer> SPECIAL_STAR5_CARDS = Arrays.asList(102, 202, 301, 402, 501, 10102);

    @EventListener
    @Order(1000)
    public void login(LoginEvent event) {
        LoginPlayer player = event.getLoginPlayer();
        Long uid = player.getUid();
        addProgress(uid, Arrays.asList(90011, 90111, 90211, 90311, 90411, 90511, 90611), 1);
    }

    @EventListener
    @Order(1000)
    public void deductSpecial(SpecialDeductEvent event) {
        EPSpecialDeduct ep = event.getEP();
        WayEnum way = ep.getWay();
        Long uid = ep.getGuId();
        int value = ep.getSpecialInfoList().size();
        switch (way) {
            case TRADE:
                addProgress(uid, Arrays.asList(90012, 90112, 90212, 90312, 90412, 90512, 90612), value);
                break;
            case TYF:
                addProgress(ep.getGuId(), 90119, 1);
                break;
            default:
                break;
        }
    }

    @EventListener
    @Order(1000)
    public void fightWin(CombatFightWinEvent event) {
        EPFightEnd source = (EPFightEnd) event.getSource();
        FightTypeEnum fightType = source.getFightType();
        Long uid = source.getGuId();
        switch (fightType) {
            case YG:
                addProgress(uid, Arrays.asList(90013, 90113, 90213, 90313, 90413, 90513, 90613), 1);
                break;
            case HELP_YG:
                addProgress(uid, 90017, 1);
                break;
            case FST:
                addProgress(uid, 90217, 1);
                break;
            case SXDH:
                addProgress(uid, 90219, 1);
                break;
            default:
                break;
        }
    }

    @EventListener
    @Order(1000)
    public void fightFail(CombatFailEvent event) {
        EPFightEnd source = (EPFightEnd) event.getSource();
        FightTypeEnum fightType = source.getFightType();
        Long uid = source.getGuId();
        switch (fightType) {
            case FST:
                addProgress(uid, 90217, 1);
                break;
            case SXDH:
                addProgress(uid, 90219, 1);
                break;
            default:
                break;
        }
    }

    @EventListener
    @Order(1000)
    public void cardLevelUp(UserCardLevelUpEvent event) {
        EPCardLevelUp ep = event.getEP();
        Long uid = ep.getGuId();
        List<UserCard> userCards = userCardService.getUserCards(uid);
        int value3 = (int) userCards.stream().filter(tmp -> tmp.getLevel() >= 3).count();
        int value5 = (int) userCards.stream().filter(tmp -> tmp.getLevel() >= 5).count();
        int value10 = (int) userCards.stream().filter(tmp -> tmp.getLevel() >= 10).count();
        updateProgress(uid, Arrays.asList(90014, 90114), value3);
        updateProgress(uid, Collections.singletonList(90214), value5);
        updateProgress(uid, Arrays.asList(90314, 90414, 90514, 90614), value10);
    }

    @EventListener
    @Order(1000)
    @SuppressWarnings("unchecked")
    public void buildingLevelUp(BuildingLevelUpEvent event) {
        EventParam<EPBuildingLevelUp> ep = (EventParam<EPBuildingLevelUp>) event.getSource();
        Long uid = ep.getGuId();
        addProgress(uid, Arrays.asList(90015, 90115, 90215, 90315, 90415, 90515, 90615), 1);
    }

    @EventListener
    @Order(1000)
    public void cardGrouping(UserCardGroupingEvent event) {
        EPCardGrouping ep = event.getEP();
        String[] values = ep.getCardGroups().split("!");
        List<Integer> cardIds = ListUtil.parseStrToInts(values[0]);
        if (0 >= cardIds.size()) {
            return;
        }
        int value = Math.min(cardIds.size(), 7);
        updateProgress(ep.getGuId(), 90016, value);
    }

    @EventListener
    @Order(1000)
    public void addEle(EleAddEvent event) {
        EPEleAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        if (WayEnum.OPEN_YuanSLB != way) {
            return;
        }
        long uid = ep.getGuId();
        addProgress(uid, 90018, 1);
    }

    @EventListener
    @Order(1000)
    public void readMenuHelp(ReadMenuHelpEvent event) {
        EPReadMenuHelp ep = event.getEP();
        addProgress(ep.getGuId(), 90019, 1);
    }

    @EventListener
    @Order(1000)
    public void addCard(UserCardAddEvent event) {
        EPCardAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        Long uid = ep.getGuId();
        if (WayEnum.OPEN_WANWU_CARD_POOL == way) {
            addProgress(uid, 90020, 1);
        }
        List<UserCard> userCards = userCardService.getUserCards(uid);
        int normal5 = (int) userCards.stream().filter(tmp -> NORMAL_STAR5_CARDS.contains(CardTool.getNormalCardId(tmp.getBaseId()))).count();
        int special5 = (int) userCards.stream().filter(tmp -> SPECIAL_STAR5_CARDS.contains(CardTool.getNormalCardId(tmp.getBaseId()))).count();
        updateProgress(uid, 95705, normal5);
        updateProgress(uid, 95706, special5);
    }

    @EventListener
    @Order(1000)
    public void deductTreasure(TreasureDeductEvent event) {
        EPTreasureDeduct ep = event.getEP();
        EVTreasure deductTreasure = ep.getDeductTreasure();
        Integer treasureId = deductTreasure.getId();
        if (TreasureTool.getFightTreasureIds().contains(treasureId)) {
            addProgress(ep.getGuId(), 90021, 1);
        }
    }

    @EventListener
    @Order(1000)
    public void drawCard(DrawEndEvent event) {
        EventParam<EPDraw> ep = (EventParam<EPDraw>) event.getSource();
        EPDraw epDraw = ep.getValue();
        Integer drawTimes = epDraw.getDrawTimes();
        addProgress(ep.getGuId(), Arrays.asList(90116, 90216, 90316, 90416, 90516, 90616), drawTimes);
    }

    @EventListener
    @Order(1000)
    public void finishGuildEightDiagramsTask(GuildTaskFinishedEvent event) {
        EPGuildTaskFinished ep = event.getEP();
        Long uid = ep.getGuId();
        addProgress(uid, 90117, 1);
    }

    @EventListener
    @Order(1000)
    public void attackMaou(BossMaouAwardSendEvent event) {
        EPBossMaou ep = event.getEP();
        ServerBossMaou bossMaou = ep.getBossMaou();
        List<BossMaouAttackSummary> ranker = bossMaouAttackSummaryService.getAttackInfoSorted(bossMaou);
        for (BossMaouAttackSummary summary : ranker) {
            Long uid = summary.getGuId();
            addProgress(uid, 90118, 1);
        }
    }

    @EventListener
    @Order(1000)
    public void finishBusinessGangTask(BusinessGangTaskAchievedEvent event) {
        EPBusinessGangTask ep = event.getEP();
        if (ep.getTaskGroup() == TaskGroupEnum.BUSINESS_GANG_WEEKLY_TASK) {
            return;
        }
        addProgress(ep.getGuId(), 90218, 1);
    }

    @EventListener
    @Order(1000)
    public void addPoint(GodTrainingTaskAddPointEvent event) {
        EPGodTrainingTaskAddPoint ep = event.getEP();
        Long uid = ep.getGuId();
        int value = ep.getPoint();
        addProgress(uid, Arrays.asList(90901, 90902, 90903, 90904, 90905), value);
    }

    @EventListener
    @Order(1000)
    public void addExp(ExpAddEvent event) {
        EPExpAdd ep = event.getEP();
        Long uid = ep.getGuId();
        Integer level = gameUserService.getGameUser(uid).getLevel();
        updateProgress(uid, 95701, level);
    }

    @EventListener
    @Order(1000)
    public void addCity(UserCityAddEvent event) {
        EventParam<EPCityAdd> ep = (EventParam<EPCityAdd>) event.getSource();
        // 梦魇城池直接return
        if (ep.getValue().isNightmare()) {
            return;
        }
        int cityId = ep.getValue().getCityId();
        int level = CityTool.getCityById(cityId).getLevel();
        Long uid = ep.getGuId();
        switch (level) {
            case 3:
                addProgress(uid, 95702, 1);
                break;
            case 4:
                addProgress(uid, 95703, 1);
                break;
            case 5:
                addProgress(uid, 95704, 1);
                break;
            default:
                break;
        }
        addProgress(uid, 95707, 1);
    }

    @EventListener
    @Order(1000)
    public void log(FinishNewbieTaskEvent event) {
        EPFinishNewbieTask ep = event.getEP();
        Long uid = ep.getGuId();
        Integer sid = gameUserService.getGameUser(uid).getServerId();
        Integer step = ep.getStep();
        String name = ep.getStepName();
        InsNewbieTaskDetail detail = InsNewbieTaskDetail.getInstance(uid, sid, step, name, "v2");
        insNewbieTaskDetailService.insert(detail);
    }

    private void addProgress(long uid, int taskId, int value) {
        UserGodTrainingTask userTrainingTask = godTrainingTaskService.getUserTrainingTask(uid, taskId);
        if (null == userTrainingTask) {
            return;
        }
        Integer status = userTrainingTask.getStatus();
        if (status >= TaskStatusEnum.ACCOMPLISHED.getValue()) {
            return;
        }
        userTrainingTask.addValue(value);
        gameUserService.updateItem(userTrainingTask);
        redNotice(uid, taskId, userTrainingTask);
    }

    private void addProgress(long uid, List<Integer> taskIds, int value) {
        for (Integer taskId : taskIds) {
            addProgress(uid, taskId, value);
        }
    }

    private void updateProgress(long uid, int taskId, int value) {
        UserGodTrainingTask userTrainingTask = godTrainingTaskService.getUserTrainingTask(uid, taskId);
        if (null == userTrainingTask) {
            return;
        }
        Integer status = userTrainingTask.getStatus();
        if (status >= TaskStatusEnum.ACCOMPLISHED.getValue()) {
            return;
        }
        userTrainingTask.updateValue(value);
        gameUserService.updateItem(userTrainingTask);
        redNotice(uid, taskId, userTrainingTask);
    }

    private void redNotice(long uid, int taskId, UserGodTrainingTask userTrainingTask) {
        if (userTrainingTask.getStatus() == TaskStatusEnum.ACCOMPLISHED.getValue()) {
            int days = userTrainingTask.getDays() == 0 ? 8 : userTrainingTask.getDays();
            CommonEventPublisher.pubAccomplishEvent(uid, ModuleEnum.GOD_TRAINING, days, taskId);
        }
    }

    private void updateProgress(long uid, List<Integer> taskIds, int value) {
        for (Integer taskId : taskIds) {
            updateProgress(uid, taskId, value);
        }
    }
}
