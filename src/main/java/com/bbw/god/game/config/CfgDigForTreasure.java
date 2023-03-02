package com.bbw.god.game.config;

import com.bbw.god.game.award.Award;
import lombok.Data;

import java.util.List;

/**
 *
 * 地图挖宝
 * @author：lwb
 * @date: 2021/3/9 17:08
 * @version: 1.0
 */
@Data
public class CfgDigForTreasure implements CfgInterface {
    private String key;
    private List<RandomAward> randomAwardsPool;// 随机奖励池
    private List<AdditionalAward> additionalAwardsPool;// 额外奖励

    @Data
    public static class RandomAward {
        private int percentage;//百分比概率  100%=》10000
        private List<Award> awards;
    }

    @Data
    public static class AdditionalAward {
        private int maxTimes;
        private List<Award> awards;
    }

    @Override
    public String getId() {
        return this.key;
    }

    @Override
    public int getSortId() {
        return 1;
    }

}