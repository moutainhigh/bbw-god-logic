package com.bbw.god.server.fst;

import com.bbw.db.redis.RedisHashUtil;
import com.bbw.db.redis.RedisZSetUtil;
import com.bbw.god.game.config.server.ServerTool;
import com.bbw.god.game.config.treasure.TreasureEnum;
import com.bbw.god.gameuser.GameUser;
import com.bbw.god.gameuser.GameUserService;
import com.bbw.god.gameuser.treasure.UserTreasureService;
import com.bbw.god.server.fst.game.FstRankingType;
import com.bbw.god.server.fst.robot.FstGameRobot;
import com.bbw.god.server.fst.robot.FstRobotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 封神台服务
 *
 * @author liuwenbin
 */
@Slf4j
public abstract class FstService {
    @Autowired
    protected GameUserService gameUserService;
    @Autowired
    protected UserTreasureService userTreasureService;
    // score分值：玩家排行，member：玩家ID
    @Autowired
    protected RedisZSetUtil<Long> rankingList;
    @Autowired
    protected FstRobotService fstRobotService;
    @Autowired
    protected RedisHashUtil<Long, Long> hasFightStateHUtil;

    protected static final Long FIGHT_TIME_OUT = 1000 * 60 * 5L;

    /**
     * 当前封神台类型
     *
     * @return
     */
    public abstract FstType getFstType();

    /**
     * 是否解锁封神台
     *
     * @param uid
     * @return
     */
    public abstract boolean isUnlock(long uid);

    /**
     * 检查编组状态
     *
     * @param uid
     */
    public abstract boolean checkCardGroupState(long uid);

    /**
     * 进入封神台主页
     *
     * @param uid
     * @return
     */
    public abstract RDFst intoFst(long uid);

    /**
     * 获得玩家排行,如果不在排行内，则加入排行
     *
     * @param uid
     * @return
     */
    public abstract int getFstRankWithIntoRanking(Long uid);

    /**
     * 获取当前排名
     *
     * @param uid
     * @return
     */
    public abstract int getFstRank(Long uid);

    /**
     * 获取榜单
     *
     * @param myRank
     * @param uid
     * @return
     */
    public abstract List<FstRankerParam> getRankList(int myRank, long uid);

    /**
     * 获取区间
     *
     * @param begin
     * @param end
     * @param uid
     * @return
     */
    public abstract List<FstRankerParam> getRangeRankList(int begin, int end, Long uid);

    /**
     * 封装数据
     *
     * @param uid
     * @param rank
     * @param canAttack
     * @return
     */
    public FstRankerParam getFstRankerParam(long uid, int rank, boolean canAttack, FstRankingType type) {
        if (uid < 0) {
            return getRobotFstRankerParam(uid, rank, canAttack);
        }
        FstRankerParam ranker = new FstRankerParam();
        GameUser user = gameUserService.getGameUser(uid);
        ranker.setId(uid);
        ranker.setHead(user.getRoleInfo().getHead());
        ranker.setIconId(user.getRoleInfo().getHeadIcon());
        ranker.setLevel(user.getLevel());
        ranker.setNickname(ServerTool.getServerShortName(user.getServerId()) + "·" + user.getRoleInfo().getNickname());
        ranker.setPvpRanking(rank);
        ranker.setFightAble(canAttack ? 1 : 0);
        ranker.setAblePoints(getPointByRank(rank, type));
        return ranker;
    }

    public FstRankerParam getRobotFstRankerParam(long robotId, int rank, boolean canAttack) {
        FstGameRobot info = fstRobotService.getRobotInfo(robotId);
        FstRankerParam ranker = new FstRankerParam();
        ranker.setId(robotId);
        ranker.setHead(info.getHead());
        ranker.setIconId(TreasureEnum.HEAD_ICON_Normal.getValue());
        ranker.setLevel(info.getLevel());
        ranker.setNickname(info.getNickname());
        ranker.setPvpRanking(rank);
        ranker.setFightAble(canAttack ? 1 : 0);
        ranker.setAblePoints(0);
        return ranker;
    }

    public abstract int getPointByRank(int rank, FstRankingType type);

    /**
     * 获取剩余挑战次数
     *
     * @param guId
     * @return
     */
    public abstract int getRemainChallengeNum(long guId);

    /**
     * 初始化封神台
     *
     * @param id
     * @return
     */
    public abstract boolean initFst(int id);

    /**
     * 交换名次
     *
     * @param uid1
     * @param uid2
     * @return
     */
    public abstract boolean swapRanking(Long uid1, FstVideoLog log1, Long uid2, FstVideoLog log2);

    /**
     * 检查双方是否可以战斗
     *
     * @param p1
     * @param p2
     */
    public abstract boolean checkFightState(long p1, long p2);

    /**
     * 移除战斗状态
     *
     * @param p1
     * @param p2
     */
    public abstract void removeFightState(long p1, long p2);

    /**
     * 获取昵称
     *
     * @param id
     * @return
     */
    public String getRankingUserNickName(long id) {
        if (id < 0) {
            return fstRobotService.getRobotInfo(id).getNickname();
        }
        return gameUserService.getGameUser(id).getRoleInfo().getNickname();
    }

    /**
     * 是否加入到了封神台
     *
     * @param uid
     * @return
     */
    public abstract boolean hasJoinFst(long uid);
}
