package com.bbw.god.gameuser.nightmarenvwam.cfg;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgEntityInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 梦魇女娲庙配置
 *
 * @author fzj
 * @date 2022/5/4 9:34
 */
@Data
public class CfgNightmareNvmEntity implements CfgEntityInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private String key;
    /** 泥人进度值单位 */
    private Integer clayFigurineValueUnit;
    /** 泥人值翻倍 */
    private Integer clayFigurineValueDoubled;
    /** 捏人需要的泥人进度值 */
    private Integer pinchPeopleNeedValue;
    /** 每日捏人有效次数 */
    private Integer dayPinchPeopleValidTimes;
    /** 捏人分数以及对应标识 */
    private List<PinchPeopleScore> pinchPeopleScore;
    /** 每日挑战奖励 */
    private List<DayChallengeAwards> dayChallengeAwards;
    /** 捏人累计可领奖分数 */
    private Integer pinchPeopleAwardNeedValue;
    /** 累计分数奖励以及概率 */
    private List<TotalScoreAwards> totalScoreAwards;
    /** 每日礼包奖励 */
    private List<DayGiftAwards> dayGiftAwards;
    /** 最多可上架的商品数量 */
    private Integer maxShelvesNum;
    /** 摊位租赁时限（小时） */
    private Integer leaseTimeLimit;
    /** 最多的要价方式 */
    private Integer maxPriceWay;
    /** 神格总进度 */
    private Integer godheadTotalProgress;
    /** 限制次数 */
    private Integer exchangeLimitTimes;
    /** 卡牌对应神格牌 */
    private List<GodHeadCard> godHeadCard;
    /** 租赁价格 */
    private Integer rentalPrice;
    /** 最多讨价数量 */
    private Integer maxBargainNum;
    /** 讨价时限 */
    private Integer bargainLimit;

    @Data
    public static class GodHeadCard {
        private int cardId;
        private int godHeadCardId;
    }

    /**
     * 捏人分数以及对应标识
     */
    @Data
    public static class PinchPeopleScore {
        /** 标识 */
        private int sign;
        /** 分数 */
        private Integer score;
    }

    /**
     * 累计分数奖励以及概率
     */
    @Data
    public static class TotalScoreAwards {
        /** 概率 */
        private Integer progress;
        /** 奖励类型 */
        private Integer awardType;
    }

    /**
     * 累计分数奖励以及概率
     */
    @Data
    public static class DayGiftAwards {
        /** 概率 */
        private Integer progress;
        /** 奖励类型 */
        private Integer awardType;
    }

    /**
     * 每日挑战奖励
     */
    @Data
    public static class DayChallengeAwards {
        /** 最小分数 */
        private double minScore;
        /** 最大分数 */
        private double maxScore;
        /** 奖励 */
        private List<Award> awards;
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
