package com.bbw.god.game.zxz.rank;

import com.bbw.common.DateUtil;
import com.bbw.common.MathTool;
import com.bbw.common.SetUtil;
import com.bbw.db.redis.RedisZSetUtil;
import com.bbw.god.game.data.redis.GameRedisKey;
import com.bbw.god.gameuser.GameUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 诛仙阵榜单，排名从1开始
 *
 * @author: suhq
 * @date: 2022/9/26 2:42 下午
 */
@Slf4j
@Service
public class ZxzRankService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private RedisZSetUtil<Long> rankingList;

    /**
     * 增加榜单的数值
     *  @param uid            玩家ID
     * @param addRegionLevel 区域等级加值
     * @param zxzLevel       诛仙阵难度
     * @param zxzRegion      诛仙阵区域
     */
    public void incrementRankValue(long uid, int addRegionLevel, int zxzLevel, int zxzRegion, int zxzBeginDate) {
        if (addRegionLevel == 0) {
            return;
        }
        int serverGroupId = gameUserService.getActiveGid(uid);
        String rankKey = getKey(serverGroupId, zxzLevel, zxzRegion, zxzBeginDate);
        double score = this.rankingList.score(rankKey, uid);
        score = MathTool.add(Math.floor(score), addRegionLevel, 0.1);
        score = MathTool.subtract(score, DateUtil.toDateTimeDouble());
        this.rankingList.add(rankKey, uid, score);
    }

    /**
     * 修护排行
     * @param uid
     * @param zxzLevel
     * @param zxzRegion
     * @param zxzBeginDate
     */
    public void setRankValue(long uid, int zxzLevel, int zxzRegion, int zxzBeginDate,double score) {
        int serverGroupId = gameUserService.getActiveGid(uid);
        String rankKey = getKey(serverGroupId, zxzLevel, zxzRegion, zxzBeginDate);
        this.rankingList.add(rankKey, uid, score);
    }

    /**
     * 获取某个玩家的排行
     *
     * @param uid          玩家ID
     * @param zxzLevel     诛仙阵难度
     * @param zxzRegion    诛仙阵区域
     * @param zxzBeginDate 诛仙阵开始时间。eg: 20221001
     * @return
     */
    public int getRank(long uid, int zxzLevel, int zxzRegion, int zxzBeginDate) {
        int serverGroupId = gameUserService.getActiveGid(uid);
        String rankKey = getKey(serverGroupId, zxzLevel, zxzRegion, zxzBeginDate);
        Long myRank = this.rankingList.reverseRank(rankKey, uid);
        if (myRank == null) {
            return 0;
        }
        return myRank.intValue();
    }

    /**
     * 获取榜单
     *
     * @param serverGroup  区服组
     * @param zxzLevel     诛仙阵难度
     * @param zxzRegion    诛仙阵区域
     * @param zxzBeginDate 诛仙阵开始时间。eg: 20221001
     * @return
     */
    public List<ZxzRanker> getRankers(int serverGroup, int zxzLevel, int zxzRegion, int zxzBeginDate, int startRank, int endRank) {
        String rankKey = getKey(serverGroup, zxzLevel, zxzRegion, zxzBeginDate);
        Set<TypedTuple<Long>> rankersSet = rankingList.reverseRangeWithScores(rankKey, startRank - 1, endRank - 1);
        if (SetUtil.isEmpty(rankersSet)) {
            return new ArrayList<>();
        }
        // 转换成List
        List<ZxzRanker> rankers = new ArrayList<>();
        int rank = startRank;
        for (TypedTuple<Long> ranker : rankersSet) {
            long uid = ranker.getValue();
            int regionLevel = ranker.getScore().intValue();
            rankers.add(new ZxzRanker(uid, rank, regionLevel,ranker.getScore()));
            rank++;
        }
        return rankers;
    }

    /**
     * 诛仙阵排行key
     *
     * @param serverGroup  区服组
     * @param zxzLevel     诛仙阵难度
     * @param zxzRegion    诛仙阵区域
     * @param zxzBeginDate 诛仙阵开始时间。eg: 20221001
     * @return
     */
    private String getKey(int serverGroup, int zxzLevel, int zxzRegion, int zxzBeginDate) {
        String zxzRankKey = GameRedisKey.getDataTypeKey(serverGroup, "zxzRank");
        zxzRankKey += GameRedisKey.SPLIT + zxzBeginDate;
        zxzRankKey += GameRedisKey.SPLIT + zxzLevel;
        zxzRankKey += GameRedisKey.SPLIT + zxzRegion;
        return zxzRankKey;
    }
}
