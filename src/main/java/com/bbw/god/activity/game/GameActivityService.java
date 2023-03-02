package com.bbw.god.activity.game;

import com.bbw.cache.GameCacheService;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activity.IActivity;
import com.bbw.god.activity.config.ActivityEnum;
import com.bbw.god.activity.config.ActivityParentTypeEnum;
import com.bbw.god.activity.config.ActivityScopeEnum;
import com.bbw.god.db.entity.CfgActivityEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.data.GameData;
import com.bbw.god.game.data.GameDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GameActivityService {
    @Autowired
    private GameCacheService gameCacheService;
    @Autowired
    private GameDataService gameDataService;

    /**
     * 获得区服活动实例集合
     *
     * @param pType
     * @return
     */
    public List<GameActivity> getGameActivitiesBySid(int sid, ActivityParentTypeEnum pType) {
        List<GameActivity> gas = getGameActivitiesBySid(sid).stream()
                .filter(ga -> ga.getParentType() == pType.getValue() && DateUtil.isBetweenIn(DateUtil.now(), ga.getBegin(), ga.getEnd()))
                .collect(Collectors.toList());
        return gas;
    }

    /**
     * 获得当前所有全服活动实例
     *
     * @param ca
     * @return
     */
    public GameActivity getGa(int sid, CfgActivityEntity ca) {
        if (ca.getScope() != ActivityScopeEnum.GAME.getValue()) {
            return null;
        }
        return this.getGameActivitiesBySid(sid).stream()
                .filter(ga -> ga.getType().intValue() == ca.getType() && ga.ifTimeValid())
                .findFirst().orElse(null);
    }

    /**
     * 获得所有全服活动，包括历史活动
     *
     * @param ca
     * @return
     */
    public List<IActivity> getGasIncludeHistory(int sid, CfgActivityEntity ca) {
        if (ca.getScope() != ActivityScopeEnum.GAME.getValue()) {
            return null;
        }
        return getGameActivitiesBySid(sid).stream()
                .filter(ga -> ga.getType().intValue() == ca.getType()).collect(Collectors.toList());
    }

    /**
     * 获得区服所有活动(基于本地缓存)
     *
     * @return
     */
    public List<GameActivity> getGameActivitiesBySid(int sid) {
        int serverGroup = ServerTool.getServerGroup(sid);
        return getGameActivitiesByServerGroup(serverGroup);
    }

    public List<GameActivity> getGameActivitiesByServerGroup(int serverGroup) {
        return gameCacheService.getGameDatas(GameActivity.class).stream()
                .filter(ga -> ga.getServerGroup() == 0 || ga.getServerGroup() == serverGroup)
                .collect(Collectors.toList());
    }

    public GameActivity getGameActivity(int serverGroup, ActivityEnum activityEnum) {
        return getGameActivitiesByServerGroup(serverGroup).stream().filter(tmp ->
                tmp.gainType().equals(activityEnum.getValue())).findFirst().orElse(null);
    }

    /**
     * 添加活动
     *
     * @param gas
     */
    public void addGameActivities(List<GameActivity> gas) {
        if (ListUtil.isEmpty(gas)) {
            return;
        }
        gameDataService.addGameDatas(gas);
    }

    public void updateGameActivities(List<GameActivity> gas) {
        if (ListUtil.isEmpty(gas)) {
            return;
        }
        gameDataService.updateGameDatas(gas);
    }

    /**
     * 删除活动
     *
     * @param gas
     */
    public void delGameActivities(List<GameActivity> gas) {
        if (ListUtil.isEmpty(gas)) {
            return;
        }
        List<Long> dataIds = gas.stream().map(GameData::getId).collect(Collectors.toList());
        gameDataService.deleteGameDatas(dataIds, GameActivity.class);
    }
}
