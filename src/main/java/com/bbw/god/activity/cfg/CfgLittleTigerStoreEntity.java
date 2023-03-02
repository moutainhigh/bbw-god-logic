package com.bbw.god.activity.cfg;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 小虎商店配置
 *
 * @author fzj
 * @date 2022/3/9 10:50
 */
@Data
public class CfgLittleTigerStoreEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 奖励号码 */
    private List<Integer> awardNum;
    /** 刷新需要的小虎币 */
    private int refreshNeedLittleTigerCoin;
    /** 刷新次数及奖励 */
    private List<RefreshAndPoolPro> refreshAndPoolPro;
    /** 奖池物品 */
    private List<PoolAndAwards> poolAndAwards;
    /** 卡池 */
    private List<CardsPool> cardsPool;
    /** 累计刷新奖励 */
    private List<CumulativeRefreshAwards> cumulativeRefreshAwards;

    @Data
    public static class CumulativeRefreshAwards {
        /** 刷新次数 */
        private int refreshTimes;
        /** 奖励 */
        private List<Award> awards;
    }

    @Data
    public static class CardsPool {
        /** 奖池iD */
        private int poolId;
        /** 卡牌奖励 */
        private List<Award> cards;
    }

    @Data
    public static class PoolAndAwards {
        /** 奖池iD */
        private int poolId;
        /** 奖励 */
        private List<Award> awards;
    }

    @Data
    public static class RefreshAndPoolPro {
        /** 最小刷新次数 */
        private int minRefreshTimes;
        /** 最大刷新次数 */
        private int maxRefreshTimes;
        /** 奖池 */
        private List<PoolPro> poolPro;
    }

    @Data
    public static class PoolPro {
        /** 奖池Id */
        private int id;
        /** 概率 */
        private int pro;
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
