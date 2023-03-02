package com.bbw.god.city.chengc.in.building;

import com.bbw.common.LM;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.in.RDBuildingInfo;
import com.bbw.god.city.chengc.in.RDBuildingOutputs.RDBuildingOutput;
import com.bbw.god.city.chengc.in.RDBuildingUpdateInfo;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.BuildingEnum;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;

/**
 * 聚贤庄
 *
 * @author suhq
 * @date 2018年11月29日 下午3:20:13
 */
public class JuXZ extends Building {

    /**
     * 领取建筑物奖励前的检查
     */
    @Override
    protected void checkBeforeDoBuildingAward(RDBuildingOutput rd) {
        if (0 == userCity.getJxz()) {
            rd.setMessage(LM.I.getMsg("city.cc.in.not.update"));
        }
    }

    public JuXZ(UserCity userCity) {
        this.userCity = userCity;
        this.bType = BuildingEnum.JXZ;
    }

    @Override
    public RDBuildingInfo getBuildingInfo() {
        RDBuildingInfo buildingInfo = new RDBuildingInfo(bType.getValue(), userCity.getJxz(), getNextLevelInfo());
        buildingInfo.setJxNum(getJxNum(this.userCity.getJxz()));
        buildingInfo.setRemainTimes(1);
        return buildingInfo;
    }

    private int getJxNum(Integer jxLevel) {
        if (jxLevel <= 0) return 0;
        else if (jxLevel <= 2) return 5;
        else if (jxLevel <= 4) return 6;
        else if (jxLevel <= 6) return 7;
        else if (jxLevel <= 8) return 8;
        else if (jxLevel <= 9) return 9;
        else if (jxLevel <= 10) return 10;
        else return 0;
    }

    @Override
    protected String getNextLevelInfo() {
        int nextBuildingLevel = userCity.getJxz() + 1;
        if (nextBuildingLevel > getBuildingMaxLevel()) {
            return "";
        }
        int curGetJxNum = getJxNum(this.userCity.getJxz());
        int nextGetJxNum = getJxNum(nextBuildingLevel);
        int valueChange = nextGetJxNum - curGetJxNum;
        if (0 == valueChange) {
            return "高星卡牌概率提高";
        }
        return "产出纳贤令*" + nextGetJxNum;
    }

    @Override
    protected void handleBuildingUpdate(GameUser gu, ChengChiInfoCache rdArriveChengC, RDBuildingUpdateInfo rd) {
        userCity.setJxz(userCity.getJxz() + 1);
    }

    @Override
    protected int getNeedCopperForUpdate() {
        return 6000 * (int) Math.pow(2, userCity.getJxz());
    }

    /**
     * 购买卡牌
     */
    @Override
    public void doBuildingAward(GameUser gu, String param, RDBuildingOutput rd) {
        int rate=getRate();
        if (hasDoubledAward(gu.getId())){
            rate+=2;
        }else if (isBanAward(gu.getId())){
            rate=0;
        }
        int num = getJxNum(this.userCity.getJxz())*rate;
        rd.setRate(rate);
        if (num==0){
            return;
        }
        TreasureEventPublisher.pubTAddEvent(gu.getId(), TreasureEnum.JU_XIAN_LING.getValue(), num, WayEnum.JXZ_AWARD, rd);
    }

    @Override
    protected String getAlreadyHandleTipCode() {
        return "city.cc.in.jxz.already.get";
    }
}
