/*
package com.bbw.god.gameuser.achievement.listener;

import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.in.event.BuildingLevelUpEvent;
import com.bbw.god.city.chengc.in.event.EPBuildingLevelUp;
import com.bbw.god.city.nvwm.EPNvWMDonate;
import com.bbw.god.city.nvwm.NwmDonateEvent;
import com.bbw.god.city.yed.EPYeDTrigger;
import com.bbw.god.city.yed.YeDTriggerEvent;
import com.bbw.god.event.EventParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.card.CardTool;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.city.YdEventEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.achievement.AchievementSerialEnum;
import com.bbw.god.gameuser.achievement.UserAchievement;
import com.bbw.god.gameuser.achievement.UserAchievementService;
import com.bbw.god.gameuser.card.event.EPCardAdd;
import com.bbw.god.gameuser.card.event.UserCardAddEvent;
import com.bbw.god.gameuser.res.copper.CopperAddEvent;
import com.bbw.god.gameuser.res.copper.EPCopperAdd;
import com.bbw.god.gameuser.res.ele.EPEleAdd;
import com.bbw.god.gameuser.res.ele.EleAddEvent;
import com.bbw.god.gameuser.shake.EPShake;
import com.bbw.god.gameuser.shake.ShakeEvent;
import com.bbw.god.gameuser.special.UserSpecialSaleRecord;
import com.bbw.god.gameuser.special.event.EPSpecialDeduct;
import com.bbw.god.gameuser.special.event.SpecialDeductEvent;
import com.bbw.god.gameuser.treasure.event.EPTreasureAdd;
import com.bbw.god.gameuser.treasure.event.TreasureAddEvent;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.server.flx.event.*;
import com.bbw.god.server.guild.event.EPGuildOpenBox;
import com.bbw.god.server.guild.event.GuildOpenBoxEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

*/
/**
 * @author suchaobin
 * @description 历练成就监听
 * @date 2020/2/21 9:09
 *//*

@Component
public class ExperienceAchievementListener {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserAchievementService userAchievementService;
    @Autowired
    private UserCityService userCityService;

    @EventListener
    @Order(1000)
    public void buildingLevelUp(BuildingLevelUpEvent event) {
        EventParam<EPBuildingLevelUp> ep = (EventParam<EPBuildingLevelUp>) event.getSource();
        long guId = ep.getGuId();
        EPBuildingLevelUp value = ep.getValue();
        UserCity uCity = userCityService.getUserCity(guId, value.getCityId());
        RDCommon rd = ep.getRd();
        // 成就任务
        if (uCity.ifUpdate5()) {
            this.userAchievementService.achieve(guId, AchievementSerialEnum.ALL_CITY_LEVEL5, 1, rd);
        }
        if (uCity.ifUpdate(6)) {
            this.userAchievementService.achieve(guId, AchievementSerialEnum.HALF_CITY_LEVEL6, 1, rd);
            this.userAchievementService.achieve(guId, AchievementSerialEnum.ALL_CITY_LEVEL6, 1, rd);
        }
        if (uCity.ifUpdate(7)) {
            this.userAchievementService.achieve(guId, AchievementSerialEnum.HALF_CITY_LEVEL7, 1, rd);
            this.userAchievementService.achieve(guId, AchievementSerialEnum.ALL_CITY_LEVEL7, 1, rd);
        }
        if (uCity.ifUpdate(8)) {
            this.userAchievementService.achieve(guId, AchievementSerialEnum.HALF_CITY_LEVEL8, 1, rd);
            this.userAchievementService.achieve(guId, AchievementSerialEnum.ALL_CITY_LEVEL8, 1, rd);
        }
        if (uCity.ifUpdate(9)) {
            this.userAchievementService.achieve(guId, AchievementSerialEnum.HALF_CITY_LEVEL9, 1, rd);
            this.userAchievementService.achieve(guId, AchievementSerialEnum.ALL_CITY_LEVEL9, 1, rd);
        }
        if (uCity.ifUpdate(10)) {
            this.userAchievementService.achieve(guId, AchievementSerialEnum.HALF_CITY_LEVEL10, 1, rd);
            this.userAchievementService.achieve(guId, AchievementSerialEnum.ALL_CITY_LEVEL10, 1, rd);
        }
    }

    @EventListener
    @Order(1000)
    public void addTreasure(TreasureAddEvent event) {
        EPTreasureAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        switch (way) {
            case BBX_PICK:
                // 捡到百宝箱会触发2次事件，故做处理
                UserAchievement userAchievement = this.userAchievementService.getUserAchievement(ep.getGuId(), 13690);
                if (userAchievement == null) {
                    this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.GET_BOX_FROM_MAP, 1, ep.getRd());
                } else {
                    long lastUpdateTime = userAchievement.getLastUpdateTime();
                    if (DateUtil.toDateInt(DateUtil.fromDateLong(lastUpdateTime)) < DateUtil.toDateInt(DateUtil.now())) {
                        this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.GET_BOX_FROM_MAP, 1, ep.getRd());
                    }
                }
                break;
            case XRD:
                this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.XIAN_REN_DONG, 1, ep.getRd());
                break;
            default:
                break;
        }
    }

    @EventListener
    @Order(2)
    public void deductSpecial(SpecialDeductEvent event) {
        EPSpecialDeduct ep = event.getEP();
        WayEnum way = ep.getWay();
        List<EPSpecialDeduct.SpecialInfo> specialInfoList = ep.getSpecialInfoList();
        List<Integer> specialIds = specialInfoList.stream().map(EPSpecialDeduct.SpecialInfo::getBaseSpecialIds).collect(Collectors.toList());
        GameUser gu = this.gameUserService.getGameUser(ep.getGuId());
        int position = gu.getLocation().getPosition();
        switch (way) {
            case TRADE:
                UserSpecialSaleRecord record = this.gameUserService.getSingleItem(ep.getGuId(), UserSpecialSaleRecord.class);
                if (record == null) {
                    record = UserSpecialSaleRecord.instanceSaleRecord(ep.getGuId(), specialIds, CityTool.getCityId(position));
                    this.gameUserService.addItem(ep.getGuId(), record);
                }
                for (EPSpecialDeduct.SpecialInfo info : specialInfoList) {
                    Integer specialId = info.getBaseSpecialIds();
                    if (record.isNewSpecial(specialId)) {
                        record.addSpecial(specialId);
                        this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.SALE_DIFFERENT_SPECIAL, 1, ep.getRd());
                    }
                }
                record.updateSaleRecord(CityTool.getCityId(position), specialIds);
                this.gameUserService.updateItem(record);
                break;
            case TYF:
                this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.TYF_DONATE_SPECIAL, 1, ep.getRd());
                break;
            default:
                break;
        }
    }

    @EventListener
    @Order(1000)
    public void nwmDonate(NwmDonateEvent event) {
        EPNvWMDonate ep = event.getEP();
        Integer satisfaction = ep.getSatisfaction();
        this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.NW_FAVOR, satisfaction, ep.getRd());
    }

    @EventListener
    @Order(1000)
    public void shake(ShakeEvent event) {
        EPShake ep = event.getEP();
        List<Integer> shakeList = ep.getShakeList();
        int shakeSum = shakeList.stream().mapToInt(Integer::intValue).sum();
        this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.MOVE, shakeSum, ep.getRd());
    }


    @EventListener
    @Order(1000)
    public void yeDRandomEvent(YeDTriggerEvent event) {
        EventParam<EPYeDTrigger> ep = (EventParam<EPYeDTrigger>) event.getSource();
        EPYeDTrigger epYeDTrigger = ep.getValue();
        YdEventEnum eventEnum = epYeDTrigger.getEvent();
        switch (eventEnum) {
            case XIAO_TOU:
            case QIANG_DAO:
                this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.MEET_THIEF_OR_ROBBER, 1, ep.getRd());
                break;
            default:
                break;
        }
        this.userAchievementService.achieveByOneDay(ep.getGuId(), 13750, AchievementSerialEnum.FU_HUO_XIANG_YI, 1, ep.getRd());
    }

    @EventListener
    @Order(2)
    public void addCopper(CopperAddEvent event) {
        EPCopperAdd ep = event.getEP();
        if (ep.getWay() == WayEnum.JB) {
            this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.JIE_BEI, 1, ep.getRd());
        }
    }

    @EventListener
    @Order(2)
    public void addEle(EleAddEvent event) {
        EPEleAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        switch (way) {
            case CZ:
                this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.CUN_ZHUANG, 1, ep.getRd());
                break;
            default:
                break;
        }
    }

    @EventListener
    @Order(2)
    public void addCard(UserCardAddEvent event) {
        EPCardAdd ep = event.getEP();
        WayEnum way = ep.getWay();
        List<EPCardAdd.CardAddInfo> addCards = ep.getAddCards();
        switch (way) {
            case KZ:
            case JXZ_AWARD:
                List<EPCardAdd.CardAddInfo> fiveStarCards = addCards.stream().filter(ac ->
                        CardTool.getCardById(ac.getCardId()).getStar() == 5).collect(Collectors.toList());
                if (ListUtil.isNotEmpty(fiveStarCards)) {
                    this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.BUY_FIVE_STAR_CARD, 1, ep.getRd());
                }
                break;
            default:
                break;
        }
    }


    @EventListener
    @Order(2)
    public void openGuildBox(GuildOpenBoxEvent event) {
        EPGuildOpenBox ep = event.getEP();
        this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.OPEN_GUILD_BOX, 1, ep.getRd());
    }

    @EventListener
    @Order(2)
    public void yaYaLe(YaYaLeWinEvent event) {
        EPYaYaLeWin ep = event.getEP();
        Integer type = ep.getType();
        if (type.equals(YaYaLeAwardTypeEnum.FIRST_PRIZE.getValue())) {
            this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.YA_YA_LE, 1, ep.getRd());
            this.userAchievementService.resetAchievement(ep.getGuId(), AchievementSerialEnum.BAI_SHI_WU_CHENG, 0, ep.getRd());
        }
    }

    @EventListener
    @Order(2)
    public void caiShuZi(CaiShuZiWinEvent event) {
        EPCaiShuZiWin ep = event.getEP();
        this.userAchievementService.achieve(ep.getGuId(), AchievementSerialEnum.CAI_SHU_ZI, 1, ep.getRd());
        this.userAchievementService.resetAchievement(ep.getGuId(), AchievementSerialEnum.BAI_SHI_WU_CHENG, 0, ep
        .getRd());
    }
}
*/
