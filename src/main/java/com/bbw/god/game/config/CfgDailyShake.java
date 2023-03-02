package com.bbw.god.game.config;

import com.bbw.god.game.award.Award;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 每日摇一摇
 *
 * @author: huanghb
 * @date: 2022/6/16 10:04
 */
@Data
public class CfgDailyShake implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = -2564165510365391727L;
    private String key;
    /** 摇一摇次数上限 */
    private int shakeTimesLimit;
    /** 第一个奖池 */
    private List<PrizePool> firstPrizePools;
    /** 第二个将此 */
    private List<PrizePool> secondPrizePools;
    /** 第三个奖池 */
    private List<PrizePool> thirdPrizePools;
    /** 福利集合 */
    private List<Welfare> welfares;

    @Data
    public static class PrizePool {
        /** id */
        private int id;
        /** 摇一摇次数 */
        private int shakeTimes;
        /** 奖励 */
        private List<Award> awards;
    }

    @Data
    public static class Welfare implements Serializable {
        private static final long serialVersionUID = 1L;
        /** 福利id */
        private int id;
        /** 商品id */
        private List<Integer> mallIds;
        /** 福利名称（效果） */
        private String name;
        /** 商品列别 */
        private int type;
        /** 福利加成 */
        private List<Integer> welfareAdds;
        /** 概率 */
        private int prob;
    }

    /**
     * 获取配置项到ID值
     *
     * @return
     */
    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 0;
    }
}
