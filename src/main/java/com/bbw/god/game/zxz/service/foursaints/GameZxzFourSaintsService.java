package com.bbw.god.game.zxz.service.foursaints;

import com.bbw.cache.GameCacheService;
import com.bbw.god.game.zxz.entity.ZxzInfo;
import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaints;
import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaintsDefender;
import com.bbw.god.game.zxz.entity.foursaints.ZxzFourSaintsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 四圣挑战 service
 * @author: hzf
 * @create: 2022-12-28 11:02
 **/
@Service
public class GameZxzFourSaintsService {
    @Autowired
    private GameCacheService gameCacheService;

    /**
     * 获取生成四圣信息
     * @return
     */
    public List<ZxzFourSaintsInfo> getZxzFourSaintsInfos(){
        return gameCacheService.getGameDatas(ZxzFourSaintsInfo.class);
    }
    /**
     * 指定时间获取敌方配置
     * @param beginDate
     * @return
     */
    public List<ZxzFourSaintsInfo> getZxzFourSaintsInfos(Integer beginDate){
        return getZxzFourSaintsInfos().stream()
                .filter(zInfo -> (zInfo.ifValid(beginDate)))
                .collect(Collectors.toList());
    }

    /**
     * 获取四圣敌方配置
     * @return
     */
    public ZxzFourSaintsInfo getZxzFourSaintsInfo(){
        return getZxzFourSaintsInfos().stream()
                .filter(zInfo -> (zInfo.ifValid()))
                .findFirst().orElse(null);
    }

    /**
     * 获取四圣单个数据
     * @param challengeType
     * @return
     */
    public ZxzFourSaints getZxzFourSaints(Integer challengeType){
        return getZxzFourSaintsInfo().getZxzFourSaintss().stream()
                .filter(tmp -> tmp.getChallengeType().equals(challengeType))
                .findFirst().orElse(null);

    }

    /**
     * 获取四圣单个关卡数据
     * @param challengeType
     * @param defenderId
     * @return
     */
    public ZxzFourSaintsDefender getZxzFourSaintsDefender(Integer challengeType,Integer defenderId){
        List<ZxzFourSaintsDefender> fourSaintsDefenders = getZxzFourSaints(challengeType).getFourSaintsDefenders();
        return fourSaintsDefenders.stream().filter(tmp -> tmp.getDefenderId().equals(defenderId)).findFirst().orElse(null);
    }


}
