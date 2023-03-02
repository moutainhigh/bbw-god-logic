package com.bbw.god.game.wanxianzhen;

import com.bbw.god.game.award.Award;
import com.bbw.god.game.config.CfgInterface;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author lwb
 * @date 2020/4/23 16:38
 */
@Data
public class CfgWanXian implements CfgInterface {
    private String key;
    private Integer defaultPlayerLv;
    private Integer defaultCardNum;
    private Integer defaultCardLv;
    private Integer defaultCardHv;
    private List<RaceConfig> raceConfig;
    private List<WanXianEmail> emails;
    private List<WanXianAward> awardList;
    private List<WanXianAward> awardSpecialList;

    /**
     * 平台赛事配置
     */
    @Data
    public static class RaceConfig implements Serializable{
        //平台号
        private int groupId;
        //常规赛第一赛季时间
        private int firstSeason;
        private boolean open;
        /**
         * 以该赛季以前的所有赛季将关闭  搭配open使用
         */
        private Integer closBeginSeason=20200101;
        /**
         * 以该赛季以后的所有赛季将开启  搭配open使用
         */
        private Integer closEndSeason=20200101;
        //特色赛第一赛季时间
        private int firstSpecialSeason;
        //特色赛安排表
        private List<WanXianRacePlan> specialSeasonPlans;

        /**
         * 判断赛季是否开启
         *
         * 当open时 且不在[closBeginSeason,closEndSeason]之间的赛区 都是开启
         * @param season
         * @return
         */
        public boolean ifSeasonOpen(int season){
            if (!open){
                return false;
            }
            return season<closBeginSeason || closEndSeason<season;
        }
    }

    /**
     * 邮件配置
     */
    @Data
    public static class WanXianEmail implements Serializable{
        private Integer id;
        private String title;
        private String content;
    }

    /**
     * 奖励配置
     */
    @Data
    public static class WanXianAward implements Serializable{
        private Integer begin;
        private Integer end;
        private List<WanXianSeasonAward> seasonAwards;
    }

    /**
     * 具体奖励信息
     */
    @Data
    public static class WanXianSeasonAward implements Serializable{
        private Integer pid;
        private String memo;
        private Integer maxRank;
        private Integer minRank;
        private List<Award> awards;
    }

    /**
     * 具体赛事安排
     */
    @Data
    public static class WanXianRacePlan implements Serializable{
        private Integer beginSeason;
        private Integer endSeason;
        private List<Integer> plans;
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
