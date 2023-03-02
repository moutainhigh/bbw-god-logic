package com.bbw.god.gameuser.businessgang.digfortreasure;

import com.bbw.common.ListUtil;
import com.bbw.exception.ExceptionForClientTip;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.UserCfgObj;
import com.bbw.god.gameuser.businessgang.digfortreasure.event.DigTreasureEventPublisher;
import com.bbw.god.gameuser.treasure.TreasureChecker;
import com.bbw.god.gameuser.treasure.event.TreasureEventPublisher;
import com.bbw.god.rd.RDCommon;
import com.bbw.god.rd.RDSuccess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 挖宝逻辑
 *
 * @author: huanghb
 * @date: 2022/1/17 16:58
 */
@Service
public class DigTreasureLogic {
    @Autowired
    private DigTreasureService digTreasureService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private DigTreasurePosService digTreasurePosService;


    /**
     * 挖宝
     *
     * @param uid
     * @return
     */
    public RDSuccess digTreasure(long uid) {
        if (!digTreasureService.isShowDigTreasure(uid)) {
            throw new ExceptionForClientTip("digTreasure.not.open");
        }
        //获得玩家挖宝信息
        int userPos = gameUserService.getGameUser(uid).getLocation().getPosition();
        UserDigTreasure userDigTreasure = digTreasureService.getUserCurrentDigTreasureByPos(uid, userPos);
        //该位置是否挖完
        if (userDigTreasure.isDugAll()) {
            throw new ExceptionForClientTip("digForTreasure.already.dug.all");
        }
        //挖宝
        int currentFloor = userDigTreasure.getCurrentFloor();
        int needTreasureId = DigTreasureTool.getDigTreasureNeedShovelIds().get(currentFloor);
        //该层是否已挖掘
        if (!userDigTreasure.ifCanDig()) {
            throw new ExceptionForClientTip("digForTreasure.already.dug.floor");
        }
        TreasureChecker.checkIsEnough(needTreasureId, 1, uid);
        userDigTreasure.digTreasure();
        //获得当前位置已挖宝的所有宝藏id
        gameUserService.updateItem(userDigTreasure);
        RDDigTreasureInfo rd = RDDigTreasureInfo.getInstance(userDigTreasure);
        //发布道具扣除事件
        TreasureEventPublisher.pubTDeductEvent(uid, needTreasureId, 1, WayEnum.YEAR_BEAST, rd);
        //发布挖宝事件
        DigTreasureEventPublisher.pubDigTreasureEvent(uid, userPos);
        //是否挖到宝藏
        Integer treasureTroveId = userDigTreasure.getCurrentFloorTreasureTroveId(currentFloor);
        boolean isDugAward = treasureTroveId != 0;
        if (!isDugAward) {
            rd.setIfAward(0);
            return rd;
        }
        //发送宝藏奖励
        List<Award> awards = DigTreasureTool.getCurrentPosAwawd(treasureTroveId);
        awardService.fetchAward(uid, awards, WayEnum.DIG_FOR_TREASURE, WayEnum.DIG_FOR_TREASURE.getName(), rd);
        //随机高级卷轴奖励
        List<Award> scrollAwards = awards.stream().filter(tmp -> tmp.getAwardId() == TreasureEnum.RANDOM_ADVANCED_SCROLL.getValue()).collect(Collectors.toList());
        //如果有随机高级卷轴奖励
        if (ListUtil.isNotEmpty(scrollAwards)) {
            //更新奖励信息为使用卷轴获得的奖励
            RDCommon.RDTreasureInfo rdAward = rd.getTreasures().get(0);
            userDigTreasure.updateResultId(userDigTreasure.getCurrentFloor(), rdAward.getId());
            rd.updateResultId(userDigTreasure);
        }
        //更新拥有项
        gameUserService.updateItem(userDigTreasure);
        rd.setIfAward(1);
        return rd;
    }

    /**
     * 获得所有挖宝信息（目前仅包括层数信息）
     *
     * @param uid
     * @return
     */
    public RdDigTreasureInfos getAlldigTreasureIfo(long uid) {
        if (!digTreasureService.isShowDigTreasure(uid)) {
            throw new ExceptionForClientTip("digTreasure.not.open");
        }
        //获得用户挖宝信息
        List<UserDigTreasure> userDigTreasures = digTreasureService.getUserCurrentDigTreasures(uid);
        List<CfgRoadEntity> roads = RoadTool.getRoads();
        //用户挖宝信息是否正常
        boolean isVaildData = ListUtil.isNotEmpty(userDigTreasures) && roads.size() <= userDigTreasures.size();
        if (isVaildData) {
            return RdDigTreasureInfos.getInstance(userDigTreasures);
        }
        //玩家拥有挖宝信息的位置
        List<Integer> ownDigTreasurePosList = userDigTreasures.stream().map(UserCfgObj::getBaseId).collect(Collectors.toList());
        //生成缺失藏宝信息
        List<UserDigTreasure> newUserDigTreasures = digTreasureService.generateMultipleUserDigTreasure(uid, ownDigTreasurePosList);
        //存储数据
        digTreasureService.addDigTreasures(newUserDigTreasures);
        //添加已拥有挖宝信息
        newUserDigTreasures.addAll(userDigTreasures);
        return RdDigTreasureInfos.getInstance(newUserDigTreasures);
    }
}
