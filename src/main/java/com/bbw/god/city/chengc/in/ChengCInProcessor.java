package com.bbw.god.city.chengc.in;

import com.bbw.common.ListUtil;
import com.bbw.common.SpringContextUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.cache.TimeLimitCacheUtil;
import com.bbw.god.city.CityChecker;
import com.bbw.god.city.UserCityService;
import com.bbw.god.city.chengc.*;
import com.bbw.god.city.chengc.in.RDBuildingOutputs.RDBuildingOutput;
import com.bbw.god.city.chengc.in.building.Building;
import com.bbw.god.city.chengc.in.building.BuildingFactory;
import com.bbw.god.city.chengc.in.event.ChengCInEventPublisher;
import com.bbw.god.city.chengc.in.event.UnlockFaTanEvent;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffEnum;
import com.bbw.god.city.miaoy.hexagram.HexagramBuffService;
import com.bbw.god.city.miaoy.hexagram.UserHexagramBuff;
import com.bbw.god.city.miaoy.hexagram.event.HexagramEventPublisher;
import com.bbw.god.event.BaseEventParam;
import com.bbw.god.fight.RDFightsInfo;
import com.bbw.god.fight.processor.PromoteFightProcessor;
import com.bbw.god.fight.processor.TrainingFightProcessor;
import com.bbw.god.game.combat.data.param.CCardParam;
import com.bbw.god.game.combat.data.param.CPlayerInitParam;
import com.bbw.god.game.combat.data.param.CombatPVEParam;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.*;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.card.CardChecker;
import com.bbw.god.gameuser.card.UserCard;
import com.bbw.god.gameuser.card.UserCardService;
import com.bbw.god.gameuser.res.ResChecker;
import com.bbw.god.gameuser.res.ResEventPublisher;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 城池
 *
 * @author suhq
 * @date 2018年10月24日 下午5:49:46
 */
@Component
public class ChengCInProcessor {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserCardService userCardService;
    @Autowired
    private ChengChiLogic chengChiLogic;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private TrainingFightProcessor trainingFightProcessor;
    @Autowired
    private PromoteFightProcessor promoteFightProcessor;
    @Autowired
    private HexagramBuffService hexagramBuffService;
    @Autowired
    private UserTreasureService userTreasureService;

    /**
     * 进入到城内
     *
     * @param guId
     * @return
     */
    public RDCityInInfo intoChengC(long guId) {
        GameUser gu = gameUserService.getGameUser(guId);
        CfgCityEntity city = gu.gainCurCity();
        CityChecker.checkIsCC(city);
        UserCity userCity = userCityService.getUserCity(gu.getId(), city.getId());
        CityChecker.checkIsOwnCC(userCity);
        RDCityInInfo citiInfo = chengChiLogic.getCityInInfo(guId, city.getId());
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(guId);
        Optional<UserHexagramBuff> hexagramBuffOp = hexagramBuffService.getHexagramBuff(guId);
        if (hexagramBuffOp.isPresent()){
            int hexagramId = hexagramBuffOp.get().getHexagramId();
            if (hexagramId== HexagramBuffEnum.HEXAGRAM_25.getId()){
                cache.setChengChiGain(2);
                HexagramEventPublisher.pubHexagramBuffDeductEvent(new BaseEventParam(gu.getId()), HexagramBuffEnum.HEXAGRAM_25.getId(), 1);
            }else if (hexagramId==HexagramBuffEnum.HEXAGRAM_61.getId()){
                cache.setChengChiGain(0);
                HexagramEventPublisher.pubHexagramBuffDeductEvent(new BaseEventParam(gu.getId()), HexagramBuffEnum.HEXAGRAM_61.getId(), 1);
            }
        }
        //获取本城出售的特产
        citiInfo.setCitySpecials(cache.getCitySpecials());
        cache.setCityInInfo(citiInfo);
        TimeLimitCacheUtil.setChengChiInfoCache(guId, cache);
        return citiInfo;
    }

    /**
     * 获取练兵信息
     *
     * @param uid
     * @return
     */
    public RDFightsInfo getTrainingInfo(long uid) {
        GameUser gu = gameUserService.getGameUser(uid);
        CfgCityEntity city = gu.gainCurCity();
        CityChecker.checkIsCC(city);
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        if (cache.isTraining() || cache.isAttack()) {
            throw new ExceptionForClientTip("city.cc.can.not.training.after.attack");
        }
        if (cache.getFightParam()==null || cache.getFightParam().getAiPlayer() ==null ||cache.getFightParam().getAiPlayer().getCards()==null){
            CombatPVEParam pveParam=new CombatPVEParam();
            pveParam.setCityLevel(city.getLevel());
            pveParam.setCityBaseId(city.getId());
            pveParam.setCityHierarchy(cache.getHv());
            trainingFightProcessor.buildFightInfo(gu, cache, pveParam);
            cache.setFightParam(pveParam);
            TimeLimitCacheUtil.setChengChiInfoCache(uid,cache);
        }
        CPlayerInitParam param = cache.getFightParam().getAiPlayer();
        RDFightsInfo info=new RDFightsInfo();
        List<RDFightsInfo.RDFightCard> cards=new ArrayList<>();
        for (CCardParam cardParam : param.getCards()) {
            cards.add(RDFightsInfo.RDFightCard.instance(cardParam));
        }
        info.setCards(cards);
        return info;
    }

    public RDFightsInfo getPromoteFight(long uid) {
        GameUser gu = gameUserService.getGameUser(uid);
        CfgCityEntity city = gu.gainCurCity();
        CityChecker.checkIsCC(city);
        ChengChiInfoCache cache = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        if (cache.getPromoteFightParam()==null || cache.getPromoteFightParam().getAiPlayer() ==null ||cache.getPromoteFightParam().getAiPlayer().getCards()==null){
            CombatPVEParam pveParam=new CombatPVEParam();
            pveParam.setCityBaseId(cache.getCityId());
            pveParam.setCityLevel(cache.getCityLv());
            pveParam.setCityHierarchy(cache.getHv());
            promoteFightProcessor.getNormalOpponentParam(gu,cache,pveParam);
            cache.setPromoteFightParam(pveParam);
            TimeLimitCacheUtil.setChengChiInfoCache(uid,cache);
        }
        CPlayerInitParam param = cache.getPromoteFightParam().getAiPlayer();
        RDFightsInfo info=new RDFightsInfo();
        List<RDFightsInfo.RDFightCard> cards=new ArrayList<>();
        for (CCardParam cardParam : param.getCards()) {
            cards.add(RDFightsInfo.RDFightCard.instance(cardParam));
        }
        info.setCards(cards);
        return info;
    }

    /**
     * 城内建筑升级
     *
     * @param guId
     * @param bType
     * @return
     */
    public RDBuildingUpdateInfo updatBuilding(long guId, int bType,boolean useTianGongTu, boolean useQianKunTu) {
        RDBuildingUpdateInfo rd = new RDBuildingUpdateInfo();
        GameUser gu = gameUserService.getGameUser(guId);
        UserCity userCity = userCityService.getUserCity(gu.getId(), gu.gainCurCity().getId());
        BuildingFactory.I.create(userCity, BuildingEnum.fromValue(bType)).update(guId, rd,useTianGongTu,useQianKunTu);
        return rd;
    }

    /**
     * 领取城内建筑奖励
     *
     * @param guId
     * @param bType
     * @param param
     * @return
     */
    public RDBuildingOutput gainBuildingAward(long guId, BuildingEnum bType, String param) {
        GameUser gu = gameUserService.getGameUser(guId);
        CfgCityEntity city = gu.gainCurCity();
        CityChecker.checkIsCC(city);
        UserCity userCity = userCityService.getUserCity(gu.getId(), city.getId());
        CityChecker.checkIsOwnCC(userCity);
        RDBuildingOutput rdOutput = BuildingFactory.I.create(userCity, bType).handleBuildingAward(gu, param);
        return rdOutput;

    }

    /**
     * 一键领取
     *
     * @param guId
     * @return
     */
    public RDBuildingOutputs getAllOutput(long guId) {
        RDBuildingOutputs rd = new RDBuildingOutputs();
        GameUser gu = gameUserService.getGameUser(guId);
        CfgCityEntity city = gu.gainCurCity();
        UserCity userCity = userCityService.getUserCity(guId, city.getId());
        CityChecker.checkIsCC(city);
        CityChecker.checkIsOwnCC(userCity);

        if (!userCity.ableGainAllByOneClick()) {
            throw new ExceptionForClientTip("city.cc.in.can.not.one.click.gain.all");
        }

        // 炼丹房检测
        UserCitySetting ucSetting = gameUserService.getSingleItem(gu.getId(), UserCitySetting.class);
        if (null == ucSetting) {
            ucSetting = UserCitySetting.instance(gu.getId());
            gameUserService.addItem(gu.getId(), ucSetting);
        }
        int ldfCard = ucSetting.getLdfCard();
        if (ldfCard == 0) {
            throw new ExceptionForClientTip("city.cc.in.ldf.need.manual1");
        }
        UserCard userCard = userCardService.getUserCard(gu.getId(), ldfCard);
        CardChecker.checkIsOwn(userCard);
        CardChecker.checkIsFullUpdate(userCard);

        // 产出
        Building building = BuildingFactory.I.create(userCity, BuildingEnum.KC);
        RDBuildingOutput kcOutput = building.handleBuildingAward(gu, null);

        building = BuildingFactory.I.create(userCity, BuildingEnum.LDF);
        RDBuildingOutput ldfOutput = building.handleBuildingAward(gu, null);

        building = BuildingFactory.I.create(userCity, BuildingEnum.LBL);
        RDBuildingOutput lblOutput = building.handleBuildingAward(gu, null);

        building = BuildingFactory.I.create(userCity, BuildingEnum.QZ);
        RDBuildingOutput qzOutput = building.handleBuildingAward(gu, null);

        building = BuildingFactory.I.create(userCity, BuildingEnum.JXZ);
        RDBuildingOutput jxzOutput = building.handleBuildingAward(gu, null);

        // 处理返回的数据
        rd.copyNotice(kcOutput);
        rd.copyNotice(ldfOutput);
        rd.copyNotice(lblOutput);
        rd.copyNotice(qzOutput);
        rd.copyNotice(jxzOutput);

        rd.setKcOutput(kcOutput);
        rd.setLdfOutput(ldfOutput);
        rd.setLblOutput(lblOutput);
        rd.setQzOutput(qzOutput);
        rd.setJxzOutput(jxzOutput);
        return rd;
    }

    /**
     * 矿场设置
     *
     * @param guId
     * @param useDefaultKcEles
     * @param defaultKcEles
     * @return
     */
    public RDSuccess setKC(long guId, int useDefaultKcEles, String defaultKcEles) {
        List<Integer> defaultKcEleList = ListUtil.parseStrToInts(defaultKcEles);
        UserCitySetting ucSetting = gameUserService.getSingleItem(guId, UserCitySetting.class);
        if (null == ucSetting) {
            ucSetting = UserCitySetting.instance(guId);
            gameUserService.addItem(guId, ucSetting);
        }
        if (useDefaultKcEles == 1) {
            ucSetting.setDefaultKcEles(defaultKcEleList);
        }
        ucSetting.setUseDefaultKcEles(useDefaultKcEles);
        gameUserService.updateItem(ucSetting);
        return new RDSuccess();
    }

    /**
     * 获得城内建筑信息
     *
     * @param gameUser
     * @param userCity
     * @return
     */
    public RDCityInInfo getCityInInfo(GameUser gameUser, UserCity userCity) {
        RDCityInInfo rd = new RDCityInInfo();
        CfgCityEntity city = userCity.gainCity();
        // 城内建筑设置
        UserCitySetting citySetting = this.gameUserService.getSingleItem(gameUser.getId(), UserCitySetting.class);
        int useDefaultKcEles = citySetting == null ? 0 : citySetting.getUseDefaultKcEles();
        List<Integer> defaultKcEles = citySetting == null ? Arrays.asList(10, 20, 30, 40, 50) :
                citySetting.getDefaultKcEles();
        Integer ldfCard = citySetting == null ? 0 : citySetting.getLdfCard();
        rd.setUseDefaultKcEles(useDefaultKcEles);
        rd.setDefaultKcEles(defaultKcEles);
        rd.setLdfCard(ldfCard);
        // 建筑信息

        List<RDBuildingInfo> info = new ArrayList<>();
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.FY).getBuildingInfo());
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.KC).getBuildingInfo());
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.QZ).getBuildingInfo());
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.TCP).getBuildingInfo());
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.JXZ).getBuildingInfo());
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.LBL).getBuildingInfo());
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.DC).getBuildingInfo());
        info.add(BuildingFactory.I.create(userCity, BuildingEnum.LDF).getBuildingInfo());
        rd.setInfo(info);
        // 兼容旧版
        //cityInInfo.setCards(info.get(4).getCards());

        rd.setRemainUpdateTimes(1);
        rd.setCityId(city.getId());
        rd.setIsAblePromote(userCity.ifAblePromote() ? 1 : 0);
        return rd;
    }

    /**
     * 获取当前城池的进阶信息
     *
     * @param uid
     * @param pos
     * @return
     */
    public RDPromoteInfo getPromoteInfo(long uid, int pos) {
        CfgRoadEntity road = RoadTool.getRoadById(pos);
        CfgCityEntity city = road.getCity();
        // 是否是城池
        CityChecker.checkIsCC(city);
        UserCity userCity = userCityService.getUserCity(uid, city.getId());
        // 城池是否占用
        CityChecker.checkIsOwnCC(userCity);
        RDPromoteInfo rd = RDPromoteInfo.instance(userCity);
        return rd;
    }

    public RDSuccess setLdfCard(long uid, int cardId) {
        UserCitySetting ucSetting = gameUserService.getSingleItem(uid, UserCitySetting.class);
        if (null == ucSetting) {
            ucSetting = UserCitySetting.instance(uid);
            gameUserService.addItem(uid, ucSetting);
        }
        ucSetting.setLdfCard(cardId);
        gameUserService.updateItem(ucSetting);
        return new RDSuccess();
    }

    /**
     * 解锁法坛
     * @param uid
     * @return
     */
    public RDCommon unlockFaTan(long uid){
        GameUser gu = gameUserService.getGameUser(uid);
        if (CityTool.getCcCount() != userCityService.getUserOwnNightmareCities(uid).size()) {
            throw new ExceptionForClientTip("nightmare.world.not.unite");
        }
        CfgCityEntity city = gu.gainCurCity();
        // 是否是城池
        CityChecker.checkIsCC(city);
        UserCity userCity = userCityService.getUserCity(uid, city.getId());
        // 城池是否占用
        CityChecker.checkIsOwnCC(userCity);
        // 解锁法坛所需铜钱检验
        long needCopper = FaTanTool.getFaTanInFo().getUnlockFaTanNeedCopper();
        ResChecker.checkCopper(gu, needCopper);
        //解锁法坛所需道具扣除及检验
        int needTreasureId = FaTanTool.getFaTanInFo().getUnlockFaTanNeedTreasureId();
        int num = FaTanTool.getFaTanInFo().getUnlockFaTanNeedTreasureNum();
        TreasureChecker.checkIsEnough(needTreasureId, num, uid);
        //法坛图纸检验
        String faTanPic = city.getName() + "法坛图纸";
        int faTanPicId = userTreasureService.getAllUserTreasures(uid).stream()
                .filter(userTreasure -> userTreasure.getName().equals(faTanPic)).map(UserCfgObj::getBaseId).findFirst().orElse(0);
        TreasureChecker.checkHasTreasure(uid, faTanPicId);
        //扣除道具及金钱
        RDCommon rd = new RDCommon();
        ResEventPublisher.pubCopperDeductEvent(uid, needCopper, WayEnum.FAT_UNLOCK, rd);
        TreasureEventPublisher.pubTDeductEvent(uid, needTreasureId, num, WayEnum.FAT_UNLOCK, rd);
        TreasureEventPublisher.pubTDeductEvent(uid, faTanPicId, 1, WayEnum.FAT_UNLOCK, rd);
        //解锁法坛
        userCity.setFt(0);
        userCity.setFtRepairValue(0);
        gameUserService.updateItem(userCity);
        //发布解锁法坛事件
        ChengCInEventPublisher.pubUnlockFaTanEvent(uid, userCity);
        return rd;
    }
}
