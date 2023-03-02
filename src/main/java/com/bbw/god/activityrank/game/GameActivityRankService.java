package com.bbw.god.activityrank.game;

import com.bbw.cache.GameCacheService;
import com.bbw.common.DateUtil;
import com.bbw.common.ListUtil;
import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.db.entity.CfgActivityRankEntity;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.data.GameDataType;
import com.bbw.god.game.data.redis.GameRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 跨服冲榜，排名从1开始
 *
 * @author suhq
 * @date 2019年3月7日 下午3:58:28
 */
@Service
public class GameActivityRankService {
    @Autowired
    private GameCacheService gameCacheService;

    public List<GameActivityRank> getGameActivityRank() {
        return this.gameCacheService.getGameDatas(GameActivityRank.class);
    }

    /**
     * 获得跨服冲榜实例
     *
     * @param serverGroup
     * @param type
     * @return
     */
    public GameActivityRank getGameActivityRank(int serverGroup, ActivityRankEnum type) {
        return getGameActivityRank(serverGroup, DateUtil.now(), type);
    }

    public GameActivityRank getGameActivityRank(int serverGroup, Date date, ActivityRankEnum type) {
        if (serverGroup==17){
            serverGroup=16;
        }
        List<GameActivityRank> list= getGameActivityRank();
        if (ListUtil.isNotEmpty(list)){
            for (GameActivityRank tmp : list) {
                if (tmp.getServerGroup() == serverGroup && tmp.getType() == type.getValue() &&
                        DateUtil.isBetweenIn(date, tmp.getBegin(), tmp.getEnd())){
                    return tmp;
                }
            }
        }
        return null;
    }

    /**
     * 获得某个时间对应的跨服冲榜活动
     *
     * @param serverGroup
     * @param date
     * @return
     */
    public List<GameActivityRank> getGameActivityRanks(int serverGroup, Date date) {
        if (serverGroup==17){
            serverGroup=16;
        }
        List<GameActivityRank> gars = getGameActivityRank();
        List<GameActivityRank> rd=new ArrayList<>();
        if (ListUtil.isNotEmpty(gars)){
            for (GameActivityRank tmp : gars) {
                if (tmp.getServerGroup() == serverGroup && DateUtil.isBetweenIn(date, tmp.getBegin(), tmp.getEnd())){
                    rd.add(tmp);
                }
            }
        }
        return rd;
    }

    /**
     * 获取活动集
     *
     * @param type
     * @return
     */
    public List<CfgActivityRankEntity> getActivities(ActivityRankEnum type) {
        return Cfg.I.get(CfgActivityRankEntity.class).stream().filter(a -> a.getType() == type.getValue())
                .collect(Collectors.toList());
    }

    /**
     * 获得冲榜的key
     *
     * @param gaRank
     * @return
     */
    public String getRAKey(GameActivityRank gaRank) {
        int dateInt = DateUtil.toDateInt(gaRank.getBegin());
        String loopKey = String.valueOf(dateInt);
        Integer type = gaRank.getType();
        ActivityRankEnum activityRankEnum = ActivityRankEnum.fromValue(type);
        if (activityRankEnum == null) {
            return null;
        }
        String redisKey = activityRankEnum.getRedisKey();
        return GameRedisKey.getDataTypeKey(GameDataType.ACTIVITY_RANK, gaRank.getServerGroup().toString(), redisKey, loopKey);
    }
}
