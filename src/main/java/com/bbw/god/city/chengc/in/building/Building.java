package com.bbw.god.city.chengc.in.building;

import com.bbw.common.LM;
import com.bbw.common.PowerRandom;
import com.bbw.common.SpringContextUtil;
import com.bbw.common.StrUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.activity.monthlogin.MonthLoginLogic;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.CityChecker;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.in.RDBuildingInfo;
import com.bbw.god.city.chengc.in.RDBuildingOutputs.RDBuildingOutput;
import com.bbw.god.city.chengc.in.RDBuildingUpdateInfo;
import com.bbw.god.city.chengc.in.RDCityInInfo;
import com.bbw.god.city.chengc.in.event.ChengCInEventPublisher;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.BuildingEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;

import java.util.Arrays;
import java.util.List;

public abstract class Building {
    protected BuildingEnum bType;
    protected UserCity userCity;
    protected GameUserService gameUserService = SpringContextUtil.getBean(GameUserService.class);
    protected MonthLoginLogic monthLoginLogic = SpringContextUtil.getBean(MonthLoginLogic.class);
    /**
     * 获取建筑等级信息
     *
     * @return
     */
    public abstract RDBuildingInfo getBuildingInfo();
    /**
     * 检查当前是否可以升级
     */
    public void checkIsAbleUpdate(){
        switch (bType){
            case DC:
                checkIsAbleUpdate(userCity.getDc());break;
            case FY:
                checkIsAbleUpdate(userCity.getFy());break;
            case KC:
                checkIsAbleUpdate(userCity.getKc());break;
            case QZ:
                checkIsAbleUpdate(userCity.getQz());break;
            case TCP:
                checkIsAbleUpdate(userCity.getTcp());break;
            case LDF:
                checkIsAbleUpdate(userCity.getLdf());break;
            case JXZ:
                checkIsAbleUpdate(userCity.getJxz());break;
            case FT:
                checkIsAbleUpdate(userCity.getFt());break;
            default:
        }
    }

    /**
     * 升级建筑 子类不可覆盖和改写该方法
     *
     * @param guId
     * @return
     */
    public final void update(long guId, RDBuildingUpdateInfo rd,boolean useTianGongTu, boolean isUseQianKunTu) {
        GameUser gu = gameUserService.getGameUser(guId);
        CfgCityEntity city = gu.gainCurCity();
        // 更新检查
        CityChecker.checkIsCC(city);
        CityChecker.checkIsOwnCC(userCity);
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(guId);
        RDCityInInfo manorInfo = cache.getCityInInfo();
        checkIsAbleUpdate();
        if (manorInfo==null){
            throw new ExceptionForClientTip("cache.ui.timeout");
        }
        if (useTianGongTu){
            TreasureChecker.checkIsExist(TreasureEnum.TIAN_GONG_TU.getValue());
            TreasureEventPublisher.pubTDeductEvent(guId,TreasureEnum.TIAN_GONG_TU.getValue(),1,WayEnum.BUILDING_UPDATE,rd);
        }else{
            if (manorInfo.getRemainUpdateTimes() <= 0) {
                throw new ExceptionForClientTip("city.cc.in.already.update");
            }
            // 更新缓存
            manorInfo.setRemainUpdateTimes(0);
        }
        // 建筑所需铜钱及铜钱检验
        long needCopper = getNeedCopperForUpdate();
        // 水属性城：城市建设费用少20%
        if (city.getProperty() == TypeEnum.Water.getValue()) {
            needCopper *= 0.8;
        }
        ResChecker.checkCopper(gu, needCopper);

        WayEnum way = WayEnum.fromValue(bType.getValue() + 3300);
        // 扣除铜钱
        ResEventPublisher.pubCopperDeductEvent(guId, needCopper, way, rd);
        // 建筑升级处理
        if (BuildingEnum.FT == bType){
            FaTan faTan = new FaTan(userCity);
            //法坛升级处理
            faTan.faTanUpdate(guId, isUseQianKunTu ,rd);
        }
        handleBuildingUpdate(gu, cache, rd);
        // Redis持久化
        gameUserService.updateItem(userCity);

        // 玩家升级
        int addedExp = 300 + (city.getType() - 210) * 10;
        ResEventPublisher.pubExpAddEvent(guId, addedExp, way, rd);
        // 建筑升级事件
        if (bType != BuildingEnum.FT){
            ChengCInEventPublisher.pubBuildingLevelUpEvent(guId, userCity, Arrays.asList(bType.getValue()), way, rd);
        }
        if (userCity.ifUpdate5()) {
            rd.setUpdateFull(1);
        }

        rd.setRemainTimes(0);
        rd.setNextLevelInfo(getNextLevelInfo());

        // 持久化缓存的redis
        TimeLimitCacheUtil.setChengChiInfoCache(guId, cache);
    }

    /**
     * 各个建筑实现自己的具体奖励领取
     *
     * @param gu
     * @param param
     * @return
     */
    public RDBuildingOutput handleBuildingAward(GameUser gu, String param) {
        RDBuildingOutput rd = new RDBuildingOutput();
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(gu.getId());
        if (cache.getCityInInfo()==null){
            throw new ExceptionForClientTip("city.not.in.chengchi");
        }
        RDBuildingInfo rdBuildingInfo = getBuildingInfoInCache(gu, cache);
        int remainTimes = rdBuildingInfo.getRemainTimes();
        if (remainTimes <= 0) {
            rd.setMessage(LM.I.getMsg(getAlreadyHandleTipCode()));
            return rd;
        }

        // 领取奖励前的检查
        checkBeforeDoBuildingAward(rd);
        if (StrUtil.isNotBlank(rd.getMessage())) {
            return rd;
        }

        // 处理具体建筑
        doBuildingAward(gu, param, rd);

        // 更新缓存，标记已领取
        rdBuildingInfo.setRemainTimes(0);
        // 持久化缓存的redis
        TimeLimitCacheUtil.setChengChiInfoCache(gu.getId(),cache);

        return rd;
    }

    /**
     * 已操作的提示信息
     *
     * @return
     */
    protected String getAlreadyHandleTipCode() {
        return "";
    }

    /**
     * 领取建筑物奖励前的检查
     */
    protected void checkBeforeDoBuildingAward(RDBuildingOutput rd) {

    }

    /**
     * 具体建筑操作
     *
     * @param gu
     * @param param
     * @param rd
     */
    protected void doBuildingAward(GameUser gu, String param, RDBuildingOutput rd) {

    }

    /**
     * 下一级别的信息
     *
     * @return
     */
    protected abstract String getNextLevelInfo();

    /**
     * 有具体建筑类去实现自己的升级
     *
     * @param gu
     * @param rd
     */
    protected abstract void handleBuildingUpdate(GameUser gu, ChengChiInfoCache cache, RDBuildingUpdateInfo rd);

    /**
     * 需要消耗的铜钱
     */
    protected abstract int getNeedCopperForUpdate();

    /**
     * 是否可升级，府衙、聚贤庄、炼宝炉需重写该方法
     *
     * @param buildingLevel
     * @return
     */
    protected void checkIsAbleUpdate(int buildingLevel) {
        if (isTopLevel(buildingLevel)) {
            throw new ExceptionForClientTip("city.cc.in.not.update.alreadyTop");
        }
        if (bType != BuildingEnum.FY && buildingLevel == userCity.getFy()) {
            throw new ExceptionForClientTip("city.cc.in.not.update.fyFirst");
        }
       /* // 聚贤庄须建同级特产铺才能建
        if (bType == BuildingEnum.JXZ && buildingLevel >= userCity.getTcp()) {
            throw new ExceptionForClientTip("city.cc.in.not.update.tcpFirst");
        }
        // 炼宝炉须建同级矿场才能兴建
        if (bType == BuildingEnum.LBL && buildingLevel >= userCity.getKc()) {
            throw new ExceptionForClientTip("city.cc.in.not.update.kcFirst");
        }*/
    }

    /**
     * 建筑是否已顶级
     *
     * @param buildingLevel
     * @return
     */
    protected boolean isTopLevel(int buildingLevel) {
        return buildingLevel >= 5 + userCity.getHierarchy();
    }

    /**
     * 建筑是否已满级
     *
     * @param buildingLevel
     * @return
     */
    protected boolean isFullLevel(int buildingLevel) {
        return buildingLevel >= 10;
    }

    /**
     * 获取建筑物最大等级
     *
     * @return
     */
    protected int getBuildingMaxLevel() {
        return 10;
    }

    /**
     * 获取建筑信息（缓存）
     *
     * @param gu
     * @return
     */
    public RDBuildingInfo getBuildingInfoInCache(GameUser gu, ChengChiInfoCache cache) {

        RDCityInInfo rdCityInInfo = cache.getCityInInfo();
        List<RDBuildingInfo> buildingInfos = rdCityInInfo.getInfo();
        RDBuildingInfo buildingInfo = buildingInfos.stream().filter(bi -> bi.getType() == bType.getValue()).findFirst()
                .orElse(null);
        return buildingInfo;
    }

    /**
     * 获得暴击率 94:5.8:0.2 9400:580:20
     *
     * @return
     */
    protected int getRate() {
        int random = PowerRandom.getRandomBySeed(10000);
        if (random <= 9400){
            return 1;
        } else if (random <= 9980){
            return 2;
        } else {
            return 3;
        }
    }

    /***
     * 是否有双倍奖励
     * @param uid
     * @return
     */
    protected boolean hasDoubledAward(long uid){
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        if (cache==null){
            return false;
        }
        if (cache.getChengChiGain()==2){
            return true;
        }
        return false;
    }

    /**
     * 是否禁止领取奖励
     * @param uid
     * @return
     */
    protected boolean isBanAward(long uid){
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        if (cache==null){
            return false;
        }
        if (cache.getChengChiGain()==0){
            return true;
        }
        return false;
    }

}
