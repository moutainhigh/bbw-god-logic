package com.bbw.god.activityrank;

import com.bbw.common.*;
import com.bbw.db.redis.RedisZSetUtil;
import com.bbw.god.activity.config.ActivityConfig;
import com.bbw.god.activity.config.ActivityScopeEnum;
import com.bbw.god.activityrank.game.GameActivityRank;
import com.bbw.god.activityrank.game.GameActivityRankService;
import com.bbw.god.activityrank.server.ServerActivityRank;
import com.bbw.god.activityrank.server.ServerActivityRankService;
import com.bbw.god.db.entity.CfgActivityRankEntity;
import com.bbw.god.db.entity.CfgServerEntity;
import com.bbw.god.game.award.Award;
import com.bbw.god.game.award.AwardService;
import com.bbw.god.game.config.Cfg;
import com.bbw.god.game.data.GameDataService;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.mail.UserMail;
import com.bbw.god.server.ServerDataService;
import com.bbw.god.server.fst.server.FstServerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 冲榜，排名从1开始
 *
 * @author suhq
 * @date 2019年3月7日 下午3:58:28
 */
@Slf4j
@Service
public class ActivityRankService {
    @Autowired
    private GameUserService gameUserService;
    @Autowired
    private ServerDataService serverDataService;
    @Autowired
    private GameDataService gameDataService;
    @Autowired
    private GameActivityRankService gameActivityRankService;
    @Autowired
    private ServerActivityRankService serverActivityRankService;
    @Autowired
    private AwardService awardService;
    @Autowired
    private ActivityConfig activityConfig;
    @Autowired
    private FstServerService fstServerService;
    /**
     * <pre>
     * 富豪榜 score分值：铜钱数，	member：玩家ID
     * 充值榜 score分值：充值元宝，	member：玩家ID
     * 元素消耗榜 score分值：累计消耗元素，	member：玩家ID
     * 胜利宝箱 	score分值：累计打开的战斗宝箱数，	member：玩家ID
     * 攻城榜 score分值：玩家拥有城池数，	member：玩家ID
     * 等级榜 score分值：玩家等级，		member：玩家ID
     * 远征榜 score分值：累计移动步数，	member：玩家ID
     * 封神台冲榜 score分值：玩家排名，	member：玩家ID
     * 元宝榜 score分值：元宝消耗量， member：玩家ID
     * 仙缘榜 score分值：抽卡次数，	member：玩家ID
     * </pre>
     */
    @Autowired
    private RedisZSetUtil<Long> rankingList;

    /**
     * 获得区服冲榜实例
     *
     * @param sId
     * @param type
     * @return
     */
    public IActivityRank getActivityRank(int sId, ActivityRankEnum type) {
        List<CfgActivityRankEntity> activities = this.getActivities(type);
        if (ListUtil.isEmpty(activities)) {
            return null;
        }
        int scope = activities.get(0).getScope();
        // 跨服
        if (scope == ActivityScopeEnum.GAME.getValue()) {
            CfgServerEntity server = Cfg.I.get(sId, CfgServerEntity.class);
            return this.gameActivityRankService.getGameActivityRank(server.getGroupId(), type);
        }
        // 区服
        return this.serverActivityRankService.getServerActivityRank(sId, type);
    }

    public IActivityRank getActivityRank(int sId, Date date, ActivityRankEnum type) {
        List<CfgActivityRankEntity> activities = this.getActivities(type);
        if (ListUtil.isEmpty(activities)) {
            return null;
        }
        int scope = activities.get(0).getScope();
        // 跨服
        if (scope == ActivityScopeEnum.GAME.getValue()) {
            CfgServerEntity server = Cfg.I.get(sId, CfgServerEntity.class);
            return this.gameActivityRankService.getGameActivityRank(server.getGroupId(), date, type);
        }
        // 区服
        return this.serverActivityRankService.getServerActivityRank(sId, date, type);
    }

    /**
     * 获得某个时间对应的冲榜活动
     *
     * @param sId
     * @param date
     * @return
     */
    public List<IActivityRank> getActivityRanks(int sId, Date date) {
        List<IActivityRank> ars = new ArrayList<>();
        // 跨服
        CfgServerEntity server = Cfg.I.get(sId, CfgServerEntity.class);
        List<GameActivityRank> gars = this.gameActivityRankService.getGameActivityRanks(server.getGroupId(), date);
        if (ListUtil.isNotEmpty(gars)) {
            ars.addAll(gars);
        }
        // 区服
        List<ServerActivityRank> sars = this.serverActivityRankService.getServerActivityRanks(sId, date);
        if (ListUtil.isNotEmpty(sars)) {
            ars.addAll(sars);
        }

        return ars;
    }

    /**
     * 增加榜单的数值
     *
     * @param guId
     * @param addValue
     * @param type
     */
    public void incrementRankValue(long guId, long addValue, ActivityRankEnum type) {
        if (addValue == 0) {
            return;
        }
        int sId = this.gameUserService.getActiveSid(guId);
        IActivityRank ar = this.getActivityRank(sId, type);
        // 榜单不存在返回
        if (ar == null) {
            return;
        }
        String arKey = this.getRAKey(ar);

        if (!this.rankingList.exists(arKey)) {
            // 榜单不存在则初始化榜单
            this.initRanker(ar);
        }
        double score = this.rankingList.score(arKey, guId);
        score = MathTool.add(Math.floor(score), addValue, 0.1);
        score = MathTool.subtract(score, DateUtil.toDateTimeDouble());
        this.rankingList.add(arKey, guId, score);
        // rankingList.incrementScore(arKey, guId, delta);
    }

    /**
     * 获取分数
     *
     * @param guId
     * @param type
     * @return
     */
    private double getScore(long guId, ActivityRankEnum type) {
        int sId = this.gameUserService.getActiveSid(guId);
        IActivityRank ar = this.getActivityRank(sId, type);
        // 榜单不存在返回
        if (ar == null) {
            return -1;
        }
        String arKey = this.getRAKey(ar);
        if (!this.rankingList.exists(arKey)) {
            return -1;
        }
        return this.rankingList.score(arKey, guId);
    }

    /**
     * 设置排行，目前仅仅用于等级榜
     *
     * @param guId
     * @param value
     * @param type
     */
    public void setRankValue(long guId,int sId, int value, ActivityRankEnum type) {
        IActivityRank ar = this.getActivityRank(sId, type);
        // 榜单不存在返回
        if (ar == null) {
            return;
        }
        String arKey = this.getRAKey(ar);
        if (!this.rankingList.exists(arKey)) {
            // 榜单不存在则初始化榜单
            this.initRanker(ar);
        }
        double score = MathTool.add(value, 0.1);
        score = MathTool.subtract(score, DateUtil.toDateTimeDouble());
        this.rankingList.add(arKey, guId, score);
        // rankingList.add(arKey, guId, value);
    }

    /**
     * 获得玩家排行
     *
     * @param guId
     * @param type
     * @return
     */
    public int getRank(long guId, ActivityRankEnum type) {
        int sId = this.gameUserService.getActiveSid(guId);
        IActivityRank ar = this.getActivityRank(sId, type);
        // 榜单不存在，返回0
        if (ar == null) {
            return 0;
        }
        return this.getRank(guId, ar);
    }

    /**
     * 获得玩家排行
     *
     * @param guId
     * @param ar
     * @return
     */
    public int getRank(long guId, IActivityRank ar) {
        String arKey = this.getRAKey(ar);
        Long myRank = this.rankingList.reverseRank(arKey, guId);
        if (myRank == null) {
            return 0;
        }
        int rank = 0;
        List<TypedTuple<Long>> rankers = this.getAllRankers(ar);
        for (TypedTuple<Long> ranker : rankers) {
            rank++;
            Long uid = Optional.ofNullable(ranker.getValue()).orElse(0L);

            // 我的排名信息
            if (uid == guId) {
                break;
            }
        }
        return rank;
    }

    /**
     * 获取指定排名,start从1开始
     *
     * @param ar
     * @param start
     * @param end
     * @return
     */
    public List<TypedTuple<Long>> getRankers(IActivityRank ar, int start, int end) {
        String key = getRAKey(ar);
        Set<TypedTuple<Long>> set = rankingList.reverseRangeWithScores(key, start - 1, end - 1);
        return new ArrayList<>(set);
    }

    /**
     * 获取整个榜单
     *
     * @param ar
     * @return
     */
    public List<TypedTuple<Long>> getAllRankers(IActivityRank ar) {
        String arKey = this.getRAKey(ar);
        if (!this.rankingList.exists(arKey)) {
            // 榜单不存在则初始化榜单
            this.initRanker(ar);
        }
        Set<TypedTuple<Long>> rankersSet = null;
        if (ar.gainType() == ActivityRankEnum.FST_RANK.getValue()) {
            rankersSet = this.rankingList.rangeWithScores(arKey);
        } else {
            rankersSet = this.rankingList.reverseRangeWithScores(arKey);
        }
        // 获得有上榜条件的活动项
        List<CfgActivityRankEntity> arWithMinValue = this.getActivities(ActivityRankEnum.fromValue(ar.gainType())).stream()
                .filter(tmp -> tmp.getMinValue() > 0).collect(Collectors.toList());
        // 转换成List
        List<TypedTuple<Long>> rankersList = new ArrayList<>();
        List<Long> needToDel = new ArrayList<>();
        int rank = 0;
        for (TypedTuple<Long> ranker : rankersSet) {
            rank++;

            Long uid = Optional.ofNullable(ranker.getValue()).orElse(0L);
            // System.out.println(uid);
            // System.out.println(ranker.getScore());
            // 入榜限制填充的记录
            if (uid < 0 && ar.gainType() != ActivityRankEnum.FST_RANK.getValue()) {
                final int finalRank = rank;
                Double score = Optional.ofNullable(ranker.getScore()).orElse(0.0);
                // 对应上榜条件的最大排行
                int maxRank = arWithMinValue.stream().filter(tmp -> tmp.getMinValue() == score.intValue())
                        .max(Comparator.comparing(CfgActivityRankEntity::getMaxRank)).get().getMaxRank();
                // 是否需要移除填充记录,当有要移除的填充记录时，需要重新评估需要删除的最大值（maxRank + needToDel.size()）
                boolean isNeedToDel = finalRank > maxRank + needToDel.size();
                if (isNeedToDel) {
                    // System.out.println("remove" + uid);
                    needToDel.add(ranker.getValue());
                    continue;
                }
            }

            rankersList.add(ranker);
        }
        if (ListUtil.isNotEmpty(needToDel)) {
            this.rankingList.remove(arKey, needToDel.toArray(new Long[needToDel.size()]));
        }

        return rankersList;
    }

    /**
     * <pre>
     * 获得某个排行区间的玩家
     * 排行从1开始。
     * </pre>
     *
     * @param sar
     * @param start
     * @param end
     * @return
     */
    public Set<Long> rangeRankers(ServerActivityRank sar, int start, int end) {
        String arKey = this.getRAKey(sar);
        return this.rankingList.range(arKey, start - 1, end - 1);
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
     * 获得奖励集
     *
     * @param ar
     * @param activity
     * @return
     */
    public List<Award> getAwards(IActivityRank ar, CfgActivityRankEntity activity) {

        List<Award> awards = new ArrayList<>();
        // 冲榜第一名加奖
        if (activity.getMinRank() == 1 && StrUtil.isNotBlank(ar.gainExtraAward())) {
            awards.addAll(JSONUtil.fromJsonArray(ar.gainExtraAward(), Award.class));
        }
        List<Award> awardsFromDb = this.awardService.parseAwardJson(activity.getAwards(),Award.class);
        List<Award> subAwards = awardsFromDb.stream().filter(award -> award.getWeek() == ar.gainOpenWeek())
                .collect(Collectors.toList());
        if (ListUtil.isEmpty(subAwards)) {
            subAwards = awardsFromDb.stream().filter(award -> award.getWeek() == 0).collect(Collectors.toList());
        }
        awards.addAll(subAwards);
        return awards;
    }

    /**
     * 发放特定冲榜的奖励
     *
     * @param ar
     */
    public void sendRankerAwardsByType(IActivityRank ar) {
        try {
            ActivityRankEnum type = ActivityRankEnum.fromValue(ar.gainType());
            int sId = ar.gainSId();
            String partInfo = "";
            int scope = this.getActivities(type).get(0).getScope();
            if (scope == ActivityScopeEnum.GAME.getValue()) {
                partInfo = "区服组" + ((GameActivityRank) ar).getServerGroup();
            } else {
                partInfo = "区服" + sId;
            }

            log.info("{}开始发送 【{}】奖励", partInfo, type.getName());
            // 榜单
            List<TypedTuple<Long>> rankers = this.getAllRankers(ar);
            if (rankers == null || rankers.size() == 0) {
                log.info("{}{}没有玩家入榜", partInfo, type.getName());
            }
            // 发放奖励
            int rank = 0;
            List<UserMail> mailList = new ArrayList<>();
            List<RankAwardRecord> recordList = new ArrayList<>();// TODO 发奖记录
            List<CfgActivityRankEntity> activities = this.getActivities(type);
            CfgActivityRankEntity rankEntity = activities.stream().max(Comparator.comparing(
                    CfgActivityRankEntity::getMaxRank)).orElse(null);
            int limitToSend = rankEntity == null ? activityConfig.getNumOfRankAwardLimit() : rankEntity.getMaxRank();
            for (TypedTuple<Long> ranker : rankers) {
                rank++;
                Long uid = Optional.ofNullable(ranker.getValue()).orElse(0L);
                // 非玩家
                if (uid <= 0) {
                    continue;
                }
                // 发放奖励
                for (CfgActivityRankEntity a : activities) {
                    if (a.getMinRank() <= rank && a.getMaxRank() >= rank) {
                        List<Award> awards = this.getAwards(ar, a);
                        String content = LM.I.getMsgByUid(uid,"mail.activity.rank.content", type.getName(), rank);
                        log.info(uid + content);
                        UserMail mail = UserMail.newAwardMail(a.getName(), content, uid, awards);
                        mailList.add(mail);
                        RankAwardRecord record = RankAwardRecord.newInstance(type.getName(), uid, sId, mail.getId(), rank);
                        double score = this.getScore(uid, type);
                        record.setScore(score);
                        recordList.add(record);
                    }
                }
                // 超过榜单限制不在发放奖励
                if (rank == limitToSend) {
                    break;
                }
            }
            this.gameUserService.addItems(mailList);
            // 记录发放记录
            if (sId > 0) {
                this.serverDataService.addServerData(recordList);
            } else {
                List<GameRankAwardRecord> gRecords = recordList.stream().map(tmp -> GameRankAwardRecord.newInstance(tmp)).collect(Collectors.toList());
                this.gameDataService.addGameDatas(gRecords);
            }
            log.info("{}{}奖励发送完毕！", partInfo, type.getName());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    /**
     * 移除榜单
     *
     * @param ar
     */
    public void removeRank(IActivityRank ar) {
        String arKey = this.getRAKey(ar);
        // 如果榜单存在移除榜单
        if (this.rankingList.exists(arKey)) {
            this.rankingList.remove(arKey);
        }
    }

    /**
     * 初始化榜单（有入榜要求的）
     *
     * @param ar
     */
    private void initRanker(IActivityRank ar) {
        try {
            ActivityRankEnum type = ActivityRankEnum.fromValue(ar.gainType());
            String arKey = this.getRAKey(ar);
            log.info("初始化榜单【{}】", type.getName());
            if (type == ActivityRankEnum.FST_RANK) {
                Set<TypedTuple<Long>> rankers = fstServerService.getAllRankers(ar.gainSId());
                for (TypedTuple<Long> ranker : rankers) {
                    Long uid = Optional.ofNullable(ranker.getValue()).orElse(0L);
                    if (uid != 0) {
                        this.rankingList.add(arKey, ranker.getValue(), ranker.getScore());
                    }
                }
                return;
            }
            List<CfgActivityRankEntity> activities = this.getActivities(type);
            // 初始化榜单
            for (CfgActivityRankEntity a : activities) {
                if (a.getMinValue() > 0) {
                    // 将入榜要求的排名初始化\
                    for (long i = a.getMinRank(); i <= a.getMaxRank(); i++) {
                        this.rankingList.add(arKey, -i, Double.valueOf(a.getMinValue()));
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    /**
     * 获得冲榜的key
     *
     * @param aRank
     * @return
     */
    private String getRAKey(IActivityRank aRank) {
        if (aRank.gainSId() == 0) {
            GameActivityRank gar = (GameActivityRank) aRank;
            return this.gameActivityRankService.getRAKey(gar);
        }
        ServerActivityRank sar = (ServerActivityRank) aRank;
        return this.serverActivityRankService.getRAKey(sar);
    }

    /**
     * 获取玩家榜单积分
     *
     * @param uid
     * @param aRank
     * @return
     */
    public int getScore(long uid, IActivityRank aRank) {
        String key = getRAKey(aRank);
        return (int) rankingList.score(key, uid);
    }
}
