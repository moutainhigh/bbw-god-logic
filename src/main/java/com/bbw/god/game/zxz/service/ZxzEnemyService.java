package com.bbw.god.game.zxz.service;

import com.bbw.cache.GameCacheService;
import com.bbw.common.PowerRandom;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.WayEnum;
import com.bbw.god.game.zxz.cfg.ZxzTool;
import com.bbw.god.game.zxz.cfg.award.ZxzAwardTool;
import com.bbw.god.game.zxz.entity.*;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.rd.RDCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 诛仙阵敌方配置service
 * @author: hzf
 * @create: 2022-10-13 13:26
 **/
@Service
public class ZxzEnemyService {

    @Autowired
    private GameCacheService gameCacheService;

    /**
     * 获取敌方配置记录
     * @return
     */
    public List<ZxzInfo> getZxzInfos(){
        return  gameCacheService.getGameDatas(ZxzInfo.class);
    }

    /**
     * 指定时间获取敌方配置
     * @param beginDate
     * @return
     */
    public List<ZxzInfo> getZxzInfos(Integer beginDate){
        return getZxzInfos().stream()
                .filter(zInfo -> (zInfo.ifValidZxz(beginDate)))
                .collect(Collectors.toList());
    }
    /**
     * 获取敌方配置
     * @return
     */
    public ZxzInfo getZxzInfo(){
        return getZxzInfos().stream()
                .filter(zInfo -> (zInfo.ifValidZxz()))
                .findFirst().orElse(null);
    }
    public ZxzInfo getZxzInfo(Integer beginDate){
        return getZxzInfos().stream()
                .filter(zInfo -> (zInfo.ifValidZxz(beginDate)))
                .findFirst().orElse(null);
    }


    /**
     * 获取难度数据
     *
     * @param difficulty
     * @return
     */
    public ZxzDifficulty getZxzLevel(Integer difficulty) {
        return getZxzInfo().getDifficultys()
                .stream().filter(zxzDifficulty -> zxzDifficulty.getDifficulty().equals(difficulty))
                .findFirst().orElse(null);
    }

    /**
     * 获取区域数据
     * @param regionId
     * @return
     */
    public ZxzRegion getZxzRegion(Integer regionId){
        Integer difficulty = ZxzTool.getDifficulty(regionId);
        return getZxzLevel(difficulty).getRegions().stream()
                .filter(region -> region.getRegionId().equals(regionId))
                .findFirst().orElse(null);
    }
    /**
     * 获取区域关卡数据
     * @param defenderId
     * @return
     */
    public ZxzRegionDefender getZxzRegionDefender(Integer defenderId){
        Integer regionId = ZxzTool.getRegionId(defenderId);
        return getZxzRegion(regionId).getDefenders().stream()
                .filter(defender -> defender.getDefenderId().equals(String.valueOf(defenderId)))
                .findFirst().orElse(null);
    }







}
