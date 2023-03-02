package com.bbw.god.gameuser.achievement;

import com.bbw.common.DateUtil;
import com.bbw.common.MathTool;
import com.bbw.db.redis.RedisZSetUtil;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.data.redis.GameRedisKey;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.server.redis.ServerRedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.bbw.god.game.data.redis.RedisKeyConst.SPLIT;

/**
 * @author suchaobin
 * @description 成就排名服务
 * @date 2020/2/19 16:21
 */
@Service
public class AchievementRankService {
    @Autowired
    private RedisZSetUtil<Long> rankingList;// score分值：玩家排行，member：玩家ID
    @Autowired
    private GameUserService gameUserService;

    private static final String RANK = "rank";
    private static final String ACHIEVEMENT = "achievement";

    /**
     * 获取成就排名的全服key
     *
     * @param serverGroup
     * @return
     */
    private String getGameRankKey(int serverGroup) {
        return GameRedisKey.PREFIX + SPLIT + serverGroup + SPLIT + RANK + SPLIT + ACHIEVEMENT;
    }

    /**
     * 获取成就排名的区服key
     *
     * @param sid 区服id
     * @return
     */
    private String getServerRankKey(int sid) {
        return ServerRedisKey.PREFIX + SPLIT + sid + SPLIT + RANK + SPLIT + ACHIEVEMENT;
    }

    /**
     * 获得某个排行区间的玩家,排行从1开始。
     *
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Long>> getGameRankers(int serverGroup, int start, int end) {
        String gameRankKey = getGameRankKey(serverGroup);
        return rankingList.reverseRangeWithScores(gameRankKey, start - 1, end - 1);
    }

    /**
     * 获得某个排行区间的玩家,排行从1开始。
     *
     * @param start
     * @param end
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Long>> getServerRankers(int sid, int start, int end) {
        String serverRankKey = getServerRankKey(sid);
        return rankingList.reverseRangeWithScores(serverRankKey, start - 1, end - 1);
    }

    /**
     * 更新排行
     *
     * @param uid
     * @param addValue
     */
    public void incrementRankValue(long uid, int addValue) {
        int sid = gameUserService.getActiveSid(uid);
        CfgServerEntity server = ServerTool.getServer(sid);
        incrementGameRankValue(uid, server.getGroupId(), addValue);
        incrementServerRankValue(uid, sid, addValue);
    }

    /**
     * 修复排名积分,排名新增后，老玩家第一次登陆调用
     *
     * @param uid
     * @param value
     */
    public void repairRankValue(long uid, int value) {
        int sid = gameUserService.getActiveSid(uid);
        CfgServerEntity server = ServerTool.getServer(sid);
        //全服排行
        String gameRankKey = getGameRankKey(server.getGroupId());
        rankingList.add(gameRankKey, uid, value);
        //区服排行
        String serverRankKey = getServerRankKey(server.getMergeSid());
        rankingList.add(serverRankKey, uid, value);
    }

    /**
     * 更新score,更新全服排行
     *
     * @param uid
     * @param addValue
     */
    private void incrementGameRankValue(long uid, int serverGroup, int addValue) {
        String gameRankKey = getGameRankKey(serverGroup);
        addOrUpdateRankValue(gameRankKey, uid, addValue);
    }

    /**
     * 更新score,更新区服排行
     *
     * @param uid
     * @param sid
     * @param addValue
     */
    private void incrementServerRankValue(long uid, int sid, int addValue) {
        String serverRankKey = getServerRankKey(sid);
        addOrUpdateRankValue(serverRankKey, uid, addValue);
    }

    private void addOrUpdateRankValue(String key, long uid, int addValue) {
        double score = this.rankingList.score(key, uid);
        score = MathTool.add(Math.floor(score), addValue, 0.1);
        score = MathTool.subtract(score, DateUtil.toDateTimeDouble());
        this.rankingList.add(key, uid, score);
    }

    /**
     * 获得玩家全服排行
     *
     * @param uid
     * @return
     */
    public int getGameRank(long uid, int serverGroup) {
        String gameRankKey = getGameRankKey(serverGroup);
        Long myRank = rankingList.reverseRank(gameRankKey, uid);
        if (myRank == null) {
            return 0;
        }
        return myRank.intValue() + 1;
    }

    /**
     * 获得玩家区服排行
     *
     * @param uid
     * @return
     */
    public int getServerRank(long uid) {
        Integer serverId = gameUserService.getGameUser(uid).getServerId();
        return getServerRank(uid, serverId);
    }

    public int getServerRank(long uid, int sid) {
        String serverRankKey = getServerRankKey(sid);
        Long myRank = rankingList.reverseRank(serverRankKey, uid);
        if (myRank == null) {
            return 0;
        }
        return myRank.intValue() + 1;
    }

    /**
     * 获取整个全服榜单数量
     *
     * @return
     */
    public int getAllGameRankersSize(int serverGroup) {
        String gameRankKey = getGameRankKey(serverGroup);
        return (int) rankingList.size(gameRankKey);
    }

    /**
     * 获取整个区服榜单
     *
     * @param sid 区服id
     * @return
     */
    public Set<ZSetOperations.TypedTuple<Long>> getAllServerRankers(int sid) {
        String serverRankKey = getServerRankKey(sid);
        Set<ZSetOperations.TypedTuple<Long>> rankersSet = rankingList.reverseRangeWithScores(serverRankKey);
        return rankersSet;
    }

    public void removeGameRank(int serverGroup) {
        String rankingKey = getGameRankKey(serverGroup);
        if (rankingList.exists(rankingKey)) {
            rankingList.remove(rankingKey);
        }
    }

    public void removeServerRank(int sid) {
        String rankingKey = getServerRankKey(sid);
        if (rankingList.exists(rankingKey)) {
            rankingList.remove(rankingKey);
        }
    }

    /**
     * 获得成就总点数
     *
     * @param uid
     * @return
     */
    public int getAchievementScoreCount(long uid) {
        Integer serverId = gameUserService.getGameUser(uid).getServerId();
        return getAchievementScoreCount(uid, serverId);
    }

    public int getAchievementScoreCount(long uid, int sid) {
        String serverRankKey = getServerRankKey(sid);
        Double score = rankingList.score(serverRankKey, uid);
        return score.intValue();
    }

//	public void removeUserInServerRank(long uid) {
//		Integer sid = this.gameUserService.getGameUser(uid).getServerId();
//		removeUserInServerRank(uid, sid);
//	}
//
//	public void removeUserInServerRank(long uid, int sid) {
//		String serverRankKey = getServerRankKey(sid);
//		rankingList.remove(serverRankKey, uid);
//	}
//
//	public void removeUserInGameRank(long uid, int serverGroup) {
//		String gameRankKey = getGameRankKey(serverGroup);
//		rankingList.remove(gameRankKey, uid);
//	}

}
