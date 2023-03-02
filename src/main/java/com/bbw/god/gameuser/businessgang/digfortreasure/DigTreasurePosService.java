package com.bbw.god.gameuser.businessgang.digfortreasure;

import com.bbw.god.game.config.city.CfgRoadEntity;
import com.bbw.god.game.config.city.RoadTool;
import com.bbw.god.gameuser.GameUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 挖宝位置服务类
 *
 * @author: huanghb
 * @date: 2022/1/30 17:21
 */
@Slf4j
@Service
public class DigTreasurePosService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private GameDigTreasureService gameDigTreasureService;


    /**
     * 添加挖宝位置信息
     *
     * @param uid
     */
    protected UserDigTreasurePos getDigTreasurePos(long uid) {
        UserDigTreasurePos userDigTreasurePos = gameUserService.getSingleItem(uid, UserDigTreasurePos.class);
        boolean isNeedRefresh = null == userDigTreasurePos || DigTreasureTool.isNeedRefresh(userDigTreasurePos.getDateTime());
        //不为空并且不在刷新时间点
        if (!isNeedRefresh) {
            return userDigTreasurePos;
        }
        //刷新格子位置
        userDigTreasurePos = randomGenerateRoadIds(uid, userDigTreasurePos);
        return userDigTreasurePos;
    }

    /**
     * 添加挖宝位置信息
     *
     * @param userDigTreasurePoses 玩家挖宝位置集合
     */
    protected void addDigTreasurePos(UserDigTreasurePos userDigTreasurePoses) {
        gameUserService.updateItem(userDigTreasurePoses);
    }

    /**
     * 随机生成格子id
     *
     * @param uid
     * @return
     */
    protected UserDigTreasurePos randomGenerateRoadIds(long uid, UserDigTreasurePos userDigTreasurePos) {
        List<CfgRoadEntity> roads = RoadTool.getRoads();
        List<Integer> roadIds = roads.stream().map(CfgRoadEntity::getId).collect(Collectors.toList());
        //洗牌
        Collections.shuffle(roadIds);
        int serverGroup = gameUserService.getActiveGid(uid);
        //获得区服组挖宝奖励数据
        GameDigTreasure gameDigTreasure = gameDigTreasureService.getGameDigTreasure(serverGroup);
        //初始化位置信息
        if (null == userDigTreasurePos) {
            userDigTreasurePos = UserDigTreasurePos.instance(uid);
        }
        userDigTreasurePos.randomGenerateDigTreasurePosInfo(gameDigTreasure);
        addDigTreasurePos(userDigTreasurePos);
        return userDigTreasurePos;
    }
}
