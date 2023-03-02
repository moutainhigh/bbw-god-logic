package com.bbw.god.city.chengc.in.building;

import com.bbw.common.LM;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.activity.monthlogin.MonthLoginEnum;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.in.RDBuildingInfo;
import com.bbw.god.city.chengc.in.RDBuildingOutputs.RDBuildingOutput;
import com.bbw.god.city.chengc.in.RDBuildingUpdateInfo;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.BuildingEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityConfig;
import com.bbw.god.game.config.god.GodEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.chamberofcommerce.server.UserCocInfoService;
import com.bbw.god.gameuser.god.UserGod;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.server.god.GodService;

import java.util.Optional;

/**
 * 钱庄
 *
 * @author suhq
 * @date 2018年11月29日 下午3:26:46
 */
public class QianZ extends Building {
    private GodService userGodService = SpringContextUtil.getBean(GodService.class);
    private UserCocInfoService userCocInfoService = SpringContextUtil.getBean(UserCocInfoService.class);

    public QianZ(UserCity userCity) {
        this.userCity = userCity;
        this.bType = BuildingEnum.QZ;
    }

    @Override
    public RDBuildingInfo getBuildingInfo() {
        RDBuildingInfo buildingInfo = new RDBuildingInfo(this.bType.getValue(), this.userCity.getQz(), getNextLevelInfo());
        buildingInfo.setRemainTimes(1);
        return buildingInfo;
    }

    @Override
    public String getNextLevelInfo() {
        int nextBuildingLevel = this.userCity.getQz() + 1;
        if (nextBuildingLevel > getBuildingMaxLevel()) {
            return "";
        }
        CfgCityEntity city = this.userCity.gainCity();
        int nextGetCopper = getQZBaseLevelCopper(city, nextBuildingLevel);
        int curGetCopper = getQZBaseLevelCopper(city, this.userCity.getQz());
        if (this.userCity.getQz() == 0) {
            curGetCopper = 0;
        }
        return curGetCopper + "(+" + (nextGetCopper - curGetCopper) + ")铜钱";
    }

    @Override
    protected void handleBuildingUpdate(GameUser gu, ChengChiInfoCache rdArriveChengC, RDBuildingUpdateInfo rd) {
		this.userCity.setQz(this.userCity.getQz() + 1);
    }

    @Override
    protected int getNeedCopperForUpdate() {
        return 500 * (int) Math.pow(2, this.userCity.getQz());
    }

    @Override
    public void doBuildingAward(GameUser gu, String param, RDBuildingOutput rd) {
        CfgCityEntity city = gu.gainCurCity();
        int addedCopper = getQZBaseLevelCopper(city, this.userCity.getQz());
        int copperRate = 1;
        /** 穷神减半 **/
        Optional<UserGod> userGod = this.userGodService.getAttachGod(gu);
        if (userGod.isPresent() && (userGod.get().getBaseId() == GodEnum.QS.getValue())) {
            addedCopper /= 2;
        } else {
            copperRate = getRate();
            addedCopper *= copperRate;
        }
        // TODO 待优化
        // 商会钱庄加成
        int cocQZRate = this.userCocInfoService.getPrivilegeBankGain(gu.getId());
        if (cocQZRate > 0) {
            addedCopper = addedCopper * (100 + cocQZRate) / 100;
        }
        // 金属性城：钱庄出产+30%
        if (city.getProperty() == 10) {
            addedCopper *= 1.3;
        }
        if (monthLoginLogic.isExistEvent(gu.getId(),MonthLoginEnum.GOOD_QZ)){
            addedCopper *= 1.3;
        }
        if (hasDoubledAward(gu.getId())){
            copperRate*=2;
            addedCopper*=2;
        }else if (isBanAward(gu.getId())){
            copperRate=0;
            addedCopper=0;
        }
        if (addedCopper>0){
            ResEventPublisher.pubCopperAddEvent(gu.getId(), addedCopper, WayEnum.QZ_AWARD, rd);
        }
        rd.setRate(copperRate);
    }

    @Override
    protected String getAlreadyHandleTipCode() {
        return "city.cc.in.qz.already.get";
    }

    /**
     * 领取建筑物奖励前的检查
     */
    @Override
    protected void checkBeforeDoBuildingAward(RDBuildingOutput rd) {
        if (0 == userCity.getQz()) {
            rd.setMessage(LM.I.getMsg("city.cc.in.not.update"));
        }
    }

    /**
     * 获得钱庄基本收成（不包括暴击）
     *
     * @param city
     * @param buildingLevel
     * @return
     */
    private int getQZBaseLevelCopper(CfgCityEntity city, int buildingLevel) {
        return (int) (city.getBaseTax() * Math.pow(CityConfig.bean().getCcData().getQzCopperRate(), buildingLevel - 1));
    }

}
