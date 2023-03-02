package com.bbw.god.city.chengc.in.building;

import com.bbw.common.LM;
import com.bbw.common.SpringContextUtil;
import com.bbw.god.activity.monthlogin.MonthLoginEnum;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.chengc.ChengChiInfoCache;
import com.bbw.god.city.chengc.UserCity;
import com.bbw.god.city.chengc.UserCitySetting;
import com.bbw.god.city.chengc.in.RDBuildingInfo;
import com.bbw.god.city.chengc.in.RDBuildingOutputs.RDBuildingOutput;
import com.bbw.god.city.chengc.in.RDBuildingUpdateInfo;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.game.config.TypeEnum;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.BuildingEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.card.CardChecker;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.card.event.CardEventPublisher;

/**
 * 炼丹房
 *
 * @author suhq
 * @date 2018年11月29日 下午3:24:44
 */
public class LianDF extends Building {
    private static UserCardService userCardService = SpringContextUtil.getBean(UserCardService.class);

    public LianDF(UserCity userCity) {
        this.userCity = userCity;
        this.bType = BuildingEnum.LDF;
    }

    @Override
    public RDBuildingInfo getBuildingInfo() {
        RDBuildingInfo buildingInfo = new RDBuildingInfo(bType.getValue(), userCity.getLdf(), getNextLevelInfo());
        buildingInfo.setRemainTimes(1);
        return buildingInfo;
    }

    @Override
    protected String getNextLevelInfo() {
        int nextBuildingLevel = userCity.getLdf() + 1;
        if (nextBuildingLevel > getBuildingMaxLevel()) {
            return "";
        }
        int cityLevel = userCity.gainCity().getLevel();
        int curGetExp = getLdfAddedExp(cityLevel, userCity.getLdf());
        int nextGetExp = getLdfAddedExp(cityLevel, nextBuildingLevel);
        return curGetExp + "(+" + (nextGetExp - curGetExp) + ")经验";
    }

    @Override
    protected void handleBuildingUpdate(GameUser gu, ChengChiInfoCache rdArriveChengC, RDBuildingUpdateInfo rd) {
        userCity.setLdf(userCity.getLdf() + 1);
    }

    @Override
    protected int getNeedCopperForUpdate() {
        return 2000 * (int) Math.pow(2, userCity.getLdf());
    }

    @Override
    public void doBuildingAward(GameUser gu, String param, RDBuildingOutput rd) {
        UserCitySetting ucSetting = gameUserService.getSingleItem(gu.getId(), UserCitySetting.class);
        if (null == ucSetting) {
            ucSetting = UserCitySetting.instance(gu.getId());
            gameUserService.addItem(gu.getId(), ucSetting);
        }
        if (param == null) {// 自动领取
            param = ucSetting.getLdfCard().toString();
        }
        Integer cardId = Integer.valueOf(param);
        UserCard userCard = userCardService.getUserCard(gu.getId(), cardId);
        CfgCityEntity city = gu.gainCurCity();
        CardChecker.checkIsOwn(userCard);
        CardChecker.checkIsFullUpdate(userCard);
        int addedExp = getLdfAddedExp(city.getLevel(), userCity.getLdf());

        // 火属性城：炼丹房所获经验+30%
        if (city.getProperty() == TypeEnum.Fire.getValue()) {
            addedExp *= 1.3;
        }

        int rate = getRate();
        if (hasDoubledAward(gu.getId())){
            rate*=2;
        }else if (isBanAward(gu.getId())){
            rate=0;
        }
        rd.setRate(rate);
        addedExp *= rate;
        if (monthLoginLogic.isExistEvent(gu.getId(),MonthLoginEnum.GOOD_LDF)){
            addedExp *= 1.3;
        }
        if (addedExp>0){
            BaseEventParam bp = new BaseEventParam(gu.getId(), WayEnum.LDF_AWARD, rd);
            CardEventPublisher.pubCardExpAddEvent(bp, cardId, addedExp);
        }
        // 设置默认卡牌
        ucSetting.setLdfCard(cardId);
        gameUserService.updateItem(ucSetting);
        ChengChiInfoCache cache= TimeLimitCacheUtil.getChengChiInfoCache(gu.getId());
        cache.getCityInInfo().setLdfCard(cardId);
        TimeLimitCacheUtil.setChengChiInfoCache(gu.getId(),cache);
    }

    @Override
    protected String getAlreadyHandleTipCode() {
        return "city.cc.in.ldf.already.get";
    }

    /**
     * 领取建筑物奖励前的检查
     */
    @Override
    protected void checkBeforeDoBuildingAward(RDBuildingOutput rd) {
        if (0 == userCity.getLdf()) {
            rd.setMessage(LM.I.getMsg("city.cc.in.not.update"));
        }
    }

    /**
     * 获得炼丹房可升级的经验数
     *
     * @param cityLevel
     * @param ldfLevel
     * @return
     */
    private int getLdfAddedExp(int cityLevel, int ldfLevel) {
        int expTmp = 0;
        switch (ldfLevel) {
            case 1:
                expTmp = 200;
                break;
            case 2:
                expTmp = 300;
                break;
            case 3:
                expTmp = 400;
                break;
            case 4:
                expTmp = 600;
                break;
            case 5:
                expTmp = 800;
                break;
            case 6:
                expTmp = 1200;
                break;
            case 7:
                expTmp = 1800;
                break;
            case 8:
                expTmp = 2400;
                break;
            case 9:
                expTmp = 3200;
                break;
            case 10:
                expTmp = 4000;
                break;
        }
        return (int) (expTmp * (0.5 + 0.5 * cityLevel));
    }

}
