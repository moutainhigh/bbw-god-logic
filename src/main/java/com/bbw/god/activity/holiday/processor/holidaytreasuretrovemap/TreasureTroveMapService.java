package com.bbw.god.activity.holiday.processor.holidaytreasuretrovemap;

import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.award.AwardStatus;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 寻藏宝图逻辑
 *
 * @author: huanghb
 * @date: 2022/2/8 13:50
 */
@Service
public class TreasureTroveMapService {
    @Autowired
    private TreasureTroveMapDataService treasureTroveMapDataService;
    @Autowired
    private AwardService awardService;


    /**
     * 刷新翻牌挑战信息
     *
     * @param
     * @return
     */
    public RDTreasureTroveMap refreshFlopChallenge(long uid, Integer treasureTroveMapLevel) {
        //参数检查
        paramCheck(treasureTroveMapLevel);
        //获得用户藏宝图信息
        UserTreasureTroveMap userTreasureTroveMap = getUserTreasureTroveMap(uid);
        //是否可以领取该等级的藏宝图奖励
        if (userTreasureTroveMap.ifCanReceiveTreasureTroveMapAwards(treasureTroveMapLevel)) {
            throw new ExceptionForClientTip("findTreasureMap.enble.award");
        }
        //是否可以刷新翻牌挑战
        if (!userTreasureTroveMap.ifCanRefreshFlopChallenge()) {
            throw new ExceptionForClientTip("flopChallenge.not.finish");
        }
        //刷新翻牌挑战
        userTreasureTroveMap.refreshFlopChallenge(treasureTroveMapLevel);
        //更新数据
        treasureTroveMapDataService.updateTreasureTroveMapToCache(uid, userTreasureTroveMap);
        return RDTreasureTroveMap.instance(userTreasureTroveMap);
    }

    /**
     * 参数检测
     *
     * @param treasureTroveMapLevel
     */
    private void paramCheck(Integer treasureTroveMapLevel) {
        //等级是否是0
        if (treasureTroveMapLevel == 0) {
            throw new ExceptionForClientTip("findTreasureMap.param.error");
        }
        //等级是否在规定范围
        TreasureTroveMapLevelEnum treasureTroveMapLevelEnum = TreasureTroveMapLevelEnum.fromValue(treasureTroveMapLevel);
        if (null == treasureTroveMapLevelEnum) {
            throw new ExceptionForClientTip("findTreasureMap.param.error");
        }
    }

    /**
     * 翻牌
     *
     * @param
     * @return
     */
    public RDCommon flop(long uid, Integer flopIndex) {
        //获得用户藏宝图信息
        UserTreasureTroveMap userTreasureTroveMap = getUserTreasureTroveMap(uid);
        //
        boolean isValidFlopIndex = userTreasureTroveMap.ifValidFlopIndex(flopIndex);
        if (!isValidFlopIndex) {
            throw new ExceptionForClientTip("findTreasureMap.param.error");
        }
        //翻牌检测
        boolean isCanFlop = userTreasureTroveMap.ifCanFlop(flopIndex);
        if (!isCanFlop) {
            throw new ExceptionForClientTip("findTreasureMap.not.flop");
        }
        //翻牌令道具检查
        TreasureChecker.checkIsEnough(TreasureTroveMapTool.getTreasureHuntPropId(), TreasureTroveMapTool.getFlopNeedFlopCardNum(), uid);
        //翻牌
        userTreasureTroveMap.flop(flopIndex);
        //翻牌奖励
        List<Award> flopAwards = TreasureTroveMapTool.gainFlopAwards();
        RDCommon rd = new RDCommon();
        //扣除道具
        TreasureEventPublisher.pubTDeductEvent(uid, TreasureTroveMapTool.getTreasureHuntPropId(), TreasureTroveMapTool.getFlopNeedFlopCardNum(), WayEnum.FIND_TREASURE_MAP, rd);
        //发送奖励
        awardService.fetchAward(uid, flopAwards, WayEnum.FIND_TREASURE_MAP, WayEnum.FIND_TREASURE_MAP.getName(), rd);
        //更新数据
        treasureTroveMapDataService.updateTreasureTroveMapToCache(uid, userTreasureTroveMap);
        return rd;
    }

    /**
     * 领取连线奖励
     *
     * @param
     * @return
     */
    public RDCommon receiveConnectionAwards(long uid, Integer connectionAwardId) {
        //获得用户藏宝图信息
        UserTreasureTroveMap userTreasureTroveMap = getUserTreasureTroveMap(uid);
        //获得连线奖励
        UserTreasureTroveMap.ConnectionAward connectionAward = userTreasureTroveMap.gainConnectionAwards(connectionAwardId);
        //是否存在连线奖励
        if (null == connectionAward) {
            throw new ExceptionForClientTip("findTreasureMap.awardId.is.error");
        }
        //目标进度检测
        if (connectionAward.getStatus() == AwardStatus.UNAWARD.getValue()) {
            throw new ExceptionForClientTip("findTreasureMap.not.achieved");
        }
        if (connectionAward.getStatus() == AwardStatus.AWARDED.getValue()) {
            throw new ExceptionForClientTip("findTreasureMap.awarded");
        }
        //更新奖励为已领取
        userTreasureTroveMap.updataConnectionStatus(connectionAward.getConnectionAwardId());
        //翻牌奖励
        List<Award> connectionAwards = connectionAward.getAwards();
        RDCommon rd = new RDCommon();
        //发放奖励
        awardService.fetchAward(uid, connectionAwards, WayEnum.FIND_TREASURE_MAP, WayEnum.FIND_TREASURE_MAP.getName(), rd);
        //更新数据
        treasureTroveMapDataService.updateTreasureTroveMapToCache(uid, userTreasureTroveMap);
        return rd;
    }

    /**
     * 领取目标奖励
     *
     * @param uid
     * @return
     */
    public RDCommon receiveFlopTargetAwards(long uid, int targetId) {
        //获得用户藏宝图信息
        UserTreasureTroveMap userTreasureTroveMap = getUserTreasureTroveMap(uid);
        //根据目标id获得目标进度
        UserTreasureTroveMap.Target userFlopTarget = userTreasureTroveMap.gainTargetByTargetId(targetId);
        if (null == userFlopTarget) {
            throw new ExceptionForClientTip("findTreasureMap.awardId.is.error");
        }
        //目标进度检测
        if (userFlopTarget.getStatus() == AwardStatus.UNAWARD.getValue()) {
            throw ExceptionForClientTip.fromi18nKey("findTreasureMap.not.achieved");
        }
        if (userFlopTarget.getStatus() == AwardStatus.AWARDED.getValue()) {
            throw ExceptionForClientTip.fromi18nKey("findTreasureMap.awarded");
        }
        //更新奖励为已领取
        userTreasureTroveMap.updateTargetToAwarded(targetId);
        treasureTroveMapDataService.updateTreasureTroveMapToCache(uid, userTreasureTroveMap);
        RDCommon rd = new RDCommon();
        //发放奖励
        CfgTreasureTroveMap.Target cfgFlopTarget = TreasureTroveMapTool.getFlopTarget(targetId);
        awardService.fetchAward(uid, cfgFlopTarget.getAwards(), WayEnum.FIND_TREASURE_MAP, "", rd);
        return rd;
    }


    /**
     * 获取藏宝图奖励用于展示
     *
     * @return
     */
    protected RDTreasureTroveMapAward getTreasureTroveMapToShow(long uid, Integer treasureTroveMapLevel) {
        //参数检查
        paramCheck(treasureTroveMapLevel);
        //获得用户藏宝图信息
        UserTreasureTroveMap userTreasureTroveMap = getUserTreasureTroveMap(uid);
        //获得奖励信息
        List<Award> awards = getTreasureTroveMapAwards(treasureTroveMapLevel, userTreasureTroveMap);
        boolean awardStatus = userTreasureTroveMap.ifCanReceiveTreasureTroveMapAwards(treasureTroveMapLevel);
        return RDTreasureTroveMapAward.getInstance(awards, awardStatus);
    }

    /**
     * 根据藏宝图等级获得对应奖励
     *
     * @param treasureTroveMapLevel
     * @return
     */
    private List<Award> getTreasureTroveMapAwards(Integer treasureTroveMapLevel, UserTreasureTroveMap userTreasureTroveMap) {
        //获得藏宝图等级下标
        int treasureTroveMapLevelIndex = TreasureTroveMapLevelEnum.fromValue(treasureTroveMapLevel).getLevelIndex();
        //寻藏宝图轮次
        int findTreasureMapTurn = userTreasureTroveMap.gainFindTreasureMapTurn(treasureTroveMapLevelIndex);
        //获得对应奖励
        return TreasureTroveMapTool.getTreasureTroveMapAwardByLevel(treasureTroveMapLevel, findTreasureMapTurn);
    }

    /**
     * 领取藏宝图奖励奖励
     *
     * @param uid
     * @return
     */
    public RDCommon receiveTreasureTroveMapAwards(long uid, int treasureTroveMapLevel) {
        //参数检查
        paramCheck(treasureTroveMapLevel);
        //获得用户藏宝图信息
        UserTreasureTroveMap userTreasureTroveMap = getUserTreasureTroveMap(uid);
        if (!userTreasureTroveMap.ifCanReceiveTreasureTroveMapAwards(treasureTroveMapLevel)) {
            throw ExceptionForClientTip.fromi18nKey("findTreasureMap.not.achieved");
        }
        //藏宝图奖励
        List<Award> TreasureTroveMapAwards = getTreasureTroveMapAwards(treasureTroveMapLevel, userTreasureTroveMap);
        //更新下一轮次藏宝图奖励
        userTreasureTroveMap.receiveTreasureTroveMapAwards(treasureTroveMapLevel);
        treasureTroveMapDataService.updateTreasureTroveMapToCache(uid, userTreasureTroveMap);
        //返回
        RDCommon rd = new RDCommon();
        //发放奖励
        awardService.fetchAward(uid, TreasureTroveMapAwards, WayEnum.FIND_TREASURE_MAP, "", rd);
        return rd;
    }

    /**
     * 获得用户藏宝图信息
     *
     * @param uid
     * @return
     */
    public UserTreasureTroveMap getUserTreasureTroveMap(long uid) {
        UserTreasureTroveMap userTreasureTroveMap = treasureTroveMapDataService.getTreasureTroveMapFromCache(uid);
        //缓存为空初始化
        if (null != userTreasureTroveMap) {
            return userTreasureTroveMap;
        }
        //数据缓存到本地和redis
        userTreasureTroveMap = UserTreasureTroveMap.instance();
        treasureTroveMapDataService.updateTreasureTroveMapToCache(uid, userTreasureTroveMap);
        return userTreasureTroveMap;
    }

}
