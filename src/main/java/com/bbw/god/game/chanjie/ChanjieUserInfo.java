package com.bbw.god.game.chanjie;

import com.bbw.common.DateUtil;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 阐截斗法玩家信息
 *
 * @author lwb
 * @version 1.0
 * @date 2019年6月14日
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ChanjieUserInfo extends UserSingleObj {
    private Date joinDatetime;// 玩家加入教派时间
    private Integer seasonId = 0;// 赛季
    private Integer religiousId = 0;// 教派
    private Integer bloodVolume = ChanjieTools.BLOOD_VOLUME;// 玩家血量 初始3
    private Integer victory = 0;// 胜场
    private Integer defeat = 0;// 败场
    private Integer honor = 0;// 荣誉点
    private Integer honorLv = 0;// 头衔等级
    private Integer firstInto = 0;// 周六首次进入判断 0 为非首次 1 为首次
    private Integer inLDFX = 0;// 是否入围乱斗封神
    private Integer remind = 0;// 成就红点提示
    private String headName = "外门弟子";
    private Integer limitBuy = ChanjieTools.BUY_BLOOD_LIMIT;// 限购血量次数
    private Integer bought = 0;// 已购次数
    private Integer dataResetDate = DateUtil.toDateInt(new Date());// 每日重置时间
    private VictoryStats victoryStats = new VictoryStats(victory);// 连胜统计
    private List<Integer> specialHonorLiskeState = new ArrayList<Integer>();// 每日点赞情况 点赞成功后对应的值为获得教派奇人的UID

    @Data
    @NoArgsConstructor
    public static class VictoryStats {
        private Date gainDatetime = new Date();// 达成时间
        private Integer max = 0;// 最大连胜
        private Integer initVictory = 0;// 连胜统计起始胜场

        public VictoryStats(int initV) {
            initVictory = initV;
        }
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.ChanjieUserInfo;
    }

    public void addVictory() {
        this.victory++;
    }

    public void addDefeat() {
        this.defeat++;
    }

    public void addHonor(int addhonor) {
        this.honor += addhonor;
    }

    public void deductBlood() {
        this.bloodVolume--;
    }

    public void addBlood() {
        this.bloodVolume++;
    }

    public void addbought() {
        this.bought++;
    }

    public Integer getFightNum() {
        return this.victory + this.defeat;
    }

    public ChanjieType getReligiousType() {
        return ChanjieType.getType(religiousId);
    }

    /**
     * 当前赛季是否已经加入了教派
     *
     * @return
     */
    public boolean hasReligiousNowSeason() {
        return seasonId.equals(ChanjieTools.getNowSeason()) && religiousId != 0;
    }

    /**
     * 今日是否进行了数据初始化
     *
     * @return
     */
    public boolean todayHasInit() {
        return dataResetDate == DateUtil.toDateInt(new Date());
    }

    /**
     * 每日数据重置：血量、购买、点赞
     */
    public void dayReset() {
        bloodVolume = ChanjieTools.BLOOD_VOLUME;
        limitBuy = ChanjieTools.BUY_BLOOD_LIMIT;
        bought = 0;
        dataResetDate = DateUtil.toDateInt(new Date());
        specialHonorLiskeState = new ArrayList<Integer>();
        restVictoryStats();
    }

    /**
     * 周日乱斗数据重置：胜负场、荣誉点、连胜
     */
    public void sundayRest() {
        bloodVolume = 1;
        victory = 0;// 胜场
        defeat = 0;// 败场
        honor = 0;// 荣誉点
        restVictoryStats();
    }

    public void restShiZun() {
        Date beginDate = DateUtil.getWeekBeginDateTime(new Date());//本周开始时间
        Date joinDate = DateUtil.getWeekBeginDateTime(getJoinDatetime());//加入时间
        long times = DateUtil.toDateInt(beginDate) - DateUtil.toDateInt(joinDate);
        if (hasReligiousNowSeason() & times > 0) {
            // 新掌教 首次更新数据
            victory = 0;// 胜场
            defeat = 0;// 败场
            honor = 0;// 荣誉点
            honorLv = 8;// 头衔等级
            firstInto = 0;// 周六首次进入判断 0 为非首次 1 为首次
            headName = "掌教师尊";
            restVictoryStats();// 连胜统计
            joinDatetime = new Date();
        }
    }

    /**
     * 重置连胜统计
     */
    public void restVictoryStats() {
        victoryStats = new VictoryStats();// 连胜统计
        victoryStats.setGainDatetime(new Date());
        victoryStats.setInitVictory(victory);
        victoryStats.setMax(0);
    }

    public void restAll() {
        victory = 0;// 胜场
        defeat = 0;// 败场
        honor = 0;// 荣誉点
        honorLv = 1;// 头衔等级
        firstInto = 0;// 周六首次进入判断 0 为非首次 1 为首次
        headName = "外门弟子";
        restVictoryStats();// 连胜统计
        dayReset();
    }

    /**
     * 失败时需要执行
     */
    public void stopVictoryStats() {
        int win = this.victory - this.victoryStats.getInitVictory();
        if (this.victoryStats.getMax() < win) {
            this.victoryStats.setMax(win);
            this.victoryStats.setGainDatetime(new Date());
        }
    }

    /**
     * 获取最大的连胜次数
     */
    public int getMaxVictory() {
        int win = this.victory - this.victoryStats.getInitVictory();
        if (this.victoryStats.getMax() > win) {
            return this.victoryStats.getMax();
        }
        return win;

    }

    public boolean hasNotLife() {
        return bloodVolume <= 0;
    }

    public boolean isNextShiZun() {
        return seasonId.equals(ChanjieTools.getNextSeason());
    }

    /**
     * 是否还有购买血量次数
     *
     * @return
     */
    public boolean canBuyBlood() {
        return limitBuy > bought;
    }

    /**
     * 是否符合赛季奖励标准
     *
     * @return
     */
    public boolean canSendSeasonAward() {
        return getHonor() >= 5;
    }

    public void updateLDFXStatus(boolean isAdd) {
        inLDFX = isAdd ? 1 : 0;
    }

    /**
     * 是否入围乱斗封神
     *
     * @return
     */
    public boolean hasLDFX() {
        return inLDFX == 1;
    }

    /**
     * 是否上榜
     *
     * @return
     */
    public boolean isRnakingLDFX() {
        return hasLDFX() && getFightNum() > 0;
    }

    public Date getJoinDatetime() {
        if (joinDatetime == null) {
            return DateUtil.fromDateInt(20190101);
        }
        return joinDatetime;
    }
}
