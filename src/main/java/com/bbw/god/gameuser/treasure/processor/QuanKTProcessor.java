package com.bbw.god.gameuser.treasure.processor;

import com.bbw.common.PowerRandom;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.CityChecker;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.in.event.ChengCInEventPublisher;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.*;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.CPUseTreasure;
import com.bbw.god.gameuser.treasure.RDUseMapTreasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 乾坤图
 *
 * @author suhq
 * @date 2018年11月28日 下午4:59:12
 */
@Service
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class QuanKTProcessor extends TreasureUseProcessor {

    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCityService userCityService;
    public QuanKTProcessor() {
        this.treasureEnum = TreasureEnum.QKT;
        this.isAutoBuy = false;
    }

    @Override
    public void check(GameUser gu, CPUseTreasure param) {
        int pos = param.gainPosition();
        CfgRoadEntity road = RoadTool.getRoadById(pos);
        CfgCityEntity city = road.getCity();
        // 是否是城池
        CityChecker.checkIsCC(city);
        UserCity userCity = userCityService.getUserCity(gu.getId(), city.getId());
        // 城池是否占用
        CityChecker.checkIsOwnCC(userCity);
        // 建筑是否升满
        if (userCity.ifUpdateFull()) {
            throw new ExceptionForClientTip("city.cc.in.already.updateFull");
        }
    }

    @Override
    public void effect(GameUser gu, CPUseTreasure param, RDUseMapTreasure rd) {
        int pos = param.gainPosition();
        CfgRoadEntity road = RoadTool.getRoadById(pos);
        CfgCityEntity city = road.getCity();
        UserCity userCity = userCityService.getUserCity(gu.getId(), city.getId());
        // 未振兴过的城池，所有建筑等级+1
        int topCCHierarchy = CityConfig.bean().getCcData().getTopCCHierarchy();
        List<Integer> levelUpBuildings = new ArrayList<>();
        if (userCity.getHierarchy() == 0) {
            // 府衙
            int newLevel = Math.min(userCity.getFy() + 1, topCCHierarchy);
            if (newLevel > userCity.getFy()) {
                userCity.setFy(newLevel);
                levelUpBuildings.add(BuildingEnum.FY.getValue());
            }
            // 矿场
            newLevel = Math.min(userCity.getKc() + 1, topCCHierarchy);
            if (newLevel > userCity.getKc()) {
                userCity.setKc(newLevel);
                levelUpBuildings.add(BuildingEnum.KC.getValue());
            }
            // 钱庄
            newLevel = Math.min(userCity.getQz() + 1, topCCHierarchy);
            if (newLevel > userCity.getQz()) {
                userCity.setQz(newLevel);
                levelUpBuildings.add(BuildingEnum.QZ.getValue());
            }
            // 特产铺
            newLevel = Math.min(userCity.getTcp() + 1, topCCHierarchy);
            if (newLevel > userCity.getTcp()) {
                userCity.setTcp(newLevel);
                levelUpBuildings.add(BuildingEnum.TCP.getValue());
            }

            // 聚贤庄
            newLevel = Math.min(userCity.getJxz() + 1, topCCHierarchy);
            if (newLevel > userCity.getJxz()) {
                userCity.setJxz(newLevel);
                levelUpBuildings.add(BuildingEnum.JXZ.getValue());
            }
            // 炼宝炉
            newLevel = Math.min(userCity.getLbl() + 1, topCCHierarchy);
            if (newLevel > userCity.getLbl()) {
                userCity.setLbl(newLevel);
                levelUpBuildings.add(BuildingEnum.LBL.getValue());
            }
            // 道场
            newLevel = Math.min(userCity.getDc() + 1, topCCHierarchy);
            if (newLevel > userCity.getDc()) {
                userCity.setDc(newLevel);
                levelUpBuildings.add(BuildingEnum.DC.getValue());
            }
            // 炼丹房
            newLevel = Math.min(userCity.getLdf() + 1, topCCHierarchy);
            if (newLevel > userCity.getLdf()) {
                userCity.setLdf(newLevel);
                levelUpBuildings.add(BuildingEnum.LDF.getValue());
            }
        } else {
            // 振兴过的城池随机一建筑等级+1
            int fullLevel = topCCHierarchy + userCity.getHierarchy();
            if (userCity.getFy() < fullLevel) {
                userCity.setFy(fullLevel);
                levelUpBuildings.add(BuildingEnum.FY.getValue());
            } else {
                // 可升级的建筑集合
                List<Integer> ableUpdateTypes = new ArrayList<Integer>();
                if (userCity.getQz() < fullLevel) ableUpdateTypes.add(BuildingEnum.QZ.getValue());
                if (userCity.getDc() < fullLevel) ableUpdateTypes.add(BuildingEnum.DC.getValue());
                if (userCity.getLdf() < fullLevel) ableUpdateTypes.add(BuildingEnum.LDF.getValue());
                if (userCity.getTcp() < fullLevel) ableUpdateTypes.add(BuildingEnum.TCP.getValue());
                else if (userCity.getJxz() < fullLevel) ableUpdateTypes.add(BuildingEnum.JXZ.getValue());
                if (userCity.getKc() < fullLevel) ableUpdateTypes.add(BuildingEnum.KC.getValue());
                else if (userCity.getLbl() < fullLevel) ableUpdateTypes.add(BuildingEnum.LBL.getValue());
                // 随意一建筑
                int type = PowerRandom.getRandomFromList(ableUpdateTypes);
                levelUpBuildings.add(type);
                switch (type) {
                    case 20:
                        userCity.setKc(fullLevel);
                        break;
                    case 30:
                        userCity.setQz(fullLevel);
                        break;
                    case 40:
                        userCity.setTcp(fullLevel);
                        break;
                    case 50:
                        userCity.setJxz(fullLevel);
                        break;
                    case 60:
                        userCity.setLbl(fullLevel);
                        break;
                    case 70:
                        userCity.setDc(fullLevel);
                        break;
                    case 80:
                        userCity.setLdf(fullLevel);
                        break;
                }
            }
        }
        // 持久化到Redis
        gameUserService.updateItem(userCity);
        // 建筑升级事件
        ChengCInEventPublisher.pubBuildingLevelUpEvent(gu.getId(), userCity, levelUpBuildings, WayEnum.TREASURE_USE, rd);

        if (userCity.ifUpdateFull()) {
            rd.setUpdateFull(1);
        }
    }

}
