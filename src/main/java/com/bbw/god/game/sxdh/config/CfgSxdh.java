package com.bbw.god.game.sxdh.config;

import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CfgSxdh implements CfgInterface, Serializable {

    private static final long serialVersionUID = 1L;
    private String key;
    // 玩家竞技解锁等级
    private Integer pvpUnlockLevel = 18;
    private Integer numToShow;// 排行显示的名次
    private Integer freeTimesPerDay;//每日免费次数
    private Integer needTicket;//每场战斗需要的门票
    private Integer roboterLevelDiffLimit;//匹配一类机器人的等级差限制
    private Integer seasonBeginHour;//赛季开始
    private Integer seasonEndHour;//赛季结束
    private Integer openBeginHour;//每日开放开始时间
    private Integer openEndHour;//每日开放结束时间
    private String resetMallRecordDate;//重置购买次数时间
    private String beanExpireDate;//仙豆过期时间
    private Integer beanBoughtLimit;//神仙大会购买限制
    private Integer fightCardNum;//神仙大会战斗卡牌数
    private List<ScoreAward> segmentScoreAwards;// 段位加分
    private List<ScoreAward> rankScoreAwards;// 排名加分
    private List<CfgSxdhZoneEntity> zones;// 战区信息
    private List<CfgSxdhMedicineEntity> medicines;//丹药信息
    private List<SeasonChanlenge> seasonChanlenge;//丹药信息
    private List<SeasonPhase> seasonPhases;//赛季阶段信息

    public Integer getNeedTicket() {
        if (SxdhTool.isSpecialSeason()) {
            return 5;
        }
        return needTicket;
    }

    @Override
    public Serializable getId() {
        return key;
    }

    @Override
    public int getSortId() {
        return 1;
    }

    @Data
    public static class ScoreAward {
        private Integer minGap;
        private Integer maxGap;
        private Integer winScore;
        private Integer failScore;
    }

    @Data
    public static class SeasonChanlenge {
        private Integer id;
        private String name;
        private Integer value;
        private Integer award;
    }

    @Data
    public static class SeasonPhase {
        private Integer id;
        private Integer begin;
        private Integer end;
        private Integer prePhase;
        private String des;
        private Boolean hasPhaseAwards;
        private String segments;
        private Integer doubles;
    }
}
