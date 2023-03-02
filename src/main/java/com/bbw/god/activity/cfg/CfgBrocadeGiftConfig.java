package com.bbw.god.activity.cfg;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 锦礼配置
 *
 * @author fzj
 * @date 2022/2/9 14:11
 */
@Data
public class CfgBrocadeGiftConfig implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 可投注的次数上限 */
    private Integer maxBetTimes;
    /** 轮次及奖励 */
    private List<TurnAndAwards> turns;
    /** 参与奖 */
    private List<ParticipateAwards> participateAwards;

    @Data
    public static class TurnAndAwards {
        /** 投注id */
        private Integer id;
        /** 轮次 */
        private Integer turn;
        /** 投注类型 */
        private Integer type;
        /** 开始时间 */
        private String begin;
        /** 开始投注时间 */
        private String beginDraw;
        /** 结束时间 */
        private String end;
        /** 投注需要消耗的法宝 */
        private Integer drawNeedTreasure;
        /** 数量 */
        private Integer num;
        /** 奖励 */
        private List<Award> awards;

        /**
         * 是否相同类别
         *
         * @param type
         * @return
         */
        public Boolean isMatch(int type) {
            return this.type % 50 == type % 50;
        }
    }

    @Data
    public static class ParticipateAwards {
        /** 概率 */
        private Integer prob;
        /** 奖励 */
        private List<Award> awards;
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }
}
