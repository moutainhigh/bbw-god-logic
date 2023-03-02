package com.bbw.god.game.transmigration;

import com.bbw.common.ListUtil;
import com.bbw.common.SetUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.city.UserCityService;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgCityEntity;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.game.transmigration.cfg.CfgTransmigration;
import com.bbw.god.game.transmigration.cfg.TransmigrationTool;
import com.bbw.god.game.transmigration.entity.*;
import com.bbw.god.game.transmigration.rd.*;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 轮回世界主逻辑
 *
 * @author: suhq
 * @date: 2021/9/15 1:59 下午
 */
@Slf4j
@Service
public class TransmigrationLogic {
    @Autowired
    private GameTransmigrationService gameTransmigrationService;
    @Autowired
    private UserTransmigrationService userTransmigrationService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private UserTransmigrationCityService userTransmigrationCityService;
    @Autowired
    private TransmigrationHightLightTotalService highLightTotalService;
    @Autowired
    private TransmigrationHightLightUserService highLightUserService;
    @Autowired
    private TransmigrationEnterService transmigrationEnterService;
    @Autowired
    private UserCityService userCityService;
    @Autowired
    private TransmigrationCityRecordService transmigrationCityRecordService;
    @Autowired
    private TransmigrationRankCityService transmigrationRankCityService;
    @Autowired
    private UserTransmigrationTargetLogic userTransmigrationTargetLogic;
    /** 默认评分奖励数量 */
    private final static Integer DEFAULT_SCORE_AWARD_NUM = 50;
    /** 80分以上高评分奖励数量 */
    private final static Integer HIGH_SCORE_AWARD_NUM = 100;

    /**
     * 轮回主界面信息
     *
     * @param uid
     * @return
     */
    public RDTransmigrationInfo getInfo(long uid) {
        RDTransmigrationInfo rd = new RDTransmigrationInfo();
        int sgId = gameUserService.getActiveGid(uid);
        GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(sgId);
        //休赛期
        if (null == curTransmigration) {
            GameTransmigration nextTransmigration = gameTransmigrationService.getNextTransmigration(sgId);
            gameTransmigrationService.checkTransmigration(nextTransmigration);
            rd.setBeginDate(nextTransmigration.getBegin().getTime());
            return rd;
        }
        //轮回世界信息
        rd.setEndDate(curTransmigration.getEnd().getTime());
        //个人信息
        UserTransmigration userTransmigration = userTransmigrationService.getTransmigration(uid);
        List<UserTransmigrationCity> transmigrationCities = userTransmigrationCityService.getTransmigrationCities(uid);
        int defenderBeatedNum = userTransmigration.gainSuccessNum();
        //有攻下，但攻下的时间早于轮回开始时间（上轮）,重置挑战
        if (ListUtil.isNotEmpty(transmigrationCities)) {
            UserTransmigrationCity anyOwn = transmigrationCities.stream().filter(tmp -> tmp.isOwn()).findFirst().orElse(null);
            if (null != anyOwn) {
                if (anyOwn.getOwnTime().before(curTransmigration.getBegin())) {
                    for (UserTransmigrationCity transmigrationCity : transmigrationCities) {
                        transmigrationCity.reset();
                    }
                    gameUserService.updateItems(transmigrationCities);
                    userTransmigration.reset();
                    gameUserService.updateItem(userTransmigration);
                    defenderBeatedNum = 0;
                }
            }

        }
        rd.setMyTotalScore(userTransmigration.gainTotalScore());
        rd.setDefenderBeatedNum(defenderBeatedNum);
        //高光
        List<RDTransmigrationItem> globalHightLights = getTotalHighLights(curTransmigration, 3);
        rd.setGlobalHightlights(globalHightLights);
        List<RDTransmigrationItem> userHighLights = getPersonalHighLights(curTransmigration, uid, 3);
        rd.setPersonalHighlights(userHighLights);
        rd.setTargetAwards(userTransmigrationTargetLogic.getTargets(uid));
        return rd;
    }

    /**
     * 进入轮回世界
     *
     * @param uid
     * @return
     */
    public List<Integer> enter(long uid) {
        int sgId = gameUserService.getActiveGid(uid);
        GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(sgId);
        gameTransmigrationService.checkTransmigration(curTransmigration);
        //本轮是否进入过，如果没有需要校验是否满足校验条件
        if (!transmigrationEnterService.isEnter(curTransmigration, uid)) {
            int nightmareCityOwnNum = userCityService.getUserOwnNightmareCities(uid).size();
            if (nightmareCityOwnNum < TransmigrationTool.getCfg().getUnlockCityNum()) {
                throw ExceptionForClientTip.fromi18nKey("transmigration.not.need");
            }
        }
        //记录进入梦魇的玩家
        transmigrationEnterService.enter(curTransmigration, uid);
        //返回区域属性信息
        return curTransmigration.getMainCityDefenderTypes();
    }

    /**
     * 轮回世界详情列表
     *
     * @param uid
     * @return
     */
    public RDTransmigrationRecords listChallengeRecords(long uid) {
        int sgId = gameUserService.getActiveGid(uid);
        GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(sgId);
        gameTransmigrationService.checkTransmigration(curTransmigration);
        List<UserTransmigrationRecord> myBestRecords = transmigrationCityRecordService.getMyRecords(curTransmigration, uid);
        List<RDTransmigrationRecord> rdRecords = new ArrayList<>();
        CfgTransmigration cfg = TransmigrationTool.getCfg();
        for (UserTransmigrationRecord myBestRecord : myBestRecords) {
            RDTransmigrationRecord rdRecord = RDTransmigrationRecord.getInstance(myBestRecord);
            List<Long> bestUids = transmigrationRankCityService.getBestUids(curTransmigration, myBestRecord.getCityId());
            if (bestUids.contains(uid)) {
                rdRecord.setExtraScore(cfg.getExtraScoreForCityNo1());
                rdRecord.setIsCityNo1(1);
            }
            UserTransmigrationCity transmigrationCity = userTransmigrationCityService.getTransmigrationCity(uid, myBestRecord.getCityId());
            rdRecord.setAwardStatus(transmigrationCity.getAwardStatus());
            rdRecords.add(rdRecord);
        }
        RDTransmigrationRecords rd = new RDTransmigrationRecords();
        rd.setRecords(rdRecords);
//		log.info(uid+"轮回挑战详情:"+JSONUtil.toJson(rd));
        return rd;
    }

    /**
     * 获取某做成的挑战信息
     *
     * @param uid
     * @param cityId
     * @return
     */
    public RDTransmigrationChallengeInfo getChallengeInfo(long uid, int cityId) {
        CfgCityEntity city = CityTool.getCityById(cityId);
        if (!city.isCC()) {
            throw ExceptionForClientTip.fromi18nKey("city.not.cc");
        }
        int sgId = gameUserService.getActiveGid(uid);
        GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(sgId);
        gameTransmigrationService.checkTransmigration(curTransmigration);

        RDTransmigrationChallengeInfo rd = new RDTransmigrationChallengeInfo();

        //个人信息
        boolean isCityNo1 = false;
        int score = 0;
        List<Integer> scoreCompositions = new ArrayList<>();
        Long myBestRecordId = transmigrationCityRecordService.getBestRecordId(curTransmigration, uid, cityId);
        if (myBestRecordId > 0) {
            Optional<UserTransmigrationRecord> op = gameUserService.getUserData(uid, myBestRecordId, UserTransmigrationRecord.class);
            if (op.isPresent()) {
                UserTransmigrationRecord utRecord = op.get();
                scoreCompositions = utRecord.getScoreCompositions();
                score = ListUtil.sumInt(scoreCompositions);
            }
        }
        rd.setScore(score);
        rd.setIsCityNo1(isCityNo1 ? 1 : 0);
        rd.setScoreCompositions(scoreCompositions);
        UserTransmigrationCity transmigrationCity = userTransmigrationCityService.getTransmigrationCity(uid, cityId);
        if (null != transmigrationCity) {
            rd.setAwardStatus(transmigrationCity.getAwardStatus());
        }

        //本城高分记录
        List<Long> bestUids = transmigrationRankCityService.getTopUids(curTransmigration, cityId, 3);
        List<RDTransmigrationItem> rdItems = new ArrayList<>();
        if (ListUtil.isNotEmpty(bestUids)) {
            for (Long bestUid : bestUids) {
                Long bestRecordId = transmigrationCityRecordService.getBestRecordId(curTransmigration, bestUid, cityId);
                Optional<UserTransmigrationRecord> op = gameUserService.getUserData(bestUid, bestRecordId, UserTransmigrationRecord.class);
                if (op.isPresent()) {
                    UserTransmigrationRecord bestRecord = op.get();
                    GameUser gu = gameUserService.getGameUser(bestRecord.getGameUserId());
                    RDTransmigrationTotalItem rdItem = new RDTransmigrationTotalItem();
                    rdItem.setServer(ServerTool.getServer(gu.getServerId()).getShortName());
                    rdItem.setNickname(gu.getRoleInfo().getNickname());
                    rdItem.setScore(bestRecord.gainScore());
                    rdItem.setVideoUrl(bestRecord.getVideoUrl());
                    rdItems.add(rdItem);
                }
            }
        }
        rd.setTops(rdItems);
        //守将信息
        TransmigrationDefender cityDefender = curTransmigration.gainCityDefender(cityId);
        RDTransmigrationDefenderInfo rdDefenderInfo = new RDTransmigrationDefenderInfo();
        rdDefenderInfo.setRunes(cityDefender.getRunes());
        rdDefenderInfo.setCards(cityDefender.gainCards());
        CfgTransmigration cfg = TransmigrationTool.getCfg();
        rdDefenderInfo.setBuffs(cfg.getCityEffects());
        rdDefenderInfo.setType(curTransmigration.gainDefenderType(cityId));
        rd.setDefenderInfo(rdDefenderInfo);
        return rd;
    }

    /**
     * 领取战斗评分奖励
     *
     * @param uid
     * @param cityId
     * @param index
     * @return
     */
    public RDCommon gainFightAwards(long uid, int cityId, int index) {
        UserTransmigrationCity transmigrationCity = userTransmigrationCityService.getTransmigrationCity(uid, cityId);
        int status = transmigrationCity.gainStatus(index);
        if (status == AwardStatus.UNAWARD.getValue()) {
            throw new ExceptionForClientTip("transmigration.fight.unaward");
        }
        if (status == AwardStatus.AWARDED.getValue()) {
            throw new ExceptionForClientTip("transmigration.fight.awarded");
        }
        RDCommon rd = new RDCommon();
        switch (index) {
            case 2:
                TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.HONOR_COPPER_COIN.getValue(), HIGH_SCORE_AWARD_NUM, WayEnum.FIGHT_TRANSMIGRATION, rd);
                break;
            default:
                TreasureEventPublisher.pubTAddEvent(uid, TreasureEnum.HONOR_COPPER_COIN.getValue(), DEFAULT_SCORE_AWARD_NUM, WayEnum.FIGHT_TRANSMIGRATION, rd);
                break;
        }
        transmigrationCity.updateToAwarded(index);
        gameUserService.updateItem(transmigrationCity);
        return rd;
    }

    /**
     * 获取高光列表
     *
     * @param isPersonal 1 个人 0 全服
     * @return
     */
    public RDTransmigrationHighLights listHighLights(long uid, boolean isPersonal) {
        RDTransmigrationHighLights rd = new RDTransmigrationHighLights();
        int sgId = gameUserService.getActiveGid(uid);
        GameTransmigration curTransmigration = gameTransmigrationService.getCurTransmigration(sgId);
        gameTransmigrationService.checkTransmigration(curTransmigration);

        CfgTransmigration cfg = TransmigrationTool.getCfg();
        int limit = cfg.getHighLightNum();
        List<RDTransmigrationItem> rdHighLights = null;
        if (isPersonal) {
            rdHighLights = getPersonalHighLights(curTransmigration, uid, limit);
        } else {
            rdHighLights = getTotalHighLights(curTransmigration, limit);
        }
        rd.setHighLights(rdHighLights);
        return rd;
    }

    /**
     * 获得全服高光
     *
     * @param transmigration
     * @param limit
     * @return
     */
    private List<RDTransmigrationItem> getTotalHighLights(GameTransmigration transmigration, int limit) {
        //高光
        Set<String> highLightRecordIds = highLightTotalService.getRankers(transmigration, 1, limit);
        if (!SetUtil.isNotEmpty(highLightRecordIds)) {
            return new ArrayList<>();
        }
        List<UserTransmigrationRecord> highLightRecord = new ArrayList<>();
        for (String highLightRecordId : highLightRecordIds) {
            List<Long> values = ListUtil.parseStrToLongs(highLightRecordId, "@");
            Optional<UserTransmigrationRecord> op = gameUserService.getUserData(values.get(0), values.get(1), UserTransmigrationRecord.class);
            if (op.isPresent()) {
                highLightRecord.add(op.get());
            }
        }
        List<RDTransmigrationItem> rdHightLightRecords = highLightRecord.stream().map(tmp -> {
            GameUser gu = gameUserService.getGameUser(tmp.getGameUserId());
            RDTransmigrationTotalItem rdItem = new RDTransmigrationTotalItem();
            rdItem.setServer(ServerTool.getServer(gu.getServerId()).getShortName());
            rdItem.setNickname(gu.getRoleInfo().getNickname());
            rdItem.setScore(ListUtil.sumInt(tmp.getScoreCompositions()));
            rdItem.setCityId(tmp.getCityId());
            rdItem.setVideoUrl(tmp.getVideoUrl());
            return rdItem;
        }).collect(Collectors.toList());
        return rdHightLightRecords;
    }


    /**
     * 获得个人高光
     *
     * @param transmigration
     * @param uid
     * @param limit
     * @return
     */
    private List<RDTransmigrationItem> getPersonalHighLights(GameTransmigration transmigration, long uid, int limit) {
        //高光
        Set<Long> highLightRecordIds = highLightUserService.getRankers(transmigration, uid, 1, limit);
        if (!SetUtil.isNotEmpty(highLightRecordIds)) {
            return new ArrayList<>();
        }
        List<UserTransmigrationRecord> highLightRecord = gameUserService.getUserDatas(uid, highLightRecordIds, UserTransmigrationRecord.class);

        List<RDTransmigrationItem> rdHightLightRecords = highLightRecord.stream().map(tmp -> {
            RDTransmigrationUserItem rdItem = new RDTransmigrationUserItem();
            rdItem.setCityId(tmp.getCityId());
            rdItem.setScore(ListUtil.sumInt(tmp.getScoreCompositions()));
            rdItem.setVideoUrl(tmp.getVideoUrl());
            return rdItem;
        }).collect(Collectors.toList());
        return rdHightLightRecords;
    }
}
