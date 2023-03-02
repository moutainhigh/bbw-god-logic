package com.bbw.god.game.transmigration;

import com.bbw.common.DateUtil;
import com.bbw.common.LM;
import com.bbw.common.ListUtil;
import com.bbw.common.MathTool;
import com.bbw.db.redis.RedisZSetUtil;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.RankerAward;
import com.bbw.god.game.config.city.ChengC;
import com.bbw.god.game.config.city.CityTool;
import com.bbw.god.game.transmigration.cfg.CfgTransmigration;
import com.bbw.god.game.transmigration.cfg.TransmigrationTool;
import com.bbw.god.game.transmigration.entity.GameTransmigration;
import com.bbw.god.game.transmigration.entity.UserTransmigrationRecord;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 轮回总榜，定时更新榜单
 *
 * @author: suhq
 * @date: 2021/9/13 2:32 下午
 */
@Slf4j
@Service
@Deprecated
public class TransmigrationRankTotalService {
    @Autowired
    private GameUserService gameUserService;
    /** score分值：评分，member：uid **/
    @Autowired
    private RedisZSetUtil<Long> rankingList;
    @Autowired
    private TransmigrationEnterService transmigrationEnterService;
    @Autowired
    private TransmigrationCityRecordService transmigrationCityRecordService;
    @Autowired
    private TransmigrationRankCityService rankCityService;
    @Autowired
    private TransmigrationCityNewRecordTimeService cityNewRecordTimeService;

    /**
     * 更新榜单
     * 定时调用
     *
     * @param transmigration
     */
    public void updateRankers(GameTransmigration transmigration) {
        // 榜单不存在返回
        if (null == transmigration) {
            return;
        }
        long start = System.currentTimeMillis();
        Set<Long> uids = transmigrationEnterService.getUids(transmigration);
        CfgTransmigration cfg = TransmigrationTool.getCfg();
        List<ChengC> chengCs = CityTool.getChengCs();
        //获取所有玩家新纪录的时间
        Map<Long, Long> allNewRecordTime = cityNewRecordTimeService.getAllNewRecordTime(transmigration);
        //获取每座城最好的玩家（多个第一名）
        Map<Integer, List<Long>> bestUidsPerCity = new HashMap<>();
        //获取每座城对应的攻打信息
        Map<Integer, Map<Long, Long>> allRecordsPerCity = new HashMap<>();
        for (ChengC chengC : chengCs) {
            int cityId = chengC.getId();
            List<Long> bestUids = rankCityService.getBestUids(transmigration, cityId);
            bestUidsPerCity.put(cityId, bestUids);
            Map<Long, Long> allRecordsWithUid = transmigrationCityRecordService.getAllRecords(transmigration, cityId);
            allRecordsPerCity.put(cityId, allRecordsWithUid);
        }
        //生成新的玩家积分
        Map<Long, Integer> newUidScoreMap = new HashMap<>();
        for (Long uid : uids) {
            List<Long> recordsPerUid = new ArrayList<>();
            int extraScore = 0;
            for (ChengC chengC : chengCs) {
                Integer cityId = chengC.getId();
                //获取玩家攻打过的城池的最好记录
                Map<Long, Long> uidsWithRecord = allRecordsPerCity.get(cityId);
                if (null != uidsWithRecord && uidsWithRecord.size() > 0) {
                    Long recordId = uidsWithRecord.get(uid);
                    if (null != recordId && recordId > 0) {
                        recordsPerUid.add(recordId);
                    }
                }
                //处理第一名加分
                List<Long> bestUids = bestUidsPerCity.get(cityId);
                if (ListUtil.isNotEmpty(bestUids) && bestUids.contains(uid)) {
                    extraScore += cfg.getExtraScoreForCityNo1();
                }
            }
            //玩家新的分数
            int score = extraScore;
            if (ListUtil.isNotEmpty(recordsPerUid)) {
                List<UserTransmigrationRecord> utRecords = gameUserService.getUserDatas(uid, recordsPerUid, UserTransmigrationRecord.class);
                score += utRecords.stream().mapToInt(tmp -> ListUtil.sumInt(tmp.getScoreCompositions())).sum();
            }
            if (score > 0) {
                newUidScoreMap.put(uid, score);
            }
        }
        //更新排行
        String rankKey = getRankKey(transmigration);
        for (Long uid : newUidScoreMap.keySet()) {
            double score = newUidScoreMap.get(uid);
            Long newRecordTime = allNewRecordTime.getOrDefault(uid, 0L);
            //时间修正，相同分数，先达到的排在前面
            score = MathTool.add(score, 0.1);
            BigDecimal b = new BigDecimal("0.0" + newRecordTime);
            double timeRevicse = b.doubleValue();
            score = MathTool.subtract(score, timeRevicse);
            this.rankingList.add(rankKey, uid, score);
        }
        long usedTime = System.currentTimeMillis() - start;
        if (usedTime > 3000) {
            log.error("轮回荣耀榜更新耗时(ms)：" + usedTime);
        }

    }

    /**
     * 获得玩家排行
     *
     * @param transmigration
     * @param uid
     * @return 如果未开启或者没有入榜，返回0
     */
    public int getRank(GameTransmigration transmigration, long uid) {
        if (null == transmigration) {
            return 0;
        }
        String rankKey = getRankKey(transmigration);
        Long myRank = this.rankingList.reverseRank(rankKey, uid);
        if (myRank == null) {
            return 0;
        }
        return myRank.intValue() + 1;
    }

    /**
     * 获取玩家榜单积分
     *
     * @param transmigration
     * @param uid
     * @return 如果未开启轮回，返回-1
     */
    public int getScore(GameTransmigration transmigration, long uid) {
        if (null == transmigration) {
            return -1;
        }
        String rankKey = getRankKey(transmigration);
        return (int) rankingList.score(rankKey, uid);
    }

    /**
     * 获取某个排名的评分
     *
     * @param transmigration
     * @param rank
     * @return
     */
    public int getScoreByRank(GameTransmigration transmigration, int rank) {
        int score = 0;
        List<ZSetOperations.TypedTuple<Long>> rankers = getRankersWithScore(transmigration, rank, rank);
        if (ListUtil.isNotEmpty(rankers)) {
            score = rankers.get(0).getScore().intValue();
        }
        return score;
    }


    /**
     * <pre>
     * 获得某个排行区间的玩家
     * 排行从1开始。
     * </pre>
     *
     * @param transmigration
     * @param start
     * @param end
     * @return
     */
    public Set<Long> getRankers(GameTransmigration transmigration, int start, int end) {
        String rankKey = getRankKey(transmigration);
        return this.rankingList.range(rankKey, start - 1, end - 1);
    }

    /**
     * 获取指定排名,start从1开始
     *
     * @param transmigration
     * @param start
     * @param end
     * @return
     */
    public List<TypedTuple<Long>> getRankersWithScore(GameTransmigration transmigration, int start, int end) {
        String rankKey = getRankKey(transmigration);
        Set<TypedTuple<Long>> set = rankingList.reverseRangeWithScores(rankKey, start - 1, end - 1);
        return new ArrayList<>(set);
    }

    /**
     * 获取整个榜单
     *
     * @param transmigration
     * @return
     */
    public List<TypedTuple<Long>> getAllRankersWithScore(GameTransmigration transmigration) {
        String rankKey = getRankKey(transmigration);
        Set<TypedTuple<Long>> rankersSet = rankingList.reverseRangeWithScores(rankKey);
        return rankersSet.stream().collect(Collectors.toList());
    }

    /**
     * 获取榜单总人数
     *
     * @param transmigration
     * @return
     */
    public int getRankerNum(GameTransmigration transmigration) {
        String rankKey = getRankKey(transmigration);
        Long size = rankingList.size(rankKey);
        return size.intValue();
    }


    /**
     * 发放榜单奖励
     * 定时调用
     *
     * @param transmigration
     */
    public void sendRankerAwards(GameTransmigration transmigration) {
        try {
            log.info("开始发送轮回世界[区服组{}]奖励", transmigration.getSgId());
            // 榜单
            List<TypedTuple<Long>> rankers = this.getAllRankersWithScore(transmigration);
            if (ListUtil.isEmpty(rankers)) {
                log.info("轮回世界[区服组{}]没有玩家入榜", transmigration.getSgId());
                return;
            }
            // 发放奖励
            int rank = 0;
            List<UserMail> mailList = new ArrayList<>();
            CfgTransmigration cfg = TransmigrationTool.getCfg();
            List<RankerAward> rankerAwards = cfg.getRankerAwards();
            int maxRank = rankerAwards.stream().mapToInt(RankerAward::getMinRank).max().getAsInt() - 1;
            Date nextBegin = DateUtil.addDays(transmigration.getEnd(), cfg.getGapDays());
            String nextBeginStr = DateUtil.toString(nextBegin, "M月d日 08:00");
            for (TypedTuple<Long> ranker : rankers) {
                rank++;
                Long uid = Optional.ofNullable(ranker.getValue()).orElse(0L);
                // 非玩家
                if (uid <= 0) {
                    continue;
                }
                int finalRank = rank;
                RankerAward rankerAward = rankerAwards.stream().filter(tmp -> tmp.getMinRank() <= finalRank && tmp.getMaxRank() >= finalRank).findFirst().get();
                List<Award> awards = rankerAward.getAwards();
                String content = LM.I.getMsgByUid(uid,"mail.transmigration.rank.inList.content", rank, nextBeginStr);
                if (rank > maxRank) {
                    content = LM.I.getMsgByUid(uid,"mail.transmigration.rank.notInList.content", rank, nextBeginStr);
                }
                log.info(uid + content);
                String title = LM.I.getMsgByUid(uid,"mail.transmigration.rank.title");
                UserMail mail = UserMail.newAwardMail(title, content, uid, awards);
                mailList.add(mail);
            }
            this.gameUserService.addItems(mailList);
            log.info("轮回世界[区服组{}]榜单奖励发送完毕！", transmigration.getSgId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    /**
     * 总榜：game:transmigration:区服组:开始日期:rank
     *
     * @param transmigration
     * @return
     */
    public static String getRankKey(GameTransmigration transmigration) {
        return TransmigrationKey.getBaseRankKey(transmigration);
    }
}
