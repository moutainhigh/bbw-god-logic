package com.bbw.god.activityrank.server;

import com.bbw.cache.ServerCacheService;
import com.bbw.common.DateUtil;
import com.bbw.god.activityrank.ActivityRankEnum;
import com.bbw.god.server.ServerDataType;
import com.bbw.god.server.ServerService;
import com.bbw.god.server.redis.ServerRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 冲榜，排名从1开始
 *
 * @author suhq
 * @date 2019年3月7日 下午3:58:28
 */
@Service
public class ServerActivityRankService {
    @Autowired
    private ServerCacheService serverCacheService;
    @Autowired
    private ServerService serverService;

    /**
     * 获取活动榜单
     *
     * @param sId
     * @return
     */
    public List<ServerActivityRank> getServerActivityRanks(int sId) {
        return serverCacheService.getServerDatas(sId, ServerActivityRank.class);
    }

    /**
     * 添加榜单
     *
     * @param sar
     */
    public void addServerActivityRank(ServerActivityRank sar) {
        serverService.addServerData(sar.getSid(), sar);
    }

    /**
     * 删除活动榜单
     *
     * @param sId
     * @param sarIdsToDel
     */
    public void delServerActivityRank(int sId, List<Long> sarIdsToDel) {
        serverService.deleteServerDatas(sId, sarIdsToDel, ServerActivityRank.class);
    }

    /**
     * 获得区服冲榜实例
     *
     * @param sId
     * @param date
     * @param type
     * @return
     */
    public ServerActivityRank getServerActivityRank(int sId, Date date, ActivityRankEnum type) {
        return getServerActivityRanks(sId).stream()
                .filter(sar -> sar.getType() == type.getValue()
                        && DateUtil.isBetweenIn(date, sar.getBegin(), sar.getEnd()))
                .findFirst().orElse(null);
    }

    public ServerActivityRank getServerActivityRank(int sId, ActivityRankEnum type) {
        return getServerActivityRanks(sId).stream()
                .filter(sar -> sar.getType() == type.getValue()
                        && DateUtil.isBetweenIn(DateUtil.now(), sar.getBegin(), sar.getEnd()))
                .findFirst().orElse(null);
    }

    /**
     * 获得某个时间对应的冲榜活动
     *
     * @param sId
     * @param date
     * @return
     */
    public List<ServerActivityRank> getServerActivityRanks(int sId, Date date) {
        List<ServerActivityRank> sars = getServerActivityRanks(sId);
        return sars.stream().filter(sar -> DateUtil.isBetweenIn(date, sar.getBegin(), sar.getEnd()))
                .collect(Collectors.toList());
    }

    /**
     * 获得冲榜的key
     *
     * @param saRank
     * @return
     */
    public String getRAKey(ServerActivityRank saRank) {
        ActivityRankEnum aRankEnum = ActivityRankEnum.fromValue(saRank.getType());
        String loopKey = saRank.getOpenWeek().toString();
        return ServerRedisKey.getDataTypeKey(saRank.getSid(), ServerDataType.ACTIVITY_RANK, aRankEnum.getRedisKey(),
                loopKey);
    }
}
