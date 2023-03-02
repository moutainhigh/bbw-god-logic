package com.bbw.god.gameuser.guide;

import com.bbw.god.city.UserCityService;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.GameUserShakeLogic;
import com.bbw.god.gameuser.guide.v1.NewerGuideEnum;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author suhq
 * @description 新手引导服务
 * @date 2019-12-27 06:42
 **/
@Service
@Slf4j
public class NewerGuideService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GameUserShakeLogic gameUserShakeLogic;
    @Autowired
    private UserTreasureService userTreasureService;
    @Autowired
    private GuideConfig guideConfig;
    @Autowired
    private UserCityService userCityService;

    public int getNewerGuide(long uid) {
        UserNewerGuide newerGuide = getUserNewerGuide(uid);
        return newerGuide.getNewerGuide();
    }

    public UserNewerGuide getUserNewerGuide(long uid) {
        return this.gameUserService.getSingleItem(uid, UserNewerGuide.class);
    }

    public boolean isPassNewerGuide(long uid) {
        UserNewerGuide newerGuide = getUserNewerGuide(uid);
        if (newerGuide == null) {
            GameUser gu = this.gameUserService.getGameUser(uid);
            int newerGuideProgress = gu.getStatus().getGuideStatus();
            boolean isPassNewerGuide = false;
            if (newerGuideProgress == com.bbw.god.gameuser.guide.v2.NewerGuideEnum.CARD_LEVEL_UP.getStep() ||
                    newerGuideProgress == com.bbw.god.gameuser.guide.v3.NewerGuideEnum.YE_GUAI.getStep() ||
                    gu.getLevel() >= 8) {
                isPassNewerGuide = true;
                newerGuideProgress = NewerGuideEnum.CARD_LEVEL_UP.getStep();
                GuideEventPublisher.pubPassNewerGuideEvent(uid, new RDCommon());
            } else {
                newerGuideProgress = NewerGuideEnum.START.getStep();
            }
            newerGuide = UserNewerGuide.getInstance(uid, newerGuideProgress, isPassNewerGuide);
            this.gameUserService.addItem(uid, newerGuide);
            GuideEventPublisher.pubLogNewerGuideEvent(uid, newerGuideProgress, new RDCommon());
        }
        return newerGuide.getIsPassNewerGuide();
    }

    /**
     * 更新玩家新手引导
     *
     * @param uid
     * @param guide
     * @param rd
     */
    public void updateNewerGuide(long uid, NewerGuideEnum guide, RDCommon rd) {
        UserNewerGuide userNewerGuide = getUserNewerGuide(uid);
        if (!userNewerGuide.getIsPassNewerGuide()) {
            userNewerGuide.updateNewerGuide(guide.getStep());
            this.gameUserService.updateItem(userNewerGuide);
            // 发布记录新手引导进度变化事件
            GuideEventPublisher.pubLogNewerGuideEvent(uid, guide.getStep(), rd);
            if (userNewerGuide.getIsPassNewerGuide()) {
                GuideEventPublisher.pubPassNewerGuideEvent(uid, rd);
            }
        }
    }

    /**
     * 调用新手引导这边service之前的业务检查
     *
     * @param uid
     * @param newerGuide
     *//*
    public void check(long uid, Integer newerGuide) {
        // 错误的新手引导步骤
        NewerGuideEnum guideEnum = NewerGuideEnum.fromValue(newerGuide);
        if (null == guideEnum) {
            log.error("玩家id={},客户端传来错误的引导值:{}", uid, newerGuide);
            throw new ExceptionForClientTip("newer.guide.error");
        }
        // 获取redis中的数据进行对比
        UserNewerGuide userNewerGuide = getUserNewerGuide(uid);
        if (userNewerGuide.getIsPassNewerGuide()) {
            log.error("玩家id={},当前玩家已经通过新手引导", uid);
            throw new ExceptionForClientTip("newer.guide.already.pass");
        }
        NewerGuideEnum newerGuideEnum = NewerGuideEnum.fromValue(userNewerGuide.getNewerGuide());
        // 客户端数据和服务端数据对不上
        if (null == newerGuideEnum || guideEnum != newerGuideEnum) {
            log.error("玩家id={},客户端传来的引导值:{},服务端的引导值:{}", uid, guideEnum, newerGuideEnum);
            throw new ExceptionForClientTip("newer.guide.error");
        }
    }

    *//**
     * 新手引导的摇骰子
     *
     * @param uid        玩家id
     * @param diceNum    骰子数
     * @param newerGuide 当前新手引导的步骤
     * @return
     *//*
    public RDAdvance shakeDice(long uid, Integer diceNum, Integer newerGuide) {
        check(uid, newerGuide);
        NewerGuideEnum guideEnum = NewerGuideEnum.fromValue(newerGuide);
        GameUser gu = gameUserService.getGameUser(uid);
        List<Integer> diceResult = getDiceResult(guideEnum, gu);
        List<PathRoad> roadPath = getRoadPath(gu.getLocation().getPosition(), gu.getLocation().getDirection(), diceResult.get(0), guideEnum);
        RDAdvance rd = new RDAdvance();
        diceResult = gameUserShakeLogic.splitNum(diceNum, roadPath.size());
        ShakeEventPublish.pubShakeEvent(diceResult, new BaseEventParam(uid, WayEnum.SHAKE_DICE, rd));
        gameUserShakeLogic.getRoads(gu, roadPath, WayEnum.SHAKE_DICE, rd);
        rd.setRandoms(diceResult);
        return rd;
    }

    *//**
     * 获取骰子点数
     *
     * @param guideEnum
     * @param gu
     * @return
     *//*
    private List<Integer> getDiceResult(NewerGuideEnum guideEnum, GameUser gu) {
        if (NewerGuideEnum.ATTACK_2 == guideEnum && gu.getLocation().getPosition() == NewerGuideEnum.YOU_SHANG_GUAN.getPos()) {
            return Collections.singletonList(NewerGuideEnum.YOU_SHANG_GUAN.getNextStepNum());
        }
        if (NewerGuideEnum.JIAOYI == guideEnum && gu.getLocation().getPosition() == NewerGuideEnum.YOU_SHANG_GUAN.getPos()) {
            return Collections.singletonList(NewerGuideEnum.YOU_SHANG_GUAN.getNextStepNum());
        }
        return Collections.singletonList(guideEnum.getNextStepNum());
    }

    private List<PathRoad> getRoadPath(int beginPos, int dir, int pathLength, NewerGuideEnum newerGuideEnum) {
        if (0 == pathLength) {
            throw new ExceptionForClientTip("newer.guide.error");
        }
        List<List<PathRoad>> allPaths = RoadPathTool.I.getAllPaths(beginPos, dir, pathLength);
        // 当前已经是新手引导的最后一步了
        NewerGuideEnum nextGuideEnum = NewerGuideEnum.getNextGuideEnum(newerGuideEnum);
        if (null == nextGuideEnum) {
            return PowerRandom.getRandomFromList(allPaths);
        }
        // 返回下一步对应的路径
        Optional<List<PathRoad>> optional = allPaths.stream().filter(tmp -> {
            PathRoad pathRoad = tmp.get(tmp.size() - 1);
            Integer nextPos = nextGuideEnum.getPos();
            Integer nextDir = nextGuideEnum.getDir();
            return pathRoad.getRoad().getId().equals(nextPos) && pathRoad.getDir().equals(nextDir);
        }).findFirst();
        // 不存在的话抛异常，记录日志
        if (!optional.isPresent()) {
            log.error("当前引导状态:{},下一步引导状态:{},当前坐标:{},目标坐标:{}", newerGuideEnum, nextGuideEnum, newerGuideEnum.getPos(), nextGuideEnum.getPos());
            throw new CoderException("新手引导丢骰子错误！");
        }
        return optional.get();
    }


    *//**
     * 领取城内建筑奖励
     *
     * @param uid
     * @param bType
     * @param param
     * @param newerGuide
     * @return
     *//*
    public RDBuildingOutputs.RDBuildingOutput gainBuildingAward(long uid, BuildingEnum bType, String param, Integer newerGuide) {
        check(uid, newerGuide);
        GameUser gu = gameUserService.getGameUser(uid);
        CfgCityEntity city = gu.gainCurCity();
        CityChecker.checkIsCC(city);
        UserCity userCity = userCityService.getUserCity(gu.getId(), city.getId());
        CityChecker.checkIsOwnCC(userCity);
        RDBuildingOutputs.RDBuildingOutput rd = new RDBuildingOutputs.RDBuildingOutput();
        ChengChiInfoCache rdArriveChengC = TimeLimitCacheUtil.getChengChiInfoCache(uid);
        RDCityInInfo rdCityInInfo = rdArriveChengC.getCityInInfo();
        List<RDBuildingInfo> buildingInfos = rdCityInInfo.getInfo();
        RDBuildingInfo rdBuildingInfo = buildingInfos.stream().filter(bi -> bi.getType() == bType.getValue()).findFirst()
                .orElse(null);
        int remainTimes = rdBuildingInfo.getRemainTimes();
        if (remainTimes <= 0) {
            rd.setMessage(LM.I.getMsg("city.cc.in.jxz.already.get"));
            return rd;
        }
        TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.JU_XIAN_LING.getValue(), 10, WayEnum.JXZ_AWARD, rd);
        rd.setRate(2);
        return rd;
    }

    public void sendTreasureToNum(long uid, int treasureId, int num, WayEnum way, RDCommon rd) {
        int treasureNum = userTreasureService.getTreasureNum(uid, treasureId);
        if (treasureNum < num) {
            int toAddNum = num - treasureNum;
            TreasureEventPublisher.pubTAddEvent(uid, treasureId, toAddNum, way, rd);
        }
    }

    *//**
     * 新手引导抽卡
     *
     * @param uid
     * @param newerGuide
     * @return
     *//*
    public RDCardDraw drawCard(long uid, Integer newerGuide) {
        check(uid, newerGuide);
        RDCardDraw rd = new RDCardDraw();
        GameUser gu = gameUserService.getGameUserWithUserData(uid);
        int index = gu.getRoleInfo().getCountry() / 10 - 1;
        int cardId = guideConfig.getDrawCardsAsJxPool().get(index);
        CardEventPublisher.pubCardAddEvent(uid, cardId, WayEnum.OPEN_JU_XIAN_CARD_POOL, "", rd);
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureEnum.JU_XIAN_LING.getValue(), 10, WayEnum.OPEN_JU_XIAN_CARD_POOL, rd);
        return rd;
    }*/
}
