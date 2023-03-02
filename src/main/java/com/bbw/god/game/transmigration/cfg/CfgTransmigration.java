package com.bbw.god.game.transmigration.cfg;

import com.bbw.common.DateUtil;
import com.bbw.god.game.award.RankerAward;
import com.bbw.god.game.config.CfgBuff;
import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 轮回世界配置类
 *
 * @author: suhq
 * @date: 2021/9/10 10:25 上午
 */
@Data
public class CfgTransmigration implements CfgInterface, Serializable {
    private static final long serialVersionUID = 7073473097670406575L;
    private String key;
    /** 开放的区服组 */
    private List<Integer> sgIds;
    /** 首轮开始时间 */
    private String firstBeginDate;
    /** 首轮结束时间 */
    private String firstEndDate;
    /** 开始时分秒 */
    private String beginHms;
    /** 结束时分秒 */
    private String endHms;
    /** 空挡天数 */
    private Integer gapDays;
    /** 持续天数 */
    private Integer duration;
    /** 高光记录数 */
    private Integer highLightNum;
    /** 高光最小分数要求 */
    private Integer highLightMinScore;
    /** 初始主城属性 */
    private List<Integer> firstMainCityDefenderTypes;
    /** 城池特性 */
    private List<Integer> cityEffects;
    /** 城池第一名加分 */
    private Integer extraScoreForCityNo1;
    /** 精英野怪额外概率 */
    private Integer eltiteYgExtraProb;
    /** 解锁需要的城池数 */
    private Integer unlockCityNum;
    /** 显示图标需要的城池数 */
    private Integer showIconCityNum;
    /** 守将规则 */
    private List<CfgTransmigrationDefender> defenders;
    /** 区域划分 */
    private Map<Integer, List<String>> areaDevision;
    /** 轮回buff */
    private List<CfgBuff> transmigrationBuff;
    /** 排行奖励 */
    private List<RankerAward> rankerAwards;
    /** 轮回目标奖励 */
    private List<CfgTransmigrationTarget> targets;

    public Date gainFirstBeginDate() {
        return DateUtil.fromDateTimeString(firstBeginDate);
    }

    public Date gainFirstEndDate() {
        return DateUtil.fromDateTimeString(firstEndDate);
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
