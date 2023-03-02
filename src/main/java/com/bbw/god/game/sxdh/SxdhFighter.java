package com.bbw.god.game.sxdh;

import com.bbw.common.DateUtil;
import com.bbw.common.ID;
import com.bbw.god.game.sxdh.config.SxdhTool;
import com.bbw.god.gameuser.UserDataType;
import com.bbw.god.gameuser.UserSingleObj;
import lombok.Data;

import java.util.Date;

/**
 * 神仙大会参与者信息
 *
 * @author suhq
 * @date 2019-06-18 11:18:21
 */
@Data
public class SxdhFighter extends UserSingleObj {
    @Deprecated
    private Integer ticket = 0;// 门票
    private Integer freeTimes = 3;//免费次数
    @Deprecated
    private Integer bean = 0;// 仙豆
    private Integer beanBoughtTimes = 0;//仙豆购买次数
    private Date beatBoughtResetDate;
    private Integer joinTimes = 0;//参赛次数
    private Integer winTimes = 0;// 胜利次数
    private Integer streak = 0;// 连胜纪录
    private Integer maxStreak = 0;//最长连胜
    private Date lastGotDate;// 最近一次获取时间
    private Date joinDate;// 加入神仙大会的时间
    private Date lastSprintAwarded;//最近一次领取冲刺奖励

    public static SxdhFighter instance(long uid) {
        SxdhFighter fighter = new SxdhFighter();
        fighter.setId(ID.INSTANCE.nextId());
        fighter.setGameUserId(uid);
        Date now = DateUtil.now();
        fighter.resetFreeTimes();
        fighter.setLastGotDate(now);
        fighter.setJoinDate(now);
        return fighter;
    }

    /**
     * 新赛季重置
     */
    public void resetForNewSeason() {
        resetFreeTimes();
        this.joinTimes = 0;
        this.winTimes = 0;
        this.streak = 0;
        this.maxStreak = 0;
    }

    public void resetFreeTimes() {
        this.freeTimes = SxdhTool.getSxdh().getFreeTimesPerDay();
    }

    public void addBeanBoughtTimes(int num) {
        this.beanBoughtTimes += num;
    }

    public void deductFreeTimes() {
        freeTimes--;
    }

    @Override
    public UserDataType gainResType() {
        return UserDataType.SXDH_FIGHTER;
    }

}
