package com.bbw.god.city.chengc.in.building;

import com.bbw.common.PowerRandom;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.in.RDBuildingInfo;
import com.bbw.god.city.chengc.in.RDBuildingUpdateInfo;
import com.bbw.god.city.chengc.in.event.ChengCInEventPublisher;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.BuildingEnum;
import com.bbw.god.game.config.city.CfgNeedTreasure;
import com.bbw.god.game.config.city.CfgUpgradeFaTan;
import com.bbw.god.game.config.city.FaTanTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;

import java.util.Arrays;
import java.util.List;

/**
 * 法坛
 *
 * @author fzj
 * @date 2021/11/5 11:06
 */
public class FaTan extends Building {

    public FaTan(UserCity userCity) {
        this.userCity = userCity;
        this.bType = BuildingEnum.FT;
    }

    @Override
    public RDBuildingInfo getBuildingInfo() {
        if (userCity.getFt() == null) {
            return new RDBuildingInfo();
        }
        RDBuildingInfo buildingInfo = new RDBuildingInfo(bType.getValue(), userCity.getFt(), getNextLevelInfo());
        buildingInfo.setRemainTimes(1);
        buildingInfo.setFtRepairValue(userCity.getFtRepairValue());
        return buildingInfo;
    }

    @Override
    protected String getNextLevelInfo() {
        return null;
    }

    @Override
    protected void handleBuildingUpdate(GameUser gu, ChengChiInfoCache cache, RDBuildingUpdateInfo rd) {
    }

    @Override
    protected int getNeedCopperForUpdate() {
        return FaTanTool.getFaTanInFo().getUpgradeFaTan().stream()
                .filter(f -> f.getFaTanLv() == userCity.getFt() + 1).map(CfgUpgradeFaTan::getNeedCopper).findFirst().orElse(0);
    }

    /**
     * 法坛升级处理
     *
     * @param uid
     * @param isUseQianKunTu
     * @param rd
     */
    public void faTanUpdate(long uid, boolean isUseQianKunTu, RDBuildingUpdateInfo rd) {
        List<CfgUpgradeFaTan> upgradeFaTan = FaTanTool.getFaTanInFo().getUpgradeFaTan();
        CfgUpgradeFaTan cfgUpgradeFaTan = upgradeFaTan.stream()
                .filter(f -> f.getFaTanLv() == userCity.getFt() + 1).findFirst().orElse(null);
        //检查扣除道具数量
        checkAndDeductTreasure(uid, rd, cfgUpgradeFaTan.getNeedTreasure(), isUseQianKunTu);
        //根据概率获取升级结果
        boolean isUpdate = PowerRandom.hitProbability(cfgUpgradeFaTan.getProbability(), 1000);
        //当前修缮值
        Integer ftRepairValue = userCity.getFtRepairValue();
        boolean isEnoughRepairValue = ftRepairValue == cfgUpgradeFaTan.getNeedRepairValue() - 1;
        //使用乾坤图或升级成功或者有足够的修缮值
        if (isUseQianKunTu || isUpdate || isEnoughRepairValue) {
            userCity.setFt(userCity.getFt() + 1);
            //修缮值清零
            userCity.setFtRepairValue(0);
            rd.setNextLevelInfo(getNextLevelInfo());
            rd.setUpdate(true);
            sendSpar(uid, rd);
            ChengCInEventPublisher.pubBuildingLevelUpEvent(uid, userCity, Arrays.asList(BuildingEnum.FT.getValue()), WayEnum.FAT_UPDATE, rd);
            return;
        }
        //如果升级失败，且修缮值不足，则修缮值加1
        userCity.setFtRepairValue(ftRepairValue + 1);
        rd.setNextLevelInfo(getNextLevelInfo());
        rd.setUpdate(false);
    }

    /**
     * 赠送晶石
     * @param uid
     * @param rd
     */
    private void sendSpar(long uid, RDBuildingUpdateInfo rd) {
        if (userCity.getFt() == 10) {
            //增送残晶60 天晶10 血晶1
            TreasureEventPublisher.pubTAddEvent(uid, 50127, 60, WayEnum.FAT_UPDATE, rd);
            TreasureEventPublisher.pubTAddEvent(uid, 50128, 10, WayEnum.FAT_UPDATE, rd);
            TreasureEventPublisher.pubTAddEvent(uid, 50129, 1, WayEnum.FAT_UPDATE, rd);
        }
    }

    /**
     * 检查扣除道具
     *
     * @param uid
     * @param rd
     * @param needTreasure
     */
    private void checkAndDeductTreasure(long uid, RDBuildingUpdateInfo rd, List<CfgNeedTreasure> needTreasure, boolean isUseQianKunTu) {
        if (isUseQianKunTu) {
            TreasureChecker.checkIsEnough(TreasureEnum.QKT.getValue(), 1, uid);
            TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.QKT.getValue(), 1, WayEnum.FAT_UPDATE, rd);
        }
        //检查
        needTreasure.forEach(t -> {
            TreasureChecker.checkIsEnough(t.getTreasureId(), t.getNum(), uid);
        });
        //扣除
        for (CfgNeedTreasure cfgNeedTreasureAndNum : needTreasure) {
            Integer treasureId = cfgNeedTreasureAndNum.getTreasureId();
            Integer num = cfgNeedTreasureAndNum.getNum();
            TreasureEventPublisher.pubTDeductEvent(uid, treasureId, num, WayEnum.FAT_UPDATE, rd);
        }
    }
}
