package com.bbw.god.gameuser.task.main;

import com.bbw.common.ListUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.in.event.BuildingLevelUpEvent;
import com.bbw.god.city.chengc.in.event.EPBuildingLevelUp;
import com.bbw.god.city.event.EPCityAdd;
import com.bbw.god.city.event.UserCityAddEvent;
import com.bbw.god.event.EventParam;
import com.bbw.god.event.common.CommonEventPublisher;
import com.bbw.god.fight.RDFightResult;
import com.bbw.god.game.config.WorldType;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.card.event.EPCardAdd.CardAddInfo;
import com.bbw.god.gameuser.card.event.EPCardDel;
import com.bbw.god.gameuser.card.event.UserCardAddEvent;
import com.bbw.god.gameuser.card.event.UserCardDelEvent;
import com.bbw.god.gameuser.task.TaskTypeEnum;
import com.bbw.god.notify.rednotice.ModuleEnum;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class UserMainTaskListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserMainTaskService mainTaskService;
    @Autowired
    private UserCityService userCityService;

    @EventListener
    @Order(1000)
    public void addCard(UserCardAddEvent event) {
        EPCardAdd ep = event.getEP();
        long guId = ep.getGuId();
        RDCommon rd = ep.getRd();
        List<CardAddInfo> newCards = ep.getAddCards().stream().filter(tmp -> tmp.isNew()).collect(Collectors.toList());

        if (ListUtil.isNotEmpty(newCards)) {
            // 去重
            Long newCount = newCards.stream().map(CardAddInfo::getCardId).distinct().count();
            if (newCount > 0) {
                this.achieveMainTask(guId, 1300, newCount.intValue(), rd);
            }
        }
    }

    @EventListener
    @Order(1000)
    public void buildingLevelUp(BuildingLevelUpEvent event) {
        EventParam<EPBuildingLevelUp> ep = (EventParam<EPBuildingLevelUp>) event.getSource();
        long guId = ep.getGuId();
        EPBuildingLevelUp value = ep.getValue();
        UserCity uCity = userCityService.getUserCity(guId, value.getCityId());
        RDCommon rd = ep.getRd();
        if (uCity.ifUpdate5()) {
            this.achieveMainTask(guId, 1200, 1, rd);
        }
    }

    @EventListener
    @Order(1000)
    public void addUserCity(UserCityAddEvent event) {
        EventParam<EPCityAdd> ep = (EventParam<EPCityAdd>) event.getSource();
        RDFightResult rd = (RDFightResult) ep.getRd();
        long guId = ep.getGuId();
        GameUser gu = gameUserService.getGameUser(guId);
        if (WorldType.NORMAL.getValue()==(gu.getStatus().getCurWordType())) {
            this.achieveMainTask(guId, 1100, 1, rd);
        }
    }

    @EventListener
    @Order(1000)
    public void delCard(UserCardDelEvent event) {
        EPCardDel ep = event.getEP();
        List<UserCard> uCards = ep.getDelCards();
        if (ListUtil.isNotEmpty(uCards)) {
            this.achieveMainTask(ep.getGuId(), 1300, -uCards.size(), ep.getRd());
        }
    }

    // @EventListener
    // @Order(1000)
    // public void fightWin(FightWinEvent event) {
    // EVFightWin ep = (EVFightWin) event.getSource();
    // long uid = ep.getGuId();
    // RDCommon rd = ep.getRd();
    // if (ep.getFightType() == FightTypeEnum.PROMOTE) {
    // CfgCityEntity city = CityTool.getCityByRoadId(ep.getPos());
    // UserCity userCity = gameUserService.getCfgItem(uid, city.getId(), UserCity.class);
    // if (userCity.getHierarchy() == 1) {
    // achieveMainTask(uid, 1400, 1, rd);
    // }
    // }
    //
    // }

    private void achieveMainTask(long guId, int taskId, int addValue, RDCommon rd) {
        UserMainTask umTask = this.mainTaskService.getUserMainTask(guId, taskId);
        umTask.addEnableAwardIndex(addValue);
        if (rd != null && umTask.isEnableAward()) {
            CommonEventPublisher.pubAccomplishEvent(guId, ModuleEnum.TASK, TaskTypeEnum.MAIN_TASK.getValue(), taskId);
            rd.setDailyTaskStatus(1);
        }
        this.gameUserService.updateItem(umTask);
    }

}
